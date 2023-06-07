package gradle;

//package com.share.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

public class SendEmail {


	public SendEmail(String senderEmail, String password, String displayName, String SMTPHost, String TLSPort, String to, String subject, String content ) {

		// Set Properties
		Properties props = new Properties();
		
		props.put( "mail.smtp.auth", "true" );  
		props.put( "mail.smtp.host", SMTPHost );
		props.put( "mail.smtp.port", TLSPort );
		props.put( "mail.smtp.starttls.enable", "true" ); 
		props.put( "mail.debug", true );
		props.put( "mail.smtp.socketFactory.port", TLSPort );
		//props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put( "mail.smtp.socketFactory.fallback", "false" );
		props.put( "mail.smtp.ssl.trust", SMTPHost );

		// Create the Session Object
		Session session = Session.getDefaultInstance(
			props,
			new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(senderEmail, password);
				}
			}
		);

		try {

			MimeMessage message = new MimeMessage( session );

			// From
			message.setFrom( new InternetAddress( senderEmail ) );

			// Reply To
			message.setReplyTo( InternetAddress.parse( senderEmail ) );
			
			// Recipient
			message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
			
			// Subject
			message.setSubject( subject );
			
			// Content
			message.setContent( content, "text/html; charset=utf-8" );

			Transport.send(message);

		} 
		catch( MessagingException exc ) {
			JOptionPane.showMessageDialog(null, "Invalid Email Setup.", "Error", JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException( exc );
		}
	}
}