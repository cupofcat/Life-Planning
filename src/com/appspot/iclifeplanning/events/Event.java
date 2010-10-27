package com.appspot.iclifeplanning.events;

import java.util.HashSet;
import java.util.Set;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;

// TODO (amadurska): Add support for repeated events
public class Event implements EventInterface {
	private CalendarEventEntry calendarEventEntry;
	private DateTime startTime;
	private DateTime endTime;
	private TextConstruct description;
	private Set<String> keywords = new HashSet<String>();
	private Set<Event> childEvents;

	public Event(CalendarEventEntry calendarEventEntry) {
		this.calendarEventEntry = calendarEventEntry;
		startTime = calendarEventEntry.getTimes().get(0).getStartTime();
		endTime = calendarEventEntry.getTimes().get(0).getEndTime();
		description = calendarEventEntry.getTitle();
		childEvents = null;
		parseKeywords(description);
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

}
