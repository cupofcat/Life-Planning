package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.analyser.IEvent;
import com.appspot.datastore.*;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.events.Event;
import com.appspot.iclifeplanning.events.EventInterface;
import com.appspot.iclifeplanning.events.EventStore;
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

	    //if (sessionToken != null) {
//	      authService.registerToken(sessionToken);
	      PersistenceManager pm = PMF.get().getPersistenceManager();
	      String user = "iclifeplanning";

	      Collection<Object[]> values = (Collection<Object[]>) pm.newQuery("select sphereName ,value from " + SphereChoice.class.getName() + " where userID=='" + user + "'").execute();	      
	      Map<SphereName, Double> choices = new HashMap<SphereName, Double>();
	      for(Object[] list : values){
	    	  choices.put( (SphereName) list[0], (Double) list[1]);
	      }
	      
	      // Set the session token as a field of the Service object. Since a new
	      // Service object is created with each get call, we don't need to
	      // worry about the anonymous token being used by other users.
//	      AuthService.client.setAuthSubToken(sessionToken);	      
	      printCalendars();
	      EventStore.getInstance().initizalize();
	      
	      Analyzer analyzer = new Analyzer();
	      List<SphereInfo> l2 = checkGoals(EventStore.getInstance().getEvents(), choices);
	      for(SphereInfo i : l2)
	    	  response.getWriter().println(i);
	      //printEvents(EventStore.getInstance().getEvents());
	    //} else {
	    	//authService.requestCalendarAccess(request, response);
	    //}
	}
	
	public List<SphereInfo> checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException{	
		
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		response.setContentType("text/html");
		for(IEvent event : events){
			//jebane strefy czasowe........
			int durationInMins = getTimeDifference(event.getStartDate(), event.getEndDate());
			Map<SphereName, Integer> sphereResults = event.getSpheres();
			Set<SphereName> keys = sphereResults.keySet();
			for(SphereName key : keys){
				double time = Math.round(( Double.valueOf(sphereResults.get(key))/100) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for(SphereName key : times.keySet()){
			currentRatios.put(key, times.get(key)/sum);
			response.getWriter().println(currentRatios.get(key));
		}
		List<SphereInfo> result = new LinkedList<SphereInfo>();
		for(SphereName key : times.keySet()){
			SphereInfo info = new SphereInfo(key, currentRatios.get(key), choices.get(key));
			info.setTimeDifference(Math.round(info.getRatioDifference() * sum));
			result.add(info);
		}
		return result;
	}
	
	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys){
		for(SphereName key : keys)
			times.put(key, 0.0);
	}
	
	private int getTimeDifference(Calendar start, Calendar 	end){
		return (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)) * 1440
		       + (end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) * 60
		       + (end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE));
	}
	

	private void printEvents(Set<Event> events) throws IOException {
        response.setContentType("text/html");
		response.getWriter().println("<h3>Your events: </h3>");

        for (Event e : events) {
  		  response.getWriter().println("<ul><li>" + 
  		      e.getDescription() + " List of spheres: " +"</li></ul>");
  		  for (SphereName k : e.getSpheres().keySet())
  			  response.getWriter().print(k.toString() + " " + String.valueOf((Integer)e.getSpheres().get(k.toString())));
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
