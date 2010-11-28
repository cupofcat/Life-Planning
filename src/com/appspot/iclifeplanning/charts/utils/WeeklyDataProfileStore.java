package com.appspot.iclifeplanning.charts.utils;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.appspot.datastore.PMF;


public class WeeklyDataProfileStore {

  public static WeeklyDataProfile getUserProfile(String userID) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      WeeklyDataProfile userProfile = pm.getObjectById(WeeklyDataProfile.class, userID);
      return userProfile;
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }

  public static void addUserProfile(WeeklyDataProfile profile) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      pm.makePersistent(profile);
    } finally {
      pm.close();
    }
  }
}
