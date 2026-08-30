package TCP.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static final String HOST = "localhost";
    public static final int PORT = Server.PORT;

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        try(
                Socket socket = new Socket(HOST, PORT)
        ){

            Thread senderThread = new Thread(new ClientSender(socket.getOutputStream(), username));
            Thread listenerThread = new Thread(new ClientListener(socket.getInputStream(), username));

            senderThread.start();
            listenerThread.start();

            senderThread.join();
            listenerThread.join();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
