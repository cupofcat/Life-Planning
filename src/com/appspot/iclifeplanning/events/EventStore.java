package com.appspot.iclifeplanning.events;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.data.Link;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.ServiceException;

public class EventStore {

	private static EventStore eventStore = null;
	private Set<Event> allEvents = new HashSet<Event>();
	private Set<URL> urls = new HashSet<URL>();
	private static final Logger log = Logger.getLogger("EventStore");
	
	private EventStore() {}
	
	public static EventStore getInstance() {
		if (eventStore == null)
				eventStore = new EventStore();
		return eventStore;
	}
	
	public void initizalize() throws IOException {

		allEvents = new HashSet<Event>();

        URL calendarFeedUrl = new URL(AuthService.CALENDAR_FULL_FEED_REQUEST_URL);
		CalendarFeed calendarResultFeed = null;

		// Connect to Google Calendar and gather data
		try {
			calendarResultFeed = CalendarUtils.client.getFeed(calendarFeedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			System.out.println("BOOOO");
			return;
		}

		CalendarEntry calendarEntry;
		Link eventFeedLink;
		URL eventFeedUrl;
		CalendarQuery query;
		CalendarEventFeed eventResultFeed;
		Event event;
		List<CalendarEventEntry> allCalendarEvents;

        for (int i = 0; i < calendarResultFeed.getEntries().size(); i++) {
          calendarEntry = calendarResultFeed.getEntries().get(i);
	  	  eventFeedLink = calendarEntry.getLink( "http://schemas.google.com/gCal/2005#eventFeed", null);
		  eventFeedUrl = new URL(eventFeedLink.getHref());
		  query = new CalendarQuery(eventFeedUrl);
		  query.setStringCustomParameter("singleevents", "true");

  		  try {
			  eventResultFeed = CalendarUtils.client.getFeed(query, CalendarEventFeed.class);
		  } catch (ServiceException e) {
			  return;
		  }

		  allCalendarEvents = eventResultFeed.getEntries();
		  for (int j = 0; j < allCalendarEvents.size(); j++) {
			  event = new Event(allCalendarEvents.get(j));
			  event.setCalendarURL(eventFeedUrl);
			  allEvents.add(event);
		  }
        }
        System.out.println("Got all events");
<<<<<<< HEAD
=======
	}

	public Set<URL> getCalendarURLs() {		
		return urls;
>>>>>>> Dirty repair + dummy suggestions
	}

	public Set<Event> getEvents() {
		return allEvents;	
	}
}
