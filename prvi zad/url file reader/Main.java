package materials.ispit.urlfilereader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("> ");
        while (sc.hasNextLine()) {
            String path = sc.nextLine().trim();
            if (path.isEmpty()) {
                System.out.print("> ");
                continue;
            }

            readFile(path);
            System.out.print("> ");
        }

        sc.close();
    }

    private static void readFile(String absolutePath) {
        try {
            URL url = new URL("file://" + absolutePath);
            URLConnection connection = url.openConnection();

            System.out.println("Otvaram fajl pomocu \"file\" protokola:");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            )) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("    " + line);
                }
            }

        } catch (MalformedURLException e) {
            System.err.println("Neispravna putanja/URL: " + absolutePath);
        } catch (FileNotFoundException e) {
            System.err.println("Fajl ne postoji: " + absolutePath);
        } catch (IOException e) {
            System.err.println("Greska pri citanju fajla: " + e.getMessage());
        }
    }
}
