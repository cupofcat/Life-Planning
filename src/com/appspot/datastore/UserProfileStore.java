package com.appspot.datastore;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfile;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfileStore;


/* Retrieve user profile from datastore */
public class UserProfileStore {

	public static UserProfile getUserProfile(String userID) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			UserProfile userProfile = pm.detachCopy(pm.getObjectById(UserProfile.class, userID));
			return userProfile;
		} 
		catch (JDOObjectNotFoundException e) {
			return null;
		} 
		finally {
			pm.close();
		}
	}

  
  /*
   * Returns all users. Useful for debugging.
   */
  public static List<UserProfile> getAllUserProfiles()
  {
  	PersistenceManager pm = PMF.get().getPersistenceManager();

    try
    {
    	List<UserProfile> userProfiles = (List<UserProfile>) pm.detachCopyAll((List<UserProfile>)
    			pm.newQuery("SELECT FROM " + UserProfile.class.getName() + " ORDER BY userID").execute());

      return userProfiles;
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }
  
  /*
   * Deletes given user from the datastore. Deletes their WeeklyProfiles as well.
   * Returns true on success.
   */
  public static String deleteUserProfile(String userID) {
    PersistenceManager pm = PMF.get().getPersistenceManager();

    String status = "";
    try
    {
    	// delete user
    	pm.deletePersistent(pm.getObjectById(UserProfile.class, userID));
    	status = status.concat("Successfully removed user: " + userID + "\n");
    }
    catch (JDOObjectNotFoundException e)
    {
    	status = status.concat("Failed to remove user: " + userID + "\n");
    }
    finally
    {
      pm.close();
    }
   	// delete their WeeklyProfiles 
   	int profilesRemoved = WeeklyDataProfileStore.removeUserWeeklyDataProfiles(userID);
   	if(profilesRemoved>=0)
   	{
   		status = status.concat("Successfully removed " + profilesRemoved + " user's Weekly Profiles.");
   	}
   	else
   	{
   		status = status.concat("Failed to remove user's Weekly Profiles.");
   	}
   	
    return status;
  }
}
