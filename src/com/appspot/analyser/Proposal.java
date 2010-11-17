package com.appspot.analyser;

import java.util.Calendar;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.*;

import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereName;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.google.appengine.api.users.User;


@PersistenceCapable
public class Proposal extends BaseCalendarSlot implements IEvent {
	
    @NotPersistent
	private boolean isRecurring;
    @NotPersistent
	private boolean canReschedule;
    @Persistent
	private Map<SphereName, Double> spheres;
    @Persistent
	private Pair<Double, Double> durationInterval;
    @Persistent
	private double multiplier;
    @Persistent
	private boolean disabled;
    @Persistent
	private Pair<Calendar, Calendar> possibleTimeSlot;
    @Persistent
	private Pair<Integer, Integer> possibleAge;
	
	public Proposal(Calendar startDate, Calendar endDate, User user) {
		super(startDate, endDate);
		key = KeyFactory.createKey(Proposal.class.getSimpleName(), title + description);
	}

	public Proposal(String title, String description, Calendar startDate,
			Calendar endDate) {
		super(title, description, startDate, endDate);
	}
	
	public Proposal(String title, String description, Calendar startDate,
			Calendar endDate, double minDuration, double maxDuration,
			boolean isRecurring, boolean canReschedule, Map<SphereName, Double> s) {
		this(title, description, startDate, endDate);
		durationInterval = new Pair<Double, Double>(minDuration, maxDuration);
		setRecurring(isRecurring);
		setReschedule(canReschedule);
		setSpheres(s);
	}
	
	public Proposal(IEvent e){
		this(e.getTitle(), e.getDescription(), e.getStartDate(), e.getEndDate(), 
				e.getDurationInterval().getFirst(), e.getDurationInterval().getSecond(),
				e.isRecurring(), e.canReschedule(), e.getSpheres());
	}
	
	public void setRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	@Override
	public boolean canReschedule() {
		return canReschedule;
	}
	
	public void setReschedule(boolean schedule){
		canReschedule = schedule;
	}

	@Override
	public Pair<Double, Double> getDurationInterval() {
		return this.durationInterval;
	}
	
	public void setDurationInterval(Double min, Double max){
		durationInterval = new Pair<Double, Double>(min, max);
	}

	@Override
	public Map<SphereName, Double> getSpheres() {
		return this.spheres;
	}
	
	public void setSpheres(Map<SphereName, Double> s){
		spheres = s;
	}

	@Override
	public boolean isRecurring() {
		return this.isRecurring;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Pair<Calendar, Calendar> getPossibleTimeSlot() {
		return possibleTimeSlot;
	}

	public void setPossibleTimeSlot(Pair<Calendar, Calendar> possibleTimeSlot) {
		this.possibleTimeSlot = possibleTimeSlot;
	}

	public Pair<Integer, Integer> getPossibleAge() {
		return possibleAge;
	}

	public void setPossibleAge(Pair<Integer, Integer> possibleAge) {
		this.possibleAge = possibleAge;
	}

	public void setDurationInterval(Pair<Double, Double> durationInterval) {
		this.durationInterval = durationInterval;
	}
	
	@Override
	public void makePersistent() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
	}
	
}
