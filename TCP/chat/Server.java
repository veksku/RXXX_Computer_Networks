package TCP.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    public static final int PORT = 12345;
    public static final Set<ServerWorker> clients = Collections.synchronizedSet(new HashSet<>());

    public static void broadcast(ServerWorker senderWorker, String message){
        synchronized (clients){
            for(ServerWorker worker : clients){
                if(worker.equals(senderWorker))
                    continue;
                worker.sendMessage(message);
            }
        }
    }

    public static List<String> getUsernames(){
        List<String> usernames = new LinkedList<>();
        synchronized (clients) {
            for(ServerWorker worker : clients) {
                usernames.add(worker.getUsername());
            }
        }
        return usernames;
    }

    public static void remove(ServerWorker worker){
        clients.remove(worker);
    }

    public static void main(String[] args){
        try (ServerSocket server = new ServerSocket(PORT)) {
            while(true){
                Socket client = server.accept();
                ServerWorker sw = new ServerWorker(client);
                clients.add(sw);
                sw.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
