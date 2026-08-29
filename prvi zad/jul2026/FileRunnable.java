package jul2026;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class FileRunnable implements Runnable{

    public static String EOW = "";
    private String cFileName;
    private BlockingQueue<String> fileQueue;

    public FileRunnable(String cFileName, BlockingQueue<String> fileQueue) {
        this.cFileName = cFileName;
        this.fileQueue = fileQueue;
    }

    @Override
    public void run() {
        try {
            readCFiles(this.cFileName);
            this.fileQueue.put(EOW);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void readCFiles(String cFileName) {
        try (BufferedReader in = new BufferedReader(
                             new InputStreamReader(
                                     new BufferedInputStream(
                                             new FileInputStream(cFileName)
                                     ),
                                     StandardCharsets.US_ASCII
                             )
        )) {
            String line;
            while((line = in.readLine()) != null) {
                this.fileQueue.put(line);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
