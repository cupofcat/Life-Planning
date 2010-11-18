package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.appengine.api.users.User;

public class InsertSuggestion extends Suggestion {


	public InsertSuggestion(BaseCalendarSlot slot) {
		super(slot);
	}

	public InsertSuggestion(IEvent e) {
		super(e);
	}

	public void makePersistent() {
		//wstawic nowy event w danym czasie
	}

	public InsertSuggestion(String title, String description,
			Calendar startDate, Calendar endDate, double minDuration,
			double maxDuration, boolean isRecurring, boolean canReschedule,
			Map<SphereName, Double> s) {
		super(title, description, startDate, endDate, minDuration, maxDuration,
				isRecurring, canReschedule, s);
		// TODO Auto-generated constructor stub
	}

	public InsertSuggestion(String title, String description,
			Calendar startDate, Calendar endDate) {
		super(title, description, startDate, endDate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getType() {
		return "New event";
	}

}
