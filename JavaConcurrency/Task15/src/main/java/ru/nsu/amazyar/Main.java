package Task15.src.main.java.ru.nsu.amazyar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws NumberFormatException, IOException {
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter number to choose a mode for application:\n0 - client\n1 - forwarder\n2 - server");
        switch(Integer.parseInt(buffReader.readLine())){
            case 0:
                // Client client = new Client(MessageForwarder.forwarderSocketAddress);
                Client client = new Client();
                client.start();
                break;
            case 1:
                MessageForwarder forwarder = new MessageForwarder();
                forwarder.start();
                break;
            case 2:
                Server server = new Server();
                server.start();
                break;
            default:
                System.out.println("Wrong damn number");
        }
    }
}
