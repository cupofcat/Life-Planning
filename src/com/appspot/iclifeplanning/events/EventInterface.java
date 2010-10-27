package com.appspot.iclifeplanning.events;

import java.util.Set;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.TextConstruct;

public interface EventInterface {

	public TextConstruct getDescription();
	public void setDescription(TextConstruct description);
	public DateTime getStartTime();
	public void setStartTime(DateTime startTime);
	public DateTime getEndTime();
	public void setEndTime(DateTime endTime);
	public Set<String> getKeywords();
	public void setKeywords(Set<String> keywords);

}
