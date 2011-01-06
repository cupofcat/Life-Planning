package com.appspot.iclifeplanning.charts.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;

@SuppressWarnings("serial")
public class AddUserDataServlet extends HttpServlet
{
	// these parameters describe how far can the random spheres achievement be from the given values
	static double UNOPTIMISED_DISPERSION = 0.06;
	static double OPTIMISED_DISPERSION = 0.017;
	
	public void doGet(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		String p = request_.getParameter("action");
		if("addUser".equals(p))
		{
			doAddUser(request_, response_);
		}
		else if("addData".equals(p))
		{
			doAddData(request_, response_);
		}
		else if("alterWeek".equals(p))
		{
			doAlterWeek(request_, response_);
		}
		else if("displayData".equals(p))
		{
			doDisplayData(request_, response_);
		}
		else if("displayUsers".equals(p))
		{
			doDisplayUsers(request_, response_);
		}
		else if("deleteUser".equals(p))
		{
			doDeleteUser(request_, response_);
		}
		else if("deleteData".equals(p))
		{
			doDeleteData(request_, response_);
		}
		else
		{
			response_.getWriter().print("Unknown action.");
		}
	}

	private void doAddUser(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		String name = request_.getParameter("name");
		int year = Integer.parseInt(request_.getParameter("year"));
		int month = Integer.parseInt(request_.getParameter("month"));
		int day = Integer.parseInt(request_.getParameter("day"));
		Double work = Double.parseDouble(request_.getParameter("work"));
		Double health = Double.parseDouble(request_.getParameter("health"));
		Double family = Double.parseDouble(request_.getParameter("family"));
		addUser(id, name, year, month, day, work, health, family);
		response_.getWriter().print("Added user.");
	}
	
	private void doAddData(HttpServletRequest request_,	HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		Double work = Double.parseDouble(request_.getParameter("work"));
		Double health = Double.parseDouble(request_.getParameter("health"));
		Double family = Double.parseDouble(request_.getParameter("family"));
		int weeks = Integer.parseInt(request_.getParameter("weeks"));
		int weeksOptimised = Integer.parseInt(request_.getParameter("weeksOptimised"));
		addData(id, work, health, family, weeks, weeksOptimised);
		response_.getWriter().print("Added data.");
	}
	
	private void doDisplayData(HttpServletRequest request_,	HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		if(id==null)
		{
			return;
		}
		List<WeeklyDataProfile> l = WeeklyDataProfileStore.getUserWeeklyDataProfiles(id);
		if(l==null)
		{
			return;
		}
		List<String> reply = new ArrayList<String>(l.size());
		for(WeeklyDataProfile wdp : l)
		{
			reply.add("Week no: " + wdp.getWeekNumber() + ", recreation value: " + wdp.getSphereResults().get(SphereName.RECREATION) + "\n");
		}
		response_.getWriter().print(new JSONArray(reply));
	}
	
	private void doDisplayUsers(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		List<UserProfile> allUsers = UserProfileStore.getAllUserProfiles();
		String reply = "Current users: \n";
		for(UserProfile u : allUsers)
		{
			reply = reply.concat(u.getUserID() + "\n");
		}
		response_.getWriter().print(reply);
	}
	
	private void doDeleteUser(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		if(id==null)
		{
			return;
		}
		response_.getWriter().print(UserProfileStore.deleteUserProfile(id));
	}

	private void doDeleteData(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		if(id==null)
		{
			return;
		}
		int profilesRemoved = WeeklyDataProfileStore.removeUserWeeklyDataProfiles(id);
		if(profilesRemoved>=0)
   	{
			response_.getWriter().print("Successfully removed " + profilesRemoved + " user's Weekly Profiles.");
   	}
   	else
   	{
   		response_.getWriter().print("Failed to remove user's Weekly Profiles.");
   	}
	}
	
	private void doAlterWeek(HttpServletRequest request_,	HttpServletResponse response_) throws IOException
	{
		String id = request_.getParameter("id");
		Double work = Double.parseDouble(request_.getParameter("work"));
		Double health = Double.parseDouble(request_.getParameter("health"));
		Double family = Double.parseDouble(request_.getParameter("family"));
		int week = Integer.parseInt(request_.getParameter("week"));
		
		HashMap<SphereName, Double> spheresAchievement = new HashMap<SphereName, Double>(5);
		spheresAchievement.put(SphereName.WORK,work);
		spheresAchievement.put(SphereName.HEALTH, health);
		spheresAchievement.put(SphereName.FAMILY, family);
		spheresAchievement.put(SphereName.RECREATION, 1 - (work + health + family));
		
		if(WeeklyDataProfileStore.updateUserWeeklyDataProfile(id, week, spheresAchievement))
		{
			response_.getWriter().print("Successfully modified data for week " + week + ".");
		}
		else
		{
			response_.getWriter().print("Failed to add data.");
		}
	}

