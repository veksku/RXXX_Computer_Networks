package r04jul2025.z01;

import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

public class Main {

    private static final String FILTER_FILE = "src/r04jul2025/z01/filter.txt";
    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("http", "https");

    public static void main(String[] args) {
        Set<String> blockedIps = loadBlockedIps();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty())
                continue;

            System.out.println(checkUrl(line, blockedIps));
        }
        sc.close();
    }

    /**
     * Ucitava hostname-ove iz filter.txt i razresava svaki u IP adresu.
     * Rezultat je skup ZABRANJENIH IP adresa (ne hostname-ova!).
     */
    private static Set<String> loadBlockedIps() {
        Set<String> blockedIps = new HashSet<>();

        try (Scanner fileScanner = new Scanner(new FileInputStream(FILTER_FILE))) {
            while (fileScanner.hasNextLine()) {
                String host = fileScanner.nextLine().trim();
                if (host.isEmpty())
                    continue;

                try {
                    InetAddress addr = InetAddress.getByName(host);
                    blockedIps.add(addr.getHostAddress());
                } catch (UnknownHostException e) {
                    System.err.println("Upozorenje: ne mogu da razresim " + host + " iz filter.txt");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Fajl filter.txt nije pronadjen. Nijedna adresa nece biti blokirana.");
        }

        return blockedIps;
    }

    /**
     * Proverava jednu unetu URL adresu: prvo protokol, pa IP adresu na koju pokazuje.
     */
    private static String checkUrl(String urlString, Set<String> blockedIps) {
        String protocol;
        String host;

        try {
            URI uri = new URI(urlString);
            protocol = uri.getScheme();
            host = uri.getHost();
        } catch (URISyntaxException e) {
            return "Protokol nije podrzan";
        }

        if (protocol == null || !SUPPORTED_PROTOCOLS.contains(protocol.toLowerCase())) {
            return "Protokol nije podrzan";
        }

        if (host == null) {
            return "Protokol nije podrzan";
        }

        try {
            InetAddress addr = InetAddress.getByName(host);
            String ip = addr.getHostAddress();

            return blockedIps.contains(ip) ? "Nije dozvoljeno" : "Dozvoljeno";
        } catch (UnknownHostException e) {
            return "Nije dozvoljeno"; // host se ne moze razresiti - tretiramo kao nedostupan/nesiguran
        }
    }
}