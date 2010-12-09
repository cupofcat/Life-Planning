package com.appspot.datastore;

import java.util.HashMap;

import javax.jdo.annotations.*;

@PersistenceCapable
public class UserDesiredLifeBalance extends BaseDataObject {
	@PrimaryKey
	@Persistent
	private String key;
	@Persistent
	private String userID;
	@Persistent
	private Long dateRegistered;
	@Persistent(serialized="true", defaultFetchGroup = "true")
	private HashMap<SphereName, Double> spherePreferences;
	
	public UserDesiredLifeBalance(String userID, Long dateRegistered,
			HashMap<SphereName, Double> spherePreferences) {
		super();
		this.userID = userID;
		this.spherePreferences = spherePreferences;
		this.dateRegistered = dateRegistered;
		this.key = userID+dateRegistered.toString();
	}

	public HashMap<SphereName, Double> getSpherePreferences() {
		return spherePreferences;
	}

	public long getDateRegistered() {
		return dateRegistered;
	}
}
