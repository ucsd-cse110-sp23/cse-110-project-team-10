package gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;
import java.util.Map;

class MockChatGPT extends ChatGPT {
    private String question;
    private HashMap<String, String> expectedAnswers;
    private HashMap<String, RuntimeException> exceptionQuestions;

    public MockChatGPT() {
        this.question = "";
        this.expectedAnswers = new HashMap<>();
        this.exceptionQuestions = new HashMap<>();
    }

    public void setExpectedAnswer(String question, String answer) {
        expectedAnswers.put(question, answer);
    }

    public void setExceptionThrowingQuestion(String question, RuntimeException exception) {
        exceptionQuestions.put(question, exception);
    }

    @Override
    public String getAnswer(String question) {
        if (exceptionQuestions.containsKey(question)) {
            throw exceptionQuestions.get(question);
        }
        if (question.contains("Create email")) {
            return expectedAnswers.getOrDefault(question, "Dear Max,I hope you are doing well. Let's meet at 7 p.m. tonight.Best Regards,Barry");
        }
        return expectedAnswers.getOrDefault(question, "");
    }
}


public class testAnswer {
    private Answer answer;
    private MockChatGPT mockChatGPT;

    @BeforeEach
    public void setUp() {
        mockChatGPT = new MockChatGPT();
        answer = new Answer(mockChatGPT);
    }

    @Test
    public void testSetStringUpdatesAnswerFields() {
        String testStr = "Test answer";
        answer.setString(testStr);

        String actualStr = answer.toString();
        assertEquals(testStr, actualStr, "Answer string does not match the expected value");

        String actualContent = answer.answerContent.getText();
        assertEquals(testStr, actualContent, "Answer content does not match the expected value");
    }

    @Test
    public void testUpdateContentUpdatesAnswerFields() throws Exception {
        String input = "What is the capital of France?";
        String output = "Paris";

        mockChatGPT.setExpectedAnswer(input, output);
        answer.updateContent(input);

        String actualStr = answer.toString();
        assertEquals(output, actualStr, "Answer string does not match the expected value");

        String actualContent = answer.answerContent.getText();
        assertEquals(output, actualContent, "Answer content does not match the expected value");
    }

    @Test
    public void testUpdateContentWithNoAnswer() throws Exception {
        String input = "What is the capital of USA?";
        String output = "";

        mockChatGPT.setExpectedAnswer(input, output);
        answer.updateContent(input);

        String actualStr = answer.toString();
        assertEquals(output, actualStr, "Answer string does not match the expected value");

        String actualContent = answer.answerContent.getText();
        assertEquals(output, actualContent, "Answer content does not match the expected value");
    }

    @Test
    public void testSetStringWithEmptyString() {
        String emptyStr = "";
        answer.setString(emptyStr);

        String actualStr = answer.toString();
        assertEquals(emptyStr, actualStr, "Answer string does not match the expected empty string");

        String actualContent = answer.answerContent.getText();
        assertEquals(emptyStr, actualContent, "Answer content does not match the expected empty string");
    }

    @Test
    public void testSetStringWithNullValue() {
        answer.setString(null);

        String actualStr = answer.toString();
        assertEquals("", actualStr, "Answer string should be empty when set with a null value");

        String actualContent = answer.answerContent.getText();
        assertEquals("", actualContent, "Answer content should be empty when set with a null value");
    }

    @Test
    public void testUpdateContentWithException() throws Exception {
        String input = "Exception throwing question";
        RuntimeException exception = new RuntimeException("API request failed");
        
        mockChatGPT.setExceptionThrowingQuestion(input, exception);

        assertDoesNotThrow(() -> answer.updateContent(input), "updateContent should handle exceptions gracefully");

        String actualStr = answer.toString();
        assertEquals("", actualStr, "Answer string should be empty when an exception is thrown");

        String actualContent = answer.answerContent.getText();
        assertEquals("", actualContent, "Answer content should be empty when an exception is thrown");
    }
}


