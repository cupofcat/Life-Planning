package com.appspot.iclifeplanning.charts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appspot.iclifeplanning.charts.utils.DataToJSONConverter;

@SuppressWarnings("serial")
// Used to get data for the pie-chart representing user's life priorities
public class PlanAchievementServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request_, HttpServletResponse response_)
			throws IOException
	{
		// TODO: replace with a call for data
		
		// enables getting data for any sphere specified as a parameter in the getJSON method in JS
		// String sphere = request_.getParameter("sphere");
				
		Integer[] data2a = {null, null, null, null, null, 6 , 11, 32, 110, 235, 369, 640, 
				1005, 1436, 2063, 3057, 4618, 6444, 9822, 15468, 20434, 24126, 
				27387, 29459, 31056, 31982, 32040, 31233, 29224, 27342, 26662, 
				26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 
				24304, 23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 
				22380, 21004, 17287, 14747, 13076, 12555, 12144, 11009, 10950, 
				10871, 10824, 10577, 10527, 10475, 10421, 10358, 10295, 10104};
		
		Integer[] data1a = {null, null, null, null, null, null, null , null , null ,null, 
			5, 25, 50, 120, 150, 200, 426, 660, 869, 1060, 1605, 2471, 3322, 
			4238, 5221, 6129, 7089, 8339, 9399, 10538, 11643, 13092, 14478, 
			15915, 17385, 19055, 21205, 23044, 25393, 27935, 30062, 32049, 
			33952, 35804, 37431, 39197, 45000, 43000, 41000, 39000, 37000, 
			35000, 33000, 31000, 29000, 27000, 25000, 24000, 23000, 22000, 
			21000, 20000, 19000, 18000, 18000, 17000, 16000};
		
		
		int[] planned1 = null;
		int[] planned2 = null;
		int plan = 20000;
		int plan2 = 15000;
		
		
		/*
		 * Sphere 1
		 */
		Map<String, Object> plannedMap1 = new HashMap<String, Object>();
		plannedMap1.put("name", "Planned1");
		planned1 = new int[data1a.length];
		for(int i=0; i<planned1.length; i++)
		{
			planned1[i] = plan;
		}
		plannedMap1.put("data", planned1);
		
		Map<String, Object> achievedMap1 = new HashMap<String, Object>();
		achievedMap1.put("name", "Achieved1");
		achievedMap1.put("data", data1a);
		
		List<Map<String, Object>> sphere1 = new ArrayList<Map<String, Object>>(3);
		sphere1.add(plannedMap1);
		sphere1.add(achievedMap1);
		
		Map<String, Object> sphere1Map = new HashMap<String, Object>();
		sphere1Map.put("sphere", "sport");
		sphere1Map.put("series", sphere1);
		
		/*
		 * Sphere 2
		 */
		Map<String, Object> plannedMap2 = new HashMap<String, Object>();
		plannedMap2.put("name", "Planned2");
		planned2 = new int[data2a.length];
		for(int i=0; i<planned2.length; i++)
		{
			planned2[i] = plan2;
		}
		plannedMap2.put("data", planned2);
		
		Map<String, Object> achievedMap2 = new HashMap<String, Object>();
		achievedMap2.put("name", "Achieved2");
		achievedMap2.put("data", data2a);
		
		List<Map<String, Object>> sphere2 = new ArrayList<Map<String, Object>>(3);
		sphere2.add(plannedMap2);
		sphere2.add(achievedMap2);
		
		Map<String, Object> sphere2Map = new HashMap<String, Object>();
		sphere2Map.put("sphere", "family");
		sphere2Map.put("series", sphere2);
		
		/*
		 * All spheres
		 */
		List<Map<String, Object>> allSpheres = new ArrayList<Map<String, Object>>(5);
		allSpheres.add(sphere1Map);
		allSpheres.add(sphere2Map);
		
		// map of everything that is sent to the browser
		Map<String, Object> allData = new TreeMap<String, Object>();
		allData.put("spheres", allSpheres);
		
		// converts a java map to JSON map
		JSONObject reply = DataToJSONConverter.convertMapToMap(allData);
		
		response_.getWriter().print(reply);
	}
}