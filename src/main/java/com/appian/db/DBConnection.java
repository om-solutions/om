package com.appian.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appian.exception.PException;

public class DBConnection {
	static String url;
	static String dbInstanceName;
	protected static String driver;
	static String userName;
	static String password;
	static String chartDT;
	static String dbName;
	static String tableName;
	static Properties dbProperties = new Properties();
	private static String oldValues = "10";
	private ArrayList<Timestamp> times = new ArrayList<Timestamp>();
	private ArrayList<Double> values = new ArrayList<Double>();
	// private ChartDB chartDB;

	private static String csvTableName;
	private static String insertQuery;
	private static int columnCount;
	private static String updateQuery;
	private static int whereClouseIndex;
	static Properties fileProperties = new Properties();;
	private static String daysToPredict;

	static {
		try {
			dbProperties.load(DBConnection.class.getResourceAsStream("/DBproperties.properties"));
			fileProperties.load(DBConnection.class.getResourceAsStream("/FileProperties.properties"));
			url = dbProperties.getProperty("url");
			dbInstanceName = dbProperties.getProperty("dbInstanceName");
			driver = dbProperties.getProperty("driver");
			userName = dbProperties.getProperty("userName");
			password = dbProperties.getProperty("password");
			daysToPredict = dbProperties.getProperty("daysToPredict");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getDaysToPredict() {
		return daysToPredict;
	}

	public static void setDaysToPredict(String daysToPredict) {
		DBConnection.daysToPredict = daysToPredict;
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

	public static Connection getConnection() throws PException {
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(getUrl() + getDbInstanceName(), getUserName(), getPassword());
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unabel to get Connection !!!");
		}

	}

	public static String updateRowTable(String[] rowData) throws PException {
		Connection conn;
		int count = 0;

		try {
			conn = DBConnection.getConnection();
			// System.out.println("! Updated");
			PreparedStatement ps = conn.prepareStatement(updateQuery);
			// System.out.println("!! Updated");
			String whereClauseValue = null;
			int j = 0;
			// System.out.println("columnCount : " + columnCount);
			for (int i = 0; i < columnCount; i++) {
				if (whereClouseIndex == i) {
					whereClauseValue = rowData[i].toString();
				} else {
					ps.setString(i + 1, rowData[i].toString());
					j++;
				}
			}
			// System.out.println("!!! Updated : " + whereClauseValue);
			ps.setString(j, whereClauseValue);
			count = ps.executeUpdate();
			ps.close();
			conn.close();
			// return jArray.toString();
			// System.out.println("Updated");
			return count > 0 ? "true" : "false";
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Unable to insert / update record into table !!!");

		}

	}

	public static String insertRowTable(String[] rowData) throws PException {
		Connection conn;
		int count = 0;

		try {
			conn = DBConnection.getConnection();
			PreparedStatement ps = conn.prepareStatement(insertQuery);
			for (int i = 0; i < columnCount; i++) {
				ps.setString(i + 1, rowData[i].toString());
			}
			count = ps.executeUpdate();
			ps.close();
			conn.close();
			// return jArray.toString();
			// System.out.println("Inserted");
			return count > 0 ? "true" : "false";
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Unable to insert / update record into table !!!");

		}

	}

	public static JSONObject getMaxDate(String tableName) throws PException {
		try {
			System.out.println("In side getMaxDate()");
			Connection connection = DBConnection.getConnection();
			String noOfDays = getDBDaysToPredict();
			String sql = "SELECT max(DateTime) startdt,DATEADD(day, " + noOfDays
					+ ", MAX(DateTime)) as enddt FROM danpac.dbo." + tableName;
			System.out.println("In side getMaxDate() Query : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			ResultSet rsDBList = psDBList.executeQuery();
			// JSONArray jArray = new JSONArray();
			JSONObject json = new JSONObject();
			if (rsDBList.next()) {
				json.put("startdt", convertDBtoDateFormat(rsDBList.getString("startdt")));
				json.put("enddt", convertDBtoDateFormat(rsDBList.getString("enddt")));

				// jArray.put(json);
			}
			return json;
		} catch (SQLException | JSONException | ParseException e) {
			// e.printStackTrace();
			throw new PException("Unable to get Max Date list !!!");
		}

	}

	public static String getDBDaysToPredict() throws PException, SQLException {
		String tempDaysToPredict;
		Connection connection = getConnection();
		String sql = "select top 1 daysToPredict from Danpac.dbo.masterData order by dt desc ";
		System.out.println("getDaysToPredict() : SQL : " + sql);
		PreparedStatement psDBList;
		psDBList = connection.prepareStatement(sql);
		ResultSet rsDBList = psDBList.executeQuery();
		if (rsDBList.next()) {
			System.out.println("getDaysToPredict() : daysToPredict" + rsDBList.getString("daysToPredict"));
			return rsDBList.getString("daysToPredict");
		}
		return "90";

	}

	private static String convertDBtoDateFormat(String string) throws ParseException {

		DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
		Date date = inputFormat.parse(string);

		// Format date into output format
		DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

		return outputFormat.format(date);
	}

	public static void createReplaceTable(String fileName, String[] headerData, String[] rowData) throws PException {
		// System.out.println("fileName : " + fileName);
		csvTableName = fileName.replace(".csv", "");
		// System.out.println("fileName : " + csvTableName);
		String query = "CREATE TABLE " + csvTableName + " ( ";
		insertQuery = " insert into " + csvTableName;
		updateQuery = " update " + csvTableName + " SET  ";
		String whereClouse = "";
		String insertColumns = " ( ";
		String insertValues = " ( ";
		columnCount = rowData.length;
		for (int i = 0; i < columnCount; i++) {
			// String regex="\\d+[\\/\\:\\-]\\d+";
			// //System.out.println(i + " : " + splitData.toString());
			String header = headerData[i].toString();
			if (header.isEmpty()) {
				header = "header" + i;
			}

			if (rowData[i].toString().matches("[0-9.]*")) {
				if (i == 0) {
					query += " " + header + " real ";
					insertColumns += header;
					insertValues += " ? ";
					updateQuery += header + " = ? ";
				} else {
					query += ", " + header + " real ";
					insertColumns += ", " + header;
					insertValues += ", ? ";
					updateQuery += "," + header + " = ? ";
				}
			} else if ((rowData[i].toString().contains("/") && rowData[i].toString().contains(":"))
					|| (rowData[i].toString().contains(":") && rowData[i].toString().contains("-"))) {
				if (i == 0) {
					query += " " + header + " datetime UNIQUE ";
					insertColumns += header;
					insertValues += " ? ";
					whereClouse = " where " + header + " = ? ";
					whereClouseIndex = i;
				} else {
					query += ", " + header + " datetime  UNIQUE";
					insertColumns += ", " + header;
					insertValues += ", ? ";
					whereClouse = " where " + header + " = ? ";
					whereClouseIndex = i;
				}
			} else {
				if (i == 0) {
					query += " " + header + " varchar(MAX)  ";
					insertColumns += header;
					insertValues += " ? ";
					updateQuery += header + " = ? ";
				} else {
					query += ", " + header + " varchar(MAX)  ";
					insertColumns += ", " + header;
					insertValues += ", ? ";
					updateQuery += "," + header + " = ? ";
				}

			}

		}
		updateQuery += whereClouse;
		insertQuery += insertColumns + " ) values " + insertValues + " ) ";
		// System.out.println("$$$ " + updateQuery);
		query += " ) ";

		Statement stmt;
		try {
			stmt = getConnection().createStatement();

			/*
			 * String sql = "CREATE TABLE REGISTRATION " +
			 * "(id INTEGER not NULL, " + " first VARCHAR(255), " +
			 * " last VARCHAR(255), " + " age INTEGER, " +
			 * " PRIMARY KEY ( id ))";
			 */

			// System.out.println("Query : " + query);
			stmt.executeUpdate(query);

		} catch (PException | SQLException e) {
			// e.printStackTrace();
			throw new PException(e.getMessage());
		}
	}

	public String validateAdmin(String username, String password) throws PException {
		try {
			Connection conn = DBConnection.getConnection();
			// //System.out.println(username + ":" + password);
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
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Invalid User !!!");
		}

	}

	public String validateUser(String username, String password) throws PException {
		try {
			Connection conn = DBConnection.getConnection();
			// //System.out.println(username + ":" + password);
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM Danpac.dbo.members inner join Danpac.dbo.masterData on uname=pUser where uname=? and pass=? ;");
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
				status = "Please login as a admin first ";
			}

			ps.close();
			rs.close();
			conn.close();
			// return jArray.toString();
			return status;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Invalid User !!!");
		}

	}

	public String register(String name, String email, String username, String password) throws PException {
		try {
			Connection conn = DBConnection.getConnection();
			// //System.out.println(username + ":" + password);
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
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException(" Uable to register !!!");
		}
	}

	public String getDBList() throws PException {
		try {
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
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Unable to get database list !!!");
		}
	}

	public String getTableList(String db) throws PException {
		try {
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
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException("Uable to get table list !!!");
		}
	}

	public String getColumnsList(String table, String db) throws PException {
		try {

			Connection connection = DBConnection.getConnection();
			String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
					+ "' ORDER BY ORDINAL_POSITION ";

			// System.out.println("SQL : " + sql);
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
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException(" Uable to get column list !!!");
		}

	}

	public Boolean setColumns(String dbName, String tableName, String columns, String chartDT, String user)
			throws PException {
		try {

			Connection conn = DBConnection.getConnection();
			PreparedStatement ps;
			if (!columns.trim().isEmpty() || !tableName.trim().isEmpty() || !dbName.trim().isEmpty()) {
				// System.out.println("Not Blank+columns");
				try {
					ps = conn.prepareStatement(
							"insert into Danpac.dbo.masterData (dbName,tableName,columnsName,chartDT,dt,url,dbInstanceName,userName,password,user) values (?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, dbName);
					ps.setString(2, tableName);
					ps.setString(3, columns);
					ps.setString(4, chartDT);
					ps.setTimestamp(5, new Timestamp(new Date().getTime()));
					ps.setString(6, DBConnection.getUrl());
					ps.setString(7, DBConnection.getDbInstanceName());
					ps.setString(8, DBConnection.getUserName());
					ps.setString(9, DBConnection.getPassword());
					ps.setString(9, user);
					ps.execute();
					ps.close();
				} catch (Exception e) {
					// e.printStackTrace();
					return false;
				}

			} else {
				// System.out.println("Blank");
				return false;
			}
			conn.close();
			return true;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException(" Uable to set column list !!!");
		}
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		DBConnection.url = url;
	}

	public static String getTableName() {
		return tableName;
	}

	public static void setTableName(String tableName) {
		DBConnection.tableName = tableName;
	}

	public static String getDbName() {
		return dbName;
	}

	public static void setDbName(String dbName) {
		DBConnection.dbName = dbName;
	}

	public static String getDbInstanceName() {
		return dbInstanceName;
	}

	public static void setDbInstanceName(String dbInstanceName) {
		DBConnection.dbInstanceName = dbInstanceName;
	}

	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		DBConnection.userName = userName;
	}

	public static String getChartDT() {
		return chartDT;
	}

	public static void setChartDT(String chartDT) {
		DBConnection.chartDT = chartDT;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		DBConnection.password = password;
	}

}
