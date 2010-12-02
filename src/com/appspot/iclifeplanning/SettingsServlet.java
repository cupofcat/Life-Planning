package com.appspot.iclifeplanning;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

/**
 * Settings servlet. 
 * 
 * @author Agnieszka Magda Madurska (amm208@doc.ic.ac.uk)
 * 
 */
@SuppressWarnings("serial")
public class SettingsServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		JSONObject settingsJSON = null;

		try {
			settingsJSON = new JSONObject(request.getReader().readLine());
			String userID = settingsJSON.getString("userID");
			boolean fullOpt = settingsJSON.getBoolean("fullOpt");
			// TODO(amadurska): Get this from JSON
			HashMap<SphereName, Double> spherePreferences = new HashMap<SphereName, Double>();
			UserProfile userProfile = UserProfileStore.getUserProfile(userID);
			userProfile.setFullyOptimized(fullOpt);
			
			userProfile.makePersistent();
		} catch (JSONException e) {
			System.out.println("Badly formatted JSON!");
			e.printStackTrace();
		}
	}
}
