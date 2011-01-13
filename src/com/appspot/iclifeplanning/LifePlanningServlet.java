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
import com.google.gdata.data.PlainTextConstruct;
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

	public synchronized void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		Suggestion beginning = new RescheduleSuggestion("Begin", null, new GregorianCalendar(2000, 3, 3, 0, 0, 0),new GregorianCalendar(2000, 3, 3, 0, 0, 0) );
		beginning.setDeurationInterval(0, 0);
		Suggestion end = new RescheduleSuggestion ("End", null, new GregorianCalendar(2000, 3, 6, 23, 59, 59),new GregorianCalendar(2000, 3, 6, 23, 59, 59) );
		end.setDeurationInterval(0, 0);
		
		Suggestion s1 = new RescheduleSuggestion("Zdrowie1", null, new GregorianCalendar(2000, 3, 3, 9, 0, 0),new GregorianCalendar(2000, 3, 3, 10, 0, 0) );
		s1.setSpheres(generateSpheres(new double[]{1.0}));
		s1.setDeurationInterval(0, 60);
		s1.setReschedule(true);
		
		Suggestion s2 = new RescheduleSuggestion("Zdrowie2", null, new GregorianCalendar(2000, 3, 3, 10, 30, 0),new GregorianCalendar(2000, 3, 3, 11, 30, 0) );
		s2.setSpheres(generateSpheres(new double[]{1.0}));
		s2.setDeurationInterval(0, 60);
		s2.setReschedule(true);
		
		Suggestion s3 = new RescheduleSuggestion("Work", null, new GregorianCalendar(2000, 3, 4, 1, 30, 0),new GregorianCalendar(2000, 3, 4, 2, 30, 0) );
		s3.setSpheres(generateSpheres(new double[]{0.0, 1.0}));
		s3.setDeurationInterval(0, 120);
		s3.setReschedule(true);
		
		Suggestion s4 = new RescheduleSuggestion("Family", null, new GregorianCalendar(2000, 3, 6, 10, 25, 0),new GregorianCalendar(2000, 3, 6, 11, 25, 0) );
		s4.setSpheres(generateSpheres(new double[]{0.0, 0.0, 1.0}));
		s4.setDeurationInterval(0, 60);
		s4.setReschedule(false);
		
		Suggestion s5 = new RescheduleSuggestion("Recreation", null, new GregorianCalendar(2000, 3, 5, 12, 00, 0),new GregorianCalendar(2000, 3, 5, 13, 0, 0) );
		s5.setSpheres(generateSpheres(new double[]{0.0, 0.0, 0.0, 1.0}));
		s5.setDeurationInterval(0, 60);
		s5.setReschedule(false);
		
		
		Suggestion s6 = new RescheduleSuggestion("Recreation2", null, new GregorianCalendar(2000, 3, 5, 23, 30, 0),new GregorianCalendar(2000, 3, 6, 0, 30, 0) );
		s6.setSpheres(generateSpheres(new double[]{0.0, 0.0, 0.0, 1.0}));
		s6.setDeurationInterval(0, 60);
		s6.setReschedule(false);

		
		List<Suggestion> list = new LinkedList<Suggestion>();
		//list.add(s);
		list.add(end);
		list.add(beginning);
		list.add(s1);
		list.add(s4);
		list.add(s3);
		list.add(s2);
		list.add(s5);
		//list.add(s6);
		HashMap<SphereName, Double> m = generateSpheres(new double[]{0.7,0.3});
		//    Collection<UserProfile> users = (Collection<UserProfile>) PMF.get().getPersistenceManager().newQuery("select from " + UserProfile.class.getName()).execute();
		//   printProfiles(users);
		////////////

		
		PersistenceManager pmf = PMF.get().getPersistenceManager();
		//for(SphereName sphere : SphereName.values())
		//pmf.deletePersistentAll((Collection<Proposal>) pmf.newQuery("select from " + Proposal.class.getName() + " where majorSphere =='" + sphere+ "'").execute());

		//Proposal a = new Proposal(s);
		//a.setSpheres(m);
		//a.makePersistent();

		//PersistenceManager pmf = PMF.get().getPersistenceManager();

		Collection<Proposal> spheres = (Collection<Proposal>) pmf.newQuery("select from " + Proposal.class.getName()).execute();//+ " where majorSphere =='" + SphereName.WORK+"'").execute();
		//pmf.deletePersistentAll(spheres);
		//Utilities.printEvents(spheres);
		//UserProfile profile = new UserProfile("rysio", "kaletnik", "ryszardKaleta@op.lp", generateSpheres(new double[]{0.5,0.5,}), false,310);
		//profile.makePersistent();
		new Analyser().getSuggestions(list, "rysio");
		//		PersistenceManager pm = PMF.get().getPersistenceManager();
		//		Collection<UserProfile> users = (Collection<UserProfile>) pm.newQuery("SELECT FROM " + UserProfile.class.getName()).execute();
		//		printProfiles(users);
	}

	private void printProfiles(Collection<UserProfile> profiles){
		for(UserProfile profile : profiles)
			System.out.println(profile);
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