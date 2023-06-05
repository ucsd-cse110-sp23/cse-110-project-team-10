package gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class AskNewQuestionBDDTests {
    private MockWhisper mockWhisper;
    private MockChatGPT mockChatGPT;
    private MainScreen mainScreen;
    private QuestionHistory questionHistory;

    @BeforeEach
    public void setup() throws Exception {
        // Given a MockWhisper set to transcribe "What is the capital of France?"
        // And a MockChatGPT set to answer "Paris" when asked "What is the capital of France?"
        mockWhisper = new MockWhisper("What is the capital of France?");
        mockChatGPT = new MockChatGPT();
        mockChatGPT.setExpectedAnswer("What is the capital of France?", "Paris");

        // And a MainScreen and QuestionHistory initialized with these mocks
        mainScreen = new MainScreen(mockWhisper, mockChatGPT);
        questionHistory = new QuestionHistory(mainScreen, mockWhisper, mockChatGPT);
    }

    @Test
    public void askNewQuestion() throws Exception {
        // Given a new Question
        Question newQuestion = new Question(mockWhisper, UUID.randomUUID());
        newQuestion.setString(mockWhisper.transcribe("")); // Using any parameters

        // When a new Question is asked on the MainScreen
        Answer newAnswer = mainScreen.AskQuestion(newQuestion);

        // Then the Question is transcribed correctly
        assertEquals("What is the capital of France?", newQuestion.toString());

        // And the Answer is generated correctly
        assertEquals("Paris", newAnswer.toString());

        // And the Question on MainScreen is the new Question
        assertEquals(newQuestion, mainScreen.getQuestionOnMain());
    }

    @Test
    public void updateQuestionHistory() throws Exception {
        // Given a new Question
        Question newQuestion = new Question(mockWhisper, UUID.randomUUID());
        newQuestion.setString(mockWhisper.transcribe("")); // Using any parameters

        // And the Question is asked on the MainScreen
        Answer newAnswer = mainScreen.AskQuestion(newQuestion);

        // When the QuestionHistory is updated
        mainScreen.updateHistory(newQuestion, newAnswer, questionHistory);

        // Then the QuestionHistory contains the new Question and Answer
        assertTrue(questionHistory.answerPanels.containsKey(newQuestion));
        assertEquals(newAnswer, questionHistory.answerPanels.get(newQuestion));
    }

    @Test
    public void clearMainScreen() throws Exception {
        // Given a new Question asked on the MainScreen
        Question newQuestion = new Question(mockWhisper, UUID.randomUUID());
        newQuestion.setString(mockWhisper.transcribe("")); // Using no parameters
        mainScreen.AskQuestion(newQuestion);

        // When the MainScreen is cleared
        mainScreen.clearAll();

        // Then the Question on MainScreen is null
        assertNull(mainScreen.getQuestionOnMain());
    }
}
