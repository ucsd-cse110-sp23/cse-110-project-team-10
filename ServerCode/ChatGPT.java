package _MS2Demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

public class ChatGPT {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";

    ChatGPT(){
    }
    
    public String getAnswer(String question, double temperature, int maxTokens) throws IOException, InterruptedException {
        String prompt = question;
     
        JSONObject requestBody = new JSONObject();
    
        requestBody.put("model",MODEL);
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens",maxTokens);
        requestBody.put("temperature",temperature);
    
        HttpClient client = HttpClient.newHttpClient();
        URI newUri = URI.create(API_ENDPOINT);
    
        HttpRequest request = HttpRequest
        .newBuilder()
        .uri(newUri)
        .header("Content-Type","application/json")
        .header("Authorization", String.format("Bearer %s",API_KEY))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
        .build();
        
        // Send the request and receive the response
        HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString()
        );
        // Process the response
        String responseBody = response.body();
    
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");
    
        return generatedText;
    }
    
}
