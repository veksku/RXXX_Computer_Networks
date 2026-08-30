package TCP.osmosmerka;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static int PORT = 12345;
    private final static String PATH = "src/TCP/osmosmerka/osmosmerka.txt";

    public static void main(String[] args){
        Osmosmerka osmosmerka;

        try {
            osmosmerka = new Osmosmerka(PATH);
        } catch (FileNotFoundException e) {
            System.out.println("Nemoz nadjem fajl");
            return;
        }

        try(
                ServerSocket server = new ServerSocket(PORT);
                ) {
            while(true){
                Socket client = server.accept();
                new Thread(new Worker(client, osmosmerka)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
