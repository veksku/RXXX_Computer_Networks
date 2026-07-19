package materials.v07.datetime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DateTimeClient {
    public static void main(String[] args) {
        
        String host = "localhost";
        try (
                Socket socket = new Socket(host, DateTimeServer.PORT);

                PrintStream printStream = new PrintStream(
                        new BufferedOutputStream(
                                socket.getOutputStream()
                        ),
                        true // "obavezan" flag, da ne bi morali da zovemo flush() svaki put
                );

                Scanner sc = new Scanner(
                        new BufferedInputStream(
                                socket.getInputStream()
                        )
                )
        ) {
            System.out.println("What information do you need? (DATE/TIME/DATETIME)");
            Scanner stdIn = new Scanner(System.in);
            String option = stdIn.next();
            if(!option.equals("DATE") && !option.equals("TIME") && !option.equals("DATETIME")) {
                System.err.println("Wrong option!");
                return;
            }

            printStream.println(option);

            System.out.println(sc.next());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host!");
        } catch (IOException e) {
            System.err.println("Communication error!");
        }

    }
}
