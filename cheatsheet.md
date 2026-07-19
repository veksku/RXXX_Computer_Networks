ZAD1 - URL/FILE PROCESSING
=
COMMON PATTERNS:
- Read URLs from a file
- Filter URLs by protocol (file://, http://, https://)
- Process files using multiple threads
- Count elements (tags, words, lines) in HTML/text files
- Use AtomicInteger for thread-safe counting

KEY IMPORTS:
```java
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
```

MAIN THREAD PATTERN:
```java
public class MainClass {
    public static void main(String[] args) {
        String filePath = "path/to/urls.txt";
        
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath)))) {
            
            List<Thread> threadList = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger(0);
            
            String line;
            while ((line = input.readLine()) != null) {
                try {
                    URL url = (new URI(line)).toURL();
                    
                    // Filter by protocol and file extension
                    if (url.getProtocol().equals("file") && 
                        url.toString().endsWith(".html")) {
                        
                        // Create and start worker thread
                        WorkerThread worker = new WorkerThread(url, searchParam, counter);
                        Thread task = new Thread(worker);
                        task.start();
                        threadList.add(task);
                    }
                } catch (URISyntaxException | IllegalArgumentException e) {
                    // Skip invalid URLs
                }
            }
            
            // Wait for all threads to complete
            for (Thread t : threadList) {
                t.join();
            }
            
            System.out.println("Result: " + counter.get());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

WORKER THREAD PATTERN:
```java
public class WorkerThread implements Runnable {
    private final URL url;
    private final String searchTarget;
    private final AtomicInteger counter;
    
    public WorkerThread(URL url, String searchTarget, AtomicInteger counter) {
        this.url = url;
        this.searchTarget = searchTarget;
        this.counter = counter;
    }
    
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(url.getFile())))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Count occurrences in line
                int index = 0;
                while ((index = line.indexOf(searchTarget, index)) != -1) {
                    counter.incrementAndGet();
                    index += searchTarget.length();
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + url.getFile());
        }
    }
}
```

URL/URI PROCESSING:
```java
// Convert string to URL safely
try {
    URL url = new URI(urlString).toURL();
    
    // Common URL methods:
    url.getProtocol()     // "http", "https", "file"
    url.getHost()         // domain name
    url.getPort()         // port number
    url.getFile()         // path to file
    url.toString()        // full URL string
    
} catch (URISyntaxException | MalformedURLException e) {
    // Handle invalid URLs
}
```
===============================================================================
                            THREADING CONCEPTS
===============================================================================

THREAD CREATION PATTERNS:
```java
// Method 1: Implement Runnable
public class Worker implements Runnable {
    @Override
    public void run() {
        // Thread work here
    }
}
Thread thread = new Thread(new Worker());
thread.start();

// Method 2: Extend Thread
public class Worker extends Thread {
    @Override
    public void run() {
        // Thread work here
    }
}
Worker worker = new Worker();
worker.start();

// Method 3: Lambda
Thread thread = new Thread(() -> {
    // Thread work here
});
thread.start();
```

THREAD SYNCHRONIZATION:
```java
// AtomicInteger for counters
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();
counter.addAndGet(value);
counter.get();

// Synchronized blocks
synchronized (sharedObject) {
    // Critical section
}

// Synchronized collections
List<Object> list = Collections.synchronizedList(new ArrayList<>());
```

WAITING FOR THREADS:
```java
List<Thread> threads = new ArrayList<>();

// Start all threads
for (int i = 0; i < count; i++) {
    Thread t = new Thread(new Worker());
    t.start();
    threads.add(t);
}

// Wait for all to complete
for (Thread t : threads) {
    t.join();
}
```

===============================================================================
                            NETWORKING ESSENTIALS
===============================================================================

TCP vs UDP:
- TCP: Reliable, connection-oriented, ordered delivery, flow control
- UDP: Unreliable, connectionless, fast, no guarantees

SOCKET TYPES:
```java
// TCP Server
ServerSocket serverSocket = new ServerSocket(port);
Socket clientSocket = serverSocket.accept();

// TCP Client
Socket socket = new Socket(hostname, port);

// UDP (both client and server)
DatagramSocket socket = new DatagramSocket();          // Client
DatagramSocket socket = new DatagramSocket(port);     // Server
```

INPUT/OUTPUT STREAMS:
```java
// TCP - Text I/O
BufferedReader in = new BufferedReader(
    new InputStreamReader(socket.getInputStream()));
PrintWriter out = new PrintWriter(
    new OutputStreamWriter(socket.getOutputStream()), true);

// TCP - Byte I/O
InputStream in = socket.getInputStream();
OutputStream out = socket.getOutputStream();

// UDP - Packet I/O
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet);
socket.send(packet);
```

COMMON EXCEPTIONS:
- IOException: General I/O problems
- SocketTimeoutException: Timeout in UDP
- UnknownHostException: Invalid hostname
- NumberFormatException: Invalid number parsing
- URISyntaxException: Invalid URI format

===============================================================================
                            EXAM TIPS & TRICKS
===============================================================================

1. THREAD SAFETY is crucial:
   - Use AtomicInteger for counters
   - Synchronize access to shared collections
   - Use Collections.synchronizedList() for lists

2. COMMAND PARSING pattern:
   ```java
   String[] parts = command.split(" ");
   String operation = parts[0];
   ```

3. TIMEOUT handling in UDP:
   ```java
   socket.setSoTimeout(timeoutMs);
   try {
       socket.receive(packet);
   } catch (SocketTimeoutException e) {
       // Retry logic
   }
   ```

4. URL PROCESSING:
   - Use URI constructor first, then toURL()
   - Check protocol and file extension
   - Handle URISyntaxException

5. ALWAYS join threads before printing final results:
   ```java
   for (Thread t : threads) {
       t.join();
   }
   System.out.println("Final result: " + counter.get());
   ```
