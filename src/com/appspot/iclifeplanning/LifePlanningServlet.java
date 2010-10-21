package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

@SuppressWarnings("serial")
public class LifePlanningServlet extends HttpServlet {

	private UserService userService = UserServiceFactory.getUserService();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	    // Initialize a client to talk to Google Data API services.
	    CalendarService client = new CalendarService("google-feedfetcher-v1");

	    String sessionToken = null;

	    if (userService.isUserLoggedIn()) {
	        User user = userService.getCurrentUser();
	        sessionToken = TokenStore.getToken(user.getUserId());
        }

	    try {
	      // Find the AuthSub token and upgrade it to a session token.
	      String authToken = AuthSubUtil.getTokenFromReply(
	          request.getQueryString());

	      // Upgrade the single-use token to a multi-use session token.
	      sessionToken = AuthSubUtil.exchangeForSessionToken(authToken, null);
	    } catch (AuthenticationException e) {
	      // Handle
	    } catch (GeneralSecurityException e) {
	      // Handle
	    } catch (NullPointerException e) {
	      // Ignore
	    }

	    if (sessionToken != null) {
	      if (userService.isUserLoggedIn()) {
	          User user = userService.getCurrentUser();
	          TokenStore.addToken(user.getUserId(), sessionToken);
	      }
	      // Set the session token as a field of the Service object. Since a new
	      // Service object is created with each get call, we don't need to
	      // worry about the anonymous token being used by other users.
	      client.setAuthSubToken(sessionToken);
	      
	        URL feedUrl = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
			CalendarFeed resultFeed = null;
			try {
				resultFeed = client.getFeed(feedUrl, CalendarFeed.class);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    response.setContentType("text/plain");
			response.getWriter().println("Your calendars: ");
			response.getWriter().println();
	   
	        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	          CalendarEntry entry = resultFeed.getEntries().get(i);
	  		  response.getWriter().println(entry.getTitle().getPlainText());
	        }

	    } else {
	      // If no session token is set, allow users to authorize this sample app
	      // to fetch personal Google Data feeds by directing them to an
	      // authorization page.

	      // Generate AuthSub URL
	      String nextUrl = request.getRequestURL().toString();
	      String requestUrl = AuthSubUtil.getRequestUrl(nextUrl,
	          "http://www.google.com/calendar/feeds/default/allcalendars/full", false, true);

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
	}
}
