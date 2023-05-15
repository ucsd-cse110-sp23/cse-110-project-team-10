package gradle;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class testQuestionHistory {
    private MockWhisper mockWhisper;
    private MockChatGPT mockChatGPT;
    private MainScreen mainScreen;
    private QuestionHistory questionHistory;

    @BeforeEach
    public void setup() throws Exception {
        mockWhisper = new MockWhisper("What is the capital of France?");
        mockChatGPT = new MockChatGPT();
        mockChatGPT.setExpectedAnswer("What is the capital of France?", "Paris");
        mainScreen = new MainScreen(mockWhisper, mockChatGPT);
        questionHistory = new QuestionHistory(mainScreen, mockWhisper, mockChatGPT);
    }

    @Test
    public void testUpdateMap() {
        Question question = new Question(mockWhisper, UUID.randomUUID());
        question.setString(mockWhisper.transcribe(""));
        Answer answer = new Answer(mockChatGPT);
        answer.setString(mockChatGPT.getAnswer(question.toString()));

        questionHistory.updateMap(question, answer);
        assertTrue(questionHistory.answerPanels.containsKey(question));
        assertEquals(answer, questionHistory.answerPanels.get(question));
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        Question question = new Question(mockWhisper, UUID.randomUUID());
        question.setString(mockWhisper.transcribe(""));
        Answer answer = new Answer(mockChatGPT);
        answer.setString(mockChatGPT.getAnswer(question.toString()));
        
        OldQuestion oldQuestion = new OldQuestion(question, answer, mainScreen, questionHistory);
        questionHistory.add(oldQuestion);
        questionHistory.deleteQuestion(oldQuestion);
        assertEquals(0, questionHistory.getComponentCount());
    }

    @Test
    public void testClearAll() throws Exception {
        for (int i = 0; i < 5; i++) {
            Question question = new Question(mockWhisper, UUID.randomUUID());
            question.setString(mockWhisper.transcribe(""));
            Answer answer = new Answer(mockChatGPT);
            answer.setString(mockChatGPT.getAnswer(question.toString()));
            
            OldQuestion oldQuestion = new OldQuestion(question, answer, mainScreen, questionHistory);
            questionHistory.add(oldQuestion);
        }
        questionHistory.clearAll();
        assertEquals(0, questionHistory.getComponentCount());
    }
}
