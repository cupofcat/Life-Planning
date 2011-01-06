package com.appspot.analyser;

import java.util.*;

import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;

public class Utilities {

	public static double getDuration(Calendar start, Calendar end) {
		return (end.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60;
		//return (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)) * 1440
			//	+ (end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) * 60 + (end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE));
	}

	public static Calendar min(Calendar c1, Calendar c2) {
		if (compareHours(c1, c2) <= 0)
			return c1;
		else
			return c2;
	}

	public static Calendar max(Calendar c1, Calendar c2) {
		if (compareHours(c1, c2) <= 0)
			return c2;
		else
			return c1;
	}

	public static int compareHours(Calendar c1, Calendar c2) {
		if (c1.get(Calendar.HOUR_OF_DAY) < c2.get(Calendar.HOUR_OF_DAY)
				|| (c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY) && c1.get(Calendar.MINUTE) < c2.get(Calendar.MINUTE)))
			return -1;
		else
			return 1;

	}

	public static Map<SphereName, Double> generateSpheres(double[] values) {
		SphereName[] names = SphereName.values();
		HashMap<SphereName, Double> res = new HashMap<SphereName, Double>();
		for (int i = 0; i < names.length; i++) {
			if (i < values.length)
				res.put(names[i], values[i]);
			else
				res.put(names[i], 0.0);
		}
		return res;
	}
	
	public static List<CalendarStatus> merge(List<CalendarStatus> eventStatuses, List<CalendarStatus> proposalStatuses) {
		List<CalendarStatus> result = new LinkedList<CalendarStatus>();
		if (eventStatuses.isEmpty())
			result.addAll(proposalStatuses);
		else if (proposalStatuses.isEmpty())
			result.addAll(eventStatuses);
		else {
			Iterator<CalendarStatus> first = eventStatuses.iterator();
			Iterator<CalendarStatus> second = proposalStatuses.iterator();
			CalendarStatus currentFirst = first.next();
			CalendarStatus currentSecond = second.next();
			while (currentFirst != null && currentSecond != null) {
				if (currentFirst.compareTo(currentSecond) < 0) {
					result.add(currentFirst);
					try{
						currentFirst = first.next();
					}
					catch(NoSuchElementException e){
						currentFirst = null;
						result.add(currentSecond);
					}

				} else {
					result.add(currentSecond);
					try{
						currentSecond = second.next();
					}
					catch(NoSuchElementException e){
						currentSecond = null;
						result.add(currentFirst);
					}
				}
			} 
			if (currentFirst == null) {
				while (second.hasNext()) 
					result.add(second.next());
			} 
			else {
				while(first.hasNext())
					result.add(first.next());
			}
		}
		return result;
	}
	
	
	public static void printEvents(Collection<? extends ICalendarSlot> events) {
		for (ICalendarSlot event : events)
			System.out.println(event.getTitle() + "  " + event.getDescription() + "  " 
					+ printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
		System.out.println("--------------------------------");
	}

	public static String printDate(Calendar cal) {
		if(cal == null)
			return null;
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" 
		+ cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
				+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}
}
