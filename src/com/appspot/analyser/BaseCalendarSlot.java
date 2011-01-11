package com.appspot.analyser;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

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
	protected Long startDate;
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
	
	public BaseCalendarSlot(String title, String description, Calendar startDate,
			Calendar endDate, User user) {
		this(title, description, startDate, endDate);
		this.user = user;
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
		if(startDate == null)
			return null;
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(startDate);
		return c;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate.getTimeInMillis();
	}

	public Calendar getEndDate() {
		if(endDate == null)
			return null;
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
		return Utilities.getDuration(start, end);
	}
	
	public void setDuration(double minutes){
		Calendar end = new GregorianCalendar();
		end.setTimeInMillis(startDate + (long) minutes * 60000);
		setEndDate(end);
	}

	public int compareTo(ICalendarSlot slot) {
		if(slot.getTitle() != null && slot.getTitle().equals("Best fit")){
			double duration = slot.getDuration();
			if(getDuration() < duration)
				return -1;
			else if (getDuration() == duration)
				return 0;
			return 1;
		}
		else{
			Calendar start = getStartDate();
			return start.compareTo(slot.getStartDate());
		}
	}

	public boolean equals(Object o){
		return ((ICalendarSlot) o).compareTo(this) == 0;
	}
	
	public String toString(){
		String ret = "";
		ret = "Title: " + title + "  Descr: " + description + "  ";
		if(startDate != null && endDate != null)
			ret = ret.concat(printDate(getStartDate()) + " - " + printDate(getEndDate()));
		return ret;
	}

	private String printDate(Calendar cal) {
		if(cal == null)
			return null;
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" 
		+ cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
				+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}
}
