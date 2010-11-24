package com.appspot.iclifeplanning.events;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.appspot.analyser.BaseCalendarSlot;
import com.appspot.analyser.IEvent;
import com.appspot.analyser.Pair;
import com.appspot.datastore.SphereName;
import com.google.gdata.data.calendar.CalendarEventEntry;

// TODO (amadurska): Ensure keywords come from both title & description
public class Event extends BaseCalendarSlot implements IEvent {
	private Set<String> keywords = new HashSet<String>();
	private Set<Event> childEvents;
	private URL calendarURL;
	private String id;
	private boolean canReschedule;
	private boolean isRecurring;
	private static final Logger log = Logger.getLogger("EventStore");

	public Event(CalendarEventEntry calendarEventEntry) {
		super(calendarEventEntry);
		childEvents = null;
		id = calendarEventEntry.getId();
		canReschedule = calendarEventEntry.getCanEdit();
		isRecurring = calendarEventEntry.getRecurrence() != null;
		parseKeywords(title);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void parseKeywords(String title) {
		String[] words = title.split("[\\s]+");
		
		for(int i = 0; i < words.length; i++) {
			if (isKeyword(words[i])) {
				keywords.add(words[i]);
			}
		}
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public Set<Event> getChildEvents() {
		return childEvents;
	}

	public void setChildEvents(Set<Event> childEvents) {
		this.childEvents = childEvents;
	}

	private boolean isKeyword(String string) {
		return true;
	}
//zobaczyc czy dziala
	public Map<SphereName, Double> getSpheres() {
		Map<String, Double> tmp = UClasifier.analyse(title);
		Map<SphereName, Double> res = new HashMap<SphereName, Double>();
		for(String key : tmp.keySet()){		
			for(SphereName name : SphereName.values()){
				if(key.equalsIgnoreCase(name.toString())){
					res.put(name, tmp.get(key));
					break;
				}
			}
		}
		return res;
	}

	public boolean canReschedule() {
		return canReschedule;
	}

	public boolean isRecurring() {
		return isRecurring;
	}

	public double minDuration() {
		double minDuration = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000 / 60;
		return minDuration;
	}

	public double maxDuration() {
		double maxDuration = (endDate.getTimeInMillis() - startDate.getTimeInMillis()) / 1000 / 60;
		return maxDuration;
	}

	public void makePersistent() {
		
	}

	public Pair<Double, Double> getDurationInterval() {
		return new Pair(minDuration(), maxDuration());
	}

	public void setCalendarURL(URL eventFeedUrl) {
		this.calendarURL = eventFeedUrl;	
	}
}