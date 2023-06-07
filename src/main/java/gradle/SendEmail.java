package gradle;

//imports
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {

	//user specific vars
    String userEmail = "";
    String userPassword = "";
    String smtpServer = "";
    String smtpPort = "";
	String name = "";

	/**
	 * constructor for email 
	 * 
	 * @param userEmail users email
	 * @param userPassword users password
	 * @param smtpServer email server for smtp
	 * @param smtpPort port for emailing
	 * @param name prefered users sign off  anme
	 */
    SendEmail(String userEmail, String userPassword, String smtpServer, String smtpPort, String name) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.smtpServer = smtpServer;
        this.smtpPort = smtpPort;
		this.name = name;
    }

	/**
	 * gets user email
	 * @return user email
	 */
    public String getUserEmail() {
        return userEmail;
    }

	/**
	 * gets user password
	 * @return user password
	 */
    public String getUserPassword() {
        return userPassword;
    }

	/**
	 * sends and email to a specified adress
	 * using SMTP
	 * 
	 * @param receiver email to send to
	 * @param message message to send in email
	 * @param subject subject to send in email
	 * @return true if email sent successful
	 * 		   false if not
	 */
    public boolean sendEmail(String receiver, String message, String subject) {

		//setup for email props
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);

		//creates mail session with props
        Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, userPassword);
            }
        });

        try {

			//sets message parts
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(userEmail));

            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message +"\n from "+name);

			//sending email
            Transport.send(mimeMessage);

            return true;
        } 
        
        catch (MessagingException e) {
            return false;
        }
    }
}
