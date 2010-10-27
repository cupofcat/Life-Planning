package com.appspot.iclifeplanning.events;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.appspot.iclifeplanning.LifePlanningServlet;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.ServiceException;

public class EventStore {

	private static EventStore eventStore = null;
	private static CalendarEventFeed resultFeed = null;
	private Set<Event> allEvents;
	private static final Logger log = Logger.getLogger("EventStore");
	
	private EventStore() {}
	
	public static EventStore getInstance() {
		if (eventStore == null)
				eventStore = new EventStore();
		return eventStore;
	}
	
	public void initizalize() throws IOException {
		URL feedUrl = new URL(AuthService.EVENT_FULL_FEED_REQUEST_URL);

		allEvents = new HashSet<Event>();
		
		CalendarService client = LifePlanningServlet.client;
		//AuthService authService = AuthService.getAuthServiceInstance();
		// Connect to Google Calendar and gather data
		try {
			resultFeed = client.getFeed(feedUrl, CalendarEventFeed.class);
		} catch (ServiceException e) {
			log.severe("Service Exception! " + e.getMessage());
			//log.severe("Service Exception! " + e.getStackTrace()[0]);//This should never happen.
			return;
		}

		log.severe("Analyzing feeds");
		Event event = null;
		List<CalendarEventEntry> allCalendarEvents = resultFeed.getEntries();
		log.severe("got Entries");
		for (int i = 0; i < allCalendarEvents.size(); i++) {
			event = new Event(allCalendarEvents.get(i));
			log.severe("adding event!");
			allEvents.add(event);
		}
	}

	public Set<Event> getEvents() {
		return allEvents;	
	}
}
