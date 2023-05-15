package gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public class testMainScreen {
	private MainScreen mainScreen;
	private MockChatGPT mockChatGPT;
	private MockWhisper mockWhisper;

	@BeforeEach
	public void setup() throws Exception {
		mockChatGPT = new MockChatGPT();
		mockWhisper = new MockWhisper("What is the capital of China?");
		mainScreen = new MainScreen(mockWhisper, mockChatGPT);
	}

	@Test
	public void testAskQuestion() throws Exception {
		String questionContent = "What is the capital of China?";
		String expectedAnswer = "Beijing";
		mockChatGPT.setExpectedAnswer(questionContent, expectedAnswer);

		Question question = new Question(mockWhisper, UUID.randomUUID());
		question.updateContent();

		Answer answer = mainScreen.AskQuestion(question);

		assertEquals(expectedAnswer, answer.toString());
		assertEquals(question, mainScreen.getQuestionOnMain());
	}
	@Test
    public void testSetAndGetQuestionOnMain() throws Exception {
        Question question = new Question(mockWhisper, UUID.randomUUID());
        question.setString("What is the capital of France?");
        mainScreen.setQuestionOnMain(question);
        assertEquals(question, mainScreen.getQuestionOnMain());
    }

    @Test
    public void testClearAll() throws Exception {
        Question question = new Question(mockWhisper, UUID.randomUUID());
        question.updateContent();

        mainScreen.AskQuestion(question);
        mainScreen.clearAll();

        assertEquals(0, mainScreen.getComponentCount());
        assertNull(mainScreen.getQuestionOnMain());
    }
}