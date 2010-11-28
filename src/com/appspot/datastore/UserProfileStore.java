package com.appspot.datastore;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;


public class UserProfileStore {

  public static UserProfile getUserProfile(String userID) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      UserProfile userProfile = pm.getObjectById(UserProfile.class, userID);

      return userProfile;
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }

  public static void addUserProfile(UserProfile profile) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      pm.makePersistent(profile);
    } finally {
      pm.close();
    }
  }
}
