package com.appspot.analyser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.iclifeplanning.events.Event;

public class Analyzer {

	public static final double CONFIDENCE = 0.1;
	static final int TRIES = 10;
	private double userBusyTime;

	public Analyzer() {
	}

	public List<Suggestion> getSuggestions(List<Event> events, String currentUserId, boolean b) throws IOException {
		return this.getSuggestions(events, currentUserId, generateSpheres(new double[] { 0.7, 0.3 }), b);
	}

	private Map<SphereName, Double> generateSpheres(double[] values) {
		SphereName[] names = SphereName.values();
		Map<SphereName, Double> res = new HashMap<SphereName, Double>();
		for (int i = 0; i < names.length; i++) {
			if (i < values.length)
				res.put(names[i], values[i]);
			else
				res.put(names[i], 0.0);
		}
		return res;
	}
	
	public List<Suggestion> getSuggestions(List<? extends IEvent> events, String userID, Map<SphereName, Double> spherePreferences,
			boolean optimizeFull) throws IOException {
		//wyciagnac spherePreferences i optimize z bazy danych
		CalendarStatus start = checkGoals(events, spherePreferences);
		if (start.isWithinConfidenceInterval())
			return null;
		return getSuggestions(events, start, optimizeFull);
	}

	private List<Suggestion> getSuggestions(List<? extends IEvent> events,CalendarStatus currentStatus, boolean optimizeFull) throws IOException {
//		iterujemy po eventach w odniesienu do current statusu, po czym bierzemy najlepszy
//		jesli ten nie daje nam confidence intervala to probujemy rekurencyjnie wejsc w funkce z calendarStatusem najlepszego eventa 
//		i wywalonym tym eventem z listy. Powtarzamy az dojdziemy i jako return concatujemy listy
		List<Suggestion> suggestions = new LinkedList<Suggestion>(); 
		Calendar end = new GregorianCalendar();
		CalendarStatus min = new CalendarStatus(null, currentStatus);
		CalendarStatus newStatus;
		for (IEvent event : events){
			newStatus = new CalendarStatus(event, currentStatus);
			newStatus.analyse();
			System.out.println(newStatus.getEvent().getTitle() + "  addTime: " + newStatus.getAdditionalEventTime() + "  coeff: "+newStatus.getCoefficient());
			if(newStatus.getCoefficient() < min.getCoefficient())
				min = newStatus;
			if (!optimizeFull && min.getCoefficient() <= 0.1) {
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + (long) (min.getAdditionalEventTime() * 60000));
				suggestions.add(new RescheduleSuggestion(event, event.getStartDate(), end));
				return suggestions;
			}
		}
		if (min.isWithinConfidenceInterval()) {
			IEvent event = min.getEvent();
			Double additionalTime = min.getAdditionalEventTime();
			if (event.getDuration() + additionalTime == 0)
				suggestions.add(new DeleteSuggestion(event));
			else {
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + (long) (additionalTime * 60000));
				suggestions.add(new RescheduleSuggestion(event, event.getStartDate(), end));
			}
		}
		return null;
	}

	private List<BaseCalendarSlot> getFreeSlots(List<? extends ICalendarSlot> events) {
		LinkedList<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Collections.sort(events);
		System.out.println("----------------------------------");
		Iterator<? extends ICalendarSlot> it = events.iterator();
		ICalendarSlot curr = it.next();
		while (it.hasNext()) {
			ICalendarSlot next = it.next();
			if (curr.getEndDate().compareTo(next.getStartDate()) < 0) {
				ret.add(new BaseCalendarSlot("Free Slot", null, curr.getEndDate(), next.getStartDate()));
				curr = next;
			} else if (curr.getEndDate().compareTo(next.getEndDate()) < 0)
				curr = next;
		}
		return ret;
	}

	public CalendarStatus checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException {
		CalendarStatus result;
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		for (IEvent event : events) {
			// jebane strefy czasowe........
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereResults = event.getSpheres();
			Set<SphereName> keys = sphereResults.keySet();
			for (SphereName key : keys) {
				double time = Math.round(sphereResults.get(key) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			currentRatios.put(key, times.get(key) / sum);
		}
		Map<SphereName, SphereInfo> sphereResults = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			sphereResults.put(key, info);
		}
		return new CalendarStatus(sum, sphereResults);
	}

	private void printEvents(Collection<? extends ICalendarSlot> events) {
		for (ICalendarSlot event : events)
			System.out.println(event.getTitle() + "  " + printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
	}

	private String printDate(Calendar cal) {
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
				+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}

	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}

}