package com.appspot.iclifeplanning.charts.utils;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.*;

import com.appspot.analyser.Pair;
import com.appspot.datastore.SphereName;

@PersistenceCapable
public class WeeklyDataProfile {
	
	@Persistent
	@PrimaryKey
	private String userID;
	@Persistent
	private long startDate;
	@Persistent
	private long endDate;
	@Persistent(serialized="true")
	private HashMap<SphereName, Double> sphereResults;
	@Persistent(serialized="true")
	private HashMap<SphereName, Double> desiredSphereResults;
	
	public WeeklyDataProfile(String userID, long startdate, long endDate, 
			HashMap<SphereName, Double> sphereResults, HashMap<SphereName, Double> desiredSphereResults) {
		super();
		this.userID = userID;
		this.startDate = startdate;
		this.endDate = endDate;
		this.sphereResults = sphereResults;
		this.desiredSphereResults = desiredSphereResults;
	}

	public String getUserID() {
		return userID;
	}

	public long getStartDate() {
		return startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public Map<SphereName, Double> getSphereResults() {
		return sphereResults;
	}

	public HashMap<SphereName, Double> getDesiredSphereResults() {
		return sphereResults;
	}
}
