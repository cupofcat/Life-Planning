package com.appspot.datastore;

import com.appspot.analyser.Analyzer;

public class SphereInfo {
	private double currentRatio;
	private double targetRatio;
	private double sphereTotalTime;

	public SphereInfo(double currentRatio, double targetRatio, double totalTime) {
		this.currentRatio = currentRatio;
		this.targetRatio = targetRatio;
		this.sphereTotalTime = totalTime;
	}

	public double getTargetRatio() {
		return targetRatio;
	}
	
	public double getCurrentRatio() {
		return currentRatio;
	}
	
	public boolean isWithinConfidenceInterval(){
		return isWithinConfidenceInterval(getCurrentRatio());
	}

	private boolean isWithinConfidenceInterval(double ratio) {
		return (Math.abs(getTargetRatio() - ratio ))/getTargetRatio() < Analyzer.CONFIDENCE;
	}
	
	public double getRatioAccuracy(double additionalTime, double totalTime) {
		double newRatio = (sphereTotalTime+additionalTime)/totalTime;
		double result = Math.abs(getTargetRatio() - newRatio);
		if (!isWithinConfidenceInterval(newRatio)) {
			result *= 100000.0;
		}
		return result;
	}
	
	public void saveResults(double additionalTime, double totalTime){
		sphereTotalTime += additionalTime; 
		currentRatio = sphereTotalTime/totalTime;
	}
	
	///////////////////////////////////////////////////
	
//	private double getDuration(double sphereInfluence, double userBusyTime, double multiplier) {
//	double targetRatio = this.targetRatio * multiplier;
//	return (targetRatio*userBusyTime-sphereTotalTime)/(sphereInfluence-targetRatio);
//}

	
	public double getConfidenceTime() {
		return sphereTotalTime *  Analyzer.CONFIDENCE * targetRatio;
	}
	
//	public double getDurationToConfidence(double sphereInfluence, double userBusyTime) {
//	if (currentRatio > targetRatio*(1+Analyzer.CONFIDENCE)) 
//		return getDuration(sphereInfluence, userBusyTime, 1+Analyzer.CONFIDENCE);
//	else
//		return getDuration(sphereInfluence, userBusyTime, 1-Analyzer.CONFIDENCE);
//}
//
//public double getDurationToTarget(double sphereInfluence, double userBusyTime) { 
//	return getDuration(sphereInfluence, userBusyTime, 1);
//}
	
}
