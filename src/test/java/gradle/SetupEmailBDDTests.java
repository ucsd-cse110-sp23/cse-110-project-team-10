package gradle;

import java.io.*;
import java.util.*;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


class MockSetUpUI{
    String autologin;
	String preEmail;
	String prePassword;
	String firstName;
	String lastName;
	String displayName;
	String emailAddress;
	String emailPassword;
	String SMTPHost;
	String TLSPort;

    public MockSetUpUI(){
        this.autologin = "";
        this.preEmail = "";
        this.prePassword = "";
        this.firstName = "";
        this.lastName = "";
        this.displayName = "";
        this.emailAddress = "";
        this.emailPassword = "";
        this.SMTPHost = "";
        this.TLSPort = "";


    }
    public void storeInformation() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"));
		String line;
		if ((line = reader.readLine()) != null){
			String[] values = line.split(" ");
			this.autologin = values[0];
			this.preEmail = values[1];
			this.prePassword = values[2];
			this.firstName = values[3];
			this.lastName = values[4];
			this.displayName = values[5];
			this.emailAddress = values[6];
			this.emailPassword = values[7];
			this.SMTPHost = values[8];
			this.TLSPort = values[9];
		}
		reader.close();

        //new Update(firstName, lastName, displayName, emailAddress, emailPassword, SMTPHost, TLSPort);
	}
}


public class SetupEmailBDDTests {
    private MockSetUpUI mockSetUpUI;
    String[] inputs = {"true","test@gmail.com","password","Joseph","Mckenney","Joseph123","joseph@gmail.com","mypassword","12453","277"};

    @BeforeEach
    public void setup(){
        mockSetUpUI = new MockSetUpUI();
    }

    @Test
    public void testSetUpEmail() throws IOException{
        mockSetUpUI.autologin = inputs[0];
        mockSetUpUI.preEmail = inputs[1];
        mockSetUpUI.prePassword = inputs[2];
        mockSetUpUI.firstName = inputs[3];
        mockSetUpUI.lastName = inputs[4];
        mockSetUpUI.displayName = inputs[5];
        mockSetUpUI.emailAddress = inputs[6];
        mockSetUpUI.emailPassword = inputs[7];
        mockSetUpUI.SMTPHost = inputs[8];
        mockSetUpUI.TLSPort = inputs[9];

        mockSetUpUI.storeInformation();

        BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"));
		String line = reader.readLine();
        reader.close();
        assertTrue(line != null);
	}

}