package com.appspot.iclifeplanning.charts.utils;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.appspot.datastore.PMF;


public class WeeklyDataProfileStore {

  public static List<WeeklyDataProfile> getUserWeeklyDataProfiles(String userID) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      List<WeeklyDataProfile> dataProfiles 
          = (List<WeeklyDataProfile>) pm.detachCopyAll((List<WeeklyDataProfile>)
        		  pm.newQuery("SELECT FROM " + 
        		  WeeklyDataProfile.class.getName() + 
        		  " WHERE userID==\"" + userID + "\"" + " ORDER BY weekNumber").execute());

      return dataProfiles;
    } catch (JDOObjectNotFoundException e) {
      System.out.println("error!");
      return null;
    } finally {
      pm.close();
    }
  }

  public static void addWeeklyDataProfile(WeeklyDataProfile profile) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      pm.makePersistent(profile);
    } finally {
      pm.close();
    }
  }
}
