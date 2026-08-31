package UDP.kviz;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private Question activeQuestion;
    private List<Question> questionsToBeAnswered;

    public Session(List<Question> questions) {
        this.questionsToBeAnswered = new ArrayList<>(questions);
    }

    public Question getActiveQuestion() {
        return activeQuestion;
    }

    public void setActiveQuestion(Question activeQuestion) {
        this.activeQuestion = activeQuestion;
    }

    public boolean hasNextQuestion() {
        return !questionsToBeAnswered.isEmpty();
    }

    public Question nextQuestion() {
        return questionsToBeAnswered.removeFirst();
    }
}
