package com.appspot.analyser;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereName;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.gdata.data.calendar.CalendarEventEntry;


@PersistenceCapable
public class Proposal extends BaseCalendarSlot implements IEvent  {

	@NotPersistent
	private boolean isRecurring;
	@NotPersistent
	private boolean canReschedule;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private Map<SphereName, Double> spheres;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private Pair<Double, Double> durationInterval;
	@Persistent
	private boolean disabled;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private Pair<Long, Long> possibleTimeSlot;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private Pair<Integer, Integer> possibleAge;

	public Proposal(Calendar startDate, Calendar endDate, User user) {
		super(startDate, endDate);
		key = KeyFactory.createKey(Proposal.class.getSimpleName(), title + description);
	}

	public Proposal(String title, String description, Calendar startDate,
			Calendar endDate) {
		super(title, description, startDate, endDate);
		key = KeyFactory.createKey(Proposal.class.getSimpleName(), title + description);

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

	public boolean canReschedule() {
		return canReschedule;
	}

	public void setReschedule(boolean schedule){
		canReschedule = schedule;
	}

	public Pair<Double, Double> getDurationInterval() {
		return this.durationInterval;
	}

	public void setDurationInterval(Double min, Double max){
		durationInterval = new Pair<Double, Double>(min, max);
	}

	public Map<SphereName, Double> getSpheres() {
		return this.spheres;
	}

	public void setSpheres(Map<SphereName, Double> s){
		spheres = s;
	}

	public boolean isRecurring() {
		return this.isRecurring;
	}

	public void setMultiplier(double multiplier) {
		for (SphereName sphere : spheres.keySet()) {
			Double influence = spheres.get(sphere);
			spheres.put(sphere, influence*multiplier);
		}
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Pair<Calendar, Calendar> getPossibleTimeSlot() {
		Calendar possibleStart = new GregorianCalendar();
		possibleStart.setTimeInMillis(possibleTimeSlot.getFirst());
		Calendar possibleEnd = new GregorianCalendar();
		possibleEnd.setTimeInMillis(possibleTimeSlot.getSecond());
		Pair<Calendar, Calendar> possibleTimeSlot = new Pair<Calendar, Calendar>(possibleStart, possibleEnd);
		return possibleTimeSlot;
	}

	public void setPossibleTimeSlot(Pair<Calendar, Calendar> possibleTimeSlot) {
		Long possibleStart = possibleTimeSlot.getFirst().getTimeInMillis();
		Long possibleEnd = possibleTimeSlot.getSecond().getTimeInMillis();
		this.possibleTimeSlot = new Pair<Long, Long>(possibleStart, possibleEnd);
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

	public void makePersistent(int alternative) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
	}

	public CalendarEventEntry getCalendarEvent() {
		return null;
	}	
}