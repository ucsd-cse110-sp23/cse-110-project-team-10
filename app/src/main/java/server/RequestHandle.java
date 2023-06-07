package server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.IOException;
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
import java.util.Base64;

public class RequestHandle implements HttpHandler {

    private String endpoint;
    MongoClient mongoClient;

    // enpoint capture for logging
    public RequestHandle(String endpoint, MongoClient mongoClient) {
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
            System.out.println("error hadle return");
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

                // get database and collection
                MongoDatabase database = mongoClient.getDatabase("users");
                MongoCollection<Document> collection = database.getCollection("users");

                // check if user already exists
                Document existingUser = collection.find(Filters.eq("username", username)).first();
                if (existingUser != null) {
                    handleReturn(httpExchange, 400, "Username already exists");

                    scanner.close();
                    return;
                }

                // create new user
                Document newUser = new Document("_id", new ObjectId());
                newUser.append("username", username)
                        .append("password", password)
                        .append("history", new ArrayList<>());

                // add new user to the database
                collection.insertOne(newUser);

                handleReturn(httpExchange, 200, "User created successfully");

            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            } catch (MongoException e) {
                handleReturn(httpExchange, 500, "Server error");
            }
        }

        if (endpoint.equals("/login")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");

                // get database and collection
                MongoDatabase database = mongoClient.getDatabase("users");
                MongoCollection<Document> collection = database.getCollection("users");

                // try to find the user
                Document user = collection.find(Filters.eq("username", username)).first();
                if (user == null) {
                    handleReturn(httpExchange, 404, "User not found");
                    scanner.close();
                    return;
                }

                // check if passwords match
                String storedPassword = user.getString("password");
                if (!storedPassword.equals(password)) {
                    handleReturn(httpExchange, 401, "Incorrect password");
                    scanner.close();
                    return;
                }

                handleReturn(httpExchange, 200, "User logged in successfully");

            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            } catch (MongoException e) {
                handleReturn(httpExchange, 500, "Server error");
            }
        }

        if (endpoint.equals("/getHistory")) {
            try {
                JSONObject postJson = new JSONObject(postData);
                String username = postJson.getString("username");
                String password = postJson.getString("password");

                // get database and collection
                MongoDatabase database = mongoClient.getDatabase("users");
                MongoCollection<Document> collection = database.getCollection("users");

                // try to find the user
                Document user = collection.find(Filters.eq("username", username)).first();
                if (user == null) {
                    handleReturn(httpExchange, 404, "User not found");
                    scanner.close();
                    return;
                }

                // check if passwords match
                String storedPassword = user.getString("password");
                if (!storedPassword.equals(password)) {
                    handleReturn(httpExchange, 401, "Incorrect password");
                    scanner.close();
                    return;
                }

                // Get user's history
                List<Object> history = (List<Object>) user.get("history");
                JSONArray historyJson = new JSONArray(history);

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

                // get database and collection
                MongoDatabase database = mongoClient.getDatabase("users");
                MongoCollection<Document> collection = database.getCollection("users");

                // try to find the user
                Document user = collection.find(Filters.eq("username", username)).first();
                if (user == null) {
                    handleReturn(httpExchange, 404, "User not found");
                    scanner.close();
                    return;
                }

                // check if passwords match
                String storedPassword = user.getString("password");
                if (!storedPassword.equals(password)) {
                    handleReturn(httpExchange, 401, "Incorrect password");
                    scanner.close();
                    return;
                }

                // decode base64 string to bytes
                byte[] audioBytes = Base64.getDecoder().decode(audioData);

                // specify file path
                String filePath = "file.wav";

                // write bytes to file
                Files.write(Paths.get(filePath), audioBytes, StandardOpenOption.CREATE);

                Whisper whisper = new Whisper();
                String response = whisper.transcribe("file.wav");

                ChatGPT chatGPT = new ChatGPT();
                String userOption = chatGPT.getAnswer(
                        "From the following text does it seem like the user wants to send email, delete this occurence, delete all of something, or ask a general question:"
                                + response
                                + " respond with either 'send email' 'delete this' 'delete all' or 'question'",
                        0, 16);

                userOption = userOption.trim();

                System.out.println("THE USER CHOSE " + userOption);

                if (userOption.equals("email")) {

                    String message = "";
                    message = chatGPT.getAnswer("what is the message they want to send in the email, say only that message exactly and nothing else: " + response, 0.4, 16);

                    String sender = "";
                    sender = chatGPT.getAnswer("What is the email in "+response, 0.4, 16);

                    System.out.println("emailing "+message+" to "+sender);
                    handleReturn(httpExchange, 200, "OK: email");
                }

                if (userOption.equals("delete all")) {
                   
                    Document localUser = collection.find(Filters.eq("username", username)).first();

                    
                    if (localUser != null) {
                        collection.updateOne(Filters.eq("username", username),
                                            Updates.set("history", new ArrayList<>()));

                        handleReturn(httpExchange, 200, "OK: delete all");
                    } 
                    else {
                        handleReturn(httpExchange, 500, "User does not exist");
                    }
                }

                if (userOption.equals("delete this")) {
                    handleReturn(httpExchange, 200, "OK: delete this");
                }

            } catch (JSONException e) {
                handleReturn(httpExchange, 400, "Invalid JSON in request");
            } catch (MongoException e) {
                handleReturn(httpExchange, 500, "Server error");
            }
        }

    }

}
