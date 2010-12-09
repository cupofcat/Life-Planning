package com.appspot.datastore;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Token {

	@PrimaryKey
	private String id;

	@Persistent
	private String token;

	public Token(String id, String token) {
		this.id = id;
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}