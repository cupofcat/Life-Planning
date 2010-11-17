package com.appspot.iclifeplanning.notifications;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.datastore.Analyzer;
import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereChoice;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.TokenStore;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.notifications.MailService.MessageType;
import com.google.appengine.api.users.User;

/**
 * Notification servlet. Responsible for checking for new events in
 * the users calendar and sending notifications about the changes.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
@SuppressWarnings("serial")
public class NotificationServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Does this work?
	    Set<Email> emails = (Set<Email>) pm.getManagedObjects(Email.class);
	    MailService ms = new MailService();
		
		for (Email em : emails) {
			String sessionToken = TokenStore.getToken(em.getID());
			String emailAddress = em.getEmail();
			
		    if (sessionToken != null) {
		      AuthService.getAuthServiceInstance().registerToken(sessionToken);
		      
		      // Set the session token as a field of the Service object. Since a new
		      // Service object is created with each get call, we don't need to
		      // worry about the anonymous token being used by other users.
		      AuthService.client.setAuthSubToken(sessionToken);
		      
		      // TODO(amadurska): Check for new events
		      // TODO(amadurska): Send e-mail with notification
		      ms.sendEmail(emailAddress, MessageType.NOTIFICATION);
		    } else {
		      ms.sendEmail(emailAddress, MessageType.TOKEN_ERROR);
		      // TODO(amadurska): Send e-mail to re-enable our application
		    }
		}
	}
}
