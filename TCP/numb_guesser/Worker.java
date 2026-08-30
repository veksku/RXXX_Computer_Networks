package TCP.numb_guesser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Worker implements Runnable {
    private Socket client;
    private int numberToGuess;

    public Worker(Socket client) {
        this.client = client;
        this.numberToGuess = ThreadLocalRandom.current().nextInt(1, 101);
    }

    private void serve() {
        try(
                Scanner scanner = new Scanner(new BufferedInputStream(client.getInputStream()));
                PrintStream printer = new PrintStream(new BufferedOutputStream(client.getOutputStream()), true);
                ){
            printer.println("Pogodi koji broj od 1 do 100 sam zamislio");
            while(scanner.hasNextInt()){
                int guess = scanner.nextInt();
                if (guess == numberToGuess) {
                    printer.println("Pogodjen broj!");
                    break;
                }
                else if (guess > numberToGuess) printer.println("Zamisljeni broj je manji od toga");
                else printer.println("Zamisljeni broj je veci od toga");
            }
            System.out.println("Gotovo, zavrsavam...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        serve();
        if(client != null && !client.isClosed()){
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
