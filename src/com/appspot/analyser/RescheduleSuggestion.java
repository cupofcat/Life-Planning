package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.appengine.api.users.User;

public class RescheduleSuggestion extends Suggestion {

	private Pair<Calendar, Calendar> oldDates;
	

	
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

	public RescheduleSuggestion(IEvent e, Calendar oldStart, Calendar oldEnd){
		super(e);
		setOldDates(oldStart, oldEnd);
	}
	
	public void setOldDates(Calendar oldStart, Calendar oldEnd){
		oldDates = new Pair<Calendar, Calendar>(oldStart, oldEnd);
	}

	public void makePersistent() {
		//wyjebac stary i wstawic nowy
		// lub alter running time
	}

	@Override
	public String getType() {
		return "Rescedule";
	}

}
