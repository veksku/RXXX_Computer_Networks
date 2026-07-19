package r01jan2026.z02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class UdpStatsClient {

    private static final String HOST = "localhost";
    private static final int PORT = UdpStatsServer.PORT;
    private static final int NUMS_COUNT = 5;
    private static final int MAX_VALUE = 100;
    private static final int BUFF_SIZE = 1024;

    public static void main(String[] args) {
        try (
                DatagramSocket socket = new DatagramSocket();
                Scanner console = new Scanner(System.in)
        ) {
            InetAddress serverAddress = InetAddress.getByName(HOST);
            byte[] buffer = new byte[BUFF_SIZE];

            while (true) {
                int[] numbers = generateRandomNumbers(NUMS_COUNT, MAX_VALUE);
                String message = numbersToMessage(numbers);
                System.out.println("Saljem brojeve: " + message);

                sendMessage(socket, serverAddress, PORT, message);

                String response = receiveMessage(socket, buffer);
                System.out.println("Odgovor servera: " + response);

                // Trazimo komandu dok korisnik ne unese TACNO "next" ili "stop"
                String command;
                while (true) {
                    System.out.print("Unesite 'next' za nove brojeve ili 'stop' za kraj: ");
                    command = console.nextLine().trim();

                    if (command.equalsIgnoreCase("next") || command.equalsIgnoreCase("stop")) {
                        break;
                    }
                    System.out.println("Nepoznata komanda. Dozvoljeno je samo 'next' ili 'stop'.");
                }

                if (command.equalsIgnoreCase("stop")) {
                    sendMessage(socket, serverAddress, PORT, "STOP");
                    String finalResponse = receiveMessage(socket, buffer);
                    System.out.println("Konacna globalna statistika: " + finalResponse);
                    break;
                }
                // ovde je command sigurno "next" - petlja se nastavlja
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] generateRandomNumbers(int count, int maxValue) {
        int[] numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = ThreadLocalRandom.current().nextInt(1, maxValue + 1);
        }
        return numbers;
    }

    private static String numbersToMessage(int[] numbers) {
        StringBuilder sb = new StringBuilder();
        for (int n : numbers) {
            sb.append(n).append(" ");
        }
        return sb.toString().trim();
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    private static String receiveMessage(DatagramSocket socket, byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }
}