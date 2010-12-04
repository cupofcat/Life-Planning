package com.appspot.analyser;

import java.util.Calendar;

import com.google.gdata.data.TextConstruct;

public interface ICalendarSlot extends Comparable<ICalendarSlot> {

	public String getTitle();
	public void setTitle(TextConstruct title);
	public String getDescription();
	public void setDescription(TextConstruct descr);
	public Calendar getStartDate();
	public void setStartDate(Calendar start);
	public Calendar getEndDate();
	public void setEndDate(Calendar end);	
	public double getDuration();
}
