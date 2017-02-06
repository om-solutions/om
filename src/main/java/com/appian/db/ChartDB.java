package com.appian.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.exception.PException;
import com.appian.nn.Network;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class ChartDB {
	public String url;
	public String dbInstanceName;
	private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public String userName;
	public String password;
	private static String oldValues = "10";
	private ArrayList<Timestamp> times = new ArrayList<Timestamp>();
	private ArrayList<Double> values = new ArrayList<Double>();
	//private String columnName;
	// private String predictedColumnName;
	public String chartDT;
	public String dbName;
	public String tableName;
	public String columns;
	public String columnA;

	public static ConcurrentHashMap<String, Network> map = new ConcurrentHashMap<>();

	public void getDBValues(HttpServletRequest request) throws PException {
		try {
			Connection connection = DBConnection.getConnection();
			String sql = "select url,dbInstanceName,dbName,tableName,columnsName,chartDt,userName,password from Danpac.dbo.masterData  where username=?";
			// System.out.println("Inside ChartDB() : " + sql);
			PreparedStatement psDBList;

			psDBList = connection.prepareStatement(sql);
			psDBList.setString(1, (String) request.getSession().getAttribute("pUser"));

			ResultSet rsDBList = psDBList.executeQuery();
			if (rsDBList.next()) {
				// System.out.println("if rsDBList");
				url = rsDBList.getString("url");
				columns = rsDBList.getString("columnsName");
				tableName = rsDBList.getString("tableName");
				dbName = rsDBList.getString("dbname");
				chartDT = rsDBList.getString("chartDT");
				dbInstanceName = ";databaseName=" + rsDBList.getString("dbInstanceName") + ";instance=SQLEXPRESS";
				userName = rsDBList.getString("userName");
				password = rsDBList.getString("password");
			} else {
				// System.out.println("ELSE rsDBList");
				url = DBConnection.url;
				// this.columns = DBConnection.columns;
				tableName = DBConnection.tableName;
				dbName = DBConnection.dbName;
				chartDT = DBConnection.chartDT;
				dbInstanceName = DBConnection.dbInstanceName;
				userName = DBConnection.userName;
				password = DBConnection.password;
			}
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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

	public ChartDB(JSONObject jobj) {
		url = (String) jobj.getString("url");
		columns = (String) jobj.getString("columns");
		tableName = (String) jobj.getString("tableName");
		dbName = (String) jobj.getString("dbName");
		dbInstanceName = (String) jobj.getString("dbInstanceName");
		userName = (String) jobj.getString("userName");
		password = (String) jobj.getString("password");
		chartDT = (String) jobj.getString("chartDT");
		dbName = (String) jobj.getString("dbName");
		// System.out.println("ChartDB --> Columns : " + columns);

	}

	public ChartDB(HttpServletRequest request) throws PException {

		String tempurl = (String) request.getSession().getAttribute("url");
		if (tempurl == null || tempurl.isEmpty()) {
			getDBValues(request);
			request.getSession().setAttribute("url", url);
			request.getSession().setAttribute("tableName", tableName);
			request.getSession().setAttribute("dbName", dbName);
			request.getSession().setAttribute("chartDT", chartDT);
			request.getSession().setAttribute("dbInstanceName", dbInstanceName);
			request.getSession().setAttribute("userName", userName);
			request.getSession().setAttribute("password", password);
			request.getSession().setAttribute("columns", columns);			
			if (columns != null) {
				request.getSession().setAttribute("columns", columns);
				request.getSession().setAttribute("columnA", columns.split(",")[0]);
				try {
					request.getSession().setAttribute("columnB", columns.split(",")[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					request.getSession().setAttribute("columnB", columns.split(",")[0]);
				}
			}

		} else {
			url = (String) request.getSession().getAttribute("url");
			columns = (String) request.getSession().getAttribute("columns");
			tableName = (String) request.getSession().getAttribute("tableName");
			dbName = (String) request.getSession().getAttribute("dbName");
			chartDT = (String) request.getSession().getAttribute("chartDT");
			dbInstanceName = (String) request.getSession().getAttribute("dbInstanceName");
			userName = (String) request.getSession().getAttribute("userName");
			password = (String) request.getSession().getAttribute("password");
			String predicted = (String) request.getSession().getAttribute("predicted");
			String proved = (String) request.getSession().getAttribute("proved");
			String chartDT = (String) request.getSession().getAttribute("chartDT");
			String dbName = (String) request.getSession().getAttribute("dbName");
			String tableName = (String) request.getSession().getAttribute("tableName");
			// System.out.println("ChartDB --> Columns : " + columns);
			try {
				this.columnA = predicted == null ? columns.split(",")[0] : predicted;
			} catch (Exception e) {

			}			
		}

		// this.provedColumnName = proved == null ?
		// ChartDB.columns.split(",")[0] : proved;
		chartDT = chartDT == null ? "" : chartDT;
		dbName = dbName == null ? "" : dbName;
		tableName = tableName == null ? "" : tableName;

		// System.out.println("DBConnection -> dbName : " + dbName + ",
		// tableName : " + tableName);
	}

	public Connection getConnection() throws PException {
		try {
			// System.out.println("URL : " + url + " : " + ",\nColumn : " +
			// columns + ",\nTableName : " + tableName
			// + "\nDBName : " + dbName + "\nChartDT : " + chartDT +
			// "\ndbInstanceName : " + dbInstanceName
			// + "\nUserName : " + userName + "\nPassword : " + password);

			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url + dbInstanceName, userName, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to connect Chart Database !!!");
		}
	}

	public ArrayList<Timestamp> getActualTimestamps(Timestamp fromDate, Timestamp toDate, String tableName)
			throws PException {
		try {
			ArrayList<Timestamp> times = new ArrayList<Timestamp>();
			Connection conn = getConnection();
			String s="SELECT tab2." + chartDT
					+ " as dt ,tab2." + columnA + " as val1 FROM " + dbName + ".dbo._" + tableName
					+ " as tab2  where tab2." + chartDT + ">=? and tab2."
					+ chartDT + "<=? order by dt ";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setTimestamp(1, fromDate);
			ps.setTimestamp(2, toDate);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				times.add(rs.getTimestamp(1));
			}
			ps.close();
			rs.close();
			conn.close();
			return times;
		} catch (Exception e) {
			throw new PException("Unable to get Actual Timestamps !!!");
		}
	}

	public ArrayList<Double> getActualValuesAndSetNormalizationFactors(Network network, Timestamp fromDate,
			Timestamp toDate, String Column) throws PException {
		try {

			times = new ArrayList<Timestamp>();
			Connection conn = getConnection();
			String query = "SELECT coalesce(tab1." + chartDT + ",tab2." + chartDT + ") as dt ,coalesce(tab1." + Column
					+ ",tab2." + Column + ") as val1 FROM " + dbName + ".dbo." + tableName
					+ " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1." + chartDT
					+ "=tab2." + chartDT + " where tab1." + chartDT + ">=? and tab1." + chartDT + "<=? order by dt ";
			// System.out.println("[getActualValuesAndSetNormalizationFactors] :
			// " + query);
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setTimestamp(1, fromDate);
			ps.setTimestamp(2, toDate);
			ResultSet rs = ps.executeQuery();
			values = new ArrayList<Double>();
			Double value;
			while (rs.next()) {
				times.add(rs.getTimestamp(1));
				// System.out.println("times : " + times);
				value = rs.getDouble(2);
				// System.out.println("times : " + times + " , value : " +
				// value);
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Actual Values And Set Normalization Factors !!!");
		}
	}

	public void savePredictedValues(NavigableMap<Timestamp, Double> values2, String column, String tableName)
			throws PException {
		try {
			Connection conn = getConnection();

			ArrayList<Timestamp> times = getActualTimestamps(values2.firstKey(), values2.lastKey(), tableName);
			PreparedStatement ps;
			for (Entry<Timestamp, Double> e : values2.entrySet()) {
				if (times.contains(e.getKey()))
					ps = conn.prepareStatement("update " + dbName + ".dbo._" + tableName + " set " + column
							+ "=? where " + chartDT + "=?");
				else
					ps = conn.prepareStatement("insert into " + dbName + ".dbo._" + tableName + " (" + column + ","
							+ chartDT + ") values (?,?)");
				ps.setDouble(1, e.getValue());
				ps.setTimestamp(2, e.getKey());
				ps.execute();
				ps.close();
			}
			conn.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public NavigableMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Timestamp toDate, Network network,
			String column) throws PException {
		try {
			boolean updateNetwork = false;
			Connection conn = getConnection();
			String query = "select top " + network.slidingWindowSize + " * from (select coalesce(tab1." + chartDT
					+ ",tab2." + chartDT + ") as dt ,coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM "
					+ dbName + ".dbo." + tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName
					+ " as tab2  ON tab1." + chartDT + "=tab2." + chartDT + " where coalesce(tab1." + chartDT + ",tab2." + chartDT + ")"
					+ " <? and coalesce(tab1." + column + ",tab2." + column + ") is not null ) as  a order by dt desc;";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			// System.out.println("--> " + query);
			// System.out.println("--> " + fromDate);
			preparedStatement.setTimestamp(1, fromDate);
			ResultSet rs = preparedStatement.executeQuery();
			ArrayList<Timestamp> previousValues = new ArrayList<Timestamp>();
			double[] previousValuesList = new double[network.slidingWindowSize];
			int i = network.slidingWindowSize - 1;
			while (rs.next()) {
				// System.out.println("@@@@ : " + rs.getTimestamp(1));
				previousValues.add(rs.getTimestamp(1));
				previousValuesList[i--] = network.normalizeValue(rs.getDouble(2));
			}
			if (previousValues.isEmpty()) {
				String query1 = "select top " + network.slidingWindowSize + " * from (select coalesce(tab1." + chartDT
						+ ",tab2." + chartDT + ") as dt ,coalesce(tab1." + column + ",tab2." + column
						+ ") as val1 FROM " + dbName + ".dbo." + tableName + " as tab1  FULL OUTER JOIN " + dbName
						+ ".dbo._" + tableName + " as tab2  ON tab1." + chartDT + "=tab2." + chartDT
						+ " where coalesce(tab1." + column + ",tab2." + column
						+ ") is not null ) as  a order by dt;";
				PreparedStatement preparedStatement1 = conn.prepareStatement(query1);
				// System.out.println("--> " + query1);
				// System.out.println("--> " + fromDate);
				rs = preparedStatement1.executeQuery();
				previousValues = new ArrayList<Timestamp>();
				previousValuesList = new double[network.slidingWindowSize];
				i = network.slidingWindowSize - 1;
				while (rs.next()) {
					// System.out.println("@@@@ : " + rs.getTimestamp(1));
					previousValues.add(rs.getTimestamp(1));
					previousValuesList[i--] = network.normalizeValue(rs.getDouble(2));
				}

			}

			preparedStatement.close();
			rs.close();
			previousValues.sort(new Comparator<Timestamp>() {
				@Override
				public int compare(Timestamp o1, Timestamp o2) {
					return o2.getTime() >= o1.getTime() ? -1 : 1;
				}
			});
			// System.out.println(previousValues.size());
			Long avgDelay = findAvgDelay(previousValues);

			PreparedStatement getValues = conn.prepareStatement("select coalesce(tab1." + chartDT + ",tab2." + chartDT
					+ ") as dt ,coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo."
					+ tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1."
					+ chartDT + "=tab2." + chartDT + " where coalesce(tab1." + chartDT + ",tab2." + chartDT + ") >? and coalesce(tab1." + chartDT + ",tab2." + chartDT + ")"
					+ "<? order by dt ");
			getValues.setTimestamp(1, fromDate);
			getValues.setTimestamp(2, toDate);
			ResultSet valuesSet = getValues.executeQuery();
			double nextVal;

			Long previousTime = findPrevTime(updateNetwork,fromDate, previousValuesList, network,
					previousValues.get(previousValues.size() - 1).getTime(), avgDelay);

			NavigableMap<Timestamp, Double> predictedValuesMap = new ConcurrentSkipListMap<Timestamp, Double>();
			while (valuesSet.next()) {
				while (valuesSet.getTimestamp(1).getTime() - previousTime > 2 * avgDelay) {
					nextVal = network.nextValAndUpdateTrainingSet(previousValuesList);
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
					previousTime = previousTime + avgDelay;
					predictedValuesMap.put(new Timestamp(previousTime), nextVal);
				}
				nextVal = network.getNextVal(previousValuesList);
				if (valuesSet.getDouble(2) == 0) {

					network.addRow(previousValuesList, new double[] { network.normalizeValue(nextVal) });
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
				} else {
					network.addRow(previousValuesList,
							new double[] { network.normalizeValue(valuesSet.getDouble(2)) });
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList,
							network.normalizeValue(valuesSet.getDouble(2)));
				}
				previousTime = valuesSet.getTimestamp(1).getTime();
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
				// System.out.println("--- > " + nextVal);
			}

			while (toDate.getTime() - previousTime > 2 * avgDelay) {
				nextVal = network.nextValAndUpdateTrainingSet(previousValuesList);
				updateNetwork=true;
				previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
				previousTime = previousTime + avgDelay;
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
				// System.out.println("--- > " + nextVal);
			}
			if(updateNetwork==true)
				network.learn();
			return predictedValuesMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Predicted Values !!!");
		}
	}

	public java.util.TreeMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Integer numberOfValues,
			Network network, String column) throws PException {
		try {
			Connection conn = getConnection();
			boolean updateNetwork=false;
			String query = "select top " + network.slidingWindowSize + " * from (select coalesce(tab1." + chartDT
					+ ",tab2." + chartDT + ") as dt ,coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM "
					+ dbName + ".dbo." + tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName
					+ " as tab2  ON tab1." + chartDT + "=tab2." + chartDT + " where coalesce(tab1." + chartDT + ",tab2." + chartDT + ")"
					+ " <? and coalesce(tab1." + column + ",tab2." + column + ") is not null ) as  a order by dt desc;";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			System.out.println("[ChartDB][getPredictedValues] : Query : " + query);
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

			query = "select top " + numberOfValues + " * from (select coalesce(tab1." + chartDT + ",tab2." + chartDT
					+ ") as dt ,coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo."
					+ tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1."
					+ chartDT + "=tab2." + chartDT + " where tab1." + chartDT + ">?) as a order by dt ";
			PreparedStatement getValues = conn.prepareStatement(query);
			getValues.setTimestamp(1, fromDate);
			ResultSet valuesSet = getValues.executeQuery();
			double nextVal;
			System.out.println("[ChartDB][getPredictedValues] : Top 10 : " + query);
			Long previousTime = findPrevTime(updateNetwork,fromDate, previousValuesList, network,
					previousValues.get(previousValues.size() - 1).getTime(), avgDelay);
			TreeMap<Timestamp, Double> predictedValuesMap = new TreeMap<Timestamp, Double>();
			while (valuesSet.next() && predictedValuesMap.size() < numberOfValues) {
				while (valuesSet.getTimestamp(1).getTime() - previousTime > 2 * avgDelay
						&& predictedValuesMap.size() < numberOfValues) {
					nextVal = network.nextValAndUpdateTrainingSet(previousValuesList);
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList, network.normalizeValue(nextVal));
					previousTime = previousTime + avgDelay;
					predictedValuesMap.put(new Timestamp(previousTime), nextVal);
				}
				if (predictedValuesMap.size() == numberOfValues) {
					break;
				}
				nextVal=network.getNextVal(previousValuesList);
				if (valuesSet.getDouble(2) == 0) {

					network.addRow(previousValuesList, new double[] { network.normalizeValue(nextVal) });
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList, nextVal);
				} else {
					network.addRow(previousValuesList,
							new double[] { network.normalizeValue(valuesSet.getDouble(2)) });
					updateNetwork=true;
					previousValuesList = this.shiftAllLeft(previousValuesList, valuesSet.getDouble(2));
				}
				previousTime = valuesSet.getTimestamp(1).getTime();
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
			}
			while (predictedValuesMap.size() < numberOfValues) {
				nextVal = network.nextValAndUpdateTrainingSet(previousValuesList);
				updateNetwork=true;
				previousValuesList = this.shiftAllLeft(previousValuesList, network.deNormalizeValue(nextVal));
				previousTime = previousTime + avgDelay;
				predictedValuesMap.put(new Timestamp(previousTime), nextVal);
			}
			if(updateNetwork==true)
				network.learn();
			return predictedValuesMap;

		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Predicted Values !!!");
		}
	}

	private Long findPrevTime(boolean updateNetwork,Timestamp fromDate, double[] previousValuesList, Network network, Long previousTime,
			Long avgDelay) {
		double nextVal;

		while (fromDate.getTime() - previousTime > avgDelay * 2) {
			updateNetwork=true;
			nextVal = network.nextValAndUpdateTrainingSet(previousValuesList);
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

	public JSONArray getMeterGraphValues(Timestamp fromDate, Timestamp toDate, String column, Boolean singleColumn)
			throws PException {
		try {
			Connection conn = getConnection();
			PreparedStatement ps;

			String orderBy = "";
			if (singleColumn) {
				orderBy = " order by dt  ";
			} else {
				orderBy = " order by dt desc ";
			}

			if (fromDate == null && toDate == null) {
				String query = "SELECT coalesce(tab1." + chartDT + ",tab2." + chartDT + ") as dt ,ISNULL(tab1." + column
						+ ",0) as predicted,ISNULL(tab2." + column + ",0) as proved from " + dbName + ".dbo."
						+ tableName + " as tab1 full outer join " + tableName + " as tab2 on tab1." + chartDT + "=tab2."
						+ chartDT + orderBy;
				System.out.println("[ChartDB][getMeterGraphValues] : " + query);
				ps = conn.prepareStatement(query);
				// System.out.println("---> : " + query);
			} else {
				String query = "SELECT coalesce(tab1." + chartDT + ",tab2." + chartDT + ") as dt ,ISNULL(tab1." + column
						+ ",0) as val1, ISNULL(tab2." + column + ",0) as val2  FROM  [" + dbName + "].[dbo].["
						+ tableName + "] as tab1 FULL OUTER join [" + dbName + "].[dbo].[_" + tableName
						+ "] as tab2 on tab1." + chartDT + "=tab2." + chartDT + " where coalesce(tab1." + chartDT
						+ ",tab2." + chartDT + ") > ? and coalesce(tab1." + chartDT + ",tab2." + chartDT + ") <? "
						+ orderBy;
				System.out.println("[ChartDB][getMeterGraphValues] : Query " + query);
				ps = conn.prepareStatement(query);

				ps.setTimestamp(1, fromDate);
				ps.setTimestamp(2, toDate);
				// System.out.println("---> : " + query);
			}
			// System.out.println("3 : " + fromDate + " : " + toDate);

			JSONArray jArray = new JSONArray();

			if (fromDate != null) {
				PreparedStatement oldValues;
				ResultSet rsOld = null;
				String query = "select * from (SELECT top " + ChartDB.oldValues + " coalesce(tab1." + chartDT + ",tab2."
						+ chartDT + ") as dt,ISNULL(tab1." + column + ",0) as val1 , ISNULL(tab2." + column
						+ ",0) as val2 FROM [" + dbName + "].[dbo].[" + tableName + "] as tab1 FULL OUTER join ["
						+ dbName + "].[dbo].[_" + tableName + "] as tab2 on tab1." + chartDT + "=tab2." + chartDT
						+ " where tab1." + chartDT + "<? order by dt desc) a order by  dt desc";
				oldValues = conn.prepareStatement(query);
				oldValues.setTimestamp(1, fromDate);
				System.out.println("[ChartDB][getMeterGraphValues] : Top10  : " + query);
				rsOld = oldValues.executeQuery();
				while (rsOld.next()) {
					String timestamp = rsOld.getString(1);
					Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
					Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;
					// System.out.println("actual : " + actual + ", predicted" +
					// predicted);

					if (actual == null || predicted == null || actual.equals(0f)) {
					} else
						Math.abs(((actual - predicted) / actual) * 100);
					JSONObject json = new JSONObject();
					// System.out.println(
					// timestamp + " : actual : " + actual.toString() + ",
					// predicted" + predicted.toString());
					json.put(chartDT, timestamp);
					json.put(column, actual == 0f ? null : actual.toString());
					json.put("_" + column, predicted == 0f ? null : predicted.toString());
					// System.out.println("json : " + json.toString());
					// json.put("error", error != null ? error.toString() :
					// null);
					// //System.out.println("[getMeterGraphValues] : JSON1 : " +
					// json.toString());
					jArray.put(json);
				}
			}
			ResultSet rs = ps.executeQuery();
			values = new ArrayList<Double>();

			while (rs.next()) {
				String timestamp = rs.getString(1);
				Float actual = rs.getString(2) != null ? Float.valueOf(rs.getString(2)) : 0f;
				Float predicted = rs.getString(3) != null ? Float.valueOf(rs.getString(3)) : 0f;

				if (actual == null || predicted == null || actual.equals(0f)) {
				} else
					Math.abs(((actual - predicted) / actual) * 100);
				JSONObject json = new JSONObject();

				// //System.out.println("timestamp : " + timestamp + ", actual :
				// "
				// + actual + ", predicted : " + predicted);
				json.put(chartDT, timestamp);
				json.put(column, actual == 0f ? null : actual.toString());
				json.put("_" + column, predicted == 0f ? null : predicted.toString());
				// json.put("error", error != null ? error.toString() : null);

				// System.out.println("json2 : " + json.toString());
				jArray.put(json);
				// //System.out.println("[getMeterGraphValues] : JSON2 : " +
				// json.toString());
			}

			ps.close();
			rs.close();
			conn.close();
			// System.err.println("[getMeterGraphValues] : " +
			// jArray.toString());
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("ERROR : Uable to get Meter Graph Values");
		}

	}

	public JSONArray getMeterGraphWithPredictValues(Timestamp fromDate, String predict, String column,
			Boolean singleColumn) throws PException {
		try {
			String orderBy = "";
			if (singleColumn) {
				orderBy = " order by dt desc ";
			} else {
				orderBy = " order by dt ";
			}

			Connection conn = getConnection();
			String querry = "SELECT TOP " + predict + " coalesce(tab1." + chartDT + ",tab2." + chartDT
					+ ") as dt ,tab1." + column + " as val1,tab2." + column + " as val2 FROM [" + dbName + "].[dbo].["
					+ tableName + "] as tab1 FULL OUTER join [" + dbName + "].[dbo].[_" + tableName
					+ "] as tab2 on tab1." + chartDT + "=tab2." + chartDT + " where  tab1." + chartDT + ">? " + orderBy;
			System.out.println("[ChartDB][getMeterGraphWithPredictValues] : Query : " + querry);
			PreparedStatement ps = conn.prepareStatement(querry);
			ps.setTimestamp(1, fromDate);

			ResultSet rs = ps.executeQuery();
			values = new ArrayList<Double>();

			JSONArray jArray = new JSONArray();
			if (fromDate != null) {
				PreparedStatement oldValues;
				ResultSet rsOld = null;
				String sql = "select * from (SELECT top " + ChartDB.oldValues + " coalesce(tab1." + chartDT + ",tab2."
						+ chartDT + ")as dt, tab1." + column + " as val1, tab2." + column + " as val2 FROM [" + dbName
						+ "].[dbo].[" + tableName + "] as tab1 FULL OUTER join [" + dbName + "].[dbo].[_" + tableName
						+ "] as tab2 on tab1." + chartDT + "=tab2." + chartDT + " where coalesce(tab1." + chartDT
						+ ",tab2." + chartDT + ") <? order by dt desc) a " + orderBy;

				System.out.println("[ChartDB][getMeterGraphWithPredictValues] : Top : " + sql);
				oldValues = conn.prepareStatement(sql);
				oldValues.setTimestamp(1, fromDate);
				rsOld = oldValues.executeQuery();
				while (rsOld.next()) {
					String timestamp = rsOld.getString(1);
					Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
					Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;
					// System.out.println(column + " -> actual : " + actual + ",
					// predicted : " + predicted);
					if (actual == null || predicted == null || actual.equals(0f)) {
					} else
						Math.abs(((actual - predicted) / actual) * 100);
					JSONObject json = new JSONObject();

					// System.out.println(column + " -> actual.toString() : " +
					// actual.toString()
					// + ", predicted.toString() : " + predicted.toString());
					json.put(chartDT, timestamp);
					json.put(column, actual == 0f ? null : actual.toString());
					json.put("_" + column, predicted == 0f ? null : predicted.toString());
					// json.put("error", error != null ? error.toString() :
					// null);

					jArray.put(json);
				}
			}

			while (rs.next()) {
				String timestamp = rs.getString(1);
				Float actual = rs.getString(2) != null ? Float.valueOf(rs.getString(2)) : 0f;
				Float predicted = rs.getString(3) != null ? Float.valueOf(rs.getString(3)) : 0f;

				try {
					Math.abs(((actual - predicted) / actual) * 100);
				} catch (Exception e) {
				}
				JSONObject json = new JSONObject();

				json.put(chartDT, timestamp);
				json.put(column, actual == 0f ? null : actual.toString());
				json.put("_" + column, predicted == 0f ? null : predicted.toString());
				// json.put("error", error != null ? error.toString() : null);

				jArray.put(json);
			}

			ps.close();
			rs.close();
			conn.close();
			return jArray;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Meter Graph With Predict Values !!!");
		}
	}

	public String getColumns(@Context HttpServletRequest request, String pUser) throws PException {
		try {
			Connection connection = getConnection();
			String sql = "select url,dbInstanceName,dbName,tableName,columnsName,chartDt,userName,password from Danpac.dbo.masterData where puser='"
					+ pUser + "' order by dt desc ";

			// System.out.println("SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			// System.out.println("1");
			ResultSet rsDBList = psDBList.executeQuery();
			// System.out.println("2");
			JSONArray jArray = new JSONArray();
			// System.out.println("3");
			if (rsDBList.next()) {
				// System.out.println("4 - IF");
				JSONObject json = new JSONObject();
				json.put("url", rsDBList.getString("url"));
				json.put("dbInstanceName", rsDBList.getString("dbInstanceName"));
				json.put("dbName", rsDBList.getString("dbName"));
				json.put("tableName", rsDBList.getString("tableName"));
				json.put("columns", rsDBList.getString("columnsName"));
				json.put("chartDt", rsDBList.getString("chartDt"));
				json.put("userName", rsDBList.getString("userName"));
				json.put("password", rsDBList.getString("password"));
				jArray.put(json);

				request.getSession().setAttribute("url", rsDBList.getString("url"));
				request.getSession().setAttribute("tableName", rsDBList.getString("tableName"));
				request.getSession().setAttribute("dbName", rsDBList.getString("dbName"));
				request.getSession().setAttribute("chartDT", rsDBList.getString("chartDt"));
				request.getSession().setAttribute("dbInstanceName", rsDBList.getString("dbInstanceName"));
				request.getSession().setAttribute("userName", rsDBList.getString("userName"));
				request.getSession().setAttribute("password", rsDBList.getString("password"));
				request.getSession().setAttribute("columns", rsDBList.getString("columnsName"));
				columns = rsDBList.getString("columnsName");
				System.out.println("Columns "+columns);
				request.getSession().setAttribute("pUser", pUser);
				if (columns != null) {
					request.getSession().setAttribute("columns", columns);
					request.getSession().setAttribute("columnA", columns.split(",")[0]);
					try {
						request.getSession().setAttribute("columnB", columns.split(",")[1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						request.getSession().setAttribute("columnB", columns.split(",")[0]);
					}

				}

				System.out.println("Session Columns "+request.getSession().getAttribute("columnA"));
				System.out.println("Session Columns "+request.getSession().getAttribute("columnB"));

				// System.out.println(" IF JSON : " + jArray.toString());
			} else {
				// System.out.println("5 - Else getColumns");
				JSONObject json = new JSONObject();
				json.put("url", DBConnection.url);
				json.put("dbInstanceName", DBConnection.dbInstanceName);
				json.put("dbName", DBConnection.dbName);
				json.put("tableName", DBConnection.tableName);
				json.put("userName", DBConnection.userName);
				json.put("password", DBConnection.password);
				jArray.put(json);
				// System.out.println("ELSE JSON : " + jArray.toString());
			}
			// System.out.println("JSON : " + jArray.toString());
			return jArray.toString();

		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Columns !!!");
		}
	}

	public boolean initialSaveValues(Network network, String coloumn) {
		try
		{
			Connection conn=getConnection();
			String dt=chartDT;	
			String query="select min("+dt+") as min,max("+dt+") as max from "+tableName;
			ResultSet rs=conn.createStatement().executeQuery(query);
			NavigableMap<java.sql.Timestamp, Double> values;
			if(rs.next())
			{
				values=getPredictedValues(rs.getTimestamp(1), rs.getTimestamp(2), network, coloumn);
				savePredictedValues(values, coloumn, tableName);
			}
			conn.close();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

}
