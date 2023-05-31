package gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;


public class testProcessCommands {

    // Mock classes

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
            return expectedAnswers.getOrDefault(question, "");
        }
    }

    class MockWhisper extends Whisper {
        private String transcription;

        MockWhisper(String transcription) {
            this.transcription = transcription;
        }

        @Override
        public String transcribe(String audio) {
            return transcription;
        }
    }

    class MockMainScreen extends MainScreen {
        boolean removeAllCalled = false;
        boolean revalidateCalled = false;
        boolean repaintCalled = false;

        MockMainScreen(Whisper whisper, ChatGPT chatGPT) throws Exception {
            super(whisper, chatGPT);
        }

        @Override
        public void removeAll() {
            removeAllCalled = true;
        }

        @Override
        public void revalidate() {
            revalidateCalled = true;
        }

        @Override
        public void repaint() {
            repaintCalled = true;
        }
    }

    class MockQuestionHistory extends QuestionHistory {
        boolean clearAllCalled = false;

        MockQuestionHistory(MainScreen mainScreen, Whisper whisper, ChatGPT chatGPT) {
            super(mainScreen, whisper, chatGPT);
        }

        @Override
        public void clearAll() {
            clearAllCalled = true;
        }
    }

    // Test method

    @Test
    public void testProcessVoiceCommand() {
        try {
            MockChatGPT mockChatGPT = new MockChatGPT();
            MockWhisper mockWhisper = new MockWhisper("Delete prompt");
            MockMainScreen mockMainScreen = new MockMainScreen(mockWhisper, mockChatGPT);
            MockQuestionHistory mockQuestionHistory = new MockQuestionHistory(mockMainScreen, mockWhisper, mockChatGPT);

            AppFrame appFrame = new AppFrame();

            // Use reflection to set mainScreen and questionHistory
            Field mainScreenField = AppFrame.class.getDeclaredField("mainscreen");
            mainScreenField.setAccessible(true);
            mainScreenField.set(appFrame, mockMainScreen);

            Field questionHistoryField = AppFrame.class.getDeclaredField("questionhistory");
            questionHistoryField.setAccessible(true);
            questionHistoryField.set(appFrame, mockQuestionHistory);

            // Call the method to test
            appFrame.processVoiceCommand("Delete prompt");

            // Verify the correct methods were called
            assertTrue(mockMainScreen.removeAllCalled);
            assertTrue(mockMainScreen.revalidateCalled);
            assertTrue(mockMainScreen.repaintCalled);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

