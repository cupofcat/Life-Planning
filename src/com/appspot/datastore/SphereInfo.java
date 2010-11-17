package com.appspot.datastore;

import com.appspot.analyser.Analyzer;

public class SphereInfo {
	private double currentRatio;
	private double targetRatio;
	private double sphereTotalTime;

	public SphereInfo(double currentRatio, double targetRatio, double totalTime) {
		super();
		this.currentRatio = currentRatio;
		this.targetRatio = targetRatio;
		this.sphereTotalTime = totalTime;
	}

	public double getTargetRatio() {
		return targetRatio;
	}

	public double getTimeDifference() {
		return Math.round(getRatioDifference() * sphereTotalTime);
	}

	public double getCurrentRatio() {
		return currentRatio;
	}

	public void updateCurrentRatio(double time){
		sphereTotalTime += time;
		currentRatio += time/sphereTotalTime;

	}

	public double getRatioDifference() {
		return targetRatio - currentRatio;
	}

	public double getConfidenceTime(){
		return sphereTotalTime *  Analyzer.CONFIDENCE * targetRatio;
	}

	public boolean isWithinConfidenceInterval(){
		return (Math.abs(currentRatio - targetRatio) )/targetRatio < Analyzer.CONFIDENCE;
	}

	public double getDurationToConfidence(double sphereInfluence, double userBusyTime) {
		if (currentRatio > targetRatio*(1+Analyzer.CONFIDENCE)) 
			return getDuration(sphereInfluence, userBusyTime, 1+Analyzer.CONFIDENCE);
		else
			return getDuration(sphereInfluence, userBusyTime, 1-Analyzer.CONFIDENCE);
	}

	public double getDurationToTarget(double sphereInfluence, double userBusyTime) { 
		return getDuration(sphereInfluence, userBusyTime, 1);
	}

	private double getDuration(double sphereInfluence, double userBusyTime, double multiplier) {
		double targetRatio = this.targetRatio * multiplier;
		return (targetRatio*userBusyTime-sphereTotalTime)/(sphereInfluence-targetRatio);
	}
	
	public double getRatioAccuracy(double additionalTime, double userBusyTime) {
		return Math.abs((sphereTotalTime+additionalTime)/userBusyTime-targetRatio);
	}
}
