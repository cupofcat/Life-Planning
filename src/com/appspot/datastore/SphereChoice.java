package com.appspot.datastore;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable="true")
public class SphereChoice {
	
	@PrimaryKey
	com.google.appengine.api.datastore.Key key;
	
	@Persistent
	private String userID;
	
	
	@Persistent
	private SphereName sphereName;
	
	@Persistent
	public double value;
	
	public SphereChoice(String userID, SphereName name, double value){
		Key k = KeyFactory.createKey(SphereChoice.class.getSimpleName(), userID.toString() + name);
		key = k;
		this.userID = userID;
		sphereName = name;
		this.value = value;
	}
	
	public void setKey(com.google.appengine.api.datastore.Key key){
		this.key = key;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public com.google.appengine.api.datastore.Key getKey() {
		return key;
	}

	public String getUserID() {
		return userID;
	}

	public SphereName getSphereName() {
		return sphereName;
	}
	
	
	
}