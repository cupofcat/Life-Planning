package com.appspot.analyser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.authentication.CalendarUtils;
import com.google.gdata.util.ServiceException;


public abstract class Suggestion extends BaseCalendarSlot implements IEvent {
	private boolean isRecurring;
	private boolean canReschedule;
	private Map<SphereName, Double> spheres;
	private Pair<Double, Double> durationInterval;
	private List<Suggestion> alternativeSuggestions = new ArrayList<Suggestion>();
	
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

	
	public Suggestion(String title, String description, Calendar startDate,
			Calendar endDate, double minDuration, double maxDuration,
			boolean isRecurring, boolean canReschedule, Map<SphereName, Double> s) {
		this(title, description, startDate, endDate);
		durationInterval = new Pair<Double, Double>(minDuration, maxDuration);
		setRecurring(isRecurring);
		setReschedule(canReschedule);
		setSpheres(s);
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

	public void makePersistent(int alternative) {
		if (alternative == 0) {
			makePersistentInternal();
		} else {
			alternativeSuggestions.get(alternative-1).makePersistent(0);
		}
	}

	public abstract String getType();

	protected abstract void makePersistentInternal();

	public List<Suggestion> getAlternativeSuggestions() {
		return alternativeSuggestions;
	}

	public void setAlternativeSuggetions(List<Suggestion> alternativeSuggestions) {
		this.alternativeSuggestions = alternativeSuggestions;
	}
}
