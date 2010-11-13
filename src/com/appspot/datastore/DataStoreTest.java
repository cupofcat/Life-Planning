package com.appspot.datastore;

import java.util.Collection;
import java.util.List;

import javax.jdo.PersistenceManager;
import java.util.List;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


public class DataStoreTest {

	public static void store(Integer id, SphereName name){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Integer i = 1;
		try{
		}
		finally{
			pm.close();
		}
	}
	
	public static void retrieve(Integer id, String name){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k = KeyFactory.createKey(SphereChoice.class.getSimpleName(), id.toString() + name);
		SphereChoice l = pm.getObjectById(SphereChoice.class, k);
		System.out.println(l.key + " " + l.value);
		pm.close();
	}
	
	public static boolean checkConsistency(Object[] valuesEntered, Class[] entities, String[] properties){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		for(int i = 0; i< valuesEntered.length; i++){
			Object val = valuesEntered[i];
			Collection list = (Collection) (pm.newQuery("select " + properties[i] + " from " + entities[i].getName()).execute());
			for(Object o : list){
				if(o.equals(val))
					return true;
			}
		}		
		return false;
	}
	
}
