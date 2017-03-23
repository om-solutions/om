package com.appian.util;

import org.json.JSONException;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class AppianUtil {

	public static String extractColumns(String t) throws JSONException {
		System.out.println("Passed String " + t);
		StringBuilder result = new StringBuilder();
		JsonParser jsonParser = new JsonParser();
		try {
			JsonArray jsonArray = (JsonArray) jsonParser.parse(t);
			jsonArray.forEach(jsonobj -> {
				String name = jsonobj.getAsJsonObject().get("column").getAsString();
				result.append(name);
				result.append(",");
				System.out.println("StringBuilder " + result);
			});

			return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
		} catch (Exception e) {
			return t;
		}
	}

}
