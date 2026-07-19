package r01jan2026.z02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UdpStatsServer {

    public static final int PORT = 8888;
    private static final int BUFF_SIZE = 1024;

    private static final GlobalStats globalStats = new GlobalStats();

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP stats server pokrenut na portu " + PORT);

            byte[] buffer = new byte[BUFF_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Sve se sad radi ODMAH, u glavnoj niti - nema Arrays.copyOf zastite
                // jer nema druge niti koja bi mogla paralelno da dodirne buffer.
                String message = new String(
                        packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8
                ).trim();

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                String response;

                if (message.equalsIgnoreCase("STOP")) {
                    response = "GLOBAL " + globalStats.snapshot();
                    System.out.println("Klijent " + clientAddress + ":" + clientPort + " je zavrsio komunikaciju.");
                } else {
                    try {
                        int[] numbers = parseNumbers(message);

                        int min = Arrays.stream(numbers).min().orElseThrow();
                        int max = Arrays.stream(numbers).max().orElseThrow();
                        double avg = Arrays.stream(numbers).average().orElseThrow();

                        globalStats.update(numbers);

                        response = String.format("LOCAL min=%d max=%d avg=%.2f", min, max, avg);
                    } catch (NumberFormatException e) {
                        response = "ERROR neispravan format poruke";
                    }
                }

                byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket respPacket = new DatagramPacket(respBytes, respBytes.length, clientAddress, clientPort);
                socket.send(respPacket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] parseNumbers(String message) {
        String[] parts = message.trim().split("\\s+");
        int[] numbers = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Integer.parseInt(parts[i]);
        }
        return numbers;
    }
}