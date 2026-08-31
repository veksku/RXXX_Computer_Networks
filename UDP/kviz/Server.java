package UDP.kviz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Server {

    static final String PATH = "src/UDP/kviz/questions.txt";
    static final int PORT = 12345;
    static final int BUFF_SIZE = 1024;
    static final List<Question> questions = new ArrayList<>();
    static final HashMap<ClientSession, Session> sessions = new HashMap<>();

    public static void main(String[] args){
        try{
            loadQuestions();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }
        try(DatagramSocket server = new DatagramSocket(PORT)){
            byte[] inputBuff = new byte[BUFF_SIZE], outputBuff;
            DatagramPacket input, output;
            while(true){
                input = new DatagramPacket(inputBuff, BUFF_SIZE);
                server.receive(input);

                ClientSession cs = new ClientSession(input.getAddress(), input.getPort());
                System.out.println("Contains key: " + sessions.containsKey(cs));
                if(sessions.containsKey(cs)){
                    Session s = sessions.get(cs);
                    String answer = new String(
                            input.getData(),
                            input.getOffset(),
                            input.getLength(),
                            StandardCharsets.UTF_8
                    );
                    boolean isCorrect = s.getActiveQuestion().checkAnswer(answer);
                    StringBuilder responseBuilder = new StringBuilder(isCorrect ? "Tacno!" : "Netacno!");
                    if(s.hasNextQuestion()){
                        Question next = s.nextQuestion();
                        responseBuilder.append("\n").append(next.getQuestion());
                        s.setActiveQuestion(next);
                    }
                    else {
                        responseBuilder.append("\nTo bi bila sva pitanja!");
                        sessions.remove(cs);
                    }

                    outputBuff = responseBuilder.toString().getBytes(StandardCharsets.UTF_8);

                } else {
                    Session s = new Session(questions);
                    sessions.put(cs, s);
                    Question first = s.nextQuestion();
                    s.setActiveQuestion(first);

                    outputBuff = first.getQuestion().getBytes(StandardCharsets.UTF_8);
                }
                output = new DatagramPacket(
                        outputBuff, outputBuff.length,
                        cs.getClientSessionAddress(), cs.getClientSessionPort());
                server.send(output);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadQuestions() throws FileNotFoundException{
        try(Scanner fin = new Scanner(new FileInputStream(PATH))){
            String[] parts;
            while(fin.hasNextLine()){
                parts = fin.nextLine().split("\\|");
                questions.add(new Question(parts[0], parts[1]));
            }
        }
    }
}
