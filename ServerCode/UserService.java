package _MS2Demo;


import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserService {

	private final DatabaseService databaseService;
	private final String collectionName = "users";

	public UserService(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}

	public Document createUser(String username, String password) {
		// get collection
		MongoCollection<Document> collection = databaseService.getCollection(collectionName);

		// check if user already exists
		Document existingUser = databaseService.findUserByUsername(collection, username);
		if (existingUser != null) {
			return null;
		}

		// create new user
		Document newUser = new Document("_id", new ObjectId());
		newUser.append("username", username).append("password", password).append("history", new ArrayList<>())
				.append("email address", "").append("email password", "").append("SMTP", "").append("TLS", "")
				.append("first name", "").append("last name", "");

		// add new user to the database
		databaseService.insertUser(collection, newUser);

		return newUser;
	}

	public Document loginUser(String username, String password) {
		// get collection
		MongoCollection<Document> collection = databaseService.getCollection(collectionName);

		// try to find the user
		Document user = databaseService.findUserByUsername(collection, username);

		// check if passwords match
		if (user != null) {
			String storedPassword = user.getString("password");
			if (!storedPassword.equals(password)) {
				user = null;
			}
		}

		return user;
	}
	
	public JSONArray getUserHistory(String username, String password) {
	    // try to find the user
	    Document user = loginUser(username, password);
	    if (user == null) {
	        return null; // User not found or password doesn't match
	    }

	    // Get user's history
	    List<Object> history = (List<Object>) user.get("history");
	    JSONArray historyJson = new JSONArray(history);
	    return historyJson;
	}


	public void updateUserHistory(String username, String password, List<Document> history) {
		Document user = loginUser(username, password);
		if (user != null) {
			// get collection
			MongoCollection<Document> collection = databaseService.getCollection(collectionName);
			databaseService.updateUserHistory(collection, username, history);
		}
	}

	public void clearUserHistory(String username, String password) {
		Document user = loginUser(username, password);
		if (user != null) {
			// get collection
			MongoCollection<Document> collection = databaseService.getCollection(collectionName);
			databaseService.clearUserHistory(collection, username);
		}
	}
	
	public Document getUserByEmail(String email) {
        MongoCollection<Document> userCollection = databaseService.getCollection("users");
        return databaseService.findUserByUsername(userCollection, email);
    }

	// Add any other user-related methods here.
}
