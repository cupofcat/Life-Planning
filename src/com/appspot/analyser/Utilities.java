package com.appspot.analyser;

import java.util.*;

import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.events.Event;

//Helper functions used across the application
public class Utilities {
	//Length of day in mins
	public static final int DAYLENGTH = 1440;
	
	public static double getDuration(Calendar start, Calendar end) {
		return (end.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60;
	}

	//Max and min of two calendars
	public static Calendar min(Calendar c1, Calendar c2) {
		if (c1.compareTo(c2) >= 0)
			return c2;
		else
			return c1;
	}

	public static Calendar max(Calendar c1, Calendar c2) {
		if (c1.compareTo(c2) >= 0)
			return c1;
		else
			return c2;
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
	//Merging of two lists containing calendars
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
	
	public static HashMap<SphereName, Double> analyseEvents(
			List<Event> events, Map<SphereName, Double> currentDesiredBalance) {
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, currentDesiredBalance.keySet());
		HashMap<SphereName, Double> result = new HashMap<SphereName, Double>();
		double sum = 0;
		for (IEvent event : events) {
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereInfluences = event.getSpheres();
			Set<SphereName> keys = sphereInfluences.keySet();
			for (SphereName key : keys) {
				double time = Math.round(sphereInfluences.get(key) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			result.put(key, times.get(key) / sum);
		}
		return result;
	}
	
	//Starting times for each sphere - set to 0
	private static void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}
	
	public static List<BaseCalendarSlot> copyFreeSlots(List<BaseCalendarSlot> freeSlots){
		List<BaseCalendarSlot> freeSlotsCopy = new LinkedList<BaseCalendarSlot>();
		freeSlotsCopy.addAll(freeSlots);
		return freeSlotsCopy;
	}
	
	public static Calendar copyCalendar(Calendar base, int additionalMins){
		Calendar ret = new GregorianCalendar();
		ret.setTimeInMillis(base.getTimeInMillis() + (long) (additionalMins * 60000));
		return ret;
	}
	
	public static void addProposals(){
		Calendar startDate = new GregorianCalendar(2000, 0, 3, 7, 0, 0);
		Calendar endDate= new GregorianCalendar(2000, 0, 3, 8, 30, 0);
		Pair<Calendar, Calendar> possibleSlot = new Pair<Calendar, Calendar>(startDate, endDate);
		Map<SphereName, Double> sphereInfluences = Utilities.generateSpheres(new double[]{1.0});
		Proposal p1 = new Proposal("Gym", "Nie ma upierdalania sja", null, null, 20, 60 , false, true, sphereInfluences);
		p1.setPossibleTimeSlot(possibleSlot);
		p1.makePersistent();
		
		Calendar startDate2 = new GregorianCalendar(2000, 0, 3, 17, 0, 0);
		Calendar endDate2= new GregorianCalendar(2000, 0, 3, 18, 30, 0);
		Pair<Calendar, Calendar> possibleSlot2 = new Pair<Calendar, Calendar>(startDate2, endDate2);
		Map<SphereName, Double> sphereInfluences2 = Utilities.generateSpheres(new double[]{0, 1.0});
		Proposal p2 = new Proposal("Meeting at work", "Szanuj szefa swego bo mozesz miec gorszego", null, null, 20, 60 , false, true, sphereInfluences2);
		p2.setPossibleTimeSlot(possibleSlot2);
		p2.makePersistent();
		
		Calendar startDate3 = new GregorianCalendar(2000, 0, 3, 15, 0, 0);
		Calendar endDate3= new GregorianCalendar(2000, 0, 3, 16, 0, 0);
		Pair<Calendar, Calendar> possibleSlot3 = new Pair<Calendar, Calendar>(startDate3, endDate3);
		Map<SphereName, Double> sphereInfluences3 = Utilities.generateSpheres(new double[]{0.0, 0.0, 1.0});
		Proposal p3 = new Proposal("Family dinner", "Stejki - masa sie liczy", null, null, 20, 60 , false, true, sphereInfluences3);
		p3.setPossibleTimeSlot(possibleSlot3);
		p3.makePersistent();
		
		Calendar startDate4 = new GregorianCalendar(2000, 0, 3, 22, 0, 0);
		Calendar endDate4 = new GregorianCalendar(2000, 0, 3, 23, 30, 0);
		Pair<Calendar, Calendar> possibleSlot4 = new Pair<Calendar, Calendar>(startDate4, endDate4);
		Map<SphereName, Double> sphereInfluences4 = Utilities.generateSpheres(new double[]{0.0, 0.0, 1.0});
		Proposal p4 = new Proposal("Watching a movie", "Robert Burneika box set!", null, null, 20, 60 , false, true, sphereInfluences4);
		p4.setPossibleTimeSlot(possibleSlot4);
		p4.makePersistent();

		
	}
	//Work out major sphere influenced by the event
	public static SphereName calculateMajorSphere(IEvent event) {
		double max = -1;
		Map<SphereName,Double> s = event.getSpheres();
		SphereName currentMajor = null;
		for(SphereName name : s.keySet()){
			if(s.get(name) > max){
				currentMajor = name;
				max = s.get(currentMajor);
			}
		}
		return currentMajor;
	}
}
