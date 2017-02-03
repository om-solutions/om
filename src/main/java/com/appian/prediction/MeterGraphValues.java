package com.appian.prediction;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appian.db.ChartDB;
import com.appian.exception.PException;
import com.appian.nn.Network;
import com.google.gson.JsonArray;

@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
public class MeterGraphValues {

	SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/meter")
	public String Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("value") String dateTo)
			throws PException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Timestamp fromDate;
		Timestamp toDate;
		boolean flag = true;
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null")) {
				fromDate = new Timestamp(0);
				flag = false;
			}

			else
				fromDate = new Timestamp(format1.parse(dateFrom).getTime());

			if (dateTo == null || "".equals(dateTo) || dateTo.equals("null")) {
				toDate = new Timestamp(new Date().getTime());
				flag = false;
			} else {
				toDate = new Timestamp(format1.parse(dateTo).getTime());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new PException("Invalid Date !!!");
		}

		if (flag) {
			PredictValues predictValues = new PredictValues();
			predictValues.Predict(request, dateFrom, dateTo);
		}

		ChartDB chartDB = new ChartDB(request);
		JSONArray meterJSONData = new JSONArray();
		JSONArray meterJSONColumnA = null;
		JSONArray meterJSONColumnB = null;

		System.out.println("2 : " + dateFrom.toString() + " : " + dateTo.toString());

		meterJSONColumnA = chartDB.getMeterGraphValues(fromDate, toDate,
				(String) request.getSession().getAttribute("columnA"));
		System.out.println("MeterJSON Column A : " + meterJSONColumnA.toString());

		meterJSONColumnB = chartDB.getMeterGraphValues(fromDate, toDate,
				(String) request.getSession().getAttribute("columnB"));
		System.out.println("MeterJSON Column B : " + meterJSONColumnB.toString());
		int length = meterJSONColumnA.length() < meterJSONColumnB.length() ? meterJSONColumnB.length()
				: meterJSONColumnA.length();
		System.out.println("!!!!!!!!!! : " + length);
		while (length > 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject = mergeJSONObjects(meterJSONColumnA.getJSONObject(length - 1),
					meterJSONColumnB.getJSONObject(length - 1));
			System.out.println(jsonObject.toString());
			meterJSONData.put(jsonObject);
			length--;
		}

		System.out.println(meterJSONData.length());

		return meterJSONData.toString();
	}

	public static JSONObject mergeJSONObjects(JSONObject json1, JSONObject json2) {
		JSONObject mergedJSON = new JSONObject();
		try {
			mergedJSON = new JSONObject(json1, JSONObject.getNames(json1));
			for (String crunchifyKey : JSONObject.getNames(json2)) {
				mergedJSON.put(crunchifyKey, json2.get(crunchifyKey));
			}

		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception" + e);
		}
		return mergedJSON;
	}

	@GET
	@Path("/changeColumn")
	public String changeColumn(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("columnA") String columnA,
			@DefaultValue("") @QueryParam("columnB") String columnB) throws PException {
		Network network = (Network) request.getSession().getAttribute("NeuralNetwork");
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		if (columnA.equals(request.getSession().getAttribute("columnA"))
				&& columnB.equals(request.getSession().getAttribute("columnB")))
			return "true";

		request.getSession().setAttribute("columnA", columnA);
		request.getSession().setAttribute("columnB", columnB);

		return "true";
	}

	@GET
	@Path("/meter2")
	public String Predict2(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("value") String predict) throws PException {
		System.out.println("!!! " + predict);

		Timestamp fromDate;
		Timestamp toDate;
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format1.parse(dateFrom).getTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}

		PredictValues predictValues = new PredictValues();
		predictValues.PredictNValues(request, dateFrom, Integer.parseInt(predict));

		ChartDB chartDB = new ChartDB(request);
		String meterJSONData = null;
		System.out.println("!!!" + fromDate + " : " + predict);
		// meterJSONData = chartDB.getMeterGraphWithPredictValues(fromDate,
		// predict);

		JSONArray jArray = new JSONArray();
		JSONObject json = new JSONObject();
		meterJSONData = chartDB.getMeterGraphWithPredictValues(fromDate, predict,
				(String) request.getSession().getAttribute("columnA"));
		json.put((String) request.getSession().getAttribute("columnA"), meterJSONData);
		meterJSONData = chartDB.getMeterGraphWithPredictValues(fromDate, predict,
				(String) request.getSession().getAttribute("columnB"));
		json.put((String) request.getSession().getAttribute("columnB"), meterJSONData);
		System.out.println(meterJSONData);

		jArray.put(json);

		System.out.println(jArray);
		return jArray.toString();
	}

}
