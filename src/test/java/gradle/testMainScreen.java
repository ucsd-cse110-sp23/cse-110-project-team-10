package gradle;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockWhisper1 extends Whisper{
    int Audio;
    MockWhisper1(){
        this.Audio = 2;    
    }
    @Override
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

class MockChatGPT1 extends ChatGPT{
    String Question;
    MockChatGPT1(String question){
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

public class testMainScreen {
    @Test
    void testAskQuestion() throws Exception{
        MockChatGPT1 mockchatgpt1 = new MockChatGPT1("What is the capital of China?");
        MockWhisper1 mockwhisper1 = new MockWhisper1();
        MainScreen mainscreen = new MainScreen(mockwhisper1, mockchatgpt1);

        Question question = new Question(mockwhisper1);
        Answer answer = mainscreen.AskQuestion(question);
        assertEquals("Beijing",answer.answerContent.getText());
    }
}
