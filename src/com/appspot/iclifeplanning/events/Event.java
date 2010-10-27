package com.appspot.iclifeplanning.events;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.Recurrence;

// TODO (amadurska): Add support for repeated events
// TODO (amadurska): Ensure keywords come from both title & description
public class Event implements EventInterface {
	private CalendarEventEntry calendarEventEntry;
	private DateTime startTime;
	private DateTime endTime;
	private TextConstruct description;
	private Set<String> keywords = new HashSet<String>();
	private Set<Event> childEvents;
	private String calendarTitle;
	private String id;
	private boolean canReschedule;
	private static final Logger log = Logger.getLogger("Event");

	public Event(CalendarEventEntry calendarEventEntry) {
		this.calendarEventEntry = calendarEventEntry;
		//startTime = calendarEventEntry.getTimes().get(0).getStartTime();
		//endTime = calendarEventEntry.getTimes().get(0).getEndTime();
		description = calendarEventEntry.getTitle();
		childEvents = null;
		id = calendarEventEntry.getId();
		canReschedule = calendarEventEntry.getCanEdit();
		parseKeywords(description);
		Recurrence r = calendarEventEntry.getRecurrence();
		if (r != null) {
			log.severe("Recurrence: " + r.getValue());
		} else {
			startTime = calendarEventEntry.getTimes().get(0).getStartTime();
			endTime = calendarEventEntry.getTimes().get(0).getEndTime();
		}
	}

	public String getCalendarTitle() {
		return calendarTitle;
	}

	public void setCalendarTitle(String calendarTitle) {
		this.calendarTitle = calendarTitle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void parseKeywords(TextConstruct description) {
		String[] words = description.getPlainText().split("[\\s]+");
		
		for(int i = 0; i < words.length; i++) {
			if (isKeyword(words[i])) {
				keywords.add(words[i]);
			}
		}
	}

	public TextConstruct getDescription() {
		return description;
	}

	public void setDescription(TextConstruct description) {
		this.description = description;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
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
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canReschedule() {
		return canReschedule;
	}

}
