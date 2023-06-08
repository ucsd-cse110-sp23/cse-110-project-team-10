package server;

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

    // initialize server port and hostname
    private static final int SERVER_PORT = 3001;
    private static final String SERVER_HOSTNAME = "127.0.0.1";

    public static void main(String[] args) throws IOException {

        System.out.println("starting");

        // start threads
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        // start server
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT), 0);

        // connect to DB
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
        
        // Connection connection

        // declare endpoints
        server.createContext("/createAccount", new RequestHandle("/createAccount", mongoClient));
        server.createContext("/login", new RequestHandle("/login", mongoClient));
        server.createContext("/getHistory", new RequestHandle("/getHistory", mongoClient));
        server.createContext("/preformLogic", new RequestHandle("/preformLogic", mongoClient));
        server.setExecutor(threadPoolExecutor);
        server.start();

        System.out.println("Server started on port " + SERVER_PORT);
    }

}
