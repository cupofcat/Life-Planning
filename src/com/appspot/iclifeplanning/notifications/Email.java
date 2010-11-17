package com.appspot.iclifeplanning.notifications;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Email {

  @PrimaryKey
  private String id;

  @Persistent
  private String email;

  public Email(String id, String email) {
    this.id = id;
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public String getID() {
	return id;
  }
}