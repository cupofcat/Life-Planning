package com.appspot.iclifeplanning.authentication;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

/**
 * Class responsible for managing the authentication within the application.
 * Uses Google account/password for token-based authentication. Majority of
 * issues are managed by Google services, mainly  by the UserService class.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
public class AuthService {

	/** AuthService instance for singleton-based design*/
	private static AuthService authServiceInstance = null;

	/**Service used to monitor the currently the users of the application*/
	private static UserService userService = UserServiceFactory.getUserService();

	/**Feed-url giving access to all calendars owned by a give user*/
	public static final  String CALENDAR_FULL_FEED_REQUEST_URL 
	    = "http://www.google.com/calendar/feeds/default/allcalendars/full";

	/**Countructor for singleton pattern*/
	private AuthService() {}
	
	public static AuthService getAuthServiceInstance() {
		if (authServiceInstance == null) authServiceInstance = new AuthService();
		return authServiceInstance;
	}
	
	public static void requestCalendarAccess(HttpServletRequest request, HttpServletResponse response)
	    throws IOException{
	      // If no session token is set, allow users to authorize this sample app
	      // to fetch personal Google Data feeds by directing them to an
	      // authorization page.
	      // Generate AuthSub URL
	      String nextUrl = request.getRequestURL().toString();
	      String requestUrl = AuthSubUtil.getRequestUrl(nextUrl,
	          CALENDAR_FULL_FEED_REQUEST_URL, false, true);

	      // Write AuthSub URL to response
	      response.setContentType("text/html");
	      response.getWriter().print("<h3>A Google Data session token could not " +
	          "be found for your account.</h3>");
	      response.getWriter().print("<p>In order to see your data, you must " +
	          "first authorize access to your personal feeds. Start this " +
	        "process by choosing a service from the list below:</p>");
	      response.getWriter().print("<ul><li><a href=\"" + requestUrl + "\">" +
	          "Google Calendar</a></li></ul>");
	}

	/**
	 * Retrieves a token enabling acces to Google calendar for the currently logged-in user.
	 * You might think this method is not working well or is sub-optimal. You ARE wrong.
	 * It's fine. Trust me.
	 */
	public static String getToken(HttpServletRequest request, HttpServletResponse response) 
	    throws IOException{
		
		String sessionToken = null;
		// Check if the user is logged-in to the application and whether his token is already
		// in the datastore.
	    if (userService.isUserLoggedIn()) {
	        sessionToken = TokenStore.getToken(userService.getCurrentUser().getUserId());
        } else {
        	response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
        }
	    
	    try {
	      String authToken = AuthSubUtil.getTokenFromReply(
	          request.getQueryString());

	      // Upgrade the single-use token to a multi-use session token.
	      sessionToken = AuthSubUtil.exchangeForSessionToken(authToken, null);
	    } catch (AuthenticationException e) {
	    	System.out.println("Authentication exception");
	    } catch (GeneralSecurityException e) {
	    	System.out.println("GeneralSecurityException");
	    } catch (NullPointerException e) {
	    	System.out.println("NullPointerException");
	    }

	    return sessionToken;
	}

	/** Stores token for the currently logged-in user in the datastore */
	public void registerToken(String token){
		 if (userService.isUserLoggedIn()) {
	          User user = userService.getCurrentUser();
	          System.out.println("Saving token");
	          TokenStore.addToken(user.getUserId(), token);
	      }
	}

	public String getLogOutURL(HttpServletRequest request) {
		return userService.createLogoutURL(request.getRequestURI());
	}
}
