package gradle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;


import static java.util.Arrays.asList;


public class Create {
    
    String uri = "mongodb+srv://joseph:I2GC8oDDOoL4a9Cu@cluster0.4kpzovg.mongodb.net/?retryWrites=true&w=majority";

    public Create(String email, String password) {

        try (MongoClient mongoClient = MongoClients.create(uri)) {


            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
            MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");

            Document user = new Document("_id", new ObjectId());
            user.append("Email", email)
                   .append("Password", password)
                   .append("Settings", asList(new Document("type", "first name").append("email", ""),
                                            new Document("type", "last name").append("last name", ""),
                                            new Document("type", "display name").append("display name", ""),
                                            new Document("type", "email address").append("email address", ""),
                                            new Document("type", "email password").append("email password", ""),
                                            new Document("type", "SMTP host").append("SMTP host", ""),
                                            new Document("type", "TLS port").append("TLS port", "")));


            userCollection.insertOne(user);
        }
    }


    // public static void main(String[] args) {
        
    // }
}
