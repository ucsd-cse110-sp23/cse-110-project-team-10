package server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.types.ObjectId;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class RequestHandler implements HttpHandler{
    private final UserService userService;
    private final EmailService emailService;
    private final AudioService audioService;
    private String endpoint;
    MongoClient mongoClient;

    public RequestHandler(UserService userService, EmailService emailService,AudioService audioService, String endpoint, MongoClient mongoClient) {
        this.userService = userService;
        this.emailService = emailService;
        this.audioService = audioService;
        this.endpoint = endpoint;
        this.mongoClient = mongoClient;
    }
    
    public void handleReturn(HttpExchange httpExchange, int code, String msg) {

        try {

            // send out response
            httpExchange.sendResponseHeaders(code, msg.length());
            OutputStream outStream = httpExchange.getResponseBody();

            // pipe stream
            outStream.write(msg.getBytes());
            outStream.close();

        }

        // logging if server chokes on sending
        catch (Exception e) {
            System.out.println("error hadling return");
        }
    }

    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();

        if (method.equals("POST")) {

            try {
                handlePost(httpExchange, mongoClient);
            }

            catch (InterruptedException e) {
                System.out.println("error handling post");
            }
        }

        else {
            handleReturn(httpExchange, 403, "Method Not Accepted");
        }
    }
    
    private void handlePost(HttpExchange httpExchange, MongoClient mongoClient)
            throws IOException, InterruptedException {

        InputStream inStream = httpExchange.getRequestBody();

        // parse body
        Scanner scanner = new Scanner(inStream);
        String postData = scanner.nextLine();


        if (endpoint.equals("/createAccount")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");

                // create new user
                Document newUser = userService.createUser(username, password);
                if (newUser == null) {
                    handleReturn(httpExchange, 401, "Username already exists");
                } else {
                    handleReturn(httpExchange, 200, "User created successfully");
                }
            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            }
        }

        if (endpoint.equals("/login")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");

                // login user
                Document user = userService.loginUser(username, password);
                if (user == null) {
                    handleReturn(httpExchange, 404, "Incorrect username or password");
                } else {
                    handleReturn(httpExchange, 200, "User logged in successfully");
                }
            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            }
        }
        
        if (endpoint.equals("/getHistory")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");

                // Get user's history
                JSONArray historyJson = userService.getUserHistory(username, password);
                if (historyJson == null) {
                    handleReturn(httpExchange, 404, "User not found or incorrect password");
                    return;
                }

                // Return history as JSON
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("history", historyJson);
                handleReturn(httpExchange, 200, jsonObject.toString());

            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            } catch (MongoException e) {
                handleReturn(httpExchange, 500, "Server error");
            }
        }
        if (endpoint.equals("/preformLogic")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");
                String audioData = postJson.getString("audioData");
                String emailAddress = postJson.getString("emailAddress");
                String emailPassword = postJson.getString("emailPassword");
                String smtp = postJson.getString("smtp");
                String tls = postJson.getString("tls");
                String firstName = postJson.getString("firstName");
                String lastName = postJson.getString("lastName");
                String selected = postJson.getString("selected");

                // Check if the user exists and the password matches
                Document user = userService.loginUser(username, password);
                if (user == null) {
                    handleReturn(httpExchange, 404, "User not found or incorrect password");
                    scanner.close();
                    return;
                }

                // decode base64 string to bytes
                byte[] audioBytes = Base64.getDecoder().decode(audioData);

                // Transcribe audio to text
                String response = audioService.transcribeAudio(audioBytes);

                // Determine user's option
                String userOption = audioService.determineUserOption(response);

                // Prepare for the history update
                JSONArray history = userService.getUserHistory(username, password);
                List<Document> historyList = new ArrayList<>();
                for (int i = 0; i < history.length(); i++) {
                    historyList.add((Document) history.get(i));
                }

                if (userOption.equals("send email")) {
                    String message = audioService.getEmailMessage(response);
                    String recipient = audioService.getEmailRecipient(response);
                    String subject = audioService.getEmailSubject(response);

                    try {
                        // Create a new instance of the EmailService
                        EmailService dynamicEmailService = new EmailService();
                        dynamicEmailService.initialize(emailAddress, emailPassword, smtp, tls);
                        dynamicEmailService.sendEmail(recipient, subject, message);

                        Document newHistoryItem = new Document();
                        newHistoryItem.put("action", "email");
                        newHistoryItem.put("transcript", response);
                        newHistoryItem.put("status", "sent");
                        historyList.add(newHistoryItem);
                        userService.updateUserHistory(username, password, historyList);

                        handleReturn(httpExchange, 200, "EMAIL: email sent");
                    }
                    catch(Exception e){
                        handleReturn(httpExchange, 500, "email send fail");
                    }
                }
                else if (userOption.equals("delete all")) {
                    userService.clearUserHistory(username, password);
                    handleReturn(httpExchange, 200, "DELETE ALL: deleted all items");
                }
                else if (userOption.equals("delete this")) {
                    try {
                        int itemIdToDelete = Integer.parseInt(selected);
                        if (itemIdToDelete < 0 || itemIdToDelete >= historyList.size()) {
                            handleReturn(httpExchange, 400, "Invalid item ID");
                            return;
                        }

                        historyList.remove(itemIdToDelete);
                        userService.updateUserHistory(username, password, historyList);

                        handleReturn(httpExchange, 200, "DELETE THIS: deleted one item");
                    }
                    catch (JSONException e) {
                        handleReturn(httpExchange, 400, "Invalid JSON getting history to delete");
                    }
                }
                else if(userOption.equals("question")){
                    String userAnswer = audioService.getAnswer(response);

                    Document newHistoryItem = new Document();
                    newHistoryItem.put("action", "question");
                    newHistoryItem.put("transcript", response);
                    newHistoryItem.put("answer", userAnswer);
                    historyList.add(newHistoryItem);
                    userService.updateUserHistory(username, password, historyList);

                    handleReturn(httpExchange, 200, "QUESTION: " + userAnswer);
                } 
                else {
                    handleReturn(httpExchange, 400, "Invalid user option");
                }
            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON");
            }
        }
    } 
}