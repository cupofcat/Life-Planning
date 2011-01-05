package com.appspot.iclifeplanning.charts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.appspot.iclifeplanning.charts.utils.DataToJSONConverter;

@SuppressWarnings("serial")
// Used to get data for the pie-chart representing user's life priorities
public class ChosenPrioritiesServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request_, HttpServletResponse response_)
			throws IOException
	{
		String userID = request_.getParameter("userName");
		UserProfile userProfile = UserProfileStore.getUserProfile(userID);
		
		if(userProfile==null)
		{
			response_.getWriter().print("{\"error\": \"nullUser\"}");
			return;
		}
		
		// converts a map to two-dimensional array
		JSONArray reply = DataToJSONConverter.convertSphereMapToArray(userProfile.getSpherePreferences());
		response_.getWriter().print(reply);
	}
}