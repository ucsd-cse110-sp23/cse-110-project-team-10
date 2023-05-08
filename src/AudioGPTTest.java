import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


class MockWhisper extends Whisper{
    int Audio;
    MockWhisper(int MockAudio){
        this.Audio = MockAudio;    
    }
    public String transcribe(String a){
        if (Audio == 1){
            return "What is the capital of France?";
        }
        else if (Audio == 2){
            return "What is the capital of China?";
        }
        else {
            return "Error: Could not transcribe audio.";
        }
    }
}
class MockChatGPT extends ChatGPT{
    String Question;
    MockChatGPT(String question){
        this.Question = question;
    }
    public String getAnswer(String q){
        if (Question == "What is the capital of France?"){
            return "Paris";
        }
        else if (Question == "What is the capital of China?"){
            return "Beijing";
        }
        else {
            return "";
        }
    }
}
public class AudioGPTTest {

    
    @Test
    void testQupdateContent(){
        MockWhisper mockwhisper1 = new MockWhisper(1);
        MockWhisper mockwhisper2 = new MockWhisper(2);
        MockWhisper mockwhisper3 = new MockWhisper(3);
        Question q1 = new Question(mockwhisper1);
        Question q2 = new Question(mockwhisper2);
        Question q3 = new Question(mockwhisper3);
        String transcription1 = q1.updateContent();
        String transcription2 = q2.updateContent();
        String transcription3 = q3.updateContent();
        assertEquals("What is the capital of France?",transcription1);
        assertEquals("What is the capital of China?",transcription2);
        assertEquals("Error: Could not transcribe audio.",transcription3);
    }
    @Test
    void testAupdateContent(){
        MockChatGPT c1 = new MockChatGPT("What is the capital of France?");
        MockChatGPT c2 = new MockChatGPT("What is the capital of China?");
        MockChatGPT c3 = new MockChatGPT("Error: Could not transcribe audio.");
        Answer a1  = new Answer(c1);
        Answer a2  = new Answer(c2);
        Answer a3  = new Answer(c3);
        String ansStr1 = a1.updateContent("a");
        String ansStr2 = a2.updateContent("a");
        String ansStr3 = a3.updateContent("a");
        assertEquals("Paris",ansStr1);
        assertEquals("Beijing",ansStr2);
        assertEquals("",ansStr3);

    }

}
