package gradle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;




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


public class testAnswer {
    @Test
    void testSetString(){
        MockChatGPT mockchatgpt = new MockChatGPT("What is the capital of China?");
        Answer answer = new Answer(mockchatgpt);

        answer.setString("What is the capital of China?");
        
        assertEquals("What is the capital of China?",answer.answerStr);
        assertEquals("What is the capital of China?",answer.answerContent.getText());
    }
    @Test
    void testToString(){
        MockChatGPT mockchatgpt = new MockChatGPT("What is the capital of China?");
        Answer answer = new Answer(mockchatgpt);

        answer.setString("What is the capital of China?");
        
        assertEquals("What is the capital of China?",answer.toString());
        
    }
    @Test
    void testUpdateContent(){
        MockChatGPT mockchatgpt = new MockChatGPT("What is the capital of China?");
        Answer answer = new Answer(mockchatgpt);

        answer.updateContent("a");
        assertEquals("Beijing",answer.answerContent.getText());
    }
}
