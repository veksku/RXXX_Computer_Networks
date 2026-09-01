package TCP_spojnice;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static final int PORT = 12345;
    static Map<String, String> pairs;

    public static void main(String[] args) {
        initPairs();

        try(ServerSocket server = new ServerSocket(PORT)){
            while(true){
                Socket client = server.accept();
                new Thread(new Worker(client, pairs)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initPairs() {
        pairs = new HashMap<>();

        pairs.put("TCP", "Socket");
        pairs.put("UDP", "DatagramPacket");
        pairs.put("HTTP", "web");
        pairs.put("DNS", "domen");
        pairs.put("IP", "adresa");
        pairs.put("port", "proces");
        pairs.put("thread", "nit");
    }
}
