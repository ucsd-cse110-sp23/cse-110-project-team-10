package gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class testOldQuestion {
    private MockWhisper mockWhisper;
    private MockChatGPT mockChatGPT;
    private MainScreen mainScreen;
    private QuestionHistory questionHistory;
    private OldQuestion oldQuestion;
    private Question question;
    private Answer answer;

    @BeforeEach
    public void setup() throws Exception {
        mockWhisper = new MockWhisper("What is the capital of France?");
        mockChatGPT = new MockChatGPT();
        mockChatGPT.setExpectedAnswer("What is the capital of France?", "Paris");
        mainScreen = new MainScreen(mockWhisper, mockChatGPT);
        questionHistory = new QuestionHistory(mainScreen, mockWhisper, mockChatGPT);
        question = new Question(mockWhisper, UUID.randomUUID());
        question.setString(mockWhisper.transcribe(""));
        answer = new Answer(mockChatGPT);
        answer.setString(mockChatGPT.getAnswer(question.toString()));
        oldQuestion = new OldQuestion(question, answer, mainScreen, questionHistory);
    }

    @Test
    public void testQToString() {
        assertEquals(question.toString(), oldQuestion.qToString());
    }

    @Test
    public void testAToString() {
        assertEquals(answer.toString(), oldQuestion.aToString());
    }

    @Test
    public void testCheckIfOnMain() {
        mainScreen.setQuestionOnMain(question);
        assertTrue(oldQuestion.checkIfOnMain());
    }
}