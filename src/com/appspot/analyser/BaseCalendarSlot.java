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
	protected Calendar startDate;
	@Persistent
	protected Calendar endDate;
	@Persistent
	protected User user;
	
	protected BaseCalendarSlot(){
	}
	
	public BaseCalendarSlot(CalendarEventEntry calendarEventEntry) {
		Calendar start, end;
		start = new GregorianCalendar();
		end = new GregorianCalendar();
		start.setTimeInMillis(calendarEventEntry.getTimes().get(0).getStartTime().getValue() + 60*60*1000);
		end.setTimeInMillis(calendarEventEntry.getTimes().get(0).getEndTime().getValue() + 60*60*1000);
		startDate = start;
		endDate= end;
		description = calendarEventEntry.getPlainTextContent();
		title = calendarEventEntry.getTitle().getPlainText();
	}
	
	public BaseCalendarSlot(Calendar startDate, Calendar endDate) {
		this.user = user;
		this.startDate = startDate;
		this.endDate = endDate;
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
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public double getDuration() {
		return (endDate.get(Calendar.DAY_OF_MONTH) - startDate.get(Calendar.DAY_OF_MONTH)) * 1440
		+ (endDate.get(Calendar.HOUR_OF_DAY) - startDate.get(Calendar.HOUR_OF_DAY)) * 60
		+ (endDate.get(Calendar.MINUTE) - startDate.get(Calendar.MINUTE));
	}
	
	public int compareTo(ICalendarSlot slot){
		return startDate.compareTo(slot.getStartDate());
	}
}
