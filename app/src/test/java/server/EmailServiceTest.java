package server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailServiceTest {
    private EmailService emailService;
    String badEmail = "tester@gmail.com";
    String goodEmail = "ilovecse110somuch@gmail.com";
    String badPass = "123";
    String goodPass = "ilovecse110somuch";
    String badSMTP = "smtp.gmal.com";
    String goodSMTP = "smtp.gmail.com";
    String badTLS = "123";
    String goodTLS = "587";
    String toEmail = "jwarzybokmckenney@gmail.com";
    



    @BeforeEach
    public void setup(){
        emailService = new EmailService();
    }

    @Test
    public void sendBadEmail() throws Exception {
        boolean excp = false;
        emailService.initialize(badEmail, goodPass, goodSMTP, goodTLS);

        try {
            emailService.sendEmail(toEmail, "Subject", "test msg");
        } catch (Exception e) {
            excp = true;
        }

        assertEquals(true, excp);
    }

    /* 
    @Test
    public void sendGoodEmail() throws Exception {
        boolean excp = false;
        emailService.initialize(goodEmail, goodPass, goodSMTP, goodTLS);

        try {
            emailService.sendEmail(toEmail, "Subject", "test msg");
        } catch (Exception e) {
            excp = true;
        }

        assertEquals(false, excp);
    }
    */
}
