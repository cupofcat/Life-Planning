package com.appspot.analyser;

import javax.jdo.annotations.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Employee {
	
    @PrimaryKey
	Key key;
	@Persistent
	String name;
	@Persistent
	String surname;
	
	public Employee(String name, String sur){
		key = KeyFactory.createKey(Employee.class.getSimpleName(), name);
		this.name = name;
		surname = sur;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	
}
