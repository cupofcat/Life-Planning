package com.appspot.iclifeplanning.charts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.appspot.iclifeplanning.charts.utils.DataToJSONConverter;

@SuppressWarnings("serial")
// Used to get data for the pie-chart representing user's life priorities
public class ChosenPrioritiesServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request_, HttpServletResponse response_)
			throws IOException
	{

		// TODO: replace with a call for data
		// Assumption: numbers in the map are percentages, namely 45% is represented as 45, not 0.45.
		// If this is not the case, notify Kamil.
		Map<String, Object> dummy = new HashMap<String, Object>();
		dummy.put("Kamil", 23);
		dummy.put("Madur", 01);
		dummy.put("Makss", 15);
		dummy.put("Bober", 40);
		dummy.put("Rysiu", 21);
		
		// converts a map to two-dimensional array
		JSONArray reply = DataToJSONConverter.convertMapToArray(dummy);
		
		response_.getWriter().print(reply);
	}
}