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
		
		Integer[] data0a = {null, null, null, null, null, 6 , 11, 32, 110, 235, 369, 640, 
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
		
		String sphere = request_.getParameter("sphere");
		
		int[] planned = null;
		int plan = 20000;
		
		Map<String, Object> dummy1 = new HashMap<String, Object>();
		dummy1.put("name", "Planned");
		Map<String, Object> dummy0 = new HashMap<String, Object>();
		dummy0.put("name", "Achieved");
		
		// enables getting data for any sphere specified as a parameter in the getJSON method in JS
		if("Russia".equals(sphere))
		{
			planned = new int[data1a.length];
			dummy0.put("data", data1a);
		}
		else
		{
			planned = new int[data0a.length];
			dummy0.put("data", data0a);
		}
		
		for(int i=0; i<planned.length; i++)
		{
			planned[i] = plan;
		}
		dummy1.put("data", planned);
		
		List datas = new ArrayList(3);
		datas.add(dummy1);
		datas.add(dummy0);
		
		Map<String, Object> allData = new TreeMap<String, Object>();
		allData.put("series", datas);
		
		// converts a map to two-dimensional array
		JSONObject reply = DataToJSONConverter.convertMapToMap(allData);
		
		response_.getWriter().print(reply);
	}
}