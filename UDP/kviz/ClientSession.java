package UDP.kviz;

import java.net.InetAddress;
import java.util.Objects;

public class ClientSession {

    private InetAddress address;
    private int port;

    public ClientSession(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getClientSessionAddress() {
        return address;
    }

    public int getClientSessionPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientSession client = (ClientSession) o;
        return Objects.equals(address, client.address) && Objects.equals(port, client.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
