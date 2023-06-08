package gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class CreateEmailBDDTests {
    private MockWhisper mockWhisper;
    private MockChatGPT mockChatGPT;
    private MainScreen mainScreen;
    private QuestionHistory questionHistory;

    @BeforeEach
    public void setup() throws Exception {
        // Given a MockWhisper set to transcribe "What is the capital of France?"
        // And a MockChatGPT set to answer "Paris" when asked "What is the capital of France?"
        mockWhisper = new MockWhisper("Create email to Max. Let's meet at 7 p.m.");
        mockChatGPT = new MockChatGPT();
        mockChatGPT.setExpectedAnswer("Create email to Max. Let's meet at 7 p.m.", "Dear Max,I hope you are doing well. Let's meet at 7 p.m. tonight.Best Regards,Barry");

        // And a MainScreen and QuestionHistory initialized with these mocks
        mainScreen = new MainScreen(mockWhisper, mockChatGPT);
        questionHistory = new QuestionHistory(mainScreen, mockWhisper, mockChatGPT);
    }

    @Test
    public void createNewEmail() throws Exception {
        Question createCommand = new Question(mockWhisper, UUID.randomUUID());
        createCommand.setString(mockWhisper.transcribe(""));

        Answer generatedEmail = mainScreen.AskQuestion(createCommand);

        assertEquals("Create email to Max. Let's meet at 7 p.m. add Best Regards and my name Fill_in_display_name at the end.", createCommand.toString());

        assertEquals("Dear Max,I hope you are doing well. Let's meet at 7 p.m. tonight.Best Regards,Barry" , generatedEmail.toString());

        assertEquals(createCommand, mainScreen.getQuestionOnMain());
    }

    @Test
    public void updateEmailtoHistory() throws Exception {
        Question createCommand = new Question(mockWhisper, UUID.randomUUID());
        createCommand.setString(mockWhisper.transcribe(""));

        Answer generatedEmail = mainScreen.AskQuestion(createCommand);

        mainScreen.updateHistory(createCommand, generatedEmail, questionHistory);

        assertTrue(questionHistory.answerPanels.containsKey(createCommand));
        assertEquals(generatedEmail, questionHistory.answerPanels.get(createCommand));
    }
}