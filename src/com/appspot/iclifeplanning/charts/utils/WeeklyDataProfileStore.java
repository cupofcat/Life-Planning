package com.appspot.iclifeplanning.charts.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereName;


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
  
  public static boolean updateUserWeeklyDataProfile(String userID_, int weekNumber_, HashMap<SphereName, Double> spheresAchievement_)
  {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      WeeklyDataProfile oldProfile = (WeeklyDataProfile)
      				((List<WeeklyDataProfile>)
        		  pm.newQuery("SELECT FROM " + 
        		  WeeklyDataProfile.class.getName() + 
        		  " WHERE userID==\"" + userID_ + "\" && weekNumber==" + weekNumber_ + "").execute()).get(0);
      if(oldProfile == null)
      {
      	return false;
      }
      
      WeeklyDataProfile newProfile = new WeeklyDataProfile(userID_, weekNumber_, spheresAchievement_, oldProfile.getDesiredSphereResults());
      pm.deletePersistent(oldProfile);
      pm.makePersistent(newProfile);

      return true;
    } catch (JDOObjectNotFoundException e) {
      return false;
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
  
  /*
   * Returns number of removed profiles; -1 for error.
   * TODO: Should throw exception instead of -1, feel free to improve it. 
   */
  public static int removeUserWeeklyDataProfiles(String userID) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
    	Collection allProfiles = (Collection) pm.newQuery("SELECT FROM " + 
    		  WeeklyDataProfile.class.getName() + 
    		  " WHERE userID==\"" + userID + "\"").execute();
    	pm.deletePersistentAll(allProfiles);
    	return allProfiles.size();

    } catch (JDOObjectNotFoundException e) {
      return -1;
    } finally {
      pm.close();
    }
  }
}
