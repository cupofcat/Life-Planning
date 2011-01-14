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
	public FreeSlotsManager slotsManager;
	private Map<SphereName, SphereInfo> sphereResults;
	private boolean containsProposal;
	private boolean successful;

	public CalendarStatus(double userBusyTime, Map<SphereName, SphereInfo> currentSphereResults, List<BaseCalendarSlot> freeSlots) {
		event = null;
		this.userBusyTime = userBusyTime;
		sphereResults = new HashMap<SphereName, SphereInfo>();
		copySphereResults(currentSphereResults);
		setCurrentCoefficient();
		slotsManager = new FreeSlotsManager(freeSlots, this);
	}

	public CalendarStatus(Proposal proposal, CalendarStatus other, List<BaseCalendarSlot> freeSlots, List<BaseCalendarSlot> possibleSlots) {
		copyOtherCalendar(other);
		this.event = proposal;
		recordProposal();
		// after recording minimum duration we improved our status and it is
		// worth analysing
		if (this.compareTo(other) < 0) {
			successful = true;
			analyse();
		}
		slotsManager = new FreeSlotsManager(freeSlots, possibleSlots, this);
		containsProposal = true;
	}

	public CalendarStatus(IEvent event, CalendarStatus other) {
		copyOtherCalendar(other);
		this.event = event;
		slotsManager = new FreeSlotsManager(Utilities.copyFreeSlots(other.getFreeSlotsManager().getFreeSlots()), this);
		analyse();
	}

	public CalendarStatus() {
		// TODO Auto-generated constructor stub
	}

	private void copyOtherCalendar(CalendarStatus other) {
		this.userBusyTime = other.getUserBusyTime();
		sphereResults = new HashMap<SphereName, SphereInfo>();
		copySphereResults(other.getSphereResults());
		coefficient = other.getCoefficient();
	}

	private void recordProposal() {
		saveSphereInfos(event.getDuration());
		setCurrentCoefficient();
		additionalEventTime = 0;
	}

	// /UPDATE LATER /////
	/////////////////////////////
	/////////////////////////////#
	
	private void deleteOtherInfluence(Map<SphereName, Double> influences, double additionalTime){
		userBusyTime += additionalTime;
		double oldTime = additionalEventTime;
		for (SphereName sphere : influences.keySet()) {
			double extraSphereTime = influences.get(sphere) * additionalTime;
			sphereResults.get(sphere).saveResults(extraSphereTime, this.userBusyTime);
		}
		if(this.containsProposal){
			recordProposal();
		}
		additionalEventTime = 0;
		this.saveSphereInfos(oldTime);
		setCurrentCoefficient();
	}
	

	private void checkMaxDuration(){
		slotsManager.updateEventMaxDuration();
		if(this.hasAlternatives()){
			for(CalendarStatus alternative : alternatives)
				alternative.checkMaxDuration();
		}
	}
	
	private void reAnalyse(){
		successful = false;
		analyse();
	}
	
	public Pair<List<CalendarStatus>, List<CalendarStatus>> recalculate(CalendarStatus other) {
		List<CalendarStatus> successes = new LinkedList<CalendarStatus>();
		List<CalendarStatus> fails = new LinkedList<CalendarStatus>();
		if(!slotsManager.getFreeSlots().equals(other.getFreeSlots())){
			setFreeSlots(other.getFreeSlots());
			if(this.hasAlternatives()){
				for(CalendarStatus alternative : alternatives)
					alternative.setFreeSlots(this.getFreeSlots());
			}
			checkMaxDuration();
			//other.checkMaxDuration();
		}
		double eventDuration = additionalEventTime;
		if(this.containsProposal)
			eventDuration += event.getDuration();
		copyOtherCalendar(other);
		reAnalyse();
		if(successful)
			successes.add(this);
		else
			fails.add(this);
		if(this.hasAlternatives()){
			for(CalendarStatus alternative : alternatives){
				alternative.copyOtherCalendar(other);
				alternative.deleteOtherInfluence(event.getSpheres(), -eventDuration);
				alternative.reAnalyse();
				if(alternative.hasImproved())
					successes.add(alternative);
				else
					fails.add(alternative);
			}
		}
		//clearing alternatives - to be rebuilt
		clearAlternatives();
		List<CalendarStatus> removed = new LinkedList<CalendarStatus>();
		if(!successes.isEmpty()){
			Collections.sort(successes);
			CalendarStatus best = successes.get(0);
			for(CalendarStatus success : successes){
				if(success.getCoefficient() > 0.05 && success.getCoefficient() > best.getCoefficient()*(1+Analyser.ALTERNATIVE))
					removed.add(success);
			}
			successes.removeAll(removed);
			for(CalendarStatus failure : fails){
				if(failure.getCoefficient() > 0.05 && failure.getCoefficient() > best.getCoefficient()*(1+Analyser.ALTERNATIVE))
					removed.add(failure);
				else
					successes.add(failure);
			}
		}
		else
			successes.addAll(fails);
		return new Pair<List<CalendarStatus>, List<CalendarStatus>>(successes, removed); 
	}


	private void clearAlternatives() {
		if(alternatives != null)
			alternatives.clear();
	}

	private void setFreeSlots(List<BaseCalendarSlot> freeSlots) {
		slotsManager.setFreeSlots(freeSlots);		
	}

	private FreeSlotsManager getFreeSlotsManager() {
		return slotsManager;
	}
	
	////////////////////

	private void copySphereResults(Map<SphereName, SphereInfo> currentSphereResults) {
		for (SphereName name : currentSphereResults.keySet()) {
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
	
	public List<BaseCalendarSlot> getFreeSlots(){
		return slotsManager.getFreeSlots();
	}

	private void setCurrentCoefficient() {
		double res = 0;
		for (SphereInfo info : sphereResults.values()) {
			double acc = info.getRatioAccuracy(0, userBusyTime);
			res += acc;
		}
		coefficient = res;
	}

	public void updateSlots() {
		if(!this.containsProposal)
			slotsManager.updateCurrentSlots(this);
		for (CalendarStatus alternative : alternatives)
			slotsManager.updateCurrentSlots(alternative);
		slotsManager.sortFreeSlots();
	}

	public boolean isWithinConfidenceInterval() {
		return coefficient < Analyser.CONFIDENCE;
	}

	public void addAlternatives(List<CalendarStatus> alternativeSuggestions) {
			if (alternatives == null)
				alternatives = new LinkedList<CalendarStatus>();
			alternatives.addAll(alternativeSuggestions);
	}

	public boolean hasAlternatives() {
		return alternatives != null && alternatives.size() > 0;
	}

	public List<CalendarStatus> getAlternatives() {
		return alternatives;
	}

	public CalendarStatus checkProposal(Proposal proposal) {
		return slotsManager.checkProposal(proposal);
	}

	public boolean containsProposal() {
		return containsProposal;
	}

	public boolean hasImproved() {
		return successful;
	}

	/* Update status of calendar after more optimal scheduling of the event */
	private void analyse() {
		Map<SphereName, Double> sphereInfluences = event.getSpheres();
		Double eventDuration = event.getDuration();
		Pair<Double, Double> eventDurationInterval = event.getDurationInterval();
		double maxLengthening = eventDurationInterval.getSecond() - (eventDuration + additionalEventTime);
		double maxShortening = (eventDuration + additionalEventTime) - eventDurationInterval.getFirst();
		/*
		 * Work out effects of making event longer/shorter e.g.
		 * [(-maxShortening) / Analyser.TRIES] tells us at what time intervals
		 * we should be decreasing the duration of event
		 */
		Pair<Double, Double> lenRes = getRatioStatus(maxLengthening / Analyser.TRIES, sphereResults, sphereInfluences);
		Pair<Double, Double> shortRes = getRatioStatus((-maxShortening) / Analyser.TRIES, sphereResults, sphereInfluences);
		Double additionalTime;
		if (lenRes.getFirst() < shortRes.getFirst()) {
			coefficient = lenRes.getFirst();
			additionalTime = lenRes.getSecond();
		} else {
			coefficient = shortRes.getFirst();
			additionalTime = shortRes.getSecond();
		}
		if (additionalTime != 0) {
			successful = true;
			saveSphereInfos(additionalTime);
		}
	}

	private void saveSphereInfos(double additionalTime) {
		userBusyTime += additionalTime;
		Map<SphereName, Double> influences = event.getSpheres();
		for (SphereName sphere : influences.keySet()) {
			double extraSphereTime = influences.get(sphere) * additionalTime;
			sphereResults.get(sphere).saveResults(extraSphereTime, this.userBusyTime);
		}
		additionalEventTime += additionalTime;
	}

	/*
	 * Calculate coefficient of accuracy (how well overall current sphere levels
	 * match to targets for spheres)
	 */
	private double getCurrentRatioStatus(Map<SphereName, SphereInfo> infos, Map<SphereName, Double> influences, double eventExtraTime) {
		double res = 0;
		for (SphereName sphere : infos.keySet()) {
			double extraSphereTime = influences.get(sphere) * eventExtraTime;
			double acc = infos.get(sphere).getRatioAccuracy(extraSphereTime, userBusyTime + eventExtraTime);
			res += acc;
		}
		return res;
	}

	private Pair<Double, Double> getRatioStatus(double step, Map<SphereName, SphereInfo> sphereResults, Map<SphereName, Double> influences) {
		double currentExtraTime = 0;
		double currentStatus = getCurrentRatioStatus(sphereResults, influences, 0.0);
		double prevStatus = currentStatus;
		int max = Analyser.TRIES;
		double timeStep = step;
		if(timeStep > 0 && timeStep < 1){
			timeStep = 1;
			max = (int) Math.round(step*Analyser.TRIES);
		}
		for (int i = 1; i <= max; i++) {
			currentExtraTime = i * timeStep;
			currentStatus = getCurrentRatioStatus(sphereResults, influences, currentExtraTime);
			if (prevStatus <= currentStatus)
				return new Pair<Double, Double>(prevStatus, (double) Math.round(currentExtraTime - timeStep));
			prevStatus = currentStatus;
		}
		return new Pair<Double, Double>(prevStatus, (double) Math.round(currentExtraTime));
	}

	/*
	 * Work out which sphere is most out of line with target. Return all spheres
	 * out of target is full optimisation
	 */
	public List<SphereName> getDeficitSpheres(boolean optimize) {
		List<SphereName> deficits = new LinkedList<SphereName>();
		if (optimize) {
			for (SphereName name : SphereName.values()) {
				if (sphereResults.get(name).getRatioDifference() > 0)
					deficits.add(name);
			}
		} else {
			double max = -1;
			SphereName currentMajor = null;
			for (SphereName name : SphereName.values()) {
				if (!sphereResults.get(name).isWithinConfidenceInterval() && sphereResults.get(name).getRatioDifference() > max) {
					currentMajor = name;
					max = sphereResults.get(currentMajor).getRatioDifference();
				}
			}
			if (currentMajor != null)
				deficits.add(currentMajor);
		}
		return deficits;
	}

	/*
	 * For ordering statuses according to how well they match to targets for
	 * spheres
	 */
	public int compareTo(CalendarStatus next) {
		if (coefficient < next.getCoefficient())
			return -1;
		else if (coefficient > next.getCoefficient())
			return 1;
		else
			return 0;
	}
}