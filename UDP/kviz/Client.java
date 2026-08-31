package UDP.kviz;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    static final String HOST = "localhost";
    static final int PORT = Server.PORT;
    static final int BUFF_SIZE = Server.BUFF_SIZE;

    public static void main(String[] args){
        try(
                DatagramSocket socket = new DatagramSocket();
                Scanner in = new Scanner(System.in);
        ){
            byte[] inputBuff = new byte[BUFF_SIZE], outputBuff;
            DatagramPacket input, output;
            String answer;

            outputBuff = new byte[1];
            output = new DatagramPacket(
                    outputBuff, outputBuff.length,
                    InetAddress.getByName(HOST), PORT
            );
            socket.send(output);

            while(true) {
                input = new DatagramPacket(inputBuff, BUFF_SIZE);
                socket.receive(input);

                String response = new String(
                        input.getData(),
                        input.getOffset(),
                        input.getLength(),
                        StandardCharsets.UTF_8
                );

                System.out.println(response);

                StringBuilder sbTacno = new StringBuilder("Tacno!").append("\nTo bi bila sva pitanja!");
                StringBuilder sbNetacno = new StringBuilder("Netacno!").append("\nTo bi bila sva pitanja!");
                if(response.equals(sbTacno.toString()) || response.equals(sbNetacno.toString()))
                    break;

                answer = in.nextLine();
                outputBuff = answer.getBytes(StandardCharsets.UTF_8);
                output = new DatagramPacket(
                        outputBuff, outputBuff.length,
                        InetAddress.getByName(HOST), PORT
                );
                socket.send(output);
            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
