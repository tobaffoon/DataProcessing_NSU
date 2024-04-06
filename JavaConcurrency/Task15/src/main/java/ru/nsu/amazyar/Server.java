package Task15.src.main.java.ru.nsu.amazyar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Server {
    public static final Charset charset = Charset.forName("UTF-8");
    private static final CharsetEncoder encoder = charset.newEncoder();
    private static final CharsetDecoder decoder = charset.newDecoder();
    public static final int PORT = 10001;
    private final InetSocketAddress serverSocketAddress;

    private final Selector selector;
    public Server() throws IOException{
        serverSocketAddress = new InetSocketAddress("localhost", PORT);
        // serverSocketAddress = new InetSocketAddress("localhost", PORT);
        selector = Selector.open();
    } 

    public void start() throws IOException{
        try ( 
            ServerSocketChannel serverSocketCh = ServerSocketChannel.open();
        ){
            serverSocketCh.bind(serverSocketAddress);
            serverSocketCh.configureBlocking(false);
            serverSocketCh.register(selector, SelectionKey.OP_ACCEPT);
            
            System.out.println("-----Server successfully started on " + serverSocketAddress + "-----");

            while(true){
                selector.select();
                for(SelectionKey key : selector.selectedKeys()){
                    // create new connection
                    if(key.isAcceptable()){
                        addClient(serverSocketCh);
                    }

                    // read client's data
                    if(key.isReadable()){
                        replyToMessage(key);
                    }
                }
            }
        }
    }

    private void addClient(ServerSocketChannel serverSocket) throws IOException{
        // add client socket 
        SocketChannel client = serverSocket.accept();
        // no client to accept
        if(client == null){
            return;
        }
        
        client.configureBlocking(false);

        // register new client socket and attach separate buffer to it
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));
        System.out.println("-----Client[" + client.getRemoteAddress() + "] conneceted-----");
    }

    private void replyToMessage(SelectionKey clientKey) throws CharacterCodingException, IOException{
        SocketChannel clientSocket = (SocketChannel)clientKey.channel();
        ByteBuffer buffer = (ByteBuffer)clientKey.attachment();
        int r = clientSocket.read(buffer);
        if(r == -1){
            clientSocket.close();
            System.out.println("Client[" + clientSocket.getRemoteAddress() + "] closed connection");
        }
        
        // new line isn't reached
        if(buffer.get(buffer.position()) != 0){
            return;
        }
        
        buffer.flip();
        String clientInput = decoder.decode(buffer).toString();
        if(clientInput.length() == 0){
            // System.out.println("we need to disconnect from " + clientSocket.getRemoteAddress());
            return;
        }
        System.out.println("Message from client[" + clientSocket.getRemoteAddress() + "]: " + clientInput);

        clientSocket.write(encoder.encode(CharBuffer.wrap(clientInput)));
        System.out.println("-----Response sent-----");
        buffer.clear();
    }

    /*
    private Thread createServerThread(SocketWithIOStreams clientSocket){
        return new Thread(() -> {
            String clientInput;

            clientSocket.out.println("Conection established. You can write to echo now");

            try {
                while ((clientInput = clientSocket.in.readLine()) != null) {
                    clientSocket.out.println(clientInput);
                }
                clientSocket.close();
            } catch (IOException ignored) {}
        });
    }
    */
}