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
	    Session session = Session.getDefaultInstance(fMailServerConfig, null);
	    MimeMessage message;
	    try {
	      //the "from" address may be set in code, or set in the
	      //config file under "mail.from" ; here, the latter style is used
	      //message.setFrom( new InternetAddress(aFromEmailAddr) );
	    
	      message = type.getMessage(session);
	      Transport.send( message );
	    }
	    catch (MessagingException ex){
	      System.err.println("Cannot send email. " + ex);
	    }
	}

	private static Properties fMailServerConfig = new Properties();
	
	static {
	  fetchConfig();
	}
	
	/**
	* Open a specific text file containing mail server
	* parameters, and populate a corresponding Properties object.
	*/
	private static void fetchConfig() {
	  InputStream input = null;
	  try {
	    //If possible, one should try to avoid hard-coding a path in this
	    //manner; in a web application, one should place such a file in
	    //WEB-INF, and access it using ServletContext.getResourceAsStream.
	    //Another alternative is Class.getResourceAsStream.
	    //This file contains the javax.mail config properties mentioned above.
		  //TODO(amadurska) Do exactly what's written above
	  input = new FileInputStream( "C:\\Temp\\MyMailServer.txt" );
	  fMailServerConfig.load( input );
	  } catch ( IOException ex ){
	    System.err.println("Cannot open and load mail server properties file.");
	  }
	  finally {
	    try {
	      if ( input != null ) input.close();
	    }
	    catch ( IOException ex ){
	      System.err.println( "Cannot close mail server properties file." );
	     }
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
		NOTIFICATION, TOKEN_ERROR;

		public MimeMessage getMessage(Session session) throws MessagingException {
		    MimeMessage message = new MimeMessage(session);
		    message.setText("Some generic title");
			return message;
		}
	}
}
