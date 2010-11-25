package com.appspot.analyser;

import java.util.Collection;
import java.util.Map;
import com.appspot.datastore.*;

public class CalendarStatus {
	private IEvent event;
	private double additionalEventTime;
	private double coefficient;
	private double userBusyTime;
	private Map<SphereName, SphereInfo> sphereResults;
	
	public CalendarStatus(double userBusyTime, Map<SphereName, SphereInfo> sphereResults){
		event = null;
		this.userBusyTime = userBusyTime;
		this.sphereResults = sphereResults;
		setCurrentCoefficient();
	}
	
	public CalendarStatus(IEvent event, double userBusyTime, Map<SphereName, SphereInfo> sphereResults) {
		this(userBusyTime, sphereResults);
		this.event = event;
	}
	
	public CalendarStatus(IEvent event, CalendarStatus other){
		this(other.getUserBusyTime(), other.getSphereResults());
		this.event = event;
	}

	public Map<SphereName, SphereInfo> getSphereResults() {
		return sphereResults;
	}

	public void setSphereResults(Map<SphereName, SphereInfo> sphereResults) {
		this.sphereResults = sphereResults;
	}

	public IEvent getEvent() {
		return event;
	}

	public double getAdditionalEventTime() {
		return additionalEventTime;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public double getUserBusyTime() {
		return userBusyTime;
	}
	
	private void setCurrentCoefficient(){
		double res = 0;
		for (SphereInfo info : sphereResults.values()) {
			double acc = info.getRatioAccuracy(0, userBusyTime);
			res += acc;
		}
		coefficient = res;
	}
	
	public boolean isWithinConfidenceInterval(){
//		Collection<SphereInfo> infos = sphereResults.values();
//		for(SphereInfo info : infos){
//			if(!info.isWithinConfidenceInterval())
//				return false;
//		}
//		return true;
		return coefficient < 0.1;
	}
	
	public void analyse(){
		Map<SphereName, Double> sphereInfluences = event.getSpheres();
		Double eventDuration = event.getDuration();
		Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
		double maxLengthening = eventDurationInterval.getSecond() - eventDuration;
		double maxShortening = eventDuration - eventDurationInterval.getFirst();
		/* Find (brute force) best (sphere-wise) duration for the event */
		Pair<Double, Double> lenRes = getRatioStatus(maxLengthening / Analyzer.TRIES, sphereResults, sphereInfluences);
		Pair<Double, Double> shortRes = getRatioStatus((-maxShortening) / Analyzer.TRIES, sphereResults, sphereInfluences);
		if (lenRes.getFirst() < shortRes.getFirst()) {
			coefficient = lenRes.getFirst();
			additionalEventTime = lenRes.getSecond();
		} else {
			coefficient = shortRes.getFirst();
			additionalEventTime = shortRes.getSecond();
		}
		userBusyTime += additionalEventTime;
		saveSphereInfos();
	}
	
	private void saveSphereInfos(){
		Map<SphereName, Double> influences = event.getSpheres();
		for (SphereName sphere : influences.keySet()) {
			double extraSphereTime = influences.get(sphere) * additionalEventTime;
			sphereResults.get(sphere).saveResults(extraSphereTime, this.userBusyTime);
		}
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

}
