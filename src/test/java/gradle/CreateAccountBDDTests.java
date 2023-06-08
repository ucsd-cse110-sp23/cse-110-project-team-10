package gradle;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Popup;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.border.Border;
import javax.sound.sampled.*;
import java.awt.event.*;
import java.awt.*;
// import java.awt.desktop.QuitEvent;
import java.io.*;
import java.util.*;

class MockCreateAccountUI{
    private String emailField;
    private String passwordField;
    private String verifyPasswordField;

    public MockCreateAccountUI(){
        this.emailField = "";
        this.passwordField = "";
        this.verifyPasswordField = "";
    }

    public void setEmailField(String email){
        this.emailField = email;
    }

    public void setPasswordField(String password){
        this.passwordField = password;
    }

    public void setVerifyPasswordField(String password){
        this.verifyPasswordField = password;
    }

    public boolean checkPasswordValidity(){
        if (passwordField.equals(verifyPasswordField)){
            return true;
        }
        return false;
    }

    public boolean checkFields(){
        if (emailField != null && passwordField != null && verifyPasswordField != null){
            return true;
        }
        return false;
    }

    public void createAccount(){
        if (checkPasswordValidity() == true){
            new Create(emailField, passwordField);
        }
    }
}
public class CreateAccountBDDTests {
    private MockCreateAccountUI mockCreateAccountUI;

    
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
    MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");
    
    
    //MongoClient mongoClient = MongoClients.create("mongodb://host1:27017,host2:27017,host3:27017");
    //MongoClient mongo = MongoClients.create("mongodb://host1:27017");
    //MongoDatabase db = mongoClient.getDatabase("Project");
    //MongoCollection<Document> userCollection = db.getCollection("Email");
    @BeforeEach
	public void setup() throws Exception {
		mockCreateAccountUI = new MockCreateAccountUI();
	}

    @Test
    public void testCreateAccount() throws Exception{
        long size = userCollection.countDocuments();
        mockCreateAccountUI.setEmailField("testCreate@gmail.com");
        mockCreateAccountUI.setPasswordField("password");
        mockCreateAccountUI.setVerifyPasswordField("password");
        mockCreateAccountUI.createAccount();
        
        assertEquals(size+1,userCollection.countDocuments());

    }

    @Test
    public void testPasswordsDontMatch() throws Exception{
        long size = userCollection.countDocuments();
        mockCreateAccountUI.setEmailField("joseph@gmail.com");
        mockCreateAccountUI.setPasswordField("123");
        mockCreateAccountUI.setVerifyPasswordField("321");
        mockCreateAccountUI.createAccount();

        assertEquals(size, userCollection.countDocuments());
    }
}
