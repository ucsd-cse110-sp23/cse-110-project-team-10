package gradle;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class ClearAllBDDTests {
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
    public void clearAll() throws Exception {
        // Given the QuestionHistory is populated with five Questions
        for (int i = 0; i < 5; i++) {
            Question question = new Question(mockWhisper, UUID.randomUUID());
            question.setString(mockWhisper.transcribe(""));
            Answer answer = mainScreen.AskQuestion(question);
            mainScreen.updateHistory(question, answer, questionHistory);
        }

        // When clearAll is invoked on the QuestionHistory
        questionHistory.clearAll();

        // Then the QuestionHistory should not contain any Questions
        assertEquals(0, questionHistory.getComponentCount());
    }
}
