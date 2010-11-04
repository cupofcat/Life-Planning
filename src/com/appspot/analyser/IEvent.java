package com.appspot.analyser;

import java.util.Map;

import com.appspot.datastore.SphereName;

public interface IEvent extends ICalendarSlot {
	double minDuration();
	double maxDuration();
	boolean isRecurring();
	boolean canReschedule();
	Map<SphereName, Integer> getSpheres();
}
