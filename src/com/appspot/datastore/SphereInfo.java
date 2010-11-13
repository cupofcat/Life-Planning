package com.appspot.datastore;

public class SphereInfo {

	private SphereName name;
	
	private double currentRatio;
	private double targetRatio;
	private double timeDifference;

	public SphereInfo(SphereName name, double currentRatio, double targetRatio) {
		super();
		this.name = name;
		this.currentRatio = currentRatio;
		this.targetRatio = targetRatio;

	}

	public double getTargetRatio() {
		return targetRatio;
	}

	public void setTargetRatio(double targetRatio) {
		this.targetRatio = targetRatio;
	}

	public double getTimeDifference() {
		return timeDifference;
	}

	public SphereName getName() {
		return name;
	}

	public double getCurrentRatio() {
		return currentRatio;
	}

	public double getRatioDifference() {
		return targetRatio - currentRatio;
	}
	
	public void setTimeDifference(double diff){
		this.timeDifference = diff;
	}
	
	public boolean isWithinConfidenceInterval(){
		return (Math.abs(currentRatio - targetRatio) )/targetRatio < Analyzer.CONFIDENCE;
	}
	
	public String toString(){
		return name + " " + currentRatio + " " + targetRatio + " " + timeDifference;
	}
}
