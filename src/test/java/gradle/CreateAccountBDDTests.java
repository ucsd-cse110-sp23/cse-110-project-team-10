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


public class CreateAccountBDDTests {
    private CreateAccountUI createAccountUI;
    String uri = "mongodb+srv://joseph:I2GC8oDDOoL4a9Cu@cluster0.4kpzovg.mongodb.net/?retryWrites=true&w=majority";
    MongoClient mongoClient = MongoClients.create(uri);
    MongoDatabase sampleTrainingDB = mongoClient.getDatabase("Project");
    MongoCollection<Document> userCollection = sampleTrainingDB.getCollection("Email");
    
    @BeforeEach
	public void setup() throws Exception {
		createAccountUI = new CreateAccountUI();
	}

    @Test
    public void testCreateAccount() throws Exception{
        long size = userCollection.countDocuments();
        createAccountUI.emailField.setText("test@gmail.com");
        createAccountUI.passwordField.setText("password");
        createAccountUI.verifyPasswordField.setText("password");
        createAccountUI.createAccountButton.doClick();
        
        assertEquals(size+1,userCollection.countDocuments());

    }

    @Test
    public void testPasswordsDontMatch() throws Exception{
        long size = userCollection.countDocuments();
        createAccountUI.emailField.setText("test@gmail.com");
        createAccountUI.passwordField.setText("123");
        createAccountUI.verifyPasswordField.setText("321");
        createAccountUI.createAccountButton.doClick();

        assertEquals(size, userCollection.countDocuments());
    }
}
