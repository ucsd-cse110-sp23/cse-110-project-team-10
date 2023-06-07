package gradle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class MockSetUpUI{
    private String firstNameField;
    private String lastNameField;
    private String displayNameField;
    private String emailField;
    private String passwordField;
    private String SMTPHostField;
    private String TLSPortField;

    public MockSetUpUI(){
        this.firstNameField = "";
        this.lastNameField = "";
        this.displayNameField = "";
        this.emailField = "";
        this.passwordField = "";
        this.SMTPHostField = "";
        this.TLSPortField = "";
    }

    public void setFirstNameField(String firstName) {
        this.firstNameField = firstName;
    }

    public void setLastNameField(String lastName) {
        this.lastNameField = lastName;
    }

    public void setDisplayNameField(String displayName) {
        this.displayNameField = displayName;
    }

    public void setEmailField(String email){
        this.emailField = email;
    }

    public void setPasswordField(String password){
        this.passwordField = password;
    }

    public void setSMTPHostField(String SMTPHost){
        this.SMTPHostField = SMTPHost;
    }

    public void setTLSPortField(String TLSPort) {
        this.TLSPortField = TLSPort;
    }

    public Update setUp(){
        Update update = new Update(firstNameField, lastNameField, displayNameField, emailField, passwordField, SMTPHostField, TLSPortField);
        return update;
    }
}

public class testSetupEmail {
    private MockSetUpUI mockSetUpUI;

    @BeforeEach
    public void setup() throws Exception {
        mockSetUpUI = new MockSetUpUI();
    }

    @Test
    public void testSetUpEmail() throws Exception {
        mockSetUpUI.setFirstNameField("test_first_name");
        mockSetUpUI.setLastNameField("test_last_name");
        mockSetUpUI.setDisplayNameField("test_displayname");
        mockSetUpUI.setEmailField("test_emailaddress");
        mockSetUpUI.setPasswordField("test_password");
        mockSetUpUI.setSMTPHostField("test_SMTP");
        mockSetUpUI.setTLSPortField("test_TLS");
        Update testUpdate = mockSetUpUI.setUp();
        assertEquals("test_first_name", testUpdate.firstName);
        assertEquals("test_last_name", testUpdate.lastName);
        assertEquals("test_displayname", testUpdate.displayName);
        assertEquals("test_emailaddress", testUpdate.emailAddress);
        assertEquals("test_password", testUpdate.emailPassword);
        assertEquals("test_SMTP", testUpdate.SMTPHost);
        assertEquals("test_TLS", testUpdate.TLSPort);
    }
}
