package src;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatGPT {

    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";
    private static final String MODEL = "text-davinci-003";

    public static void main(String[] args) {

        // Set request parameters
        String prompt = args[0];
        int maxTokens = Integer.parseInt(args[1]);

        // Create a request body which you will pass into request object
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", 1.0);

        // Create the HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create the request object
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", String.format("Bearer %s", API_KEY))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // Send the request and receive the response
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            System.out.println(responseBody);

            JSONObject responseJson = new JSONObject(responseBody);

            JSONArray choices = responseJson.getJSONArray("choices");
            String generatedText = choices.getJSONObject(0).getString("text");
            System.out.println(generatedText);

        } catch (IOException e) {
            System.out.println("error " + e);
        } catch (InterruptedException e) {
            System.out.println("interuppted " + e);
        }
    }
}