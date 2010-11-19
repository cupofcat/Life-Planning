package com.appspot.analyser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

	private double getCurrentRatioStatus(Map<SphereName, SphereInfo> infos, Map<SphereName, Double> influences, double eventExtraTime) {
		double res = 0;
		for (SphereName sphere : infos.keySet()) {
			double extraSphereTime = influences.get(sphere) * eventExtraTime;
			res += infos.get(sphere).getRatioAccuracy(extraSphereTime, userBusyTime + eventExtraTime);
		}
		return res;
	}
	
	private boolean getSpheresStatus(Map<SphereName, SphereInfo> infos, Map<SphereName, Double> influences, double eventExtraTime) {
		/* Doesn't tell which sphere fails test */
		for (SphereName sphere : infos.keySet()) {
			double extraSphereTime = influences.get(sphere) * eventExtraTime;
			if (!infos.get(sphere).isWithinConfidenceInterval(extraSphereTime))
				return false;
		}
		return true;
	}
	
	private boolean getSpheresStatus(Map<SphereName, SphereInfo> infos) {
		for (SphereName sphere : infos.keySet()) {
			if (!infos.get(sphere).isWithinConfidenceInterval())
				return false;
		}
		return true;
	}

	private Pair<Double, Double> getRatioStatus(double timeStep, Map<SphereName, SphereInfo> infos, Map<SphereName, Double> influences ){
		double currentExtraTime = 0;
		double currentStatus = getCurrentRatioStatus(infos,	influences, 0.0);
		double prevStatus = currentStatus;
		for(int i = 1; i <= Analyzer.TRIES; i++){
			currentExtraTime = i * timeStep;
			currentStatus = getCurrentRatioStatus(infos, influences, currentExtraTime);
			if(prevStatus < currentStatus)
				return new Pair<Double, Double>(prevStatus , currentExtraTime - timeStep);
			prevStatus = currentStatus;
		}
		return new Pair<Double, Double>(prevStatus, currentExtraTime);
	}

	public List<Suggestion> getSuggestions(Collection<? extends IEvent> events,
			String userID) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Map<SphereName, List<Suggestion>> result = new HashMap<SphereName, List<Suggestion>>();
		Map<SphereName, Double> spherePreferences = new HashMap<SphereName, Double>();
		Collection<SphereChoice> res = (Collection<SphereChoice>) pm.newQuery("select from " + SphereChoice.class.getName()
				+ " where userID='" + userID).execute();
		for (SphereChoice choice : res)
			spherePreferences.put(choice.getSphereName(), choice.getValue());
		Map<SphereName, SphereInfo> sphereResults = checkGoals(events,
				spherePreferences);
		
		if (getSpheresStatus(sphereResults))
			return null;
		
		List<Suggestion> suggestions = new LinkedList<Suggestion>();		
			for (IEvent event : events) {
				Map<SphereName, Double> sphereInfluences = event.getSpheres();
				long additionalTime = 0;
				Double eventDuration = event.getDuration();
				Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
				double maxLengthening = eventDurationInterval.getSecond()- eventDuration;
				double maxShortening = eventDuration - eventDurationInterval.getFirst();
				/* Find (brute force) best (sphere-wise) duration for the event */
				Pair<Double,Double> lenRes = getRatioStatus(maxLengthening/Analyzer.TRIES, sphereResults, sphereInfluences);
				Pair<Double,Double> shortRes = getRatioStatus((-maxShortening)/Analyzer.TRIES, sphereResults, sphereInfluences);
				Calendar end = new GregorianCalendar();
				double spheresCoefficient;
				if(lenRes.getFirst() < shortRes.getFirst()) {
					//dodac id do IEventu
					//sphereCoefficient nie ma z BaseCalendarSlot (bo free time nie powinien go miec, tylko w Suggestion + z dupy konstruktory
					spheresCoefficient = lenRes.getFirst();
					additionalTime = (long) (lenRes.getSecond()*60000);
				}
				else {
					spheresCoefficient = shortRes.getFirst();
					additionalTime = (long) (shortRes.getSecond()*60000);
				}
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + additionalTime);
//				double suggestionRating = rateSuggestion(sphere, additionalTime, spheresCoefficient);
				suggestions.add(new RescheduleSuggestion(event, event.getStartDate(), event.getEndDate(), spheresCoefficient));
				/* Check if needs any further scheduling modification */
				if (getSpheresStatus(sphereResults, sphereInfluences, additionalTime))
					return suggestions;
			}
		return suggestions;
	}

	public double rateSuggestion(SphereInfo sphere, double additionalTime, double sphereCoefficient) {
		double newRatio = sphere.getNewRatio(additionalTime, userBusyTime);
		return sphereCoefficient*sphere.getRatioAccuracy(additionalTime, newRatio);
	}
	
	public Map<SphereName, SphereInfo> checkGoals(
			Collection<? extends IEvent> events, Map<SphereName, Double> choices)
			throws IOException {
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
				double time = Math.round((Double
						.valueOf(sphereResults.get(key)) / 100)
						* durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			currentRatios.put(key, times.get(key) / sum);
		}
		Map<SphereName, SphereInfo> result = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices
					.get(key), times.get(key));
			result.put(key, info);
		}
		userBusyTime = sum;
		return result;
	}

	private void initializeTimes(Map<SphereName, Double> times,
			Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}

}
