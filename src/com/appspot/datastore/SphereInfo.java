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

	public double getConfidenceTime() {
		return sphereTotalTime *  Analyzer.CONFIDENCE * targetRatio;
	}

	public boolean isWithinConfidenceInterval() {
		return this.isWithinConfidenceInterval(currentRatio);
	}
	
//	public double getDurationToConfidence(double sphereInfluence, double userBusyTime) {
//		if (currentRatio > targetRatio*(1+Analyzer.CONFIDENCE)) 
//			return getDuration(sphereInfluence, userBusyTime, 1+Analyzer.CONFIDENCE);
//		else
//			return getDuration(sphereInfluence, userBusyTime, 1-Analyzer.CONFIDENCE);
//	}
//
//	public double getDurationToTarget(double sphereInfluence, double userBusyTime) { 
//		return getDuration(sphereInfluence, userBusyTime, 1);
//	}
	
	public double getNewRatio(double additionalTime, double userBusyTime) {
		return (sphereTotalTime+additionalTime)/userBusyTime;
	}
	
	public double getRatioAccuracy(double additionalTime, double userBusyTime) {
		double newRatio = getNewRatio(additionalTime, userBusyTime);
		double result = newRatio-targetRatio;
		if (!isWithinConfidenceInterval() && isWithinConfidenceInterval(newRatio)) {
			result *= 0.001;
		}
		else if (isWithinConfidenceInterval() && !isWithinConfidenceInterval(newRatio)) {
			result *= 100.0;
		}
		return Math.abs(result);
	}
	
	private double getRatioDifference() {
		return targetRatio - currentRatio;
	}
	
	public boolean isWithinConfidenceInterval(double ratio){
		return (Math.abs(ratio - targetRatio) )/targetRatio < Analyzer.CONFIDENCE;
	}
	
//	private double getDuration(double sphereInfluence, double userBusyTime, double multiplier) {
//		double targetRatio = this.targetRatio * multiplier;
//		return (targetRatio*userBusyTime-sphereTotalTime)/(sphereInfluence-targetRatio);
//	}
}
