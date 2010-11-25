package com.appspot.datastore;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.*;

@PersistenceCapable
public class UserProfile extends BaseDataObject {
	
	@Persistent
	@PrimaryKey
	private String userID;
	@Persistent
	private String name;
	@Persistent
	private String surname;
	@Persistent
	private String email;
	@Persistent(serialized="true")
	private HashMap<SphereName, Double> spherePreferences;
	@Persistent
	private boolean fullyOptimized;
	
	public UserProfile(String userID, String name, String surname, String email, HashMap<SphereName, Double> spherePreferences, boolean fullyOptimized) {
		super();
		this.userID = userID;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.spherePreferences = spherePreferences;
		this.fullyOptimized = fullyOptimized;
	}
	
	

	public void setSpherePreferences(HashMap<SphereName, Double> spherePreferences) {
		this.spherePreferences = spherePreferences;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		//add checks and throw exception
		this.email = email;
	}

	public boolean isFullyOptimized() {
		return fullyOptimized;
	}

	public void setFullyOptimized(boolean fullyOptimized) {
		this.fullyOptimized = fullyOptimized;
	}

	public String getUserID() {
		return userID;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public Map<SphereName, Double> getSpherePreferences() {
		return spherePreferences;
	}
	
	
	
}
