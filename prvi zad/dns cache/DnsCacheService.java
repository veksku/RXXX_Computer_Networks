package r01jan2026.z01;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Deljeni resurs izmedju svih niti - drzi kes (mapa domen -> IP)
 * i deli isti PrintWriter za upis u izlazni fajl.
 *
 * Kljucno: provera kesa, eventualni DNS lookup, upis u fajl i dodavanje
 * u kes moraju biti JEDNA atomska operacija (synchronized metoda), inace
 * dve niti mogu "istovremeno" videti da domen NIJE u kesu, obe uraditi
 * lookup i obe ga upisati - sto je race condition (provera-pa-upis mora
 * biti nedeljiva).
 */
public class DnsCacheService {

    private final Map<String, String> cache = new HashMap<>();
    private final PrintWriter out;

    public DnsCacheService(PrintWriter out) {
        this.out = out;
    }

    public synchronized void resolve(String domain) {
        if (cache.containsKey(domain)) {
            String ip = cache.get(domain);
            out.println(domain + " -> " + ip + " (preuzeto iz cache-a)");
            return;
        }

        try {
            InetAddress addr = InetAddress.getByName(domain);
            String ip = addr.getHostAddress();

            cache.put(domain, ip);
            out.println(domain + " -> " + ip + " (dohvaceno sa DNS servera)");
        } catch (UnknownHostException e) {
            out.println(domain + " -> NEPOZNAT HOST");
        }
    }
}
