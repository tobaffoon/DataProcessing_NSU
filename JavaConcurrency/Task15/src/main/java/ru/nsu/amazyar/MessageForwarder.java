package Task15.src.main.java.ru.nsu.amazyar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import sun.misc.Signal;
import sun.misc.SignalHandler;


public class MessageForwarder {
    private class Tunnel{
        public final SocketChannel pairedChannel;
        public final ByteBuffer buffer;

        public Tunnel(SocketChannel pairedChannel, ByteBuffer buffer){
            this.pairedChannel = pairedChannel;
            this.buffer = buffer;
        }
    }
    public static final int PORT = 10000;
    private final InetSocketAddress forwarderSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), PORT);

    private InetSocketAddress serverSocketAddress;
    private ServerSocketChannel forwarderSocket;
    private final Selector selector;

    public MessageForwarder() throws IOException{
        /*
        Signal.handle(new Signal ("INT"), new SignalHandler() { 
            public void handle(Signal sig) {
                try {
                    handleShutdown();
                } catch (IOException ignored) {}
            }
        });
        */

        selector = Selector.open();

        forwarderSocket = ServerSocketChannel.open();
        forwarderSocket.bind(forwarderSocketAddress);
        forwarderSocket.configureBlocking(false);
        forwarderSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("-----Forwarder started-----");
    }

    public void start() throws UnknownHostException, IOException{
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));

        // get server's address and port
        System.out.println("Provide servers' address: ");
        InetAddress servAddress = InetAddress.getByName(buffReader.readLine());
        System.out.println("Provide servers' port: ");
        int servPort = Integer.parseInt(buffReader.readLine());
        serverSocketAddress = new InetSocketAddress(servAddress, servPort);

        System.out.println("-----Retranslation to " + serverSocketAddress + " is ready to be started. Waiting for clients. Remember 256 is the limit for characters in a message.-----");

        // manage all sockets (including the one listening to new connections) with select
        while(true){
            selector.select();
            for(SelectionKey key : selector.selectedKeys()){
                if(key.isAcceptable()){
                    registerClient();
                }

                if(key.isReadable()){
                    forwardMessage((SocketChannel)key.channel(), (Tunnel)key.attachment());
                }
            }
        }
    }

    private void forwardMessage(SocketChannel fromSocket, Tunnel tunnel) throws IOException{
        int r = fromSocket.read(tunnel.buffer);
        // connection was closed
        if(r == -1){
            System.out.println(fromSocket);
            fromSocket.close();
            tunnel.pairedChannel.close();

            // if(fromSocket.getLocalAddress().equals(serverSocketAddress)){
            //     System.out.println("-----Server closed connection with Client[" + toSocket.getLocalAddress() + "]-----");
            // }
            // else{
            //     System.out.println("-----Client[" + toSocket.getLocalAddress() + "] closed connection with Server-----");
            // }
            return;
        }

        // new line isn't reached
        if(tunnel.buffer.get(tunnel.buffer.position()) != 0){
            return;
        }
        
        if(fromSocket.getRemoteAddress().equals(serverSocketAddress)){
            System.out.println("-----Server sends message-----");
        }
        else{
            System.out.println("-----Client sends message-----");
        }

        tunnel.buffer.flip();
        tunnel.pairedChannel.write(tunnel.buffer);
        tunnel.buffer.clear();
    }
    
    private void registerClient() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        
        // add client socket 
        SocketChannel client = forwarderSocket.accept();
        // no client to accept
        if(client == null){
            return;
        }
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
        System.out.println("-----Client[" + client.getRemoteAddress() + "] conneceted-----");

        // add server socket
        SocketChannel server = SocketChannel.open(serverSocketAddress);
        server.configureBlocking(false);
        SelectionKey serverKey = server.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        System.out.println("-----Client connected to the server-----");

        // attach sockets to each other to determine socket to forward message to
        Tunnel clientToServer = new Tunnel(server, buffer);
        Tunnel serverToClient = new Tunnel(client, buffer);
        clientKey.attach(clientToServer);
        serverKey.attach(serverToClient); 
    }

    private void handleShutdown() throws IOException{
        terminate();
    }    

    public void terminate() throws IOException{
        for(SelectionKey key : selector.keys()){
            SocketChannel channel = (SocketChannel)key.channel();
            channel.close();
        }
        selector.close();
        System.out.println("-----System exits-----");
    }
}