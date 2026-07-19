# TCP/UDP Java Cheatsheet — najčešći obrasci

---

## 1. TCP SERVER — osnovni skelet

```java
public class MyServer {
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();   // blokira dok neko ne dodje
                System.out.println("Client accepted: " + client);
                new Thread(new MyWorker(client)).start(); // odmah predaj dalje, vrati se na accept()
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Komentar:** Glavna nit servera NIKAD ne sme sama da opslužuje klijenta — samo prihvata i deleguje, da bi mogla odmah da se vrati na `accept()` za sledećeg. `while(true)` se praktično nikad ne završava (server se gasi spolja).

---

## 2. TCP WORKER — po jedan klijent, u svojoj niti

```java
public class MyWorker implements Runnable {
    private Socket client;

    public MyWorker(Socket client) {
        this.client = client;
    }

    private void serve() {
        try (
                Scanner in = new Scanner(
                        new BufferedInputStream(client.getInputStream())
                );
                PrintStream out = new PrintStream(
                        new BufferedOutputStream(client.getOutputStream()),
                        true // autoFlush = OBAVEZNO za interaktivnu komunikaciju
                )
        ) {
            out.println("Welcome!");
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.equalsIgnoreCase("bye")) break;
                out.println("Echo: " + line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        serve();
        if (client != null && !client.isClosed()) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
```

**Komentar:**
- `client.getInputStream()`/`getOutputStream()` — dva kraja iste "cevi"; ono što ti pišeš u output, druga strana čita iz svog inputa.
- `autoFlush = true` u `PrintStream`/`PrintWriter` je **kritično** kod interaktivne (ping-pong) komunikacije — bez toga poruke ostaju u baferu i ne stižu na vreme.
- `close()` na samom `Socket`-u ide **posebno**, van try-with-resources za stream-ove, jer zatvaranje `Scanner`/`PrintStream` ne zatvara automatski i sam socket.
- Petlja se prirodno završi i kad klijent naglo prekine konekciju — `hasNextLine()` postane `false` kad se stream zatvori.

---

## 3. TCP KLIJENT — osnovni skelet

```java
public class MyClient {
    private static final String HOST = "localhost";
    private static final int PORT = MyServer.PORT;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                Scanner serverIn = new Scanner(
                        new BufferedInputStream(socket.getInputStream())
                );
                PrintStream serverOut = new PrintStream(
                        new BufferedOutputStream(socket.getOutputStream()),
                        true
                );
                Scanner localIn = new Scanner(System.in)
        ) {
            while (serverIn.hasNextLine()) {
                String message = serverIn.nextLine();
                System.out.println("server: " + message);
                if (message.equals("bye")) break;

                System.out.print("you: ");
                String reply = localIn.nextLine();
                serverOut.println(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Komentar:** `new Socket(HOST, PORT)` **aktivno pokreće** konekciju (TCP handshake) — za razliku od `ServerSocket` koji čeka. Dva `Scanner`-a (`serverIn` sa mreže, `localIn` sa tastature) su čest izvor konfuzije — obavezno ih razlikuj po imenu.

---

## 4. TCP KLIJENT sa DVE niti (kad treba nezavisno čitati i pisati)

Kad komunikacija **nije** striktno naizmenična (npr. chat gde poruke stižu bilo kad, ne kao odgovor na tvoju poruku):

```java
// U main():
Thread sender = new Thread(new ChatSender(socket.getOutputStream()));
Thread listener = new Thread(new ChatListener(socket.getInputStream()));
sender.start();
listener.start();
sender.join();
listener.join();
```

```java
// Sender - salje ono sto ukucas
class ChatSender implements Runnable {
    private PrintStream out;
    ChatSender(OutputStream os) {
        this.out = new PrintStream(new BufferedOutputStream(os), true);
    }
    public void run() {
        try (Scanner in = new Scanner(System.in)) {
            String msg;
            do {
                msg = in.nextLine();
                out.println(msg);
            } while (!msg.equalsIgnoreCase("bye"));
        }
        out.close();
    }
}

// Listener - ispisuje sve sto stigne
class ChatListener implements Runnable {
    private Scanner in;
    ChatListener(InputStream is) {
        this.in = new Scanner(new BufferedInputStream(is));
    }
    public void run() {
        while (in.hasNextLine()) {
            System.out.println(in.nextLine());
        }
        in.close();
    }
}
```

**Komentar:** Isti `Socket` se "cepa" na dva nezavisna toka — čitanje i pisanje na različitim nitima nad istim socketom je bezbedno (full-duplex, nema deljenog stanja između njih).

---

## 5. TCP — deljeno stanje između klijenata na SERVERU (broadcast/registry obrazac)

```java
public class ChatServer {
    public static final Set<Worker> clients = Collections.synchronizedSet(new HashSet<>());

    public static void broadcast(Worker sender, String message) {
        synchronized (clients) {              // OBAVEZNO oko iteracije,
            for (Worker w : clients) {        // synchronizedSet ne stiti iteraciju samu!
                if (!w.equals(sender)) w.sendMessage(message);
            }
        }
    }
}
```

**Komentar:** `Collections.synchronizedX(...)` štiti **pojedinačne** operacije (`add`/`remove`), ali **NE** i iteraciju kroz kolekciju — za to je i dalje potreban eksplicitan `synchronized` blok oko `for` petlje, inače rizikuješ `ConcurrentModificationException` ili race condition ako neko doda/ukloni element dok iteriraš.

---

## 6. UDP SERVER — osnovni skelet

```java
public class MyUdpServer {
    private static final int PORT = 5050;
    private static final int BUFF_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buff = new byte[BUFF_SIZE]; // napravi JEDNOM, van petlje

            while (true) {
                DatagramPacket request = new DatagramPacket(buff, buff.length); // nov OBJEKAT svaki put
                socket.receive(request); // blokira dok ne stigne paket

                String message = new String(
                        request.getData(), 0, request.getLength(), // OBAVEZNO getLength(), NE ceo buff!
                        StandardCharsets.UTF_8
                );

                String response = process(message); // tvoja logika

                byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                DatagramPacket reply = new DatagramPacket(
                        respBytes, respBytes.length,
                        request.getAddress(), request.getPort() // adresa se ČITA iz zahteva!
                );
                socket.send(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Komentar — najvažnije zamke:**
- `new DatagramSocket(PORT)` (sa argumentom) na serveru — mora imati fiksan, unapred poznat port.
- `buff` niz se pravi **jednom** i ponovo koristi; `DatagramPacket` **omotač** oko njega se pravi **iznova svaki prolaz** (nosi promenljivo stanje — dužinu, adresu pošiljaoca).
- **UVEK** koristi `request.getLength()` (i `getOffset()`) pri čitanju sadržaja, NIKAD ceo `getData()` niz — ostatak bafera je "smeće" iz prethodnog punjenja.
- Server **mora** čitati `request.getAddress()`/`getPort()` da bi znao kome da odgovori — nema konekcije koja bi to pamtila umesto njega.
- `default`/`else` grana za neočekivan/neispravan format poruke je OBAVEZNA — jedan loš paket ne sme srušiti `while(true)` petlju i time ceo server.

---

## 7. UDP KLIJENT — osnovni skelet

```java
public class MyUdpClient {
    private static final String HOST = "localhost";
    private static final int PORT = MyUdpServer.PORT;
    private static final int BUFF_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) { // BEZ argumenta - proizvoljan lokalni port
            InetAddress serverAddr = InetAddress.getByName(HOST);

            String message = "hello";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket toSend = new DatagramPacket(data, data.length, serverAddr, PORT);
            socket.send(toSend);

            byte[] buff = new byte[BUFF_SIZE];
            DatagramPacket toReceive = new DatagramPacket(buff, buff.length);
            socket.receive(toReceive);

            String response = new String(
                    toReceive.getData(), toReceive.getOffset(), toReceive.getLength(),
                    StandardCharsets.UTF_8
            );
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Komentar:** `new DatagramSocket()` bez argumenta — klijentu ne treba fiksan port, server mu odgovara direktno preko adrese iz primljenog paketa.

---

## 8. UDP — "prazan" ping paket (kad je bitan samo dolazak paketa)

```java
DatagramPacket ping = new DatagramPacket(
        new byte[1], 1, InetAddress.getByName(HOST), PORT
);
socket.send(ping);
```

**Komentar:** Koristi se kad sadržaj zahteva nije bitan — samo činjenica da je paket stigao je "signal" (npr. "daj mi nasumičan citat"). Bajt nosi default vrednost `0`, nikad se eksplicitno ne postavlja.

---

## 9. UDP — simulacija "sesije" preko stateless protokola (SERVER strana)

Kad server mora da PAMTI nešto o klijentu između poruka (kviz, korpa, bilo koje višekoraČno stanje):

```java
// Identitet klijenta kao kljuc mape - MORA imati equals()/hashCode()!
public class ClientId {
    private InetAddress address;
    private Integer port;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientId c = (ClientId) o;
        return Objects.equals(address, c.address) && Objects.equals(port, c.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
```

```java
// U serveru:
Map<ClientId, Session> sessions = new HashMap<>();
...
ClientId id = new ClientId(request.getAddress(), request.getPort());
if (sessions.containsKey(id)) {
    Session s = sessions.get(id);
    // nastavi tamo gde je stao
} else {
    Session s = new Session(...);
    sessions.put(id, s);
    // pocni iznova
}
```

**Komentar:** UDP nema ugrađen koncept konekcije — ako ti treba "pamćenje po klijentu", MORAŠ ga sam izgraditi preko mape `identitet → stanje`. Identitet može biti (adresa, port) par (bezbednije, teže lažirati) ili nešto što klijent sam pošalje kao ime/token (jednostavnije, ali svako ko zna to ime može da se predstavi kao taj klijent — razmisli o ovom trade-offu pri izboru).

---

## 10. Zajednički obrasci — VAŽE ZA OBA (TCP i UDP)

### a) Sinhronizacija oko deljenog stanja

```java
// Ako je stanje deljeno izmedju vise niti (npr. thread-per-request server):
public synchronized void update(...) { ... }   // cela metoda, ili:
synchronized (lockObject) { ... }               // eksplicitan blok
```
Pravilo: čim dve niti mogu **istovremeno** čitati/pisati isti podatak, i bar jedna piše — treba sinhronizacija. Radi se najčešće oko: globalnih brojača/statistika, mapa sesija/konekcija, deljenih kolekcija.

### b) Kopiranje bafera pre prosleđivanja drugoj niti (UDP + thread-per-request)

```java
socket.receive(request);
byte[] data = Arrays.copyOf(request.getData(), request.getLength()); // KOPIJA!
new Thread(() -> handle(data, request.getAddress(), request.getPort())).start();
```
Ako server šalje obradu paketa u NOVU nit, mora kopirati podatke PRE toga — isti `buff` niz se prepisuje sledećim `receive()` pozivom u glavnoj petlji dok worker možda još čita stare podatke.

### c) `ThreadLocalRandom` vs `Random`

```java
ThreadLocalRandom.current().nextInt(1, 101);  // kad ima VISE niti (thread-per-request)
new Random().nextInt(n);                       // kad je server JEDNONITAN
```

---

## Brza referenca — koje klase idu uz šta

| | TCP | UDP |
|---|---|---|
| Server socket | `ServerSocket(port)` → `.accept()` | `DatagramSocket(port)` → `.receive(packet)` |
| Klijent socket | `new Socket(host, port)` | `new DatagramSocket()` (bez porta) |
| Slanje | `socket.getOutputStream()` + `Writer`/`PrintStream` | `new DatagramPacket(bytes, len, addr, port)` + `socket.send(packet)` |
| Primanje | `socket.getInputStream()` + `Reader`/`Scanner` | `new DatagramPacket(buff, buff.length)` + `socket.receive(packet)` |
| Identitet sagovornika | Ugrađen u sam `Socket` objekat | Mora se čitati iz svakog paketa (`getAddress()`/`getPort()`) |
| Tipičan server pattern | `accept()` u petlji → `new Thread(worker).start()` | `receive()` u petlji → obradi (jednonitno ili thread-per-request) |
