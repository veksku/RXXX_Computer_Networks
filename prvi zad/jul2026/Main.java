package jul2026;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int THREAD_NUM = 5;
    private static final int QUEUE_SIZE = 10;
    private static final String C_FILE_NAME = "src/jul2026/c_fajlovi.txt";

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> fileQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        AtomicInteger totalLines = new AtomicInteger(0);

        FileRunnable ftw = new FileRunnable(C_FILE_NAME, fileQueue);
        new Thread(ftw).start();

        List<Thread> threadList = new ArrayList<>();
        for(int i = 0; i < THREAD_NUM; i++){
            ReadLinesRunnable sf = new ReadLinesRunnable(totalLines, fileQueue);
            Thread t = new Thread(sf);
            t.start();
            threadList.add(t);
        }

        for(Thread thread : threadList){
            thread.join();
        }

        System.out.println("result: " + totalLines.get());
    }
}
