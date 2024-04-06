package Task15.src.main.java.ru.nsu.amazyar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Client {
    public static final Charset charset = Charset.forName("UTF-8");
    public static final CharsetEncoder encoder = charset.newEncoder();
    public static final CharsetDecoder decoder = charset.newDecoder();

    public static final String POISON_PILL = "/stop";
    private final ByteBuffer readBuffer;
    public Client() throws IOException{
        this.readBuffer = ByteBuffer.allocate(256);
    }
            

    public void start() throws IOException {
        try(BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));){

            // get server's address and port
            System.out.println("Provide forwarder's address: ");
            InetAddress forwAddress = InetAddress.getByName(stdIn.readLine());
            System.out.println("Provide forwarder's port: ");
            int servPort = Integer.parseInt(stdIn.readLine());
            InetSocketAddress forwarderSocketAddress = new InetSocketAddress(forwAddress, servPort);

            try(
                SocketChannel socketCh = SocketChannel.open(forwarderSocketAddress);
            ){
                // configure socket
                socketCh.configureBlocking(true);
                System.out.println("Connection to Forwarder[" + forwarderSocketAddress + "] established");

                String userInput;
                while (!(userInput = stdIn.readLine()).equals(POISON_PILL)) {
                    if(!socketCh.isConnected()){
                        System.out.println("Server closed connection through Forwarder");
                        socketCh.close();
                        break;
                    }
                    socketCh.write(encoder.encode(CharBuffer.wrap(userInput)));
                    socketCh.read(readBuffer);
                    readBuffer.flip();
                    System.out.println("Reply from server: " + decoder.decode(readBuffer));
                    readBuffer.clear();
                }
                
                System.out.println("Closing due to '/stop' command");
            }
        }    
    }
}