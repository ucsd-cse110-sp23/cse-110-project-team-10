package _MS2Demo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class AudioService {
    public String transcribeAudio(byte[] audioBytes) throws IOException {
        // specify file path
        String filePath = "file.wav";

        // write bytes to file
        Files.write(Paths.get(filePath), audioBytes, StandardOpenOption.CREATE);

        Whisper whisper = new Whisper();
        return whisper.transcribe("file.wav");
    }

    public String determineUserOption(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        String userOption = chatGPT.getAnswer(
                "From the following text does it seem like the user wants to send email, delete this occurence, delete all of something, or ask a general question:"
                        + response
                        + " respond with either 'send email' 'delete this' 'delete all' or 'question'",
                0, 16);

        return userOption.toLowerCase().trim();
    }
    
    public String getEmailRecipient(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        return chatGPT.getAnswer("Who is the recipient in "+response, 0.4, 16).trim();
    }


    public String getEmailMessage(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        return chatGPT.getAnswer("what is the message they want to send in the email, say only that message exactly and nothing else: " + response, 0.4, 16).trim();
    }

    public String getEmailSender(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        return chatGPT.getAnswer("What is the email in "+response, 0.4, 16).trim();
    }

    public String getEmailSubject(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        return chatGPT.getAnswer("What is the subject in "+response, 0.4, 16).trim();
    }

    public String getAnswer(String response) throws IOException, InterruptedException {
        ChatGPT chatGPT = new ChatGPT();
        return chatGPT.getAnswer(response, 0, 16).trim();
    }
}
