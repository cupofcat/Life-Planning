package com.appspot.iclifeplanning.notifications;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;


public class MailService {
	public static ArrayList<Thread> users = new ArrayList<Thread>();
	private static final int time_slice = 10 * 60 * 1000; // 10 minutes
	
	public void sendEmail(String email, MessageType type) {
		
		//Here, no Authenticator argument is used (it is null).
	    //Authenticators are used to prompt the user for user
	    //name and password.
		Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message;
	    try {
	      //the "from" address may be set in code, or set in the
	      //config file under "mail.from" ; here, the latter style is used
	      //message.setFrom( new InternetAddress(aFromEmailAddr) );
	    
	      message = type.getMessage(session);
	      message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, ""));
	      message.setFrom(new InternetAddress("iclifeplanning@gmail.com", "Life Planning"));
	      message.setSubject("Updates from your Life Planning Utility!");
	      Transport.send( message );
	    } catch (MessagingException ex) {
	      System.err.println("Cannot send email. " + ex);
	    } catch (UnsupportedEncodingException e) {
	    	
	    }
	}
	
	/*  
	    # Configuration file for javax.mail 
		# If a value for an item is not provided, then 
		# system defaults will be used. These items can 
		# also be set in code.
		
		# Host whose mail services will be used 
		# (Default value : localhost) 
		mail.host=mail.blah.com
		
		# Return address to appear on emails 
		# (Default value : username@host) 
		mail.from=webmaster@blah.net
		
		# Other possible items include: 
		# mail.user= 
		# mail.store.protocol= 
		# mail.transport.protocol= 
		# mail.smtp.host= 
		# mail.smtp.user= 
		# mail.debug=
	*/
	
	public enum MessageType {
		NOTIFICATION {

			public MimeMessage getMessage(Session session) throws MessagingException {
			    MimeMessage message = new MimeMessage(session);
			    message.setText("Hi! This is your regular update e-mail!");
				return message;
			}
		}, TOKEN_ERROR {

			public MimeMessage getMessage(Session session) throws MessagingException {
				MimeMessage message = new MimeMessage(session);
			    message.setText("Hi! You need to reset the right for our application!");
				return message;
			}
		};

		public abstract MimeMessage getMessage(Session session) throws MessagingException;
	}
}
