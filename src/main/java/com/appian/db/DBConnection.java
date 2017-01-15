package com.appian.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.nn.Network;

public class DBConnection {
	private static String url = "jdbc:sqlserver://localhost:1433";
	private static String dbName = ";databaseName=Danpac;instance=SQLEXPRESS";
	private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static String userName = "sa";
	private static String password = "root";
	private static String oldValues = "10";
	private ArrayList<Timestamp> times = new ArrayList<Timestamp>();
	private ArrayList<Double> values = new ArrayList<Double>();
	private String provedColumnName = "k_factor";
	private String predictedColumnName = "k_factor";
	private ChartDB chartDB;
	private String chartDT;

	public ArrayList<Double> getValues() {
		return values;
	}

	public void setValues(ArrayList<Double> values) {
		this.values = values;
	}

	public ArrayList<Timestamp> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<Timestamp> times) {
		this.times = times;
	}

	public DBConnection(HttpServletRequest request) {
		String predicted = (String) request.getSession().getAttribute("predicted");
		String proved = (String) request.getSession().getAttribute("proved");
		String chartDT = (String) request.getSession().getAttribute("chartDT");
		this.predictedColumnName = predicted == null ? "predicted_k_factor" : predicted;
		this.provedColumnName = proved == null ? "proved_k_factor" : proved;
		this.chartDT = chartDT == null ?  "timestamp"  : chartDT;
	}

	public static Connection getConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbName, userName, password);
		return conn;
	}

	public ArrayList<Timestamp> getActualTimestamps(Timestamp fromDate, Timestamp toDate)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		ArrayList<Timestamp> times = new ArrayList<Timestamp>();
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
						+ ") as val1 FROM Danpac.dbo.meterdata where timestamp>? and timestamp<? order by timestamp");
		ps.setTimestamp(1, fromDate);
		ps.setTimestamp(2, toDate);
		ResultSet rs = ps.executeQuery();
		Double value;
		while (rs.next()) {
			times.add(rs.getTimestamp(1));
		}
		ps.close();
		rs.close();
		conn.close();
		return times;
	}

	public ArrayList<Double> getActualValuesAndSetNormalizationFactors(Network network, Timestamp fromDate,
			Timestamp toDate)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		times = new ArrayList<Timestamp>();
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
						+ ") as val1 FROM Danpac.dbo.meterdata where timestamp>=? and timestamp<=? order by timestamp");
		ps.setTimestamp(1, fromDate);
		ps.setTimestamp(2, toDate);
		ResultSet rs = ps.executeQuery();
		values = new ArrayList<Double>();
		Double value;
		while (rs.next()) {
			times.add(rs.getTimestamp(1));
			value = rs.getDouble(2);
			values.add(value);
			if (network.max < value)
				network.max = value;
			else if (network.min > value) {
				network.min = value;
			}
		}
		ps.close();
		rs.close();
		conn.close();
		return values;
	}

	public void savePredictedValues(TreeMap<Timestamp, Double> values2)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = DBConnection.getConnection();

		ArrayList<Timestamp> times = getActualTimestamps(values2.firstKey(), values2.lastKey());
		PreparedStatement ps;
		for (Entry<Timestamp, Double> e : values2.entrySet()) {
			if (times.contains(e.getKey()))
				ps = conn.prepareStatement(
						"update Danpac.dbo.meterdata set " + predictedColumnName + "=? where timestamp=?");
			else
				ps = conn.prepareStatement(
						"insert into Danpac.dbo.meterdata (" + predictedColumnName + ",timestamp) values (?,?)");
			ps.setDouble(1, e.getValue());
			ps.setTimestamp(2, e.getKey());
			ps.execute();
			ps.close();
		}
		conn.close();
	}

	public java.util.TreeMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Timestamp toDate,
			Network network)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		Connection conn = DBConnection.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement("select top " + network.slidingWindowSize
				+ " * from (select timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
				+ ") as value1 FROM Danpac.dbo.meterdata where timestamp <? and coalesce(" + provedColumnName + ","
				+ predictedColumnName + ") is not null ) as  a order by timestamp desc;");
		preparedStatement.setTimestamp(1, fromDate);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Timestamp> previousValues = new ArrayList<Timestamp>();
		double[] previousValuesList = new double[network.slidingWindowSize];
		int i = network.slidingWindowSize - 1;
		while (rs.next()) {
			previousValues.add(rs.getTimestamp(1));
			previousValuesList[i--] = network.normalizeValue(rs.getDouble(2));
		}
		preparedStatement.close();
		rs.close();
		previousValues.sort(new Comparator<Timestamp>() {
			@Override
			public int compare(Timestamp o1, Timestamp o2) {
				return o2.getTime() >= o1.getTime() ? -1 : 1;
			}
		});
		Long avgDelay = findAvgDelay(previousValues);

		PreparedStatement getValues = conn
				.prepareStatement("select timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
						+ ") as value1 FROM Danpac.dbo.meterdata where timestamp>? and timestamp<? order by timestamp");
		getValues.setTimestamp(1, fromDate);
		getValues.setTimestamp(2, toDate);
		ResultSet valuesSet = getValues.executeQuery();
		double nextVal;

		Long previousTime = findPrevTime(fromDate, previousValuesList, network,
				previousValues.get(previousValues.size() - 1).getTime(), avgDelay);

		TreeMap<Timestamp, Double> predictedValuesMap = new TreeMap<Timestamp, Double>();
		while (valuesSet.next()) {
			while (valuesSet.getTimestamp(1).getTime() - previousTime > 2 * avgDelay) {
				nextVal = network.nextVal(previousValuesList);
				previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
				previousTime = previousTime + avgDelay;
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
			}
			network.getNeuralNetwork().setInput(previousValuesList);
			network.getNeuralNetwork().calculate();
			nextVal = network.deNormalizeValue(network.getNeuralNetwork().getOutput()[0]);
			if (valuesSet.getDouble(2) == 0) {
				network.trainingSet.addRow(previousValuesList, new double[] { network.normalizeValue(nextVal) });
				previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
			} else {
				network.trainingSet.addRow(previousValuesList,
						new double[] { network.normalizeValue(valuesSet.getDouble(2)) });
				previousValuesList = this.shiftAllLeft(previousValuesList,
						network.normalizeValue(valuesSet.getDouble(2)));
			}
			network.getNeuralNetwork().learn(network.trainingSet);
			previousTime = valuesSet.getTimestamp(1).getTime();
			predictedValuesMap.put(new Timestamp(previousTime), nextVal);
		}

		while (toDate.getTime() - previousTime > 2 * avgDelay) {
			nextVal = network.nextVal(previousValuesList);
			previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
			previousTime = previousTime + avgDelay;
			predictedValuesMap.put(new Timestamp(previousTime), nextVal);
		}
		return predictedValuesMap;

	}

	public java.util.TreeMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Integer numberOfValues,
			Network network)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement preparedStatement = conn.prepareStatement("select top " + network.slidingWindowSize
				+ " * from (select timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
				+ ") as value1 FROM Danpac.dbo.meterdata where timestamp <? and coalesce(" + provedColumnName + ","
				+ predictedColumnName + ") is not null ) as  a order by timestamp desc;");
		preparedStatement.setTimestamp(1, fromDate);
		ResultSet prevValRs = preparedStatement.executeQuery();
		ArrayList<Timestamp> previousValues = new ArrayList<Timestamp>();
		double[] previousValuesList = new double[network.slidingWindowSize];
		int i = network.slidingWindowSize - 1;
		while (prevValRs.next() && i > 0) {
			previousValues.add(prevValRs.getTimestamp(1));
			previousValuesList[i--] = network.normalizeValue(prevValRs.getDouble(2));
		}
		preparedStatement.close();
		prevValRs.close();
		previousValues.sort(new Comparator<Timestamp>() {
			@Override
			public int compare(Timestamp o1, Timestamp o2) {
				return o2.getTime() >= o1.getTime() ? -1 : 1;
			}
		});
		Long avgDelay = findAvgDelay(previousValues);

		PreparedStatement getValues = conn.prepareStatement("select top " + numberOfValues
				+ " * from (select timestamp,coalesce(" + provedColumnName + "," + predictedColumnName
				+ ") as value1 FROM Danpac.dbo.meterdata where timestamp>?) as a order by timestamp");
		getValues.setTimestamp(1, fromDate);
		ResultSet valuesSet = getValues.executeQuery();
		double nextVal;

		Long previousTime = findPrevTime(fromDate, previousValuesList, network,
				previousValues.get(previousValues.size() - 1).getTime(), avgDelay);
		TreeMap<Timestamp, Double> predictedValuesMap = new TreeMap<Timestamp, Double>();
		while (valuesSet.next() && predictedValuesMap.size() < numberOfValues) {
			while (valuesSet.getTimestamp(1).getTime() - previousTime > 2 * avgDelay
					&& predictedValuesMap.size() < numberOfValues) {
				nextVal = network.nextVal(previousValuesList);
				previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
				previousTime = previousTime + avgDelay;
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
			}
			if (predictedValuesMap.size() == numberOfValues) {
				break;
			}
			network.getNeuralNetwork().setInput(previousValuesList);
			network.getNeuralNetwork().calculate();
			nextVal = network.deNormalizeValue(network.getNeuralNetwork().getOutput()[0]);
			if (valuesSet.getDouble(2) == 0) {

				network.trainingSet.addRow(previousValuesList, new double[] { network.normalizeValue(nextVal) });
				previousValuesList = this.shiftAllLeft(previousValuesList, nextVal);
			} else {
				network.trainingSet.addRow(previousValuesList,
						new double[] { network.normalizeValue(valuesSet.getDouble(2)) });
				previousValuesList = this.shiftAllLeft(previousValuesList, valuesSet.getDouble(2));
			}
			network.getNeuralNetwork().learn(network.trainingSet);
			previousTime = valuesSet.getTimestamp(1).getTime();
			predictedValuesMap.put(new Timestamp(previousTime), nextVal);
		}
		while (predictedValuesMap.size() < numberOfValues) {
			nextVal = network.nextVal(previousValuesList);
			previousValuesList = this.shiftAllLeft(previousValuesList, network.deNormalizeValue(nextVal));
			previousTime = previousTime + avgDelay;
			predictedValuesMap.put(new Timestamp(previousTime), nextVal);
		}
		return predictedValuesMap;
	}

	private Long findPrevTime(Timestamp fromDate, double[] previousValuesList, Network network, Long previousTime,
			Long avgDelay) {
		double nextVal;
		while (fromDate.getTime() - previousTime > avgDelay * 2) {
			nextVal = network.nextVal(previousValuesList);
			this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
			previousTime = previousTime + avgDelay;
		}
		return previousTime;
	}

	private Long findAvgDelay(ArrayList<Timestamp> previousValues) {
		Timestamp prevVal = null, curVal;
		Long totalDelay = 0l;
		for (Timestamp e : previousValues) {
			curVal = e;
			if (prevVal != null) {
				totalDelay = totalDelay + curVal.getTime() - prevVal.getTime();
			}
			prevVal = curVal;
		}
		return totalDelay / previousValues.size();
	}

	private double[] shiftAllLeft(double[] previousValuesList, double nextVal) {
		for (int i = 1; i < previousValuesList.length; i++) {
			previousValuesList[i - 1] = previousValuesList[i];
		}
		previousValuesList[previousValuesList.length - 1] = nextVal;
		return previousValuesList;
	}

	public String getMeterGraphValues(Timestamp fromDate, Timestamp toDate,String chartDT)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		Connection conn = DBConnection.getConnection();
		PreparedStatement ps;

		if (fromDate == null && toDate == null) {
			ps = conn.prepareStatement("SELECT "+chartDT+",proved_k_factor as val1," + predictedColumnName
					+ " as val2, MTemp as val3, MPressure,Flowrate FROM Danpac.dbo.meterdata order by "+chartDT+"");
		} else {
			ps = conn.prepareStatement("SELECT "+chartDT+",proved_k_factor as val1," + predictedColumnName
					+ " as val2, MTemp as val3, MPressure,Flowrate FROM Danpac.dbo.meterdata where "+chartDT+">? and "+chartDT+"<? order by "+chartDT+"");
			ps.setTimestamp(1, fromDate);
			ps.setTimestamp(2, toDate);
		}
		// System.out.println("3 : " + fromDate + " : " + toDate);

		JSONArray jArray = new JSONArray();

		if (fromDate != null) {
			PreparedStatement oldValues;
			ResultSet rsOld = null;
			oldValues = conn.prepareStatement("select * from (SELECT top " + DBConnection.oldValues
					+ " "+chartDT+",proved_k_factor as val1," + predictedColumnName
					+ " as val2, MTemp as val3, MPressure,Flowrate FROM [danpac].[dbo].[meterdata] where "+chartDT+"<? order by "+chartDT+" desc) a order by "+chartDT+"");
			oldValues.setTimestamp(1, fromDate);
			;
			rsOld = oldValues.executeQuery();
			while (rsOld.next()) {
				String timestamp = rsOld.getString(1);
				Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
				Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;
				Float temp = rsOld.getString(4) != null ? Float.valueOf(rsOld.getString(4)) : 0f;
				Float pressure = rsOld.getString(5) != null ? Float.valueOf(rsOld.getString(5)) : 0f;
				Float flowrate = rsOld.getString(6) != null ? Float.valueOf(rsOld.getString(6)) : 0f;

				Float error = null;
				if (actual == null || predicted == null || actual.equals(0f))
					error = null;
				else
					error = Math.abs(((actual - predicted) / actual) * 100);
				JSONObject json = new JSONObject();

				json.put("datetime", timestamp);
				json.put("actual", actual == 0f ? null : actual.toString());
				json.put("predicted", predicted == 0f ? null : predicted.toString());
				json.put("error", error != null ? error.toString() : null);
				json.put("temp", temp.equals(0f) ? null : temp.toString());
				json.put("pressure", pressure.equals(0f) ? null : pressure.toString());
				json.put("flowrate", flowrate.equals(0f) ? null : flowrate.toString());

				jArray.put(json);
			}
		}
		ResultSet rs = ps.executeQuery();
		values = new ArrayList<Double>();

		while (rs.next()) {
			String timestamp = rs.getString(1);
			Float actual = rs.getString(2) != null ? Float.valueOf(rs.getString(2)) : 0f;
			Float predicted = rs.getString(3) != null ? Float.valueOf(rs.getString(3)) : 0f;
			Float temp = rs.getString(4) != null ? Float.valueOf(rs.getString(4)) : 0f;
			Float pressure = rs.getString(5) != null ? Float.valueOf(rs.getString(5)) : 0f;
			Float flowrate = rs.getString(6) != null ? Float.valueOf(rs.getString(6)) : 0f;

			Float error = null;
			if (actual == null || predicted == null || actual.equals(0f))
				error = null;
			else
				error = Math.abs(((actual - predicted) / actual) * 100);
			JSONObject json = new JSONObject();

			json.put("datetime", timestamp);
			json.put("actual", actual == 0f ? null : actual.toString());
			json.put("predicted", predicted == 0f ? null : predicted.toString());
			json.put("error", error != null ? error.toString() : null);
			json.put("temp", temp.equals(0f) ? null : temp.toString());
			json.put("pressure", pressure.equals(0f) ? null : pressure.toString());
			json.put("flowrate", flowrate.equals(0f) ? null : flowrate.toString());
			// System.out.println("json : " + json.toString());
			jArray.put(json);
		}

		ps.close();
		rs.close();
		conn.close();
		return jArray.toString();

	}

	public String getMeterGraphWithPredictValues(Timestamp fromDate, String predict)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement ps = conn.prepareStatement("SELECT TOP " + predict + " timestamp,proved_k_factor as val1,"
				+ predictedColumnName
				+ " as val2, MTemp as val3, MPressure,Flowrate FROM Danpac.dbo.meterdata where timestamp>? order by timestamp ");
		ps.setTimestamp(1, fromDate);

		ResultSet rs = ps.executeQuery();
		values = new ArrayList<Double>();

		JSONArray jArray = new JSONArray();
		if (fromDate != null) {
			PreparedStatement oldValues;
			ResultSet rsOld = null;
			oldValues = conn.prepareStatement("select * from (SELECT top " + DBConnection.oldValues
					+ " timestamp,proved_k_factor as val1," + predictedColumnName
					+ " as val2, MTemp as val3, MPressure,Flowrate FROM [danpac].[dbo].[meterdata] where timestamp<? order by timestamp desc) a order by timestamp");
			oldValues.setTimestamp(1, fromDate);
			;
			rsOld = oldValues.executeQuery();
			while (rsOld.next()) {
				String timestamp = rsOld.getString(1);
				Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
				Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;
				Float temp = rsOld.getString(4) != null ? Float.valueOf(rsOld.getString(4)) : 0f;
				Float pressure = rsOld.getString(5) != null ? Float.valueOf(rsOld.getString(5)) : 0f;
				Float flowrate = rsOld.getString(6) != null ? Float.valueOf(rsOld.getString(6)) : 0f;

				Float error = null;
				if (actual == null || predicted == null || actual.equals(0f))
					error = null;
				else
					error = Math.abs(((actual - predicted) / actual) * 100);
				JSONObject json = new JSONObject();

				json.put("datetime", timestamp);
				json.put("actual", actual == 0f ? null : actual.toString());
				json.put("predicted", predicted == 0f ? null : predicted.toString());
				json.put("error", error != null ? error.toString() : null);
				json.put("temp", temp.equals(0f) ? null : temp.toString());
				json.put("pressure", pressure.equals(0f) ? null : pressure.toString());
				json.put("flowrate", flowrate.equals(0f) ? null : flowrate.toString());

				jArray.put(json);
			}
		}

		while (rs.next()) {
			String timestamp = rs.getString(1);
			Float actual = rs.getString(2) != null ? Float.valueOf(rs.getString(2)) : 0f;
			Float predicted = rs.getString(3) != null ? Float.valueOf(rs.getString(3)) : 0f;
			Float temp = rs.getString(4) != null ? Float.valueOf(rs.getString(4)) : 0f;
			Float pressure = rs.getString(5) != null ? Float.valueOf(rs.getString(5)) : 0f;
			Float flowrate = rs.getString(6) != null ? Float.valueOf(rs.getString(6)) : 0f;

			Float error = null;
			try {
				error = Math.abs(((actual - predicted) / actual) * 100);
			} catch (Exception e) {
				error = 0f;
			}
			JSONObject json = new JSONObject();

			json.put("datetime", timestamp);
			json.put("actual", actual == 0f ? null : actual.toString());
			json.put("predicted", predicted == 0f ? null : predicted.toString());
			json.put("error", error != null ? error.toString() : null);
			json.put("temp", temp.equals(0f) ? null : temp.toString());
			json.put("pressure", pressure.equals(0f) ? null : pressure.toString());
			json.put("flowrate", flowrate.equals(0f) ? null : flowrate.toString());

			jArray.put(json);
		}

		ps.close();
		rs.close();
		conn.close();
		return jArray.toString();
	}

	public String validateUser(String username, String password)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = DBConnection.getConnection();
		// System.out.println(username + ":" + password);
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM Danpac.dbo.members where uname=? and pass=? ");
		ps.setString(1, username);
		ps.setString(2, password);
		ResultSet rs = ps.executeQuery();
		values = new ArrayList<Double>();
		String status;
		JSONArray jArray = new JSONArray();
		if (rs.next()) {
			/*
			 * JSONObject json = new JSONObject(); json.put("username",
			 * username); json.put("flag", true); jArray.put(json);
			 */
			status = "True";
		} else {
			/*
			 * JSONObject json = new JSONObject(); json.put("flag", false);
			 * jArray.put(json);
			 */
			status = "False";
		}

		ps.close();
		rs.close();
		conn.close();
		// return jArray.toString();
		return status;
	}

	public String register(String name, String email, String username, String password)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = DBConnection.getConnection();
		// System.out.println(username + ":" + password);
		PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO Danpac.dbo.members (first_name, last_name,email,uname,pass,regdate) VALUES (?,?,?,?,?,?)");
		ps.setString(1, name.split(" ")[0]);
		ps.setString(2, name.split(" ").length > 1 ? name.split(" ")[1] : "");
		ps.setString(3, email);
		ps.setString(4, username);
		ps.setString(5, password);
		ps.setTimestamp(6, new Timestamp(new Date().getTime()));
		int count = ps.executeUpdate();
		ps.close();
		conn.close();
		// return jArray.toString();

		return count > 0 ? "true" : "false";
	}

	public String getDBList()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		Connection connection = DBConnection.getConnection();
		String sql = "SELECT name FROM master.dbo.sysdatabases";
		PreparedStatement psDBList = connection.prepareStatement(sql);
		ResultSet rsDBList = psDBList.executeQuery();
		JSONArray jArray = new JSONArray();
		while (rsDBList.next()) {
			JSONObject json = new JSONObject();

			json.put("dbname", rsDBList.getString("name"));
			jArray.put(json);
		}
		return jArray.toString();
	}

	public String getTableList(String db)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection connection = DBConnection.getConnection();
		String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='"
				+ db + "' ";
		PreparedStatement psDBList = connection.prepareStatement(sql);
		ResultSet rsDBList = psDBList.executeQuery();
		JSONArray jArray = new JSONArray();
		while (rsDBList.next()) {
			JSONObject json = new JSONObject();

			json.put("tableName", rsDBList.getString("TABLE_NAME"));
			jArray.put(json);
		}
		return jArray.toString();
	}

	public String getColumnsList(String table, String db)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection connection = DBConnection.getConnection();
		String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
				+ "' ORDER BY ORDINAL_POSITION ";

		System.out.println("SQL : " + sql);
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
		return jArray.toString();

	}

	public Boolean setColumns(String dbName, String tableName, String columns, String chartDT)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		Connection conn = DBConnection.getConnection();
		PreparedStatement ps;
		if (!columns.trim().isEmpty() || !tableName.trim().isEmpty() || !dbName.trim().isEmpty()) {
			System.out.println("Not Blank+columns");
			try {
				ps = conn.prepareStatement(
						"insert into Danpac.dbo.masterData (dbName,tableName,columnsName,chartDT,dt) values (?,?,?,?,?)");
				ps.setString(1, dbName);
				ps.setString(2, tableName);
				ps.setString(3, columns);
				ps.setString(4, chartDT);
				ps.setTimestamp(5, new Timestamp(new Date().getTime()));
				ps.execute();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		} else {
			System.out.println("Blank");
			return false;
		}
		conn.close();
		return true;

	}

	public String getColumns()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection connection = DBConnection.getConnection();
		String sql = "select dbname,tableName,columnsName,chartDt from Danpac.dbo.masterData order by dt desc ";

		System.out.println("SQL : " + sql);
		PreparedStatement psDBList = connection.prepareStatement(sql);
		ResultSet rsDBList = psDBList.executeQuery();
		JSONArray jArray = new JSONArray();
		if (rsDBList.next()) {
			JSONObject json = new JSONObject();
			json.put("columnsName", rsDBList.getString("columnsName"));
			json.put("tableName", rsDBList.getString("tableName"));
			json.put("dbName", rsDBList.getString("dbname"));
			json.put("chartDT", rsDBList.getString("chartDT"));
			jArray.put(json);
			chartDB = new ChartDB(rsDBList.getString("dbname"), rsDBList.getString("tableName"),
					rsDBList.getString("columnsName"), rsDBList.getString("chartDT"));
			System.out.println("JSON : " + jArray.toString());
		}
		return jArray.toString();
	}

	public ChartDB getChartDB() {
		return chartDB;
	}

}
