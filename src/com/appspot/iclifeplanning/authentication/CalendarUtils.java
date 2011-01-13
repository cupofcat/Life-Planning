package com.appspot.iclifeplanning.authentication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import com.appspot.analyser.Utilities;
import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.Token;
import com.appspot.datastore.TokenStore;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.charts.utils.AddUserDataServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.Link;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * Class responsible for managing the authentication within the application.
 * Uses Google account/password for token-based authentication. Majority of
 * issues are managed by Google services, mainly by the UserService class.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 * 
 */
public class CalendarUtils {

	/** AuthService instance for singleton-based design */
	private static CalendarUtils calendarUtilsInstance = null;

	/** Service used to monitor the currently the users of the application */
	private static UserService userService = UserServiceFactory.getUserService();

	/** Feed-url giving access to all calendars accesible by a give user */
	public static final String CALENDAR_FULL_FEED_REQUEST_URL = "http://www.google.com/calendar/feeds/default/allcalendars/full";

	public static final String DEFAULT_FULL_FEED_REQUEST_URL = "http://www.google.com/calendar/feeds/default";

	public static CalendarService client = new CalendarService("ic-lifeplanning-v1");

	/** Constructor for singleton pattern */
	private CalendarUtils() {
	}

	public static CalendarUtils getCalendarUtils() {
		if (calendarUtilsInstance == null) {
			calendarUtilsInstance = new CalendarUtils();
		}
		return calendarUtilsInstance;
	}

	public String getCalendarAccessUrl(String nextUrl) throws IOException {

		String requestUrl = AuthSubUtil.getRequestUrl(nextUrl, DEFAULT_FULL_FEED_REQUEST_URL, false, true);

		return requestUrl;
	}

	/** Stores token for the currently logged-in user in the datastore */
	public void setTokenFromReply(String reply) {
		String authToken = AuthSubUtil.getTokenFromReply(reply);
		try {
			authToken = AuthSubUtil.exchangeForSessionToken(authToken, null);
			client.setAuthSubToken(authToken);
			User user = userService.getCurrentUser();
			TokenStore.addToken(user.getUserId(), authToken);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getLogOutURL(HttpServletRequest request) {
		return userService.createLogoutURL(request.getRequestURI());
	}

	public static String getCurrentUserId() {
		return userService.getCurrentUser().getUserId();
	}

	public Set<String> getCalendarURLs() throws IOException, TokenException {
		Set<String> urls = new HashSet<String>();

		URL calendarFeedUrl = null;
		try {
			calendarFeedUrl = new URL(AuthService.CALENDAR_FULL_FEED_REQUEST_URL);
		} catch (MalformedURLException e1) {
			assert false;
		}

		CalendarFeed calendarResultFeed = null;

		// Connect to Google Calendar and gather data
		try {
			client.setAuthSubToken(TokenStore.getToken(getCurrentUserId()));
			calendarResultFeed = client.getFeed(calendarFeedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			TokenStore.deleteTokend(userService.getCurrentUser().getUserId());
			// e.printStackTrace();
			throw new TokenException();
		} catch (MalformedURLException e1) {
			assert false;
		}

		CalendarEntry calendarEntry;
		Link eventFeedLink;
		String eventFeedUrl = null;

		for (int i = 0; i < calendarResultFeed.getEntries().size(); i++) {
			calendarEntry = calendarResultFeed.getEntries().get(i);
			eventFeedLink = calendarEntry.getLink("http://schemas.google.com/gCal/2005#eventFeed", null);

			eventFeedUrl = eventFeedLink.getHref();
			eventFeedUrl = eventFeedUrl.substring(37, eventFeedUrl.length() - 13);

			urls.add(eventFeedUrl);
		}

		return urls;
	}

	public static void setUpIfNewUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user != null) {
			String id = user.getUserId();
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery("SELECT userID FROM " + UserProfile.class.getName());
			List<String> userIDs = (List<String>) query.execute();
			
			if (!contains(userIDs, id)) {
				HashMap<SphereName, Double> spherePreferences = new HashMap<SphereName, Double>();
				for (SphereName s : SphereName.values()) {
					spherePreferences.put(s, s.defaultValue());
				}
				long now = Calendar.getInstance().getTimeInMillis();
				UserProfile newProfile 
				    = new UserProfile(id, user.getNickname(), user.getEmail(), 
				    		spherePreferences, true, 
				    		now, now, now + (long)30*24*60*60*1000);
				newProfile.makePersistent();
				// generate dummy data for graphs
				AddUserDataServlet.addData(id, 	SphereName.WORK.defaultValue(),
																				SphereName.HEALTH.defaultValue(),
																				SphereName.FAMILY.defaultValue(),
																				80,  // number of weeks
																				30); // number of weeks optimised
				Utilities.addProposals();
			}
		}
	}

	private static boolean contains(List<String> userIDs, String id) {
		for (int i = 0; i < userIDs.size(); i++) {
			if (userIDs.get(i).equals(id))
				return true;
		}
		return false;
	}
}