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

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

@SuppressWarnings("serial")
public class AddUserDataServlet extends HttpServlet
{
	public void doGet(HttpServletRequest request_, HttpServletResponse response_) throws IOException
	{
		String p = request_.getParameter("action");
		if("addUser".equals(p))
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
		else if("addData".equals(p))
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
		else if("displayData".equals(p))
		{
			List<WeeklyDataProfile> l = WeeklyDataProfileStore.getUserWeeklyDataProfiles("kac08");
			List<String> reply = new ArrayList<String>(l.size());
			for(WeeklyDataProfile wdp : l)
			{
				reply.add("Week no: " + wdp.getWeekNumber() + ", recreation value: " + wdp.getSphereResults().get(SphereName.RECREATION) + "\n");
			}
			response_.getWriter().print(new JSONArray(reply));
			
		}
		else
		{
			response_.getWriter().print("Did nothing.");
		}
		
	}

	public static void addUser(String id, String name, int year, int month, int day, double work, double health, double family)
	{
		HashMap<SphereName, Double> spheresAssignment = new HashMap<SphereName, Double>(5);
		spheresAssignment.put(SphereName.WORK, 0.4);
		spheresAssignment.put(SphereName.HEALTH, 0.1);
		spheresAssignment.put(SphereName.FAMILY, 0.2);
		spheresAssignment.put(SphereName.RECREATION, 0.3);
		
		UserProfile temp = new UserProfile(id, name, id + "@doc", spheresAssignment, true, new GregorianCalendar(year, month, day).getTimeInMillis());
		
		temp.makePersistent();
	}
	
	public static void addData(String userID, Double work, Double health, Double family, int weeks, int weeksOptimised)
	{
		HashMap<SphereName, Double> spheresAssignment = new HashMap<SphereName, Double>(5);
		spheresAssignment.put(SphereName.WORK, work);
		spheresAssignment.put(SphereName.HEALTH, health);
		spheresAssignment.put(SphereName.FAMILY, family);
		spheresAssignment.put(SphereName.RECREATION, 1 - (work + health + family));
		
		double unoptimised = 0.05;
		double optimised = 0.01;
		
		double ranWork = work;
		double ranHealth = health;
		double ranFamily = family;
		double ranRecreation = 1 - (work + health + family);
		
		for(int i=0; i<weeks-weeksOptimised; i++)
		{
			ranWork =  getRandom(work, unoptimised, ranWork);
			ranHealth =  getRandom(health, unoptimised, ranHealth);
			ranFamily =  getRandom(family, unoptimised, ranFamily);
			ranRecreation = 1 - ranWork - ranHealth - ranFamily;
			
			HashMap<SphereName, Double> spheresAchievement = new HashMap<SphereName, Double>(5);
			spheresAchievement.put(SphereName.WORK,ranWork);
			spheresAchievement.put(SphereName.HEALTH, ranHealth);
			spheresAchievement.put(SphereName.FAMILY, ranFamily);
			spheresAchievement.put(SphereName.RECREATION, ranRecreation);
			
			WeeklyDataProfileStore.addWeeklyDataProfile(new WeeklyDataProfile(userID, i, spheresAchievement, spheresAssignment));
		}
		
		for(int i=weeks-weeksOptimised; i<weeks; i++)
		{
			ranWork =  getRandom(work, optimised, ranWork);
			ranHealth =  getRandom(health, optimised, ranHealth);
			ranFamily =  getRandom(family, optimised, ranFamily);
			ranRecreation = 1 - ranWork - ranHealth - ranFamily;
			
			HashMap<SphereName, Double> spheresAchievement = new HashMap<SphereName, Double>(5);
			spheresAchievement.put(SphereName.WORK,ranWork);
			spheresAchievement.put(SphereName.HEALTH, ranHealth);
			spheresAchievement.put(SphereName.FAMILY, ranFamily);
			spheresAchievement.put(SphereName.RECREATION, ranRecreation);
			
			WeeklyDataProfileStore.addWeeklyDataProfile(new WeeklyDataProfile("kac08", i, spheresAchievement, spheresAssignment));
		}
	}
	
	static int sum = 0;
	static double count = 0;
	
	private static Double getRandom(Double value_, Double halfRange_, Double previous_)
	{
		double maxHop = 0.2 * halfRange_;
		
		double lower = Math.max(value_ - halfRange_, previous_ - maxHop);
		double upper = Math.min(value_ + halfRange_, previous_ + maxHop);
		
		return getRandomBetween(lower, upper);
	}
	
	private static Double getRandomBetween(double lower_, double upper_)
	{
		return lower_ + Math.random() * 2 * (upper_-lower_);
	}

}
