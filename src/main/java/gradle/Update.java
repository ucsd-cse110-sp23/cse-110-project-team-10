package gradle;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;



public class Update {
    String autologin;
    String email;
    String password;
    String firstName;
    String lastName;
    String displayName;
    String emailAddress;
    String emailPassword;
    String SMTPHost;
    String TLSPort;
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    public Update(String firstName,
        String lastName,
        String displayName,
        String emailAddress,
        String emailPassword, 
        String SMTPHost, 
        String TLSPort) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
            this.emailAddress = emailAddress;
            this.emailPassword = emailPassword;
            this.SMTPHost = SMTPHost;
            this.TLSPort = TLSPort;
            File file = new File("credentials.txt");
        
            try {
                Scanner scanner = new Scanner(file);
            
                // Read the values and store them as separate strings
                String autologin = scanner.next();
                String email = scanner.next(); 
                String password = scanner.next(); 
            
                scanner.close(); // Close the scanner
                
                this.autologin = autologin;
                this.email = email;
                this.password = password;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try (MongoClient mongoClient = MongoClients.create(uri)) {
                MongoDatabase ProjectDB = mongoClient.getDatabase("Project");
                MongoCollection<Document> usersCollection = ProjectDB.getCollection("Email");

                Document updatedDocument = new Document(email, password)
                    .append("Email", email)
                    .append("Password", password)
                    .append("Settings", asList(new Document("type", "first name").append("first", firstName),
                                                new Document("type", "last name").append("last name", lastName),
                                                new Document("type", "display name").append("display name", displayName),
                                                new Document("type", "email address").append("email address", emailAddress),
                                                new Document("type", "email password").append("email password", emailPassword),
                                                new Document("type", "SMTP host").append("SMTP host", SMTPHost),
                                                new Document("type", "TLS port").append("TLS port", TLSPort)));

                // update one document
                Bson filter = eq("Email", email);
                ReplaceOptions opts = new ReplaceOptions().upsert(true);
                usersCollection.replaceOne(filter, updatedDocument, opts);

                file.delete();
                String fileName = "credentials.txt";
                String content = autologin + " " + email + " " + password + " " + firstName + " " + lastName + " " + displayName + " " + emailAddress + " " + emailPassword + " " + SMTPHost + " " + TLSPort;

                try {
                    FileWriter fileWriter = new FileWriter(fileName);
                    fileWriter.write(content);
                    fileWriter.close();
                    System.out.println("File created and content written successfully.");
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file.");
                    e.printStackTrace();
                }

            }
    }
}
