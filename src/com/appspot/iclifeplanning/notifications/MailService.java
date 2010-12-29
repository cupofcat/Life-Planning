package com.appspot.iclifeplanning.notifications;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import com.appspot.analyser.Suggestion;
import com.appspot.datastore.SphereName;


public class MailService {
	public static ArrayList<Thread> users = new ArrayList<Thread>();
	private static final int time_slice = 10 * 60 * 1000; // 10 minutes
	
	public void sendEmail(String email, EmailContent content) {
		
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
	    
	      message = new MimeMessage(session);
	      message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, ""));
	      message.setFrom(new InternetAddress("iclifeplanning@gmail.com", "Life Planning"));
	      message.setSubject("Updates from your Life Planning Utility!");
	      message.setText(content.toString());
	      Transport.send(message);
	    } catch (MessagingException ex) {
	      System.err.println("Cannot send email. " + ex);
	    } catch (UnsupportedEncodingException e) {
	    	
	    }
	}
}
