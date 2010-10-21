package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.MalformedURLException;
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
	private CalendarService myService = new CalendarService("exampleCo-exampleApp-1.0");
	private String sessionToken = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		User user = ensureUserLoggedIn(req, resp);
		boolean success = true;
		if (user != null) {
			sessionToken = TokenStore.getToken(user.getUserId());
		}
		if (sessionToken != null) {
			success = displayCalendarList(req, resp);
		}
		if (success == false || sessionToken == null) {
			sessionToken = getToken(req, resp);
			if (sessionToken != null) {
	        	TokenStore.addToken(user.getUserId(), sessionToken);
			}
			displayCalendarList(req, resp);
		}	
	}

	private String getToken(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String token = null;
		if (req.getQueryString() != null) {
	        token = AuthSubUtil.getTokenFromReply(req.getQueryString());
	        try {
				token = AuthSubUtil.exchangeForSessionToken(token, null);
	        	myService.setAuthSubToken(token);
			} catch (GeneralSecurityException e) {
				resp.getWriter().println("GeneralSecurityException!");
				e.printStackTrace();
			} catch (AuthenticationException e) {
				resp.getWriter().println("AuthenticationException!");
				e.printStackTrace();
			}	
		}
		
		if (token == null) {
			String next = req.getRequestURL().toString();
			String requestUrl =
				  AuthSubUtil.getRequestUrl(next,
				      "http://www.google.com/calendar/feeds/default/allcalendars/full",
				      false, true);
	
			String suggestAuthorization = "<p>LifePlanning needs access to your" +
		        "Google Calendar account to read your Calendar feed. Please, " +
				"<a href=\"" + requestUrl + "\">authorize</a> LifePlanning to access your account.</p>";

			resp.setContentType("text/html");    
			resp.getWriter().println(suggestAuthorization);
		}
		return token;
	}

	private boolean displayCalendarList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        URL feedUrl = null;
        CalendarFeed resultFeed = null;
		try {
			feedUrl = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
			resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);

		    resp.setContentType("text/plain");
			resp.getWriter().println("Your calendars: ");
			resp.getWriter().println();
	   
	        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
	          CalendarEntry entry = resultFeed.getEntries().get(i);
	  		  resp.getWriter().println(entry.getTitle().getPlainText());
	        }	
		} catch (MalformedURLException e) {
			resp.getWriter().println("MalformedURLException!");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			resp.getWriter().println("IOException!");
			e.printStackTrace();
			return false;
		} catch (ServiceException e) {
			return false;
		}	
		return true;
	}

	private User ensureUserLoggedIn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user == null) {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
        return user;
	}
}
