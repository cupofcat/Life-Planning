package com.appspot.datastore;

import com.appspot.analyser.Analyser;

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

	public double getSphereTotalTime() {
		return sphereTotalTime;
	}

	public boolean isWithinConfidenceInterval() {
		return isWithinConfidenceInterval(getCurrentRatio());
	}

	private boolean isWithinConfidenceInterval(double ratio) {
		return (Math.abs(getTargetRatio() - ratio ))/getTargetRatio() < Analyser.CONFIDENCE;
	}

	/* Calculate new ratio for some sphere after new 
	 * duration for some event affecting that sphere */
	public double getRatioAccuracy(double additionalTime, double totalTime) {
		double newRatio = (sphereTotalTime+additionalTime)/totalTime;
		double result = Math.abs(getTargetRatio() - newRatio);
		if (!isWithinConfidenceInterval(newRatio)) {
			/* Decreases chances of scheduling */
			result *= 100000.0;
		}
		return result;
	}

	public void saveResults(double additionalTime, double totalTime) {
		sphereTotalTime += additionalTime; 
		currentRatio = sphereTotalTime/totalTime;
	}

	private double getDuration(double sphereInfluence, double userBusyTime, double multiplier) {
		double targetRatio = this.targetRatio * multiplier;
		return (targetRatio*userBusyTime-sphereTotalTime)/(sphereInfluence-targetRatio);
	}


	public double getConfidenceTime() {
		return sphereTotalTime *  Analyser.CONFIDENCE * targetRatio;
	}

	/* Calculate extra duration for some event until this 
	 * sphere is within confidence interval of the target */
	public double getDurationToConfidence(double sphereInfluence, double userBusyTime) {
		if (currentRatio > targetRatio * (1 + Analyser.CONFIDENCE)) 
			return getDuration(sphereInfluence, userBusyTime, 1 + Analyser.CONFIDENCE);
		else
			return getDuration(sphereInfluence, userBusyTime, 1 - Analyser.CONFIDENCE);
	}

	public double getDurationToTarget(double sphereInfluence, double userBusyTime) { 
		return getDuration(sphereInfluence, userBusyTime, 1);
	}

	public double getRatioDifference() {
		return targetRatio - currentRatio;
	}
}
