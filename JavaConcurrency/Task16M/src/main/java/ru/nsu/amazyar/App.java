package ru.nsu.amazyar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

/**
 * Hello world!getElementById
 *
 */
public class App 
{
    public static final Charset charset = Charset.forName("UTF-8");
    private static final CharsetDecoder decoder = charset.newDecoder();

    private static final byte[] URL_STARTER = {'\n','-','-','-','-','-','-','s','t','a','r','t','-','-','-','-','-','-','\n','\n'};
    private static final byte[] URL_ENDER = {'\n','-','-','-','-','-','-','e','n','d','-','-','-','-','-','-','\n','\n'};

    private static final int DEFAULT_BUFFER_BYTES = 16384;
    // private static final int DEFAULT_BUFFER_BYTES = 32768;
    private static final NonblockingCircularByteBuffer buffer = new NonblockingCircularByteBuffer(DEFAULT_BUFFER_BYTES);

    public static void main( String[] args ) throws IOException{
        int n_urls = Integer.parseInt(args[0]);
        try{
            String[] urls = Arrays.copyOfRange(args, 1, n_urls+1);
            Thread loader = new Thread(() -> {
                try {
                    loadURLS(urls);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            });
            
            Thread printer = new Thread(() -> printURLs());
            loader.start();
            printer.start();
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println(n_urls-args.length + " URLs not provided");
            return;
        }

    }
    
    private static void waitUser() {
        System.out.println("\nPress enter to scroll down.");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printURLs(){
        while(true){
            synchronized(buffer){
                while(buffer.getReadLimit() == 0){ // while there is nothing to read
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    

            waitUser(); // wait for user to approve printing
            synchronized(buffer){
            for (int i = 0; i < 25; i++) {
                if(buffer.getReadLimit() == 0){
                    break;
                }
                    byte last_byte = printCachedLine();
                    if(last_byte == '\0'){  // all URLs are read
                        return;
                    }
                }
                
                buffer.notifyAll(); // notify loaders that some data has been read
            }
        }
    }
    

    // called with presumption that buffer is not empty
    // returns last read byte
    private static byte printCachedLine(){
        byte[] read_byte = {'0'};
        while(read_byte[0] != '\n' && read_byte[0] != '\0'){
            buffer.poll(read_byte);
            try {
                System.out.print(decoder.decode(ByteBuffer.wrap(read_byte)));
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }
        }
        return read_byte[0];
    }

    private static void loadURLS(String[] urls) throws IOException{
        synchronized(buffer){
            for(String url : urls){
                // Initialise connection
                System.out.println("[] Trying to cache " + url);
                URL fileUrl = URI.create(url).toURL();
                HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                System.out.println("[] GET Response Code :: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    if(DEFAULT_BUFFER_BYTES < getRequiredSpace(connection)){
                        System.out.println("[] Resource " + connection.getURL() + " is too big to cache. " + (getRequiredSpace(connection) - DEFAULT_BUFFER_BYTES) + " buffer bytes needed.");
                        continue;
                    }   

                    // cache page if buffer is free enough
                    cacheURL(connection);
                } else {
                System.out.println("[] GET request did not work for " + connection.getURL());
                }
            }
                while(buffer.getWriteLimit() < 1){
                    try {
                        buffer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                buffer.offer((byte)'\0');
                System.out.println("[] All URLs cached");

                buffer.notifyAll();
        }
    }

    private static void cacheURL(HttpURLConnection connection) throws IOException {
        synchronized(buffer){
            while(buffer.getWriteLimit() < getRequiredSpace(connection)){ // wait if buffer is not yet free
                try {
                    System.out.println("[] Can't cache "  + connection.getURL() + " right now. Need " + (getRequiredSpace(connection) - buffer.getWriteLimit()) + " bytes");
                    buffer.wait(); // wait for printer to print next 25 lines
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("[] Caching "  + connection.getURL() + " right now");
            buffer.offer(URL_STARTER);
            InputStream in = connection.getInputStream();
            int next_byte;
            while ((next_byte = in.read()) != -1) {
                buffer.offer((byte)next_byte);
            }
            buffer.offer(URL_ENDER);

            System.out.println("[] Successfully cached " + connection.getURL());
            System.out.println("[] " + buffer.getWriteLimit() + " bytes left in buffer");

            buffer.notifyAll();
            in.close();
            connection.disconnect();
        }
    } 


    private static int getRequiredSpace(HttpURLConnection connection){
        return connection.getContentLength() + URL_STARTER.length + URL_ENDER.length;
    }
}
