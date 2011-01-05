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

import org.json.JSONArray;
import org.json.JSONObject;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.charts.utils.DataToJSONConverter;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfile;
import com.appspot.iclifeplanning.charts.utils.WeeklyDataProfileStore;

@SuppressWarnings("serial")
// Used to get data for the pie-chart representing user's life priorities
public class PlanAchievementServlet extends HttpServlet
{
	private final static int ACHIEVED = 0;
	private final static int PLANNED = 1;

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
		if(listOfAllWeeks==null || listOfAllWeeks.size()==0)
		{
			response_.getWriter().print("{\"error\": \"nullData\"}");
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

		// Three-dimensional array holding weekly data for each sphere. Data for sphere with name
		// in sphereNames[x] is placed in spheresArray[x]
		// second dimension distinguishes between planned and achieved data
		Double[][][] spheresArray = new Double[numberOfSpheres][2][listOfAllWeeks.size()];
		// Iterate through the list of weekly data and put appropriate numbers in the spheresArray
		for(WeeklyDataProfile wdp : listOfAllWeeks)
		{
			Map<SphereName, Double> sphereResults = wdp.getSphereResults();
			Map<SphereName, Double> desiredSphereResults = wdp.getDesiredSphereResults();
			for(int s=0; s<numberOfSpheres; s++)
			{
				spheresArray[s][ACHIEVED][wdp.getWeekNumber()] = sphereResults.get(sphereNames[s]);
				spheresArray[s][PLANNED][wdp.getWeekNumber()] = desiredSphereResults.get(sphereNames[s]);
			}
		}
		
		// Array of maps holding series that will be sent to JS
		HashMap<String, Object>[] sphereMaps = new HashMap[numberOfSpheres];
		
		for(int sphereNumber = 0; sphereNumber<numberOfSpheres; sphereNumber++)
		{
			HashMap<String, Object> plannedMap = new HashMap<String, Object>(2);
			plannedMap.put("name", "Planned " + sphereNames[sphereNumber].toString());
			plannedMap.put("pointInterval", 7 * 24 * 3600 * 1000); // one week
			plannedMap.put("pointStart", userProfile.getJoinTime());
			plannedMap.put("data", spheresArray[sphereNumber][PLANNED]);
			
			
			HashMap<String, Object> achievedMap = new HashMap<String, Object>(2);
			achievedMap.put("name", "Achieved " + sphereNames[sphereNumber].toString());
			achievedMap.put("pointInterval", 7 * 24 * 3600 * 1000); // one week
			achievedMap.put("pointStart", userProfile.getJoinTime());
			achievedMap.put("data", spheresArray[sphereNumber][ACHIEVED]);
			
			// put the current map in the array of all sphere maps
			List<Map<String, Object>> currentSphere = new ArrayList<Map<String, Object>>(2);
			currentSphere.add(plannedMap);
			currentSphere.add(achievedMap);
			
			sphereMaps[sphereNumber] = new HashMap<String, Object>(2);
			sphereMaps[sphereNumber].put("sphereName", sphereNames[sphereNumber].toString());
			sphereMaps[sphereNumber].put("series", currentSphere);
		}
		
		// convert the array to a list
		List<Map<String, Object>> allSpheres = new ArrayList<Map<String, Object>>(numberOfSpheres);
		for(int s=0; s<numberOfSpheres; s++)
		{
			allSpheres.add(sphereMaps[s]);
		}
		
		// map of everything that is sent to the browser
		Map<String, Object> allData = new TreeMap<String, Object>();
		allData.put("spheres", allSpheres);
		
		// converts a java map to JSON map
		JSONObject reply = DataToJSONConverter.convertMapToMap(allData);
		
		response_.getWriter().print(reply);
	}
}