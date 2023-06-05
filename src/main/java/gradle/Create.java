package gradle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
//import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;
import java.io.FileWriter;
import java.io.IOException;

import static java.util.Arrays.asList;

import javax.swing.JOptionPane;


public class Create {
    
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    boolean ex;

    public Create(String email, String password) {
        // generate setting file
        String fileName = "credentials.txt";
        String content = "0 " + email + " " + password + " " + "Fill_in_first_name " + "Fill_in_last_name " + "Fill_in_display_name " + " " + "Fill_in_email_address " + "Fill_in_email_password " + "Fill_in_SMTP_host " + "Fill_in_TLS_port";

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(content);
            fileWriter.close();
            System.out.println("File created and content written successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
        try (MongoClient mongoClient = MongoClients.create(uri)) {


            MongoDatabase ProjectDB = mongoClient.getDatabase("Project");
            MongoCollection<Document> userCollection = ProjectDB.getCollection("Email");

            //Document user = new Document("_id", new ObjectId());
            Document user = new Document(email, password);


            FindIterable<Document> results = userCollection.find(user);
            boolean exists = results.iterator().hasNext();
            this.ex = exists;
            if (exists) {
                JOptionPane.showMessageDialog(null, "Account already exists");
            }
            else {
                user.append("Email", email)
                    .append("Password", password)
                    .append("Settings", asList(new Document("type", "first name").append("first name", ""),
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
