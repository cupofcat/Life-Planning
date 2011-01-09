package com.appspot.iclifeplanning.notifications;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.analyser.Analyser;
import com.appspot.analyser.Suggestion;
import com.appspot.analyser.Utilities;
import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.TokenStore;
import com.appspot.datastore.UserProfile;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.appspot.iclifeplanning.events.Event;
import com.appspot.iclifeplanning.events.EventStore;

/**
 * Notification servlet. Responsible for checking for new events in
 * the users calendar and sending notifications about the changes.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
@SuppressWarnings("serial")
public class NotificationServlet extends HttpServlet {
	private static long timer = 0;
	private static final Logger log = Logger.getLogger("EventStore");

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Does this work?
	    Query query = pm.newQuery("SELECT FROM " + UserProfile.class.getName());
	    Collection<UserProfile> profiles = (Collection<UserProfile>) query.execute();
	    MailService ms = new MailService();
		
		for (UserProfile profile : profiles) {
			String sessionToken = TokenStore.getToken(profile.getUserID());
			String emailAddress = profile.getEmail();
			EmailContent content;
		    if (sessionToken != null) {
		      AuthService.getAuthServiceInstance().registerToken(sessionToken);
		      
		      // Set the session token as a field of the Service object.
		      CalendarUtils.client.setAuthSubToken(sessionToken);
		      EventStore eventStore = EventStore.getInstance();
		      //eventStore.initizalize();
		      long startTime = profile.getStartOptimizing();
		      long endTime = profile.getFinishOptimizing();
		      List<Event> events = eventStore.getEventsFromTimeRange(startTime, endTime);
		      Analyser analyser = new Analyser();
		      List<List<Suggestion>> suggestions
		    	  = analyser.getSuggestions(events, profile.getUserID());
		      HashMap<SphereName, Double> desiredLifeBalance 
		          = profile.getSpherePreferences();
		      HashMap<SphereName, Double> currentLifeBalance 
		          = Utilities.analyseEvents(events, desiredLifeBalance);
		      content = new NotificationEmailContent(suggestions, 
		    		  desiredLifeBalance, currentLifeBalance, profile.getName());
		    } else {
		    	content = new ErrorEmailContent(ErrorEmailContent.TOKEN_PROBLEM);
		    }
		    ms.sendEmail(emailAddress, content);
		}
	}
}
