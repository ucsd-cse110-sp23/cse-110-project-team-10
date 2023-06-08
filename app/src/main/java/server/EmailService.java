package server;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailService {

    private Session session;
    private String senderEmail;

    public static class SendEmailException extends Exception {
        public SendEmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Default constructor
    public EmailService() {
    }

    public void initialize(String senderEmail, String password, String SMTPHost, String TLSPort) {
        this.senderEmail = senderEmail;

        // Set Properties
        Properties props = new Properties();

        props.put( "mail.smtp.auth", "true" );
        props.put( "mail.smtp.host", SMTPHost );
        props.put( "mail.smtp.port", TLSPort );
        props.put( "mail.smtp.starttls.enable", "true" );
        props.put( "mail.debug", true );
        props.put( "mail.smtp.socketFactory.port", TLSPort );
        props.put( "mail.smtp.socketFactory.fallback", "false" );
        props.put( "mail.smtp.ssl.trust", SMTPHost );

        // Create the Session Object
        this.session = Session.getDefaultInstance(
            props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, password);
                }
            }
        );
    }

    public void sendEmail(String to, String subject, String content) throws SendEmailException {
        if (this.session == null || this.senderEmail == null) {
            throw new IllegalStateException("EmailService not initialized");
        }

        try {
            MimeMessage message = new MimeMessage(this.session);

            // From
            message.setFrom( new InternetAddress( this.senderEmail ) );

            // Reply To
            message.setReplyTo( InternetAddress.parse( this.senderEmail ) );

            // Recipient
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );

            // Subject
            message.setSubject( subject );

            // Content
            message.setContent( content, "text/html; charset=utf-8" );

            Transport.send(message);

        } catch( MessagingException exc ) {
            throw new SendEmailException("Failed to send email", exc);
        }
    }
}
