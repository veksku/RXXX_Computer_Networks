package r01jan2026.z01;

import java.io.*;
import java.util.*;

public class Main {

    private static final int THREADS_NUM = 5;
    private static final String IN_TXT = "src/r01jan2026/z01/domains.txt";
    private static final String OUT_TXT = "src/r01jan2026/z01/dns_cache_output.txt";

    public static void main(String[] args) {
//        System.out.println(new File(".").getAbsolutePath());
        List<String> domains = new ArrayList<>();

        try (Scanner in = new Scanner(new FileInputStream(IN_TXT))) {
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (!line.isEmpty())
                    domains.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Fajl domains.txt nije pronadjen!");
            return;
        }

        try (
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter(OUT_TXT)),
                        true
                )
        ) {
            DnsCacheService service = new DnsCacheService(out);

            List<List<String>> chunks = splitIntoChunks(domains, THREADS_NUM);

            List<Thread> threads = new ArrayList<>();
            for (List<String> chunk : chunks) {
                Thread t = new Thread(new DnsWorker(chunk, service));
                t.start();
                threads.add(t);
            }

            for (Thread t : threads) {
                t.join();
            }

            System.out.println("Obrada zavrsena. Rezultat je u fajlu: " + OUT_TXT);

        } catch (IOException e) {
            System.err.println("Greska pri Input/Output operacijama!");
        } catch (InterruptedException e) {
            System.err.println("Glavna nit je prekinuta dok je cekala radne niti!");
            Thread.currentThread().interrupt();
        }
    }

    // Deli listu domena u numChunks priblizno jednakih delova, round-robin nacinom
    private static List<List<String>> splitIntoChunks(List<String> domains, int numChunks) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < numChunks; i++) {
            chunks.add(new ArrayList<>());
        }
        for (int i = 0; i < domains.size(); i++) {
            chunks.get(i % numChunks).add(domains.get(i));
        }
        return chunks;
    }
}
