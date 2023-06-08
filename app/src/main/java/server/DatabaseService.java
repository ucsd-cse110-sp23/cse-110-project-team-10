package server;

import com.mongodb.client.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class DatabaseService {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public DatabaseService(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(databaseName);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public Document findUserByUsername(MongoCollection<Document> collection, String username) {
        return collection.find(Filters.eq("username", username)).first();
    }

    public void insertUser(MongoCollection<Document> collection, Document newUser) {
        collection.insertOne(newUser);
    }

    public void updateUserHistory(MongoCollection<Document> collection, String username, List<Document> history) {
        collection.updateOne(Filters.eq("username", username), Updates.set("history", history));
    }

    public void clearUserHistory(MongoCollection<Document> collection, String username) {
        collection.updateOne(Filters.eq("username", username), Updates.set("history", new ArrayList<>()));
    }
    public List<Object> getUserHistory(String username) {
        // get collection
        MongoCollection<Document> collection = getCollection("users");

        // find the user
        Document user = findUserByUsername(collection, username);
        if (user == null) {
            return null;
        }

        // Get user's history
        return (List<Object>) user.get("history");
    }
    // Add any other database-related methods here.
}
