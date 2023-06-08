package _MS2Demo;

import java.net.InetSocketAddress;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class App {

    // Initialize server port and hostname
    private static final int SERVER_PORT = 3001;
    private static final String SERVER_HOSTNAME = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        System.out.println("starting");

        // Start threads
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        // Start server
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT), 0);

        // Connect to DB
        String connectionString = "mongodb+srv://project:passwordMongo@cluster1.zqt7j.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(serverApi)
            .build();

        MongoClient mongoClient = MongoClients.create(settings);

        try {
            MongoDatabase database = mongoClient.getDatabase("users");
            database.runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            e.printStackTrace();
        }

        // Initialize DatabaseService
        DatabaseService databaseService = new DatabaseService(mongoClient, "users");

        // Initialize services
        UserService userService = new UserService(databaseService);
        EmailService emailService = new EmailService();
        AudioService audioService = new AudioService();

        // Declare endpoints
        server.createContext("/createAccount", new RequestHandler(userService, emailService, audioService, "/createAccount", mongoClient));
        server.createContext("/login", new RequestHandler(userService, emailService, audioService, "/login", mongoClient));
        server.createContext("/getHistory", new RequestHandler(userService, emailService, audioService, "/getHistory", mongoClient));
        server.createContext("/preformLogic", new RequestHandler(userService, emailService, audioService, "/preformLogic", mongoClient));
        
        server.setExecutor(threadPoolExecutor);
        server.start();

        System.out.println("Server started on port " + SERVER_PORT);
    }
}
