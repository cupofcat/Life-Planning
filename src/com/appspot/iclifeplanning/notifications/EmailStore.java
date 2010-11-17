package com.appspot.iclifeplanning.notifications;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.appspot.datastore.PMF;


public class EmailStore {

  public static String getEmail(String id) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      Email email = pm.getObjectById(Email.class, id);

      return email.getEmail();
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }

  public static void addEmail(String id, String em) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      Email email = new Email(id, em);
      pm.makePersistent(email);
    } finally {
      pm.close();
    }
  }

  public static void deleteEmail(String id) {
	PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
        Email email = pm.getObjectById(Email.class, id);
        pm.deletePersistent(email);
      } catch (JDOObjectNotFoundException e) {
      } finally {
        pm.close();
      }
  }
}