	public static void addUser(String id_, String name_, int year_, int month_, int day_, double work_, double health_, double family_)
	{
		HashMap<SphereName, Double> spheresAssignment = new HashMap<SphereName, Double>(5);
		spheresAssignment.put(SphereName.WORK, work_);
		spheresAssignment.put(SphereName.HEALTH, health_);
		spheresAssignment.put(SphereName.FAMILY, family_);
		spheresAssignment.put(SphereName.RECREATION, 1 - (work_ + health_ + family_));
		
		UserProfile temp = new UserProfile(id_, name_, id_ + "@doc", spheresAssignment, true, new GregorianCalendar(year_, month_, day_).getTimeInMillis());
		
		temp.makePersistent();
	}
	
	public static void addData(String userID_, Double work_, Double health_, Double family_, int weeks_, int weeksOptimised_)
	{
		HashMap<SphereName, Double> spheresAssignment = new HashMap<SphereName, Double>(5);
		spheresAssignment.put(SphereName.WORK, work_);
		spheresAssignment.put(SphereName.HEALTH, health_);
		spheresAssignment.put(SphereName.FAMILY, family_);
		spheresAssignment.put(SphereName.RECREATION, 1 - (work_ + health_ + family_));
		
		double ranWork = work_;
		double ranHealth = health_;
		double ranFamily = family_;
		double ranRecreation = 1 - (work_ + health_ + family_);
		
		for(int i=0; i<weeks_-weeksOptimised_; i++)
		{
			ranWork =  getRandom(work_, UNOPTIMISED_DISPERSION, ranWork);
			ranHealth =  getRandom(health_, UNOPTIMISED_DISPERSION, ranHealth);
			ranFamily =  getRandom(family_, UNOPTIMISED_DISPERSION, ranFamily);
			ranRecreation = 1 - ranWork - ranHealth - ranFamily;
			
			HashMap<SphereName, Double> spheresAchievement = new HashMap<SphereName, Double>(5);
			spheresAchievement.put(SphereName.WORK,ranWork);
			spheresAchievement.put(SphereName.HEALTH, ranHealth);
			spheresAchievement.put(SphereName.FAMILY, ranFamily);
			spheresAchievement.put(SphereName.RECREATION, ranRecreation);
			
			WeeklyDataProfileStore.addWeeklyDataProfile(new WeeklyDataProfile(userID_, i, spheresAchievement, spheresAssignment));
		}
		
		for(int i=weeks_-weeksOptimised_; i<weeks_; i++)
		{
			ranWork =  getRandom(work_, OPTIMISED_DISPERSION, ranWork);
			ranHealth =  getRandom(health_, OPTIMISED_DISPERSION, ranHealth);
			ranFamily =  getRandom(family_, OPTIMISED_DISPERSION, ranFamily);
			ranRecreation = 1 - ranWork - ranHealth - ranFamily;
			
			HashMap<SphereName, Double> spheresAchievement = new HashMap<SphereName, Double>(5);
			spheresAchievement.put(SphereName.WORK, ranWork);
			spheresAchievement.put(SphereName.HEALTH, ranHealth);
			spheresAchievement.put(SphereName.FAMILY, ranFamily);
			spheresAchievement.put(SphereName.RECREATION, ranRecreation);
			
			WeeklyDataProfileStore.addWeeklyDataProfile(new WeeklyDataProfile(userID_, i, spheresAchievement, spheresAssignment));
		}
	}
	
	private static Double getRandom(Double value_, Double halfRange_, Double previous_)
	{
		// maxHop prevents rapid changes of values
		double maxHop = 0.8 * halfRange_;
		
		double lower = Math.max(value_ - halfRange_, previous_ - maxHop);
		double upper = Math.min(value_ + halfRange_, previous_ + maxHop);
		
		return getRandomBetween(lower, upper);
	}
	
	private static Double getRandomBetween(double lower_, double upper_)
	{
		return lower_ + Math.random() * (upper_-lower_);
	}

}
