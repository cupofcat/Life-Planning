package com.appspot.analyser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;

public class InsertSuggestion extends Suggestion {

	public InsertSuggestion(BaseCalendarSlot slot) {
		super(slot);
	}

	public InsertSuggestion(IEvent e) {
		super(e);
	}

	public InsertSuggestion(String title, String description,
			Calendar startDate, Calendar endDate, double minDuration,
			double maxDuration, boolean isRecurring, boolean canReschedule,
			Map<SphereName, Double> s) {
		super(title, description, startDate, endDate, minDuration, maxDuration,
				isRecurring, canReschedule, s);
	}

	public InsertSuggestion(String title, String description,
			Calendar startDate, Calendar endDate) {
		super(title, description, startDate, endDate);
	}

	public String getType() {
		return "NewEvent";
	}

	protected void makePersistentInternal() {
		CalendarEventEntry newEntry = new CalendarEventEntry();

		newEntry.setTitle(new PlainTextConstruct(title));
		newEntry.setContent(new PlainTextConstruct(description));

		DateTime startTime = new DateTime(startDate);
		DateTime endTime = new DateTime(endDate);
		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		newEntry.addTime(eventTimes);

		URL postUrl = null;
		try {
			postUrl = new URL(CalendarUtils.DEFAULT_FULL_FEED_REQUEST_URL);
			CalendarUtils.client.insert(postUrl, newEntry);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public CalendarEventEntry getCalendarEvent() {
		return null;
	}

	public String toString() {
		SimpleDateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
		return "Schedule " + title 
		    + " for "
		    + date.format(new Date(startDate));
	}
}
