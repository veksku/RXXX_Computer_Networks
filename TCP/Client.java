package TCP_spojnice;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    static String HOST = "localhost";
    static int PORT = Server.PORT;

    static void main(String[] args) {
        try(
                Socket socket = new Socket(HOST, PORT);
                Scanner serverIn = new Scanner(
                        new BufferedInputStream(socket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(
                        new BufferedOutputStream(socket.getOutputStream()), true);
                Scanner stdIn = new Scanner(System.in);
                ) {
            while(true){
                String left = serverIn.nextLine();
                if(left.startsWith("POGODJENO:")){
                    System.out.println(left);
                    break;
                }

                String right = serverIn.nextLine();
                String leftPair = serverIn.nextLine();

                System.out.println(left.replace(";", "\n"));
                System.out.println(right.replace(";", "\n"));
                System.out.println("NEXT PAIR: " + leftPair);

                System.out.print("YOUR GUESS: ");
                String guess = stdIn.nextLine();
                serverOut.println(guess);

                String answer = serverIn.nextLine();
                System.out.println(answer);
                if(answer.equals("ERROR!"))
                    break;
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
