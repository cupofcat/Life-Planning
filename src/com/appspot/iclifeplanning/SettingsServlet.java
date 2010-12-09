package com.appspot.iclifeplanning;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserDesiredLifeBalance;
import com.appspot.datastore.UserDesiredLifeBalanceStore;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;
import com.google.appengine.repackaged.org.json.JSONArray;
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

		try {
			JSONObject settingsJSON = new JSONObject(request.getReader().readLine());
			String userID = settingsJSON.getString("userID");
			boolean fullOpt = settingsJSON.getBoolean("fullOpt");
			JSONArray spheres = settingsJSON.getJSONArray("spheresSettings");
			HashMap<SphereName, Double> spherePreferences = new HashMap<SphereName, Double>();
			JSONObject preference;
			String sphere;

			for (int i = 0 ; i < spheres.length(); i++) {
				preference = spheres.getJSONObject(i);
				sphere = preference.getString("name");
				spherePreferences.put(SphereName.getSphereName(sphere), preference.getDouble("value"));
			}

			UserProfile userProfile = UserProfileStore.getUserProfile(userID);
			userProfile.setFullyOptimized(fullOpt);
			userProfile.setSpherePreferences(spherePreferences);
			userProfile.makePersistent();
			long now = Calendar.getInstance().getTimeInMillis();
			UserDesiredLifeBalanceStore
			    .addDesiredLifeBalance(new UserDesiredLifeBalance(userID, now, spherePreferences));
		} catch (JSONException e) {
			System.out.println("Badly formatted JSON!");
			e.printStackTrace();
		}
	}
}
