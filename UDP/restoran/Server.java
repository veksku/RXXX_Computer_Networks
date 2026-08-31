package UDP.restoran;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;

public class Server {

    protected static final int PORT = 12345;
    private static final int BUFF_SIZE = 1024;
    private static final HashMap<LocalTime, String> reservations = new HashMap<>();

    public static void main(String[] args){
        try(
                DatagramSocket server = new DatagramSocket(PORT)
        ) {
            byte[] inputBuff = new byte[BUFF_SIZE];
            byte[] outputBuff;
            DatagramPacket input, output;
            while(true) {
                input = new DatagramPacket(inputBuff, BUFF_SIZE);
                server.receive(input);
                String request = new String(
                        input.getData(),
                        input.getOffset(),
                        input.getLength(),
                        StandardCharsets.UTF_8
                );

                String[] parts = request.split(" ");
                LocalTime requestTime = LocalTime.parse(parts[1]);
                String name = parts[0];

                String response;
                if(!reservations.containsKey(requestTime)) {
                    response = "Uspesno ste rezervisali mesto za \"" + name + "\" u " + requestTime.toString();
                    reservations.put(requestTime, name);
                } else
                    response = "Vec postoji rezervacija od strane \"" + reservations.get(requestTime) + "\" u to vreme";

                outputBuff = response.getBytes(StandardCharsets.UTF_8);
                output = new DatagramPacket(
                        outputBuff,
                        outputBuff.length,
                        input.getAddress(),
                        input.getPort()
                );
                server.send(output);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
