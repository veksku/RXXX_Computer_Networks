package r01jan2026.z01;

import java.util.List;

public class DnsWorker implements Runnable {

    private final List<String> domains;
    private final DnsCacheService service;

    public DnsWorker(List<String> domains, DnsCacheService service) {
        this.domains = domains;
        this.service = service;
    }

    @Override
    public void run() {
        for (String domain : domains) {
            service.resolve(domain);
        }
    }
}
