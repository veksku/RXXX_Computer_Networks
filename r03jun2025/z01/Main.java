package r03jun2025.z01;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Main {

    private static final String PATH = "src/r03jun2025/z01/reachable_ips.txt";
    private static final List<String> addresses = new ArrayList<>();

    public static void main(String[] args) {
        for (int i=1; i<=100; i++){
            addresses.add("192.168.0." + i);
        }

        List<List<String>> chunks = new ArrayList<>();

        for (int i=0; i<5; i++){
            List<String> chunk = new ArrayList<>();
            for (int j=0; j<20; j++) {
                chunk.add(addresses.get(i * 20 + j));
            }
            chunks.add(chunk);
        }

        List<List<String>> results = new ArrayList<>();
        for (int i=0; i < chunks.size(); i++)
            results.add(new ArrayList<>());

        List<Thread> threads = new ArrayList<>();

        for (List<String> chunk : chunks){
            List<String> result = new ArrayList<>();
            Thread t = new Thread(() -> {
                for (String ip : chunk){
                    try {
                        InetAddress addr = InetAddress.getByName(ip);
                        if (addr.isReachable(2000)){
                            result.add(ip);
                        }
                    } catch (UnknownHostException e) {
                        System.err.println("Los host " + e);
                    } catch (IOException e) {
                        System.err.println("IO greska host " + e);
                    }
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Thread exception " + e);
                Thread.currentThread().interrupt();
            }
        }

        try (PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(PATH)),
                true)) {

            for (List<String> result : results){
                for (String ip : result){
                    out.println(ip);
                }
            }

        } catch (IOException e) {
            System.err.println("Greska " + e);
        }
    }
}
