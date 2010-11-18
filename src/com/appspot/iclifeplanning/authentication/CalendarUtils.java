package com.appspot.iclifeplanning.authentication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.appspot.datastore.TokenStore;
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
 * issues are managed by Google services, mainly  by the UserService class.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
public class CalendarUtils {

	/** AuthService instance for singleton-based design*/
	private static CalendarUtils calendarUtilsInstance = null;

	/**Service used to monitor the currently the users of the application*/
	private static UserService userService = UserServiceFactory.getUserService();

	/**Feed-url giving access to all calendars accesible by a give user*/
	public static final  String CALENDAR_FULL_FEED_REQUEST_URL 
	    = "http://www.google.com/calendar/feeds/default/allcalendars/full";

	public static final  String DEFAULT_FULL_FEED_REQUEST_URL 
		= "http://www.google.com/calendar/feeds/default";
	
	private static CalendarService client = new CalendarService("ic-lifeplanning-v1");

	/**Constructor for singleton pattern*/
	private CalendarUtils() {}
	
	public static CalendarUtils getCalendarUtils() {
		if (calendarUtilsInstance == null) {
			calendarUtilsInstance = new CalendarUtils();
		}
		return calendarUtilsInstance;
	}
	
	public String getCalendarAccessUrl(String nextUrl)
	    throws IOException{
	      
	      String requestUrl = AuthSubUtil.getRequestUrl(nextUrl,
	          DEFAULT_FULL_FEED_REQUEST_URL, false, true);
	      
	      return requestUrl;
	}

	/** Stores token for the currently logged-in user in the datastore */
	public void setTokenFromReply(String reply){
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

	public Set<URL> getCalendarURLs() throws IOException, TokenException {		
		Set<URL> urls = new HashSet<URL>();

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
			//e.printStackTrace();
			throw new TokenException(); 
		} catch (MalformedURLException e1) {
			assert false;
		} 

		CalendarEntry calendarEntry;
		Link eventFeedLink;
		URL eventFeedUrl = null;

        for (int i = 0; i < calendarResultFeed.getEntries().size(); i++) {
          calendarEntry = calendarResultFeed.getEntries().get(i);
	  	  eventFeedLink
	  	  	= calendarEntry.getLink( "http://schemas.google.com/gCal/2005#eventFeed", null);
		  
	  	  try {
			eventFeedUrl = new URL(eventFeedLink.getHref());
		  } catch (MalformedURLException e) {
			assert false;
		  }
		  
		  urls.add(eventFeedUrl);
        }

		return urls;
	}
}