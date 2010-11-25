package com.appspot.iclifeplanning;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.analyser.*;
import com.appspot.datastore.*;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.events.Event;
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
		Suggestion beginning = new RescheduleSuggestion("Begin", null, new GregorianCalendar(2000, 3, 3, 0, 0, 0),new GregorianCalendar(2000, 3, 3, 0, 0, 0) );
		Suggestion end = new RescheduleSuggestion("End", null, new GregorianCalendar(2000, 3, 3, 23, 59, 59),new GregorianCalendar(2000, 3, 3, 23, 59, 59) );
		Suggestion s = new RescheduleSuggestion("Event1", null, new GregorianCalendar(2000, 3, 3, 13, 0, 0),new GregorianCalendar(2000, 3, 3, 14, 40, 0) );
		s.setSpheres(generateSpheres(new double[]{0.6, 0.4}));
		s.setDeurationInterval(30, 120);
		Suggestion s2 = new RescheduleSuggestion("Event2", null, new GregorianCalendar(2000, 3, 3, 15, 00, 0),new GregorianCalendar(2000, 3, 3, 15, 30, 0) );
		s2.setSpheres(generateSpheres(new double[]{1.0}));
		s2.setDeurationInterval(0, 80);
		Suggestion s3 = new RescheduleSuggestion("Event3", null, new GregorianCalendar(2000, 3, 3, 16, 30, 0),new GregorianCalendar(2000, 3, 3, 16, 35, 0) );
		s3.setSpheres(generateSpheres(new double[]{0.0, 1.0}));
		s3.setDeurationInterval(0, 20);
		List<Suggestion> list = new LinkedList<Suggestion>();
		list.add(s);
		//list.add(end);
		//list.add(beginning);
		list.add(s2);
		list.add(s3);
		
		new Analyzer().getSuggestions(list, "", generateSpheres(new double[]{0.6,0.4}), true);
//		HashMap<SphereName, Double> m = generateSpheres(new double[]{0.5,0.3});
//		UserProfile p = new UserProfile("msb08", "Macj", "obr", "obr@op.pl",m, true );
//		p.makePersistent();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Collection<UserProfile> users = (Collection<UserProfile>) pm.newQuery("SELECT FROM " + UserProfile.class.getName()).execute();
		for(UserProfile user : users)
			System.out.println(user.getSpherePreferences().get(SphereName.HEALTH));
		Proposal p2 = new Proposal("dsa", "dfsa", null, null);
		p2.setDurationInterval(new Pair<Double, Double>(12.0, 45.0));
		p2.makePersistent();
	}
	
	private HashMap<SphereName, Double> generateSpheres(double[] values){
		SphereName[] names = SphereName.values();
		HashMap<SphereName, Double> res = new HashMap<SphereName, Double>();
		for(int i = 0; i < names.length; i++){
			if(i < values.length)
				res.put(names[i], values[i]);
			else
				res.put(names[i], 0.0 );
		}
		return res;
	}

	private void initializeTimes(Map<SphereName, Double> times,
			Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}

	private void printEvents(Set<Event> events) throws IOException {
		response.setContentType("text/html");
		response.getWriter().println("<h3>Your events: </h3>");

		for (Event e : events) {
			response.getWriter().println(
					"<ul><li>" + e.getDescription() + " List of spheres: "
							+ "</li></ul>");
			for (SphereName k : e.getSpheres().keySet())
				response.getWriter().print(
						k.toString()
								+ " "
								+ String.valueOf(e.getSpheres().get(
										k.toString())));
		}

		String logOutURL = authService.getLogOutURL(request);
		response
				.getWriter()
				.println(
						"<a href=\""
								+ logOutURL
								+ "\">"
								+ "Log Out (will log you out of all google services)</a>");
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
			resultFeed = AuthService.client
					.getFeed(feedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			authService.revokeToken();
			response.sendRedirect(request.getRequestURI());
			return;
		}

		response.setContentType("text/html");
		response.getWriter().println("<h3>Your calendars: </h3>");

		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEntry entry = resultFeed.getEntries().get(i);
			response.getWriter()
					.println(
							"<ul><li>" + entry.getTitle().getPlainText()
									+ "</li></ul>");
		}

		String logOutURL = authService.getLogOutURL(request);
		response
				.getWriter()
				.println(
						"<a href=\""
								+ logOutURL
								+ "\">"
								+ "Log Out (will log you out of all google services)</a>");
	}
}
