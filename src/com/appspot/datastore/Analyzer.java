package com.appspot.datastore;

import java.io.IOException;
import java.util.*;

import com.appspot.iclifeplanning.events.*;

public class Analyzer {
	
	private double[] times;
	private double[] currentRatios;
	
	public Analyzer(){
		times = new double[SphereName.values().length];
		currentRatios = new double[times.length];
	}
	
	public String checkGoals(Collection<? extends EventInterface> events, double[] choices){		
//		for(EventInterface event : events){
//			Calendar startTime = new GregorianCalendar();
//			startTime.setTimeInMillis(event.getStartTime().getValue());
//			Calendar endTime = new GregorianCalendar();
//			endTime.setTimeInMillis(event.getEndTime().getValue() + 60*60*1000);
//			//jebane strefy czasowe........
//			int durationInMins = getTimeDifference(startTime, endTime);
//			Map<SphereName, Integer> sphereResults = event.getSpheres();
//			Set<SphereName> keys = sphereResults.keySet();
//			for(SphereName key : keys){
//				double time = ( Double.valueOf(sphereResults.get(key))/100) * durationInMins;
//				times[key.ordinal()] += time;
//			}
//		}
//		double sum = 0;
//		for(double val : times)
//			sum += val;
//		for(int i = 0; i< times.length; i++)
//			currentRatios[i] = times[i]/sum;
//		String res = "";
//		
//		for(int i = 0; i < times.length; i++){
//			res += currentRatios[i] + "  " + choices[i];
//			//res += currentRatios[i] != choices[i] ? "Spierdalaj" : "Super";
//			
//		}
		return null;
	}
	
	private int getTimeDifference(Calendar start, Calendar 	end){
		return (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)) * 1440
		       + (end.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY)) * 60
		       + (end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE));
	}

}
