package _MS2Demo;

import org.bson.Document;
import org.bson.types.ObjectId;
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

public class DatabaseServiceTest {
    private static MongoClient mongoClient;
    private static DatabaseService databaseService;

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

        // Initialize DatabaseService
        databaseService = new DatabaseService(mongoClient, "users");
    }

    @AfterAll
    public static void cleanup() {
        mongoClient.close();
    }

    @Test
    public void testGetCollection() {
        // Invoke the getCollection method
        MongoCollection<Document> collection = databaseService.getCollection("users");

        // Assert the result
        assertNotNull(collection);
        assertEquals("users", collection.getNamespace().getCollectionName());
    }

    @Test
    public void testFindUserByUsername() {
        // Prepare the username
        String username = "testuser1";

        // Invoke the findUserByUsername method
        Document foundUser = databaseService.findUserByUsername(databaseService.getCollection("users"), username);

        // Assert the result
        assertNull(foundUser); // Assuming the user doesn't exist in the test database
    }

    @Test
    public void testInsertUser() {
        // Generate a random username
        String username = UUID.randomUUID().toString();

        // Prepare the user document
        Document userDocument = new Document("_id", new ObjectId())
                .append("username", username)
                .append("password", "password");

        // Invoke the insertUser method
        databaseService.insertUser(databaseService.getCollection("users"), userDocument);

        // No assertion or verification needed as it's a void method
    }
    @Test
    public void testUpdateUserHistory() {
        // Prepare the username and user history
        String username = "testuser";
        List<Document> history = new ArrayList<>();
        history.add(new Document("action", "email"));

        // Invoke the updateUserHistory method
        databaseService.updateUserHistory(databaseService.getCollection("users"), username, history);

        // Retrieve the updated user document
        Document updatedUser = databaseService.findUserByUsername(databaseService.getCollection("users"), username);

        // Verify the updated user document
        assertNotNull(updatedUser);
        List<Document> updatedHistory = (List<Document>) updatedUser.get("history");
        assertEquals(history, updatedHistory);
    }

}
