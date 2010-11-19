package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.appengine.api.users.User;


public abstract class Suggestion extends BaseCalendarSlot implements IEvent {
	private boolean isRecurring;
	private boolean canReschedule;
	private double rating;
	private Map<SphereName, Double> spheres;
	private Pair<Double, Double> durationInterval;
	
	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate){
		super(title, description, startDate, endDate);
	}
	
	public Suggestion(BaseCalendarSlot slot){
		super(slot.getTitle(), slot.getDescription(), slot.getStartDate(), slot.getEndDate());
	}
	
	public Suggestion(IEvent e){
		this(e.getTitle(), e.getDescription(), e.getStartDate(), e.getEndDate(), 
				e.getDurationInterval().getFirst(), e.getDurationInterval().getSecond(),
				e.isRecurring(), e.canReschedule(), e.getSpheres());
	}
	
	public Suggestion(IEvent e, double rating){
		this(e.getTitle(), e.getDescription(), e.getStartDate(), e.getEndDate(), 
				e.getDurationInterval().getFirst(), e.getDurationInterval().getSecond(),
				e.isRecurring(), e.canReschedule(), e.getSpheres(), rating);
	}
	
	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate, double minDuration, double maxDuration,
			boolean isRecurring, boolean canReschedule, Map<SphereName, Double> s) {
		this(title, description, startDate, endDate);
		durationInterval = new Pair<Double, Double>(minDuration, maxDuration);
		setRecurring(isRecurring);
		setReschedule(canReschedule);
		setSpheres(s);
	}
	
	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate, double minDuration, double maxDuration,
			boolean isRecurring, boolean canReschedule, Map<SphereName, Double> s, double rating) {
		this(title, description, startDate, endDate);
		durationInterval = new Pair<Double, Double>(minDuration, maxDuration);
		setRecurring(isRecurring);
		setReschedule(canReschedule);
		setSpheres(s);
		setRating(rating);
	}

	public boolean isRecurring() {
		return isRecurring;
	}
	
	public void setRecurring(boolean r){
		isRecurring = r;
	}

	public boolean canReschedule() {
		return canReschedule;
	}
	
	public void setReschedule(boolean can){
		canReschedule = can;
	}

	public Map<SphereName, Double> getSpheres() {
		return spheres;
	}
	
	public void setSpheres(Map<SphereName,Double> spheres){
		this.spheres = spheres;
	}

	public Pair<Double, Double> getDurationInterval() {
		return durationInterval;
	}
	
	public void setDeurationInterval(double min, double max){
		durationInterval = new Pair<Double, Double>(min, max);
	}
	
	public abstract String getType();
	
	public double getRating() {
		return rating;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
}
