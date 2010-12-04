package com.appspot.datastore;

import javax.jdo.PersistenceManager;

/* Object for saving into datastore. 
 * Other objects which we wish to save extend this class */
public abstract class BaseDataObject {

	public void makePersistent() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(this);
		}
		finally {
			pm.close();
		}
	}
}
