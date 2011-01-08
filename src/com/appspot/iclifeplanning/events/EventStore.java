package com.appspot.iclifeplanning.events;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.authentication.AuthService;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.google.apphosting.api.UserServicePb.UserService;
import com.google.gdata.client.Query.CustomParameter;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.ServiceException;

public class EventStore {

	private static EventStore eventStore = null;
	private List<Event> allEvents = new ArrayList<Event>();
	private static final Logger log = Logger.getLogger("EventStore");
	
	private EventStore() {}
	
	public static EventStore getInstance() {
		if (eventStore == null)
				eventStore = new EventStore();
		return eventStore;
	}
	
	public void initizalize() throws IOException {
	    String userID = CalendarUtils.getCurrentUserId();
	    UserProfile profile = UserProfileStore.getUserProfile(userID);
	    long from = profile.getStartOptimizing();
	    long to = profile.getFinishOptimizing();
		allEvents = getEventsFromTimeRange(from, to);
	}

	public List<Event> getEvents() {
		return allEvents;	
	}

	public List<Event> getEventsFromTimeRange(long startTime, long endTime) throws IOException{
		List<Event> events = new ArrayList<Event>();

        URL calendarFeedUrl = new URL(AuthService.CALENDAR_FULL_FEED_REQUEST_URL);
		CalendarFeed calendarResultFeed = null;

		// Connect to Google Calendar and gather data
		try {
			calendarResultFeed = CalendarUtils.client.getFeed(calendarFeedUrl, CalendarFeed.class);
		} catch (ServiceException e) {
			log.severe("SERVICE EXCEPTION: " + e.getStackTrace());
			return null;
		}

		CalendarEntry calendarEntry;
		Link eventFeedLink;
		URL eventFeedUrl;
		CalendarQuery query;
		CalendarEventFeed eventResultFeed;
		Event event;
		List<CalendarEventEntry> allCalendarEvents;

		// temporary default values. Should really be set by user through UI
		long now = System.currentTimeMillis();
		long future = now + (long)30*24*60*60*1000;//2592000000l; // month in miliseconds
        
		for (int i = 0; i < calendarResultFeed.getEntries().size(); i++) {
          calendarEntry = calendarResultFeed.getEntries().get(i);
	  	  eventFeedLink = calendarEntry.getLink( "http://schemas.google.com/gCal/2005#eventFeed", null);
		  eventFeedUrl = new URL(eventFeedLink.getHref());
		  query = new CalendarQuery(eventFeedUrl);
		  query.addCustomParameter(new CustomParameter("singleevents", "true"));
		  query.addCustomParameter(new CustomParameter("orderby", "starttime"));
		  query.addCustomParameter(new CustomParameter("sortorde", "ascending"));

		  query.setMinimumStartTime(new DateTime(startTime));
		  query.setMaximumStartTime(new DateTime(endTime));

  		  try {
			  eventResultFeed = CalendarUtils.client.getFeed(query, CalendarEventFeed.class);
		  } catch (ServiceException e) {
			  return null;
		  }

		  allCalendarEvents = eventResultFeed.getEntries();
		  for (int j = 0; j < allCalendarEvents.size(); j++) {
			  event = new Event(allCalendarEvents.get(j));
			  events.add(event);
		  }
        }

		Event firstDummyEvent = new Event(now, now);
		Event lastDummyEvent = new Event(future, future);
		events.add(0, firstDummyEvent);
		events.add(lastDummyEvent);
		return events;
	}
}