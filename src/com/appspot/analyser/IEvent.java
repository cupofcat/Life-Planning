package com.appspot.analyser;

import java.util.Map;

import com.appspot.datastore.SphereName;
import com.google.gdata.data.calendar.CalendarEventEntry;

public interface IEvent extends ICalendarSlot {
	public boolean isRecurring();
	public boolean canReschedule();
	public Map<SphereName, Double> getSpheres();
	public Pair<Double, Double> getDurationInterval();
	public void setDurationInterval(Pair<Double, Double> newInterval);
	public void makePersistent(int alternative);
	public CalendarEventEntry getCalendarEvent();
}
