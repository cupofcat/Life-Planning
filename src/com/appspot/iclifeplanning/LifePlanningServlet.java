package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.servlet.http.*;

import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.events.Event;
import com.appspot.iclifeplanning.events.EventStore;

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
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AuthService authService = AuthService.getAuthServiceInstance();
	private String sessionToken;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

	    // Initialise a client to talk to Google Data API services.
	    this.request = request;
	    this.response = response;
	    this.sessionToken = authService.getToken(request, response);

	    if (sessionToken != null) {
	      authService.registerToken(sessionToken);

	      // Set the session token as a field of the Service object. Since a new
	      // Service object is created with each get call, we don't need to
	      // worry about the anonymous token being used by other users.
	      AuthService.client.setAuthSubToken(sessionToken);	      
	      printCalendars();
	      EventStore.getInstance().initizalize();
	      printEvents(EventStore.getInstance().getEvents());
	    } else {
	    	authService.requestCalendarAccess(request, response);
	    }
	}

	private void printEvents(Set<Event> events) throws IOException {
        response.setContentType("text/html");
		response.getWriter().println("<h3>Your events: </h3>");

        for (Event e : events) {
  		  response.getWriter().println("<ul><li>" + 
  		      e.getDescription().getPlainText() + " List of spheres: " +"</li></ul>");
  		  for (String k : e.getSpheres().keySet())
  			  response.getWriter().print(k + " " + String.valueOf((Integer)e.getSpheres().get(k)));
        }

        String logOutURL = authService.getLogOutURL(request);
	    response.getWriter().println("<a href=\"" + logOutURL + "\">" +
	          "Log Out (will log you out of all google services)</a>");
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
			resultFeed = AuthService.client.getFeed(feedUrl, CalendarFeed.class);
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
