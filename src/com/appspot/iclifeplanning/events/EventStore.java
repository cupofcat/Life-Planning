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
import com.google.gdata.data.Link;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.ServiceException;

public class EventStore {

	private static EventStore eventStore = null;
	private Set<Event> allEvents = new HashSet<Event>();
	private static final Logger log = Logger.getLogger("EventStore");
	
	private EventStore() {}
	
	public static EventStore getInstance() {
		if (eventStore == null)
				eventStore = new EventStore();
		return eventStore;
	}
	
	public void initizalize() throws IOException {

		allEvents = new HashSet<Event>();
		
		CalendarService client = LifePlanningServlet.client;
		//AuthService authService = AuthService.getAuthServiceInstance();
		// Connect to Google Calendar and gather data
		
        URL calendarFeedUrl = new URL(AuthService.CALENDAR_FULL_FEED_REQUEST_URL);
		CalendarFeed calendarResultFeed = null;

		// Connect to Google Calendar and gather data
		try {
			calendarResultFeed = client.getFeed(calendarFeedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			return;
		}

        for (int i = 0; i < calendarResultFeed.getEntries().size(); i++) {
          CalendarEntry calendarEntry = calendarResultFeed.getEntries().get(i);
	  	  Link eventFeedLink = calendarEntry.getLink( "http://schemas.google.com/gCal/2005#eventFeed", null);
		  URL eventFeedUrl = new URL(eventFeedLink.getHref());
		  CalendarEventFeed eventResultFeed = null;
  		  try {
			  eventResultFeed = client.getFeed(eventFeedUrl, CalendarEventFeed.class);
		  } catch (ServiceException e) {
			  log.severe("Service Exception! ");
			  return;
		  }

  		  log.severe("Analyzing feeds");
		  Event event = null;
		  List<CalendarEventEntry> allCalendarEvents = eventResultFeed.getEntries();
		  log.severe("got Entries");
		  for (int j = 0; j < allCalendarEvents.size(); j++) {
			  event = new Event(allCalendarEvents.get(j));
			  event.setCalendarTitle(calendarEntry.getTitle().toString());
			  allEvents.add(event);
		  }
        }
	}

	public Set<Event> getEvents() {
		return allEvents;	
	}
}
