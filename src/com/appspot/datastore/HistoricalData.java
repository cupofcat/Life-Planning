package com.appspot.datastore;

import javax.jdo.annotations.*;

@PersistenceCapable
public class HistoricalData extends BaseDataObject{

	@Persistent
	@PrimaryKey
	private String userID;
	
	
}
