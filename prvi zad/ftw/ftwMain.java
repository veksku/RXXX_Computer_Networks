package ftw;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ftwMain {
    private static final int THREAD_NUM = 5;
    private static final int QUEUE_SIZE = 10;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter base dir: ");
        String baseDir = sc.nextLine();
        System.out.print("Get keyword: ");
        String keyword = sc.nextLine();
        sc.close();

        BlockingQueue<Path> fileQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        FileTreeWalkerRunnable ftw = new FileTreeWalkerRunnable(Paths.get(baseDir), fileQueue);
        new Thread(ftw).start();


        for(int i = 0; i < THREAD_NUM; i++){
            SearchFileRunnable sf = new SearchFileRunnable(keyword, fileQueue);
            new Thread(sf).start();
        }
    }
}
