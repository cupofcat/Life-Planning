package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.*;

import com.appspot.iclifeplanning.authentication.AuthService;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.ServiceException;

/**
 * Main servlet for now. No particular purpose.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
@SuppressWarnings("serial")
public class LifePlanningServlet extends HttpServlet {
	private CalendarService client;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AuthService authService = AuthService.getAuthServiceInstance();
	private String sessionToken;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

	    // Initialize a client to talk to Google Data API services.
	    client = new CalendarService("ic-lifeplanning-v1");
	    this.request = request;
	    this.response = response;
	    this.sessionToken = authService.getToken(request, response);

	    if (sessionToken != null) {
	      AuthService.getAuthServiceInstance().registerToken(sessionToken);

	      // Set the session token as a field of the Service object. Since a new
	      // Service object is created with each get call, we don't need to
	      // worry about the anonymous token being used by other users.
	      client.setAuthSubToken(sessionToken);
	      
	      printCalendars();
	    } else {
	    	authService.requestCalendarAccess(request, response);
	    }
	}

	/**
	 * Print a list of Google calendars owned by a given user.
	 *
	 * @throws IOException
	 */
	private void printCalendars() throws IOException {
        URL feedUrl = new URL(AuthService.CALENDAR_FULL_FEED_REQUEST_URL);
		CalendarFeed resultFeed = null;

		// Connect to Google Calendar and gather data
		try {
			resultFeed = client.getFeed(feedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			authService.revokeToken();
			response.sendRedirect(request.getRequestURI());
			return;
		}

        response.setContentType("text/html");
		response.getWriter().println("<h3>Your calendars: </h3>");

        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
          CalendarEntry entry = resultFeed.getEntries().get(i);
  		  response.getWriter().println("<ul><li>" + entry.getTitle().getPlainText() + "</li></ul>");
        }

        String logOutURL = authService.getLogOutURL(request);
	    response.getWriter().println("<a href=\"" + logOutURL + "\">" +
	          "Log Out (will log you out of all google services)</a>");
	}
}
