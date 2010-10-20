package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.servlet.http.*;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

@SuppressWarnings("serial")
public class LifePlanningServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
        CalendarService myService = new CalendarService("exampleCo-exampleApp-1.0");
        String token = AuthSubUtil.getTokenFromReply(req.getQueryString());
        try {
        	try {
				token = AuthSubUtil.exchangeForSessionToken(token, null);
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
        	myService.setAuthSubToken(token);
			//myService.setUserCredentials("amadurska@gmail.com", "po00lk00");
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
            
        URL feedUrl = null;
		try {
			feedUrl = new URL("http://www.google.com/calendar/feeds/default/allcalendars/full");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        CalendarFeed resultFeed = null;
		try {
			resultFeed = myService.getFeed(feedUrl, CalendarFeed.class);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
            
		resp.getWriter().println("Your calendars: ");
		resp.getWriter().println();
        
        for (int i = 0; i < resultFeed.getEntries().size(); i++) {
          CalendarEntry entry = resultFeed.getEntries().get(i);
  		  resp.getWriter().println(entry.getTitle().getPlainText());
        }
	}
}
