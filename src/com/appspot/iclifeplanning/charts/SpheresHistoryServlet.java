package com.appspot.iclifeplanning.charts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.charts.utils.DataToJSONConverter;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfile;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfileStore;

@SuppressWarnings("serial")
public class SpheresHistoryServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request_, HttpServletResponse response_)
			throws IOException
	{
		String userID = request_.getParameter("userName");
		UserProfile userProfile = UserProfileStore.getUserProfile(userID);
		if(userProfile==null)
		{
			return;
		}
		// Get data for all weeks
		List<WeeklyDataProfile> listOfAllWeeks = WeeklyDataProfileStore.getUserWeeklyDataProfiles(userID);
		if(listOfAllWeeks == null || listOfAllWeeks.size()==0)
		{
			return;
		}
		// Extract names of spheres from the first week entry
		Set<SphereName> sphereNamesSet = listOfAllWeeks.get(0).getSphereResults().keySet();
		int numberOfSpheres = sphereNamesSet.size();
		SphereName[] sphereNames = new SphereName[numberOfSpheres];
		// Put names of spheres in an array
		int pos = 0;
		for(SphereName s : sphereNamesSet)
		{
			sphereNames[pos] = s;
			pos++;
		}

		// Two-dimensional array holding weekly data for each sphere. Data for sphere with name
		// in sphereNames[x] is placed in spheresArray[x]
		Double[][] spheresArray = new Double[numberOfSpheres][listOfAllWeeks.size()];
		// Iterate through the list of weekly data and put appropriate numbers in the spheresArray
		for(WeeklyDataProfile wdp : listOfAllWeeks)
		{
			Map<SphereName, Double> sphereResults = wdp.getSphereResults();
			for(int s=0; s<numberOfSpheres; s++)
			{
				spheresArray[s][wdp.getWeekNumber()] = sphereResults.get(sphereNames[s]);
			}
		}
		
		// Array of maps holding series that will be sent to JS
		HashMap<String, Object>[] sphereMaps = new HashMap[numberOfSpheres];
		for(int sphereNumber = 0; sphereNumber<numberOfSpheres; sphereNumber++)
		{
			HashMap<String, Object> currentSphere = new HashMap<String, Object>(5);
			currentSphere.put("type", "area");
			currentSphere.put("name", sphereNames[sphereNumber].toString());
			currentSphere.put("pointInterval", 7 * 24 * 3600 * 1000); // one week
			currentSphere.put("pointStart", userProfile.getJoinTime());
			currentSphere.put("data", spheresArray[sphereNumber]);
			// put the current map in the array of all sphere maps
			sphereMaps[sphereNumber] = currentSphere;
		}
		
		// convert the array to a list
		// TODO: is there a built-in java method for array -> list conversion?
		List<Map<String, Object>> seriesList = new ArrayList<Map<String, Object>>(numberOfSpheres);
		for(int s=0; s<numberOfSpheres; s++)
		{
			seriesList.add(sphereMaps[s]);
		}
		
		Map<String, Object> allData = new TreeMap<String, Object>();
		allData.put("series", seriesList);
		
		JSONObject reply = DataToJSONConverter.convertMapToMap(allData); 
		
		response_.getWriter().print(reply);
	}
}
