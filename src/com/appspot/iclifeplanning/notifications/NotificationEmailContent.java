package com.appspot.iclifeplanning.notifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.appspot.analyser.Suggestion;
import com.appspot.datastore.SphereName;

public class NotificationEmailContent implements EmailContent {

	private boolean isEmpty = true;
	private List<List<Suggestion>> suggestions;
    private HashMap<SphereName, Double> desiredLifeBalance;
    private HashMap<SphereName, Double> currentLifeBalance;
    private String userName;

    public NotificationEmailContent(List<List<Suggestion>> suggestions, 
    		HashMap<SphereName, Double> desiredLifeBalance, 
    		HashMap<SphereName, Double> currentLifeBalance, String userName) {
    	isEmpty = false;
    	this.suggestions = suggestions;
    	this.desiredLifeBalance = desiredLifeBalance;
    	this.currentLifeBalance = currentLifeBalance;
    }

    public boolean isEmpty() {
    	return isEmpty;
    }

    public String toString() {
    	String title = "Hi " + userName + "!\n";
    	String openingLine = "This is a regular update from your Life Planning utility.";
    	String headerCurrent = "Your current life balance is: \n";
    	String currentLifeBalanceDescription = "";
    	for (Entry<SphereName, Double> sphere : currentLifeBalance.entrySet()) {
    		currentLifeBalanceDescription += sphere.getKey().toString();
    		currentLifeBalanceDescription += ": ";
    		currentLifeBalanceDescription += sphere.getValue().toString();
    		currentLifeBalanceDescription += "%\n";
    	}
    	currentLifeBalanceDescription += "\n";
    	
    	String headerDesired = "Your desired life balance is: \n";
    	String desiredLifeBalanceDescription = "";
    	for (Entry<SphereName, Double> sphere : desiredLifeBalance.entrySet()) {
    		desiredLifeBalanceDescription += sphere.getKey().toString();
    		desiredLifeBalanceDescription += ": ";
    		desiredLifeBalanceDescription += sphere.getValue().toString();
    		desiredLifeBalanceDescription += "%\n";
    	}
    	desiredLifeBalanceDescription += "\n";
    	
    	String headerSuggestions = "Here are some of our suggestions how to improve: \n";
    	//TODO(amadurska): Describe suggestions (should have toString implemented?
    	
    	String headerGreetings = "Have an optimized day!\n";
    	String signature = "Your Life Planning Team\n";
    	String result = title + openingLine 
    	    + headerCurrent + currentLifeBalanceDescription 
    	    + headerDesired + desiredLifeBalanceDescription
    	    + headerSuggestions + headerGreetings + signature;
    	return result;
    }
}
