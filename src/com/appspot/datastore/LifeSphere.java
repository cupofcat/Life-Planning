package com.appspot.datastore;

import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.*;

@PersistenceCapable(detachable="true")
public class LifeSphere {

	@PrimaryKey
	Key key;
	
	@Persistent(nullValue = NullValue.EXCEPTION)
	private SphereName name;
	
	@Persistent
	private int priority;
	
	public LifeSphere(SphereName name){
		this.name = name;
		priority = 1;
	}
	
	public LifeSphere(SphereName name, int priority){
		key = KeyFactory.createKey(LifeSphere.class.getSimpleName(), name.toString());
		this.name = name;
		this.priority = priority;
	}
	
	public SphereName getName(){
		return name;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public void setPriority(int param){
		priority = param;
	}
}
