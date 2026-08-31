package UDP.kviz;

public class Question {

    private final String question;
    private final String answer;

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion(){
        return question;
    }

    public boolean checkAnswer(String attempt){
        return this.answer.equalsIgnoreCase(attempt);
    }
}
