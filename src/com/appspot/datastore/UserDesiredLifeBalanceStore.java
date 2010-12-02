package com.appspot.datastore;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;


public class UserDesiredLifeBalanceStore {
	 public static List<UserDesiredLifeBalance> getUserDesiredLifeBalances(String userID) {
		    PersistenceManager pm = PMF.get().getPersistenceManager();

		    try {
		      List<UserDesiredLifeBalance> dataProfiles 
		          = (List<UserDesiredLifeBalance>) pm.detachCopyAll((List<UserDesiredLifeBalance>)
		        		  pm.newQuery("SELECT FROM " + 
		        		  UserDesiredLifeBalance.class.getName() + 
		        		  " WHERE userID==\"" + userID + "\"" + " ORDER BY weekNumber").execute());

		      return dataProfiles;
		    } catch (JDOObjectNotFoundException e) {
		      System.out.println("error!");
		      return null;
		    } finally {
		      pm.close();
		    }
		  }

		  public static void addDesiredLifeBalance(UserDesiredLifeBalance profile) {
		    PersistenceManager pm = PMF.get().getPersistenceManager();

		    try {
		      pm.makePersistent(profile);
		    } finally {
		      pm.close();
		    }
		  }
}
