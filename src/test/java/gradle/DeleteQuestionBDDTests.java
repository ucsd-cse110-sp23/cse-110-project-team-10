package gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteQuestionBDDTests {
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
    public void deleteQuestion() throws Exception {
        // Given a new Question
        Question newQuestion = new Question(mockWhisper, UUID.randomUUID());
        newQuestion.setString(mockWhisper.transcribe("")); // Using any parameters

        // And the Question is asked on the MainScreen
        Answer newAnswer = mainScreen.AskQuestion(newQuestion);

        // And the QuestionHistory is updated
        OldQuestion oldQuestion = mainScreen.updateHistory(newQuestion, newAnswer, questionHistory);
        // When a Question is deleted from the QuestionHistory
        //OldQuestion oldQuestion = new OldQuestion(newQuestion, newAnswer, mainScreen, questionHistory);
        questionHistory.deleteQuestion(oldQuestion);

        // Then the QuestionHistory does not contain the deleted Question
        assertEquals(0, questionHistory.getComponentCount());
    }
}
