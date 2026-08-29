package ftw;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;

public class FileTreeWalkerRunnable implements Runnable {
    private Path pathBase;
    private BlockingQueue<Path> fileQueue;
    public static Path END_OF_WORK = Paths.get("");

    public FileTreeWalkerRunnable(Path pathBase, BlockingQueue<Path> fileQueue) {
        this.pathBase = pathBase;
        this.fileQueue = fileQueue;
    }

    @Override
    public void run() {
        try {
            obidji(this.pathBase);
            this.fileQueue.put(END_OF_WORK);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void obidji(Path dir) throws InterruptedException {
        try (DirectoryStream<Path> dirs = Files.newDirectoryStream(dir)) {
            for (Path path : dirs)
                if (Files.isDirectory(path))
                    obidji(path);
            else this.fileQueue.add(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
