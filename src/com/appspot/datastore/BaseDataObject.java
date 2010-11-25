package com.appspot.datastore;

import javax.jdo.PersistenceManager;

public abstract class BaseDataObject {

	public void makePersistent(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(this);
		}
		finally{
			pm.close();
		}
	}
	
	
}
