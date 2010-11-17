package com.appspot.analyser;

import java.util.Map;

import com.appspot.datastore.SphereName;

public interface IEvent extends ICalendarSlot {
	boolean isRecurring();

	boolean canReschedule();

	Map<SphereName, Double> getSpheres();

	Pair<Double, Double> getDurationInterval();

	void makePersistent();
}
