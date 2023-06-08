package _MS2Demo;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockChatGPT extends ChatGPT {
    private String question;
    private double temperature;
    private int maxTokens;
    private String expectedAnswer;
    private RuntimeException exception;

    public MockChatGPT() {
        this.question = "";
        this.temperature = 0.0;
        this.maxTokens = 0;
        this.expectedAnswer = "";
        this.exception = null;
    }

    public void setExpectedAnswer(String question, double temperature, int maxTokens, String answer) {
        this.question = question;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.expectedAnswer = answer;
        this.exception = null;
    }

    public void setExceptionThrowingQuestion(String question, double temperature, int maxTokens, RuntimeException exception) {
        this.question = question;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.expectedAnswer = "";
        this.exception = exception;
    }

    @Override
    public String getAnswer(String question, double temperature, int maxTokens) throws IOException, InterruptedException {
        if (exception != null) {
            throw exception;
        }
        return expectedAnswer;
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
public class AudioServiceTest {

    @Test
    public void testTranscribeAudio() throws IOException {
        // Create a mock Whisper instance
        MockWhisper whisper = new MockWhisper("Transcribed text");

        // Create an instance of AudioService using the mock Whisper
        AudioService audioService = new AudioService(whisper, null);

        // Prepare the audio bytes
        byte[] audioBytes = {1, 2, 3, 4, 5};

        // Perform the transcribeAudio operation
        String result = audioService.transcribeAudio(audioBytes);

        // Verify the result
        assertEquals("Transcribed text", result);
    }
    @Test
    public void testDetermineUserOption() throws IOException, InterruptedException {
        // Create a mock ChatGPT instance
        MockChatGPT chatGPT = new MockChatGPT();
        chatGPT.setExpectedAnswer("From the following text does it seem like the user wants to send email, delete this occurrence, delete all of something, or ask a general question: Some text respond with either 'send email' 'delete this' 'delete all' or 'question'", 0, 16, "send email");

        // Create an instance of AudioService using the mock ChatGPT
        AudioService audioService = new AudioService(null, chatGPT);

        // Prepare the response text
        String response = "Some text";

        // Perform the determineUserOption operation
        String result = audioService.determineUserOption(response);

        // Verify the result
        assertEquals("send email", result);
    }
    
}