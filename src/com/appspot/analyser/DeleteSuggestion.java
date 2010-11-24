package com.appspot.analyser;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
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
 	
	public void makePersistent() {
		try {
			URL deleteUrl = new URL(event.getEditLink().getHref());
			CalendarUtils.client.delete(deleteUrl);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public CalendarEventEntry getCalendarEvent() {
		return null;
	}

}