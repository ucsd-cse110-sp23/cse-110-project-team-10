package server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    private static final String BASE_URL = "http://127.0.0.1:3001";
    private static HttpServer server;
    private static ExecutorService executor;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.start();

        // start server thread
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                App.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // wait for server
        Thread.sleep(2000);
    }

    // killl server
    @AfterAll
    public static void tearDown() {
        server.stop(0);
        executor.shutdown();
    }

    @Test
    public void testCreateAccountEndpoint() throws IOException, InterruptedException {
    
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
    
        // Additional attributes
        String emailAddress = "test@example.com";
        String emailPassword = "emailPass";
        String smtp = "smtp.example.com";
        String tls = "2059";
        String firstName = "Test";
        String lastName = "User";
    
        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();
    
        // Set post
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"emailAddress\": \"%s\", \"emailPassword\": \"%s\", \"smtp\": \"%s\", \"tls\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\"}",
        		username, password, emailAddress, emailPassword, smtp, tls, firstName, lastName);

    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createAccount"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        assertEquals(200, response.statusCode());
        assertEquals("User created successfully", response.body());
    }
    
    @Test
    public void testCreateAccountEndpointExists() throws IOException, InterruptedException {
    
        // Use existing user
        String username = "vince";
        String password = "pass";
    
        // Additional attributes
        String emailAddress = "vince@example.com";
        String emailPassword = "emailPass";
        String smtp = "smtp.example.com";
        String tls = "2059";
        String firstName = "Vince";
        String lastName = "User";
    
        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();
    
        // Set post
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"emailAddress\": \"%s\", \"emailPassword\": \"%s\", \"smtp\": \"%s\", \"tls\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\"}",
        		username, password, emailAddress, emailPassword, smtp, tls, firstName, lastName);

    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createAccount"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        assertEquals(401, response.statusCode());
        assertEquals("Username already exists", response.body());
    }

    @Test
    public void testLoginNonExistingUser() throws IOException, InterruptedException {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Set post
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Incorrect username or password", response.body());
    }

    @Test
    public void testLoginExistingUser() throws IOException, InterruptedException {
        // Use existing user
        String username = "vince";
        String password = "pass";

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Set post
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("User logged in successfully", response.body());
    }

    @Test
    public void testGetHistoryExistingUser() throws IOException, InterruptedException {
        // Use existing user
        String username = "vince";
        String password = "pass";

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Set post
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getHistory"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetHistoryNonExistingUser() throws IOException, InterruptedException {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        // Create an HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Set post
        String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getHistory"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }


    @Test
    public void askQuestion() throws IOException, InterruptedException {
        // Generate a random username and password
        String username = "vince";
        String password = "pass";
    
        String audioData = "";
        String filePath = "./src/test/resources/whatIsTheCapitalOfFrance.txt";
    
        try {
            System.out.println("Current directory: " + System.getProperty("user.dir"));

            byte[] audioBytes = Files.readAllBytes(Paths.get(filePath));
            audioData = new String(audioBytes);
    
            // Create an HTTP client
            HttpClient client = HttpClient.newHttpClient();
    
            // Set post
            String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\", \"audioData\": \"" + audioData + "\", \"emailAddress\": \"\", \"emailPassword\": \"\", \"smtp\": \"\", \"tls\": \"\", \"firstName\": \"\", \"lastName\": \"\", \"selected\": \"\"}";
    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/preformLogic"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            assertEquals(200, response.statusCode());
    
        } catch (IOException e) {
            System.out.println("Error grabbing file data" + e);

            //throw case fail
            assertEquals(200, 400);
        }
    }
    

}
