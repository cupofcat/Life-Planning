package com.appspot.iclifeplanning.charts.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appspot.datastore.SphereName;

public class DataToJSONConverter
{
	// converts a map of string-double objects (or parsable to double) to a two dimensional JSON array
	// (inner arrays have form: [key, value] )
	public static JSONArray convertMapToArray(Map<String, Object> map_)
	{
		int pos = 0;
		Object[][] retObj = new Object[map_.size()][2];

		for (Map.Entry<String, Object> entry : map_.entrySet())
		{
			retObj[pos][0] = entry.getKey();
			retObj[pos][1] = new Double(entry.getValue().toString());
			pos++;
		}
		JSONArray ret = null;
		try
		{
			ret = new JSONArray(retObj);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public static JSONArray convertSphereMapToArray(HashMap<SphereName, Double> map_)
	{
		int pos = 0;
		Object[][] retObj = new Object[map_.size()][2];

		for (Map.Entry<SphereName, Double> entry : map_.entrySet())
		{
			retObj[pos][0] = entry.getKey().toString();
			retObj[pos][1] = entry.getValue();
			pos++;
		}
		JSONArray ret = null;
		try
		{
			ret = new JSONArray(retObj);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public static JSONArray convertListToHistoricObject(List datas)
	{
		return new JSONArray(datas);
	}
	
	public static JSONObject convertMapToMap(Map map_)
	{
		return new JSONObject(map_);
	}
}
