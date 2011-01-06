package com.appspot.iclifeplanning.charts.utils;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.*;

import com.appspot.datastore.SphereName;

@PersistenceCapable
public class WeeklyDataProfile {
	
	@PrimaryKey
	@Persistent
	private String key;
	@Persistent
	private String userID;
	@Persistent
	private Integer weekNumber;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private HashMap<SphereName, Double> sphereResults;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private HashMap<SphereName, Double> desiredSphereResults;
	
	public WeeklyDataProfile(String userID, Integer weekNumber, HashMap<SphereName, Double> sphereResults, 
			HashMap<SphereName, Double> desiredSphereResults) {
		super();
		this.userID = userID;
		this.sphereResults = sphereResults;
		this.desiredSphereResults = desiredSphereResults;
		this.weekNumber = weekNumber;
		this.key = userID + weekNumber.toString();
	}

	public String getUserID() {
		return userID;
	}

	public Map<SphereName, Double> getSphereResults() {
		return sphereResults;
	}

	public HashMap<SphereName, Double> getDesiredSphereResults() {
		return desiredSphereResults;
	}

	public int getWeekNumber() {
		return weekNumber;
	}
}
