package gradle;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {

    String userEmail = "";
    String userPassword = "";
    String smtpServer = "";
    String smtpPort = "";
	String name = "";

    SendEmail(String userEmail, String userPassword, String smtpServer, String smtpPort, String name) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
		this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public boolean sendEmail(String receiver, String message, String subject) {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, userPassword);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(userEmail));

            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message +"\n from "+name);

            Transport.send(mimeMessage);

            return true;
        } 
        
        catch (MessagingException e) {
            return false;
        }
    }
}
