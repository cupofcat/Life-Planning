package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.appengine.api.users.User;


public abstract class Suggestion extends BaseCalendarSlot implements IEvent {
	private double minDuration;
	private double maxDuration;
	private boolean isRecurring;
	private boolean canReschedule;
	private Map<SphereName, Integer> spheres;
	
	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate, User user, double minDuration, double maxDuration) {
		super(title, description, startDate, endDate, user);
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		isRecurring = false;
		canReschedule = false;
	}

	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate, User user, double minDuration, double maxDuration,
			boolean isRecurring, boolean canReschedule) {
		this(title, description, startDate, endDate, user, minDuration, maxDuration);
		this.isRecurring = isRecurring;
		this.canReschedule = canReschedule;
	}
	
	public double minDuration() {
		return 0;
	}

	public double maxDuration() {
		return 0;
	}

	public boolean isRecurring() {
		return false;
	}

	public boolean canReschedule() {
		return false;
	}

	public Map<SphereName, Integer> getSpheres() {
		return null;
	}


}
