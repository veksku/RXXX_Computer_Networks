package UDP.restoran;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = Server.PORT;
    private static final int BUFF_SIZE = 1024;


    public static void main(String[] args){
        try(
                DatagramSocket socket = new DatagramSocket();
                Scanner in = new Scanner(System.in)
        ) {
            String request = in.nextLine();
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
            DatagramPacket output = new DatagramPacket(
                    requestBytes,
                    requestBytes.length,
                    InetAddress.getByName(HOST),
                    PORT
            );
            socket.send(output);

            byte[] responseBytes = new byte[BUFF_SIZE];
            DatagramPacket input = new DatagramPacket(responseBytes, BUFF_SIZE);
            socket.receive(input);
            String response = new String(
                    input.getData(),
                    input.getOffset(),
                    input.getLength(),
                    StandardCharsets.UTF_8
            );
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
