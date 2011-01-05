package com.appspot.analyser;

import java.util.*;

import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;

/* Keeps status of a calendar */
public class CalendarStatus implements Comparable<CalendarStatus> {
	private IEvent event;
	private double additionalEventTime;
	private double coefficient;
	private double userBusyTime;
	private List<CalendarStatus> alternatives;
	private FreeSlotsManager slotsManager;
	private Map<SphereName, SphereInfo> sphereResults;	
	private boolean containsProposal;

	public CalendarStatus(double userBusyTime, Map<SphereName, SphereInfo> currentSphereResults, List<BaseCalendarSlot> freeSlots)  {
		event = null;
		this.userBusyTime = userBusyTime;
		sphereResults = new HashMap<SphereName, SphereInfo>();
		copySphereResults(currentSphereResults);
		setCurrentCoefficient();
		slotsManager = new FreeSlotsManager(freeSlots, this);
	}

	public CalendarStatus(Proposal proposal, CalendarStatus other, List<BaseCalendarSlot> freeSlots, List<BaseCalendarSlot> possibleSlots)  {
		this.userBusyTime = other.getUserBusyTime();
		sphereResults = new HashMap<SphereName, SphereInfo>();
		copySphereResults(other.getSphereResults());
		coefficient = other.getCoefficient();
		this.event = proposal;
		recordProposal();
		analyse();
		slotsManager = new FreeSlotsManager(freeSlots,possibleSlots, this);
		containsProposal = true;
	}

	public CalendarStatus(IEvent event, CalendarStatus other) {
		this.userBusyTime = other.getUserBusyTime();
		sphereResults = new HashMap<SphereName, SphereInfo>();
		copySphereResults(other.getSphereResults());
		coefficient = other.getCoefficient();
		this.event = event;
		slotsManager = new FreeSlotsManager(other.getFreeSlotsManager().getFreeSlots(), this);
		analyse();
	}

	private void recordProposal() {
		additionalEventTime = event.getDuration();
		saveSphereInfos();
		setCurrentCoefficient();
		additionalEventTime = 0;
	}

	private FreeSlotsManager getFreeSlotsManager() {
		return slotsManager;
	}

	private void copySphereResults(Map<SphereName, SphereInfo> currentSphereResults) {
		for(SphereName name : currentSphereResults.keySet()){
			SphereInfo currentInfo = currentSphereResults.get(name);
			sphereResults.put(name, new SphereInfo(currentInfo.getCurrentRatio(), currentInfo.getTargetRatio(), currentInfo.getSphereTotalTime()));
		}
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

	public boolean isWithinConfidenceInterval() {
		return coefficient < Analyser.CONFIDENCE;
	}

	public void addAlternative(CalendarStatus alternativeSuggestion) {
		if(alternatives == null)
			alternatives = new LinkedList<CalendarStatus>();
		alternatives.add(alternativeSuggestion);
	}

	public boolean hasAlternatives() {
		return alternatives != null;
	}

	public List<CalendarStatus> getAlternatives() {
		return alternatives;
	}

	public CalendarStatus checkProposal(Proposal proposal){
		return slotsManager.checkProposal(proposal);
	}

	public boolean containsProposal() {
		return containsProposal;
	}

	/* Update status of calendar after more optimal scheduling of the event */
	private void analyse() {
		Map<SphereName, Double> sphereInfluences = event.getSpheres();
		Double eventDuration = event.getDuration();
		Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
		double maxLengthening = eventDurationInterval.getSecond() - eventDuration;
		double maxShortening = eventDuration - eventDurationInterval.getFirst();
		/* Work out effects of making event longer/shorter 
		 * e.g. [(-maxShortening) / Analyser.TRIES] tells us at what 
		 * time intervals we should be decreasing the duration of event
		 */
		Pair<Double, Double> lenRes = getRatioStatus(maxLengthening / Analyser.TRIES, sphereResults, sphereInfluences);
		Pair<Double, Double> shortRes = getRatioStatus((-maxShortening) / Analyser.TRIES, sphereResults, sphereInfluences);
		if (lenRes.getFirst() < shortRes.getFirst()) {
			coefficient = lenRes.getFirst();
			additionalEventTime = lenRes.getSecond();
		} else {
			coefficient = shortRes.getFirst();
			additionalEventTime = shortRes.getSecond();
		}
		saveSphereInfos();
	}

	private void saveSphereInfos() {
		userBusyTime += additionalEventTime;
		Map<SphereName, Double> influences = event.getSpheres();
		for (SphereName sphere : influences.keySet()) {
			double extraSphereTime = influences.get(sphere) * additionalEventTime;
			sphereResults.get(sphere).saveResults(extraSphereTime, this.userBusyTime);
		}
	}

	/* Calculate coefficient of accuracy (how well overall 
	 * current sphere levels match to targets for spheres) */
	private double getCurrentRatioStatus(Map<SphereName, SphereInfo> infos, 
			Map<SphereName, Double> influences, double eventExtraTime) {
		double res = 0;
		for (SphereName sphere : infos.keySet()) {
			double extraSphereTime = influences.get(sphere) * eventExtraTime;
			double acc = infos.get(sphere).getRatioAccuracy(extraSphereTime, userBusyTime + eventExtraTime);
			res += acc;
		}
		return res;
	}

	private Pair<Double, Double> getRatioStatus(double timeStep, Map<SphereName, 
			SphereInfo> sphereResults, Map<SphereName, Double> influences) {
		double currentExtraTime = 0;
		double currentStatus = getCurrentRatioStatus(sphereResults, influences, 0.0);
		double prevStatus = currentStatus;
		for (int i = 1; i <= Analyser.TRIES; i++) {
			currentExtraTime = i * timeStep;
			currentStatus = getCurrentRatioStatus(sphereResults, influences, currentExtraTime);
			if (prevStatus <= currentStatus)
				return new Pair<Double, Double>(prevStatus, currentExtraTime - timeStep);
			prevStatus = currentStatus;
		}
		return new Pair<Double, Double>(prevStatus, currentExtraTime);
	}

	/* Work out which sphere is most out of line with target.
	 * Return all spheres out of target is full optimisation */
	public List<SphereName> getDeficitSpheres(boolean optimize) {
		List<SphereName> deficits = new LinkedList<SphereName>();
		if (optimize) {
			for(SphereName name : SphereName.values()) {
				if(sphereResults.get(name).getRatioDifference() > 0)
					deficits.add(name);
			}
		}
		else {
			double max = -1;
			SphereName currentMajor = null;
			for(SphereName name : SphereName.values()) {
				if(!sphereResults.get(name).isWithinConfidenceInterval() && sphereResults.get(name).getRatioDifference() > max){
					currentMajor = name;
					max = sphereResults.get(currentMajor).getRatioDifference();
				}
			}
			if(currentMajor != null)
				deficits.add(currentMajor);
		}
		return deficits;
	}

	/* For ordering statuses according to how well they match to targets for spheres */
	public int compareTo(CalendarStatus next) {
		if (coefficient < next.getCoefficient())
			return -1;
		else if (coefficient > next.getCoefficient()) 
			return 1;
		else 
			return 0;
	}
}
