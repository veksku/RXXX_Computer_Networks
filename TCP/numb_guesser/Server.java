package TCP.numb_guesser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int PORT = 12321;

    public static void main(String[] args) {
        try(
                ServerSocket server = new ServerSocket(PORT);
                ) {
            while (true){
                Socket client = server.accept();
                System.out.println("Client accepted");
                new Thread(new Worker(client)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
