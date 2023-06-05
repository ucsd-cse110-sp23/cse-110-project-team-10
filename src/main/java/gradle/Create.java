package gradle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;

import static java.util.Arrays.asList;

import javax.swing.JOptionPane;


public class Create {
    
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    boolean ex;

    public Create(String email, String password) {

        try (MongoClient mongoClient = MongoClients.create(uri)) {


            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
            MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");

            Document user = new Document("_id", new ObjectId());
            //Document user = new Document(email, password);


            FindIterable<Document> results = userCollection.find(user);
            boolean exists = results.iterator().hasNext();
            this.ex = exists;
            if (exists) {
                JOptionPane.showMessageDialog(null, "Account already exists");
            }
            else {
                user.append("Email", email)
                    .append("Password", password)
                    .append("Settings", asList(new Document("type", "first name").append("first", ""),
                                            new Document("type", "last name").append("last name", ""),
                                            new Document("type", "display name").append("display name", ""),
                                            new Document("type", "email address").append("email address", ""),
                                            new Document("type", "email password").append("email password", ""),
                                            new Document("type", "SMTP host").append("SMTP host", ""),
                                            new Document("type", "TLS port").append("TLS port", "")));


                userCollection.insertOne(user);
                try {
                    JOptionPane.showMessageDialog(null, "Account created successfully!");
                    new AppFrame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
    }

}
