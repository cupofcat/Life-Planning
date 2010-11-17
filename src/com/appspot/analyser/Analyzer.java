package com.appspot.analyser;

import java.io.IOException;
import java.util.Collection;
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
	
	private double[] times;
	private double[] currentRatios;
	private double userBusyTime;
	
	public Analyzer(){
		times = new double[SphereName.values().length];
		currentRatios = new double[times.length];
	}
	
	
	public List<Suggestion> getSuggestions(Collection<? extends IEvent> events, String userID) throws IOException{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Map<SphereName, Double> choices = new HashMap<SphereName, Double>();
		Map<SphereName, LinkedList<IEvent>> eventsBySpheres = new HashMap<SphereName, LinkedList<IEvent>>();
		Collection<SphereChoice> res = (Collection<SphereChoice>) pm.newQuery("select from " + SphereChoice.class.getName() + " where userID='" + userID).execute();
		for(SphereChoice choice : res)
			choices.put(choice.getSphereName(), choice.getValue());
		Map<SphereName, SphereInfo> sphereResults = checkGoals(events, choices);
		for(IEvent event : events) {
			Map<SphereName, Double> sphereInfluences = event.getSpheres();
			Double eventDuration = event.getDuration();
			Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
			for(SphereName sphere : sphereInfluences.keySet()) {
				SphereInfo info = sphereResults.get(sphere);
				//Double accuracy = info.getRatioAccuracy(sphereInfluences.get(sphere) * eventLength, userBusyTime + eventLength);
			}
		}
		return null;
	}
	
	public Map<SphereName, SphereInfo> checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException{	
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		for(IEvent event : events){
			//jebane strefy czasowe........
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereResults = event.getSpheres();
			Set<SphereName> keys = sphereResults.keySet();
			for(SphereName key : keys){
				double time = Math.round(( Double.valueOf(sphereResults.get(key))/100) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for(SphereName key : times.keySet()){
			currentRatios.put(key, times.get(key)/sum);
		}
		Map<SphereName, SphereInfo> result = new HashMap<SphereName, SphereInfo>();
		for(SphereName key : times.keySet()){
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			result.put(key, info);
		}
		userBusyTime = sum;
		return result;
	}
	
	
	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys){
		for(SphereName key : keys)
			times.put(key, 0.0);
	}

}
