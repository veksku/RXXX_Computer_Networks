package UDP.datetime;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static final int BUFF_SIZE = 1024;
    private static final String HOST = "localhost";
    public static final int PORT = 12222;

    public static void main(String[] args){
        try(
                DatagramSocket socket = new DatagramSocket();
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("What information do you need? (DATE/TIME/DATETIME)");
            String option = scanner.nextLine();
            if(!option.equals("DATE") && !option.equals("TIME") && !option.equals("DATETIME")) {
                System.err.println("Wrong option");
                return;
            }

            byte[] optionBytes = option.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packetToSend = new DatagramPacket(
                    optionBytes,
                    optionBytes.length,
                    InetAddress.getByName(HOST),
                    PORT
            );
            socket.send(packetToSend);

            DatagramPacket packetToReceive = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
            socket.receive(packetToReceive);

            String response = new String(
                    packetToReceive.getData(),
                    0,
                    packetToReceive.getLength(),
                    StandardCharsets.UTF_8
            );
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
