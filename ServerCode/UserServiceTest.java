package _MS2Demo;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static MongoClient mongoClient;
    private static DatabaseService databaseService;
    private static UserService userService;

    @BeforeAll
    public static void setup() {
        // Connect to DB
        String connectionString = "mongodb+srv://project:passwordMongo@cluster1.zqt7j.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        mongoClient = MongoClients.create(settings);

        try {
            MongoDatabase database = mongoClient.getDatabase("users");
            database.runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            e.printStackTrace();
        }

        // Initialize DatabaseService and UserService
        databaseService = new DatabaseService(mongoClient, "users");
        userService = new UserService(databaseService);
    }

    @AfterAll
    public static void cleanup() {
        mongoClient.close();
    }

    @Test
    public void testCreateUser() {
        // Generate a random username
        String username = UUID.randomUUID().toString();
        String password = "password";

        // Invoke the createUser method
        Document newUser = userService.createUser(username, password);

        // Assert the result
        assertNotNull(newUser);
        assertEquals(username, newUser.getString("username"));
        assertEquals(password, newUser.getString("password"));
    }

    @Test
    public void testLoginUser() {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = "password";

        // Create a new user
        userService.createUser(username, password);

        // Invoke the loginUser method with correct credentials
        Document loggedInUser = userService.loginUser(username, password);

        // Assert the result
        assertNotNull(loggedInUser);
        assertEquals(username, loggedInUser.getString("username"));
        assertEquals(password, loggedInUser.getString("password"));

        // Invoke the loginUser method with incorrect password
        loggedInUser = userService.loginUser(username, "wrongpassword");

        // Assert the result
        assertNull(loggedInUser);
    }

    @Test
    public void testGetUserHistory() {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = "password";

        // Create a new user
        userService.createUser(username, password);

        // Invoke the getUserHistory method
        JSONArray history = userService.getUserHistory(username, password);

        // Assert the result
        assertNotNull(history);
        assertEquals(0, history.length());
    }

    @Test
    public void testUpdateUserHistory() {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = "password";

        // Create a new user
        userService.createUser(username, password);

        // Prepare the user history
        List<Document> history = new ArrayList<>();
        history.add(new Document("action", "email"));

        // Invoke the updateUserHistory method
        userService.updateUserHistory(username, password, history);

        // Retrieve the updated user document
        Document updatedUser = databaseService.findUserByUsername(databaseService.getCollection("users"), username);

        // Verify the updated user document
        assertNotNull(updatedUser);
        List<Document> updatedHistory = (List<Document>) updatedUser.get("history");
        assertEquals(history, updatedHistory);
    }

    @Test
    public void testClearUserHistory() {
        // Generate a random username and password
        String username = UUID.randomUUID().toString();
        String password = "password";

        // Create a new user
        userService.createUser(username, password);

        // Prepare the user history
        List<Document> history = new ArrayList<>();
        history.add(new Document("action", "email"));

        // Update the user history
        userService.updateUserHistory(username, password, history);

        // Invoke the clearUserHistory method
        userService.clearUserHistory(username, password);

        // Retrieve the updated user document
        Document updatedUser = databaseService.findUserByUsername(databaseService.getCollection("users"), username);

        // Verify the cleared user history
        assertNotNull(updatedUser);
        List<Document> updatedHistory = (List<Document>) updatedUser.get("history");
        assertTrue(updatedHistory.isEmpty());
    }

    @Test
    public void testGetUserByEmail() {
        // Generate a random email
        String email = UUID.randomUUID().toString() + "@example.com";

        // Create a new user with the email as the username
        userService.createUser(email, "password");

        // Invoke the getUserByEmail method
        Document user = userService.getUserByEmail(email);

        // Assert the result
        assertNotNull(user);
        assertEquals(email, user.getString("username"));
    }
}

