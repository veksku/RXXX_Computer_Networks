package r02feb2025.z01;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Main {

    private static class DomainResult {
        String domain;
        long responseTime;

        DomainResult(String domain, long responseTime){
            this.domain = domain;
            this.responseTime = responseTime;
        }
    }

    public static void main(String[] args) {

        System.out.println(new File(".").getAbsolutePath());

        List<DomainResult> reachable = new ArrayList<>();
        List<String> unreachable = new ArrayList<>();

        try (
                Scanner in = new Scanner(
                        new FileInputStream("r02feb2025/z01/domains.txt")
                );
                PrintStream out = new PrintStream(
                        new BufferedOutputStream(
                                new FileOutputStream("r02feb2025/z01/reachable_stats.txt")
                        ),
                        true
                )
        ) {
            while(in.hasNextLine()){
                String domain = in.nextLine();

                try {
                    InetAddress addr = InetAddress.getByName(domain);

                    long start = System.currentTimeMillis();
                    boolean isReachable = addr.isReachable(3000);
                    long end = System.currentTimeMillis();

                    if (isReachable) {
                        reachable.add(new DomainResult(domain, end - start));
                    } else {
                        unreachable.add(domain);
                    }
                } catch (UnknownHostException e) {
                    System.err.println("Nepoznat domen: " + domain);
                    unreachable.add(domain);
                } catch (IOException e) {
                    System.err.println("Dostupnost greska: " + domain);
                    unreachable.add(domain);
                }
            }

            reachable.sort(Comparator.comparingLong(r -> r.responseTime));

            out.println("Ukupan broj dostupnih domena: " + reachable.size());
            out.println("Ukupan broj nedostupnih domena: " + unreachable.size());
            out.println();
            out.println("Dostupni domeni sa vremenom odziva:");

            for (DomainResult r : reachable){
                out.println(r.domain + " - " + r.responseTime + " ms");
            }

            out.println();
            out.println("Top 3 najbrza domena:");
            int floor = Math.min(3, reachable.size());
            for (int i = 0; i < floor; i++) {
                DomainResult r = reachable.get(i);
                out.print(i+1 + ". " + r.domain + " - " + r.responseTime + " ms");
                out.println();
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
