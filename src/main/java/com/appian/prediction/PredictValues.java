package com.appian.prediction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.db.AdminDB;
import com.appian.db.ChartDB;
import com.appian.db.DBConnection;
import com.appian.exception.PException;
import com.appian.nn.Network;

@Path("/predict")
@Produces(MediaType.APPLICATION_JSON)
public class PredictValues {

	SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@GET
	@Path("/predictbetween")
	public NavigableMap<Timestamp, Double> Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo) throws PException {
		//System.out.println("@@@@ inside the predictbetween \ndateFrom:" + dateFrom + "\ndateTo:" + dateTo);
		Timestamp fromDate;
		Timestamp toDate;
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());

			if (dateTo == null || "".equals(dateTo) || dateTo.equals("null"))
				toDate = new Timestamp(new Date().getTime());
			else
				toDate = new Timestamp(format.parse(dateTo).getTime());
			// System.out.println(fromDate + " : " + toDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		// System.out.println(fromDate + " : " + toDate);
		// System.out.println("!!--> " + dateFrom + " : " + dateFrom);

		String networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnA");

		Network networkA = ChartDB.map.get(networkName);
		if (networkA == null)
			networkA = new TrainNetwork().Train(request, format.format(new Date(0)), format.format(new Date()),
					(String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));
		networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnB");

		Network networkB = ChartDB.map.get(networkName);
		if (networkB == null)
			networkB = new TrainNetwork().Train(request, format.format(new Date(0)), format.format(new Date()),
					(String) request.getSession().getAttribute("columnB"),
					(String) request.getSession().getAttribute("tableName"));

		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		ChartDB chartDB = new ChartDB(request);
		NavigableMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, toDate, networkA,
				(String) request.getSession().getAttribute("columnA"));
		// //System.out.println("columnA - Values : " + values.toString());

		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));
		// System.out.println(networkB.slidingWindowSize + " : network B : " +
		// networkB.toString());

		if (!((String) request.getSession().getAttribute("columnA"))
				.equals((String) request.getSession().getAttribute("columnB"))) {
			values = chartDB.getPredictedValues(fromDate, toDate, networkB,
					(String) request.getSession().getAttribute("columnB"));
			// System.out.println("columnB - Values : " + values.toString());
			if (values != null && values.size() > 0)
				chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"),
						(String) request.getSession().getAttribute("tableName"));
		}
		return values;
	}

	@GET
	@Path("/prePredict")
	public void PrePredict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo,
			@DefaultValue("") @QueryParam("tableName") String tableName) throws PException {
		//System.out.println("@@@@ inside the predictbetween \ndateFrom:" + dateFrom + "\ndateTo:" + dateTo
		//		+ "\n tableName:" + tableName);
		Timestamp fromDate = null;
		Timestamp toDate = null;

		tableName = tableName.replace(".csv", "");
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());

			if (dateTo == null || "".equals(dateTo) || dateTo.equals("null"))
				toDate = new Timestamp(new Date().getTime());
			else
				toDate = new Timestamp(format.parse(dateTo).getTime());
			// System.out.println(fromDate + " : " + toDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// System.out.println(fromDate + " : " + toDate);
		// System.out.println("!!--> " + dateFrom + " : " + dateFrom);
		JSONArray jsonArray = getRealColumnsList(tableName);
		try {
			createTableCopy(tableName);
		} catch (Exception e) {
		}

		//System.out.println("Real Column List : " + jsonArray.toString());

		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String columnName = jsonObject.getString("columnName");

			String networkName = tableName + columnName;

			Network network = ChartDB.map.get(networkName);
			if (network == null)
				network = new TrainNetwork().PreTrain(request, format1.format(new Date(0)), format1.format(new Date()),
						columnName, tableName);

			ChartDB chartDB = new ChartDB(request);
			NavigableMap<Timestamp, Double> values = null;

			values = chartDB.getPredictedValues(fromDate, toDate, network, columnName);
			// //System.out.println("columnA - Values : " + values.toString());

			if (values != null && values.size() > 0)
				chartDB.savePredictedValues(values, columnName, tableName);
			// networkB.toString());

		}

	}

	public boolean createTableCopy(String table) throws PException {
		try {

			Connection connection = DBConnection.getConnection();
			String sql = "SELECT * INTO danpac.dbo._" + table + " FROM danpac.dbo." + table + " WHERE 1=2;";

			// System.out.println("SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			psDBList.execute();

			return true;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException(" Unable to create copy of table !!!");
		}

	}

	public JSONArray getRealColumnsList(String table) throws PException {
		try {

			Connection connection = DBConnection.getConnection();
			String sql = "SELECT * FROM danpac.INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
					+ "' and DATA_TYPE='real'  ORDER BY ORDINAL_POSITION ";

			//System.out.println("getRealColumnsList : SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			ResultSet rsDBList = psDBList.executeQuery();
			JSONArray jArray = new JSONArray();
			while (rsDBList.next()) {
				JSONObject json = new JSONObject();
				json.put("isNull", rsDBList.getString("IS_NULLABLE"));
				json.put("dataType", rsDBList.getString("DATA_TYPE"));
				json.put("columnName", rsDBList.getString("COLUMN_NAME"));
				jArray.put(json);
			}
			return jArray;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException(" Uable to get column list !!!");
		}

	}

	@GET
	@Path("/predictnvalues")
	public TreeMap<Timestamp, Double> PredictNValues(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("5") @QueryParam("numberOfValues") Integer numberOfValues) throws PException {
		Timestamp fromDate;
		try {
			if ("".equals(dateFrom))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());
			// System.out.println("fromDate : " + fromDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		String networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnA");

		Network networkA = ChartDB.map.get(networkName);
		if (networkA == null)
			networkA = new TrainNetwork().Train(request, format.format(new Date(0)), format.format(new Date()),
					(String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));
		networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnB");

		Network networkB = ChartDB.map.get(networkName);
		if (networkB == null)
			networkB = new TrainNetwork().Train(request, format.format(new Date(0)), format.format(new Date()),
					(String) request.getSession().getAttribute("columnB"),
					(String) request.getSession().getAttribute("tableName"));
		ChartDB chartDB = new ChartDB(request);
		TreeMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, numberOfValues, networkA,
				(String) request.getSession().getAttribute("columnA"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));

		values = chartDB.getPredictedValues(fromDate, numberOfValues, networkB,
				(String) request.getSession().getAttribute("columnB"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"),
					(String) request.getSession().getAttribute("tableName"));
		/* network.lock.unlock(); */
		return values;
	}

}
