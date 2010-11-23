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

import javax.jdo.PersistenceManager;

import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereChoice;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;

public class Analyzer {

	public static final double CONFIDENCE = 0.1;
	private static final int TRIES = 10;
	private double userBusyTime;

	public Analyzer() {
	}

	public List<Suggestion> getSuggestions(List<? extends IEvent> events, String userID, Map<SphereName, Double> spherePreferences,
			boolean optimizeFull) throws IOException {
		// ------------------- Sphere preferences from database
		// ------------------------------------
		// PersistenceManager pm = PMF.get().getPersistenceManager();
		// Map<SphereName, List<Suggestion>> result = new HashMap<SphereName,
		// List<Suggestion>>();
		// Map<SphereName, Double> spherePreferences = new HashMap<SphereName,
		// Double>();
		// Collection<SphereChoice> res = (Collection<SphereChoice>)
		// pm.newQuery("select from " + SphereChoice.class.getName()
		// + " where userID='" + userID).execute();
		// for (SphereChoice choice : res)
		// spherePreferences.put(choice.getSphereName(), choice.getValue());
		//printEvents(getFreeSlots(events));
		Map<SphereName, SphereInfo> sphereResults = checkGoals(events, spherePreferences);
		if (getSpheresStatus(sphereResults))
			return null;
		Pair<Double, Pair<IEvent, Double>> min = new Pair<Double, Pair<IEvent, Double>>(Double.MAX_VALUE, null);
		Calendar end = new GregorianCalendar();
		List<Suggestion> suggestions = new LinkedList<Suggestion>();
		for (IEvent event : events) {
			Map<SphereName, Double> sphereInfluences = event.getSpheres();
			Double additionalTime = 0.0;
			Double eventDuration = event.getDuration();
			Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
			double maxLengthening = eventDurationInterval.getSecond() - eventDuration;
			double maxShortening = eventDuration - eventDurationInterval.getFirst();
			/* Find (brute force) best (sphere-wise) duration for the event */
			Pair<Double, Double> lenRes = getRatioStatus(maxLengthening / Analyzer.TRIES, sphereResults, sphereInfluences);
			Pair<Double, Double> shortRes = getRatioStatus((-maxShortening) / Analyzer.TRIES, sphereResults, sphereInfluences);
			// this "rates" the suggestions, aim to have it ~0
			double spheresCoefficient;
			if (lenRes.getFirst() < shortRes.getFirst()) {
				// dodac id do IEventu
				spheresCoefficient = lenRes.getFirst();
				additionalTime = lenRes.getSecond();
			} else {
				spheresCoefficient = shortRes.getFirst();
				additionalTime = shortRes.getSecond();
			}
			if (spheresCoefficient < min.getFirst()) {
				min.setFirst(spheresCoefficient);
				min.setSecond(new Pair<IEvent, Double>(event, additionalTime));
			}
			// End przesuwamy tylko teraz, a co jesli sie zazębia? Moze
			// przesunąć do tyłu o różnicę i dopiero dodać?
			// Może się moga zazębiać? Jak coś to patrzymy na nastepny event i
			// czy jest conflict i potem rozwiązujemy
			/*
			 * Check if needs any further scheduling modification - decreasing
			 * steps (maybe for our best option only?)
			 */
			// check all the rest of events and pick minimum
			if (!optimizeFull && spheresCoefficient <= 0.1) {
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + (long) (additionalTime * 60000));
				suggestions.add(new RescheduleSuggestion(event, event.getStartDate(), end));
				return suggestions;
			}
		}
		if (min.getFirst() <= 0.1) {
			IEvent event = min.getSecond().getFirst();
			Double additionalTime = min.getSecond().getSecond();
			if (event.getDuration() + additionalTime == 0)
				suggestions.add(new DeleteSuggestion(event));
			else {
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + (long) (additionalTime * 60000));
				suggestions.add(new RescheduleSuggestion(event, event.getStartDate(), end));
			}
		}
		return suggestions;
	}

	private List<BaseCalendarSlot> getFreeSlots(List<? extends ICalendarSlot> events) {
		LinkedList<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Collections.sort(events);
		System.out.println("----------------------------------");
		// take i, check i + 1 start date and compare with i'th end date. if
		// smaller, carry on with i + 1
		// if not generate free slot( i.end, (i+1) start) and date of i-th end
		// date
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

	// calculate our coefficient of accuracy
	private double getCurrentRatioStatus(Map<SphereName, SphereInfo> infos, Map<SphereName, Double> influences, double eventExtraTime) {
		double res = 0;
		for (SphereName sphere : infos.keySet()) {
			double extraSphereTime = influences.get(sphere) * eventExtraTime;
			double acc = infos.get(sphere).getRatioAccuracy(extraSphereTime, userBusyTime + eventExtraTime);
			res += acc;
		}
		return res;
	}

	// Are all spheres within confidence interval
	private boolean getSpheresStatus(Map<SphereName, SphereInfo> infos) {
		for (SphereName sphere : infos.keySet()) {
			if (!infos.get(sphere).isWithinConfidenceInterval())
				return false;
		}
		return true;
	}

	private Pair<Double, Double> getRatioStatus(double timeStep, Map<SphereName, SphereInfo> sphereResults, Map<SphereName, Double> influences) {
		double currentExtraTime = 0;
		double currentStatus = getCurrentRatioStatus(sphereResults, influences, 0.0);
		double prevStatus = currentStatus;
		for (int i = 1; i <= Analyzer.TRIES; i++) {
			currentExtraTime = i * timeStep;
			currentStatus = getCurrentRatioStatus(sphereResults, influences, currentExtraTime);
			if (prevStatus <= currentStatus)
				return new Pair<Double, Double>(prevStatus, currentExtraTime - timeStep);
			prevStatus = currentStatus;
		}
		return new Pair<Double, Double>(prevStatus, currentExtraTime);
	}

	public Map<SphereName, SphereInfo> checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException {
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
		Map<SphereName, SphereInfo> result = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			result.put(key, info);
		}
		userBusyTime = sum;
		return result;
	}

	private void printEvents(Collection<? extends ICalendarSlot> events) {
		for (ICalendarSlot event : events)
			System.out.println(event.getTitle() + "  " + printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
	}

	private String printDate(Calendar cal) {
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
				+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}

	private void printMap(Map<?, SphereInfo> map) {
		for (Object key : map.keySet()) {
			SphereInfo info = map.get(key);
			System.out.println(key + " : currentRatio=" + info.getCurrentRatio() + "  sphereTotal=" + info.getTotalSphereTime());
		}
	}

	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}

}
