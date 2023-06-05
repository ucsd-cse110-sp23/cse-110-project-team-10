package gradle;

import static java.util.Arrays.asList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
// import com.mongodb.client.model.FindOneAndUpdateOptions;
// import com.mongodb.client.model.ReturnDocument;
// import com.mongodb.client.model.UpdateOptions;
// import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
// import org.bson.json.JsonWriterSettings;


// import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
// import static com.mongodb.client.model.Updates.*;


public class Update {

        String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
        public Update(String firstName,
                    String lastName,
                    String displayName,
                    String emailAddress,
                    String emailPassword, 
                    String SMTPHost, 
                    String TLSPort) {
            try (MongoClient mongoClient = MongoClients.create(uri)) {
                MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
                MongoCollection<Document> usersCollection = sampleTrainingDB.getCollection("Email");

                Document updatedDocument = new Document()
                    .append("Email", emailAddress)
                    .append("Password", emailPassword)
                    .append("Settings", asList(new Document("type", "first name").append("first", firstName),
                                            new Document("type", "last name").append("last name", lastName),
                                            new Document("type", "display name").append("display name", displayName),
                                            new Document("type", "email address").append("email address", emailAddress),
                                            new Document("type", "email password").append("email password", emailPassword),
                                            new Document("type", "SMTP host").append("SMTP host", SMTPHost),
                                            new Document("type", "TLS port").append("TLS port", TLSPort)));

                // update one document
                Bson filter = eq(emailAddress, emailPassword);
                usersCollection.replaceOne(filter, updatedDocument);
            }
        }
}
