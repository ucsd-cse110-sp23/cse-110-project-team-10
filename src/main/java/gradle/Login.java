package gradle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
// import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;

import javax.swing.JOptionPane;


public class Login {
    
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    boolean ex;

    public Login(String email, String password) {

        try (MongoClient mongoClient = MongoClients.create(uri)) {


            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
            MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");

            // Document user = new Document("_id", new ObjectId());
            Document user = new Document(email, password);

            FindIterable<Document> results = userCollection.find(user);
            boolean exists = results.iterator().hasNext();
            this.ex = exists;
            if (exists) {
                boolean automaticLogin = showAutomaticLoginDialog();
                if (automaticLogin) {
                    // Enable automatic login
                    JOptionPane.showMessageDialog(null, "Logged in successfully! Automatic login enabled.");
                } else {
                    // Disable automatic login
                    JOptionPane.showMessageDialog(null, "Logged in successfully! Automatic login disabled.");
                }
                try {
                    new AppFrame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Invalid Email or Password");
            }
            
        }
    }

    public boolean showAutomaticLoginDialog() {
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to enable automatic login?", "Automatic Login", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
	}

}
