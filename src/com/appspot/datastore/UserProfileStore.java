package com.appspot.datastore;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

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
}
