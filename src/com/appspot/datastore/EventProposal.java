package com.appspot.datastore;

import javax.jdo.annotations.*;

import com.google.appengine.api.datastore.Key;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;

public class EventProposal {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private SphereName name;
	@Persistent
	private String title;
	@Persistent
	private String description;
	@Persistent
	private DateTime startTime;
	@Persistent
	private DateTime endTime;
	@Persistent
	private boolean disabled;
	
	public EventProposal(Key key, SphereName name, String title,
			String description, DateTime startTime, DateTime endTime,
			boolean disabled) {
		super();
		this.key = key;
		this.name = name;
		this.title = title;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.disabled = disabled;
	}
	
	public SphereName getName() {
		return name;
	}
	public void setName(SphereName name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
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
	public Key getKey() {
		return key;
	}
	public boolean isDisabled() {
		return disabled;
	}
	
	
}
