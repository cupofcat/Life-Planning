package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.appengine.api.users.User;

public class DeleteSuggestion extends Suggestion {

	public DeleteSuggestion(BaseCalendarSlot slot) {
		super(slot);
	}

	public DeleteSuggestion(IEvent e) {
		super(e);
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
		// wywalic event calkowicie

	}

}