package com.appspot.iclifeplanning;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.analyser.Analyzer;
import com.appspot.analyser.DeleteSuggestion;
import com.appspot.analyser.IEvent;
import com.appspot.analyser.Suggestion;
import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.appspot.iclifeplanning.events.Event;
import com.appspot.iclifeplanning.events.EventStore;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

/**
 * Suggestion servlet. responsible for managing the "optimise button".
 * Initialises the EventStore and runs the analyser to create suggestions.
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 * 
 */
@SuppressWarnings("serial")
public class SuggestionServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		EventStore eventStore = EventStore.getInstance();
		eventStore.initizalize();
		Collection<Event> events = eventStore.getEvents();
		// ------------------- Dummy data
		// Analyzer analyser = new Analyzer();
		List<Suggestion> suggestions = new ArrayList();// analyser.getSuggestions(events, CalendarUtils.getCurrentUserId());
		Suggestion sug = new DeleteSuggestion((IEvent)events.toArray()[0]);
		suggestions.add(sug);
		// ------------------- Dummy data
		JSONArray suggestionArray = new JSONArray();
		for (Suggestion s : suggestions) {
			suggestionArray.put(suggestionToJSONOBject(s));
		}
		
		response.getWriter().print(suggestionArray);
	}

	private JSONObject suggestionToJSONOBject(Suggestion s) {
		JSONObject suggestionObject = new JSONObject();
		try {
			suggestionObject.put("title", s.getTitle());
			suggestionObject.put("description", s.getDescription());
			suggestionObject.put("repeating", "");
			
			SimpleDateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
			suggestionObject.put("startDateTime", date.format(s.getStartDate().getTime()));
			suggestionObject.put("endDateTime", date.format(s.getEndDate()));
			
			List<String> spheres = new ArrayList<String>();
			for (SphereName sphere : s.getSpheres().keySet()) {
				if (s.getSpheres().get(sphere) > 0) {
					spheres.add(sphere.name());
				}
			}
			suggestionObject.put("spheres", spheres);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
