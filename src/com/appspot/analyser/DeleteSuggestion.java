package com.appspot.analyser;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.ServiceException;

public class DeleteSuggestion extends Suggestion {
	
	private CalendarEventEntry event;

	public DeleteSuggestion(BaseCalendarSlot slot) {
		super(slot);
	}

	public DeleteSuggestion(IEvent e) {
		super(e);
		event = e.getCalendarEvent();
	}

	public DeleteSuggestion(String title, String description,
			Calendar startDate, Calendar endDate, double minDuration,
			double maxDuration, boolean isRecurring, boolean canReschedule,
			Map<SphereName, Double> s) {
		super(title, description, startDate, endDate, minDuration, maxDuration,
				isRecurring, canReschedule, s);
	}

	public DeleteSuggestion(String title, String description,
			Calendar startDate, Calendar endDate) {
		super(title, description, startDate, endDate);
	}

	public String getType() {
		return "Remove";
	}

	protected void makePersistentInternal() {
		try {
			CalendarService clientCopy = CalendarUtils.client;
			clientCopy.getRequestFactory().setHeader("If-Match", "*");
			URL deleteUrl = new URL(event.getEditLink().getHref());
			clientCopy.delete(deleteUrl);
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
		return getType() + " " + title 
		    + " which is currently scheduled for "
		    + date.format(new Date(startDate));
	}
}