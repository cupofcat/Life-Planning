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
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.ServiceException;

public class RescheduleSuggestion extends Suggestion {

	private Pair<Long, Long> newDates;
	private CalendarEventEntry event;

	public RescheduleSuggestion(BaseCalendarSlot slot) {
		super(slot);
	}

	public RescheduleSuggestion(String title, String description,
			Calendar startDate, Calendar endDate, double minDuration,
			double maxDuration, boolean isRecurring, boolean canReschedule,
			Map<SphereName, Double> s) {
		super(title, description, startDate, endDate, minDuration, maxDuration,
				isRecurring, canReschedule, s);
	}

	public RescheduleSuggestion(String title, String description,
			Calendar startDate, Calendar endDate) {
		super(title, description, startDate, endDate);
	}

	public RescheduleSuggestion(IEvent e, Calendar newStart, Calendar newEnd){
		super(e);
		setNewDates(newStart.getTimeInMillis(), newEnd.getTimeInMillis());
		event = e.getCalendarEvent();
	}

	public RescheduleSuggestion(IEvent e) {
		super(e);
		event = e.getCalendarEvent();
		setNewDates(e.getStartDate().getTimeInMillis(), e.getEndDate().getTimeInMillis()+ (long)60*60*1000);
	}

	public void setNewDates(long newStart, long newEnd){
		newDates = new Pair<Long, Long>(newStart, newEnd);
	}

	public Pair<Long, Long> getNewDates(){
		return newDates;
	}

	public String getType() {
		return "Reschedule";
	}

	protected void makePersistentInternal() {
		event.getTimes().get(0).setStartTime(new DateTime(newDates.getFirst()));
		event.getTimes().get(0).setEndTime(new DateTime(newDates.getSecond()));
		URL editUrl;
		try {
			editUrl = new URL(event.getEditLink().getHref());
			CalendarUtils.client.update(editUrl, event);
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
}
