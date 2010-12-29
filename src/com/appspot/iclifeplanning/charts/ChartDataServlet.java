package com.appspot.iclifeplanning.charts;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.analyser.Analyser;
import com.appspot.analyser.IEvent;
import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.TokenStore;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfile;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfileStore;
import com.appspot.iclifeplanning.events.Event;
import com.appspot.iclifeplanning.events.EventStore;

/**
 * Notification servlet. Responsible for checking for new events in
 * the users calendar and sending notifications about the changes.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 *
 */
@SuppressWarnings("serial")
public class ChartDataServlet extends HttpServlet {
	private static long startTime = 0;
	private static long endTime = 0;
	private static int weekNumber = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery("SELECT userID FROM " + UserProfile.class.getName());
		List<String> userIDs = (List<String>) query.execute();
		HashMap<SphereName, Double> currentDesiredBalance;
		UserProfile userProfile;
		String token;

		for (String userID : userIDs) {
			System.out.println("Analyzing user: " + userID);
			userProfile = UserProfileStore.getUserProfile(userID);
			currentDesiredBalance = userProfile.getSpherePreferences();
			endTime = Calendar.getInstance().getTimeInMillis();
			startTime = endTime - (long)7*24*60*60*1000;
			//startTime = Math.max(startTime, userProfile.getJoinTime());
			token = TokenStore.getToken(userID);
			CalendarUtils.client.setAuthSubToken(token);
			List<Event> events = EventStore.getInstance().getEventsFromTimeRange(startTime, endTime);
			if (events != null) {
				System.out.println("Saving data");
				HashMap<SphereName, Double> sphereResults = Analyser.analyseEvents(events, currentDesiredBalance);
				WeeklyDataProfile profile = new WeeklyDataProfile(userID, weekNumber, sphereResults, currentDesiredBalance);
				WeeklyDataProfileStore.addWeeklyDataProfile(profile);
			}
		}
		weekNumber++;
	}

	/*
	private HashMap<SphereName, Double> analyseEvents(String userID,
			    List<Event> events, Map<SphereName, Double> currentDesiredBalance) {
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, currentDesiredBalance.keySet());
		HashMap<SphereName, Double> result = new HashMap<SphereName, Double>();
		int sum = 0;

		for (IEvent event : events) {
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereResults = event.getSpheres();
			Set<SphereName> keys = sphereResults.keySet();
			for (SphereName key : keys) {
				double time = Math.round(sphereResults.get(key) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			result.put(key, times.get(key) / sum);
		}

		return result;
	}*/

	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}
}
