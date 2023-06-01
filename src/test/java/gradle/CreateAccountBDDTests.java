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


class MockCreateAccountUI extends CreateAccountUI{
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

    String uri = "mongodb+srv://joseph:I2GC8oDDOoL4a9Cu@cluster0.4kpzovg.mongodb.net/?retryWrites=true&w=majority";
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
    MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");
    
    @BeforeEach
	public void setup() throws Exception {
		mockCreateAccountUI = new MockCreateAccountUI();
	}

    @Test
    public void testCreateAccount() throws Exception{
        long size = userCollection.countDocuments();
        mockCreateAccountUI.setEmailField("joseph@gmail.com");
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
