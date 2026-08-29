package ftw;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class SearchFileRunnable implements Runnable {
    private BlockingQueue<Path> fileQueue;
    private String keyword;

    public SearchFileRunnable(String keyword, BlockingQueue<Path> fileQueue) {
        this.keyword = keyword;
        this.fileQueue = fileQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Path path = this.fileQueue.take();
                if (path.equals(FileTreeWalkerRunnable.END_OF_WORK)){
                    this.fileQueue.put(path);
                    break;
                }
                this.pretrazi(path);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void pretrazi(Path file) {
        try (Scanner sc = new Scanner(file)){
            int ln = 0;
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                ln++;
                if(line.contains(this.keyword))
                    System.out.println(file.getFileName() + ":" + ln);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
