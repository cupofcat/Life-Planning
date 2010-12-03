package com.appspot.analyser;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jdo.annotations.*;

import com.appspot.datastore.BaseDataObject;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class BaseCalendarSlot extends BaseDataObject implements ICalendarSlot {

	@PrimaryKey
	@Persistent
	protected Key key;
	@Persistent
	protected String title;
	@Persistent
	protected String description;
	@Persistent
	protected Long startDate;
	@Persistent
	protected Long endDate;
	@Persistent
	protected User user;

	protected BaseCalendarSlot(){
	}

	public BaseCalendarSlot(CalendarEventEntry calendarEventEntry) {
		startDate = calendarEventEntry.getTimes().get(0).getStartTime().getValue() + 60*60*1000;
		endDate = calendarEventEntry.getTimes().get(0).getEndTime().getValue() + 60*60*1000;
		description = calendarEventEntry.getPlainTextContent();
		title = calendarEventEntry.getTitle().getPlainText();
	}

	public BaseCalendarSlot(Calendar startDate, Calendar endDate) {
		this.startDate = startDate.getTimeInMillis();
		this.endDate = endDate.getTimeInMillis();
	}

	public BaseCalendarSlot(String title, String description, Calendar startDate,
			Calendar endDate) {
		this(startDate, endDate);
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(TextConstruct title) {
		this.title = title.getPlainText();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(TextConstruct description) {
		this.description = description.getPlainText();
	}

	public Calendar getStartDate() {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(startDate);
		return c;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate.getTimeInMillis();
	}

	public Calendar getEndDate() {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(endDate);
		return c;	
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate.getTimeInMillis();
	}

	public double getDuration() {
		Calendar start = getStartDate();
		Calendar end = getEndDate();
		return (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)) * 1440
		+ (end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) * 60
		+ (end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE));
	}

	public int compareTo(ICalendarSlot slot) {
		Calendar start = getStartDate();
		return start.compareTo(slot.getStartDate());
	}

	public boolean equals(Object o){
		return ((ICalendarSlot) o).compareTo(this) == 0;
	}
}
