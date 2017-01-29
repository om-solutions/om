package com.appian.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.exception.PException;
import com.appian.nn.Network;
import com.appian.prediction.TrainNetwork;

public class ChartDB {
	public static String url;
	public static String dbInstanceName;
	private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String userName;
	public static String password;
	private static String oldValues = "10";
	private ArrayList<Timestamp> times = new ArrayList<Timestamp>();
	private ArrayList<Double> values = new ArrayList<Double>();
	private String columnName;
	// private String predictedColumnName;
	public static String chartDT;
	public static String dbName;
	public static String tableName;
	public static String columns;

	public static HashMap<String, TrainNetwork> map = new HashMap<>();

	public ChartDB() throws PException {
		try {
			Connection connection = DBConnection.getConnection();
			String sql = "select url,dbInstanceName,dbName,tableName,columnsName,chartDt,userName,password from Danpac.dbo.masterData order by dt desc ";
			System.out.println("SQL1 : " + sql);
			PreparedStatement psDBList;

			psDBList = connection.prepareStatement(sql);

			ResultSet rsDBList = psDBList.executeQuery();
			if (rsDBList.next()) {
				System.out.println("if rsDBList");
				ChartDB.url = rsDBList.getString("url");
				ChartDB.columns = rsDBList.getString("columnsName");
				ChartDB.tableName = rsDBList.getString("tableName");
				ChartDB.dbName = rsDBList.getString("dbname");
				ChartDB.chartDT = rsDBList.getString("chartDT");
				ChartDB.dbInstanceName = ";databaseName=" + rsDBList.getString("dbInstanceName")
						+ ";instance=SQLEXPRESS";
				ChartDB.userName = rsDBList.getString("userName");
				ChartDB.password = rsDBList.getString("password");
			} else {
				System.out.println("ELSE rsDBList");
				ChartDB.url = DBConnection.url;
				// this.columns = DBConnection.columns;
				ChartDB.tableName = DBConnection.tableName;
				ChartDB.dbName = DBConnection.dbName;
				ChartDB.chartDT = DBConnection.chartDT;
				ChartDB.dbInstanceName = DBConnection.dbInstanceName;
				ChartDB.userName = DBConnection.userName;
				ChartDB.password = DBConnection.password;
			}
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

	public ChartDB(HttpServletRequest request) throws PException {

		// this();
		String predicted = (String) request.getSession().getAttribute("predicted");
		String proved = (String) request.getSession().getAttribute("proved");
		String chartDT = (String) request.getSession().getAttribute("chartDT");
		String dbName = (String) request.getSession().getAttribute("dbName");
		String tableName = (String) request.getSession().getAttribute("tableName");
		this.columnName = predicted == null ? ChartDB.columns.split(",")[0] : predicted;
		// this.provedColumnName = proved == null ?
		// ChartDB.columns.split(",")[0] : proved;
		ChartDB.chartDT = chartDT == null ? "" : chartDT;
		ChartDB.dbName = dbName == null ? "" : dbName;
		ChartDB.tableName = tableName == null ? "" : tableName;

		System.out.println("DBConnection -> dbName : " + dbName + ", tableName : " + tableName);
	}

	public static Connection getConnection() throws PException {
		try {
			System.out.println("URL : " + url + " : " + ",\nColumn : " + columns + ",\nTableName : " + tableName
					+ "\nDBName : " + dbName + "\nChartDT : " + chartDT + "\ndbInstanceName : " + dbInstanceName
					+ "\nUserName : " + userName + "\nPassword : " + password);

			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url + dbInstanceName, userName, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to connect Chart Database !!!");
		}
	}

	public ArrayList<Timestamp> getActualTimestamps(Timestamp fromDate, Timestamp toDate) throws PException {
		try {
			ArrayList<Timestamp> times = new ArrayList<Timestamp>();
			Connection conn = ChartDB.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT tab1." + chartDT + ",coalesce(tab1." + columnName
					+ ",tab2." + columnName + ") as val1 FROM " + dbName + ".dbo." + tableName
					+ " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1." + chartDT
					+ "=tab2." + chartDT + "  where tab1." + chartDT + ">? and tab1." + chartDT + "<? order by tab1."
					+ chartDT + "");
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
			e.printStackTrace();
			throw new PException("Unable to get Actual Timestamps !!!");
		}
	}

	public ArrayList<Double> getActualValuesAndSetNormalizationFactors(Network network, Timestamp fromDate,
			Timestamp toDate, String Column) throws PException {
		try {
			times = new ArrayList<Timestamp>();
			Connection conn = ChartDB.getConnection();
			String query = "SELECT tab1." + chartDT + ",coalesce(tab1." + Column + ",tab2." + Column + ") as val1 FROM "
					+ dbName + ".dbo." + tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName
					+ " as tab2  ON tab1." + chartDT + "=tab2." + chartDT + " where tab1." + chartDT + ">=? and tab1."
					+ chartDT + "<=? order by tab1." + chartDT + "";
			System.out.println("[getActualValuesAndSetNormalizationFactors] : " + query);
			PreparedStatement ps = conn.prepareStatement(query);
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Actual Values And Set Normalization Factors !!!");
		}
	}

	public void savePredictedValues(TreeMap<Timestamp, Double> values2, String column) throws PException {
		try {
			Connection conn = ChartDB.getConnection();

			ArrayList<Timestamp> times = getActualTimestamps(values2.firstKey(), values2.lastKey());
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
			createTableCopy(tableName);
			e.printStackTrace();
		}
	}

	public boolean createTableCopy(String table) throws PException {
		try {

			Connection connection = DBConnection.getConnection();
			String sql = "SELECT * INTO danpac.dbo._" + table + " FROM danpac.dbo." + table + " WHERE 1=2;'";

			System.out.println("SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			psDBList.executeQuery();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException(" Unable to create copy of table !!!");
		}

	}

	public java.util.TreeMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Timestamp toDate,
			Network network, String column) throws PException {
		try {
			Connection conn = ChartDB.getConnection();
			String query = "select top " + network.slidingWindowSize + " * from (select tab1." + chartDT
					+ ",coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo." + tableName
					+ " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1." + chartDT
					+ "=tab2." + chartDT + " where tab1." + chartDT + " <? and coalesce(tab1." + column + ",tab2."
					+ column + ") is not null ) as  a order by a." + chartDT + " desc;";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			System.out.println("--> " + query);
			System.out.println("--> " + fromDate);
			preparedStatement.setTimestamp(1, fromDate);
			ResultSet rs = preparedStatement.executeQuery();
			ArrayList<Timestamp> previousValues = new ArrayList<Timestamp>();
			double[] previousValuesList = new double[network.slidingWindowSize];
			int i = network.slidingWindowSize - 1;
			while (rs.next()) {
				System.out.println("@@@@ : " + rs.getTimestamp(1));
				previousValues.add(rs.getTimestamp(1));
				previousValuesList[i--] = network.normalizeValue(rs.getDouble(2));
			}
			if (previousValues.isEmpty()) {
				String query1 = "select top " + network.slidingWindowSize + " * from (select tab1." + chartDT
						+ ",coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo."
						+ tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName
						+ " as tab2  ON tab1." + chartDT + "=tab2." + chartDT + " where coalesce(tab1." + column
						+ ",tab2." + column + ") is not null ) as  a order by a." + chartDT + " desc;";
				PreparedStatement preparedStatement1 = conn.prepareStatement(query1);
				System.out.println("--> " + query1);
				System.out.println("--> " + fromDate);
				rs = preparedStatement1.executeQuery();
				previousValues = new ArrayList<Timestamp>();
				previousValuesList = new double[network.slidingWindowSize];
				i = network.slidingWindowSize - 1;
				while (rs.next()) {
					System.out.println("@@@@ : " + rs.getTimestamp(1));
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
			System.out.println(previousValues.size());
			Long avgDelay = findAvgDelay(previousValues);

			PreparedStatement getValues = conn
					.prepareStatement("select tab1." + chartDT + ",coalesce(tab1." + column + ",tab2." + column
							+ ") as val1 FROM " + dbName + ".dbo." + tableName + " as tab1  FULL OUTER JOIN " + dbName
							+ ".dbo._" + tableName + " as tab2  ON tab1." + chartDT + "=tab2." + chartDT
							+ " where tab1." + chartDT + ">? and tab1." + chartDT + "<? order by tab1." + chartDT + "");
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Predicted Values !!!");
		}

	}

	public java.util.TreeMap<Timestamp, Double> getPredictedValues(Timestamp fromDate, Integer numberOfValues,
			Network network, String column) throws PException {
		try {
			Connection conn = ChartDB.getConnection();
			PreparedStatement preparedStatement = conn.prepareStatement(
					"select top " + network.slidingWindowSize + " * from (select tab1." + chartDT + ",coalesce(tab1."
							+ column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo." + tableName
							+ " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1."
							+ chartDT + "=tab2." + chartDT + " where tab1." + chartDT + " <? and coalesce(tab1."
							+ column + ",tab2." + column + ") is not null ) as  a order by " + chartDT + " desc;");
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

			PreparedStatement getValues = conn.prepareStatement("select top " + numberOfValues + " * from (select tab1."
					+ chartDT + ",coalesce(tab1." + column + ",tab2." + column + ") as val1 FROM " + dbName + ".dbo."
					+ tableName + " as tab1  FULL OUTER JOIN " + dbName + ".dbo._" + tableName + " as tab2  ON tab1."
					+ chartDT + "=tab2." + chartDT + " where tab1." + chartDT + ">?) as a order by a." + chartDT
					+ "");
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

		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Predicted Values !!!");
		}
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

	public JSONArray getMeterGraphValues(Timestamp fromDate, Timestamp toDate, String column) throws PException {
		try {
			Connection conn = ChartDB.getConnection();
			PreparedStatement ps;

			if (fromDate == null && toDate == null) {
				String query = "SELECT tab1." + chartDT + ",tab1." + column + " as predicted,tab2." + column
						+ " as proved from " + dbName + ".dbo." + tableName + " as tab1 full outer join " + tableName
						+ " as tab2 on tab1." + chartDT + "=tab2." + chartDT + " order by tab1." + chartDT + "";
				System.out.println("[getMeterGraphValues] : from\\to date is null Query : " + query);
				ps = conn.prepareStatement(query);
				System.out.println("---> : " + query);
			} else {
				String query = "SELECT tab1." + chartDT + ",tab1." + column + " as val1, tab2." + column
						+ " as val2  FROM  [" + dbName + "].[dbo].[" + tableName + "] as tab1 FULL OUTER join ["
						+ dbName + "].[dbo].[_" + tableName + "] as tab2 on tab1." + chartDT + "=tab2." + chartDT
						+ " where tab1." + chartDT + ">? and tab1." + chartDT + "<? order by tab1." + chartDT + "";
				System.out.println("[getMeterGraphValues] : from \\ to date Not null Query : " + query);
				ps = conn.prepareStatement(query);

				ps.setTimestamp(1, fromDate);
				ps.setTimestamp(2, toDate);
				System.out.println("---> : " + query);
			}
			// System.out.println("3 : " + fromDate + " : " + toDate);

			JSONArray jArray = new JSONArray();

			if (fromDate != null) {
				PreparedStatement oldValues;
				ResultSet rsOld = null;
				String query = "select * from (SELECT top " + ChartDB.oldValues + " tab1." + chartDT + ",tab1." + column
						+ " as val1 , tab2." + column + " as val2 FROM [" + dbName + "].[dbo].[" + tableName
						+ "] as tab1 FULL OUTER join [" + dbName + "].[dbo].[_" + tableName + "] as tab2 on tab1."
						+ chartDT + "=tab2." + chartDT + " where tab1." + chartDT + "<? order by tab1." + chartDT
						+ " desc) a order by a." + chartDT + "";
				oldValues = conn.prepareStatement(query);
				oldValues.setTimestamp(1, fromDate);
				System.out.println("[getMeterGraphValues] : " + query);
				rsOld = oldValues.executeQuery();
				while (rsOld.next()) {
					String timestamp = rsOld.getString(1);
					Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
					Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;

					if (actual == null || predicted == null || actual.equals(0f)) {
					} else
						Math.abs(((actual - predicted) / actual) * 100);
					JSONObject json = new JSONObject();

					json.put(chartDT, timestamp);
					json.put(column, actual == 0f ? null : actual.toString());
					json.put("_" + column + column, predicted == 0f ? null : predicted.toString());
					// json.put("error", error != null ? error.toString() :
					// null);
					// System.out.println("[getMeterGraphValues] : JSON1 : " +
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

				// System.out.println("timestamp : " + timestamp + ", actual : "
				// + actual + ", predicted : " + predicted);
				json.put(chartDT, timestamp);
				json.put(column, actual == 0f ? null : actual.toString());
				json.put("_" + column, predicted.toString());
				// json.put("error", error != null ? error.toString() : null);

				// System.out.println("json2 : " + json.toString());
				jArray.put(json);
				// System.out.println("[getMeterGraphValues] : JSON2 : " +
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

	public String getMeterGraphWithPredictValues(Timestamp fromDate, String predict, String column) throws PException {
		try {
			Connection conn = ChartDB.getConnection();
			String querry = "SELECT TOP " + predict + " tab1." + chartDT + ",tab1." + column + " as val1,tab2." + column
					+ " as val2 FROM [" + dbName + "].[dbo].[" + tableName + "] as tab1 FULL OUTER join [" + dbName
					+ "].[dbo].[_" + tableName + "] as tab2 on tab1." + chartDT + "=tab1." + chartDT + " where "
					+ chartDT + ">? order by " + chartDT + " ";
			System.out.println("[getMeterGraphWithPredictValues] : " + querry);
			PreparedStatement ps = conn.prepareStatement(querry);
			ps.setTimestamp(1, fromDate);

			ResultSet rs = ps.executeQuery();
			values = new ArrayList<Double>();

			JSONArray jArray = new JSONArray();
			if (fromDate != null) {
				PreparedStatement oldValues;
				ResultSet rsOld = null;
				String sql = "select * from (SELECT top " + ChartDB.oldValues + " tab1." + chartDT + ",tab1" + column
						+ " as val1, tab2." + column + " as val2 FROM [" + dbName + "].[dbo].[" + tableName + "] where "
						+ chartDT + "<? order by " + chartDT + " desc) a order by " + chartDT + "";
				System.out.println("[getMeterGraphWithPredictValues] : " + sql);
				oldValues = conn.prepareStatement(sql);
				oldValues.setTimestamp(1, fromDate);
				rsOld = oldValues.executeQuery();
				while (rsOld.next()) {
					String timestamp = rsOld.getString(1);
					Float actual = rsOld.getString(2) != null ? Float.valueOf(rsOld.getString(2)) : 0f;
					Float predicted = rsOld.getString(3) != null ? Float.valueOf(rsOld.getString(3)) : 0f;

					if (actual == null || predicted == null || actual.equals(0f)) {
					} else
						Math.abs(((actual - predicted) / actual) * 100);
					JSONObject json = new JSONObject();

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
			return jArray.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Meter Graph With Predict Values !!!");
		}
	}

	public String getColumns(String pUser) throws PException {
		try {
			Connection connection = ChartDB.getConnection();
			String sql = "select url,dbInstanceName,dbName,tableName,columnsName,chartDt,userName,password from Danpac.dbo.masterData where puser='"
					+ pUser + "' order by dt desc ";

			System.out.println("SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			System.out.println("1");
			ResultSet rsDBList = psDBList.executeQuery();
			System.out.println("2");
			JSONArray jArray = new JSONArray();
			System.out.println("3");
			if (rsDBList.next()) {
				System.out.println("4");
				JSONObject json = new JSONObject();
				json.put("url", rsDBList.getString("url"));
				json.put("dbInstanceName", rsDBList.getString("dbInstanceName"));
				json.put("dbName", rsDBList.getString("dbName"));
				json.put("tableName", rsDBList.getString("tableName"));
				json.put("columnsName", rsDBList.getString("columnsName"));
				json.put("chartDt", rsDBList.getString("chartDt"));
				json.put("userName", rsDBList.getString("userName"));
				json.put("password", rsDBList.getString("password"));
				jArray.put(json);
				System.out.println(" IF JSON : " + jArray.toString());
			} else {
				System.out.println("5");
				JSONObject json = new JSONObject();
				json.put("url", DBConnection.url);
				json.put("dbInstanceName", DBConnection.dbInstanceName);
				json.put("dbName", DBConnection.dbName);
				json.put("tableName", DBConnection.tableName);
				json.put("userName", DBConnection.userName);
				json.put("password", DBConnection.password);
				jArray.put(json);
				System.out.println("ELSE JSON : " + jArray.toString());
			}
			System.out.println("JSON : " + jArray.toString());
			return jArray.toString();

		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to get Columns !!!");
		}
	}

}
