package com.appian.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.exception.PException;

public class DBConnection {
	static String url = "jdbc:sqlserver://localhost:1433";
	static String dbInstanceName = ";databaseName=Danpac;instance=SQLEXPRESS";
	protected static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	static String userName = "sa";
	static String password = "root";
	static String chartDT;
	static String dbName;
	static String tableName;

	private static String oldValues = "10";
	private ArrayList<Timestamp> times = new ArrayList<Timestamp>();
	private ArrayList<Double> values = new ArrayList<Double>();
	private String provedColumnName = "k_factor";
	private String predictedColumnName = "k_factor";
	// private ChartDB chartDB;

	private static String csvTableName;
	private static String insertQuery;
	private static int columnCount;

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

	public static String insertRowTable(String[] rowData) throws PException {
		Connection conn;
		int count = 0;
		conn = DBConnection.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(insertQuery);
			for (int i = 0; i < columnCount; i++) {
				ps.setString(i + 1, rowData[i].toString());
			}
			count = ps.executeUpdate();
			ps.close();
			conn.close();
			// return jArray.toString();

			return count > 0 ? "true" : "false";
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unable to insert record into table !!!");
		}

	}

	public static void createReplaceTable(String fileName, String[] headerData, String[] rowData) throws PException {
		System.out.println("fileName : " + fileName);
		csvTableName = fileName.replace(".csv", "");
		System.out.println("fileName : " + csvTableName);
		String query = "CREATE TABLE " + csvTableName + " ( ";
		insertQuery = " insert into " + csvTableName;
		String insertColumns = " ( ";
		String insertValues = " ( ";
		columnCount = rowData.length;
		for (int i = 0; i < columnCount; i++) {
			// String regex="\\d+[\\/\\:\\-]\\d+";
			// System.out.println(i + " : " + splitData.toString());
			String header = headerData[i].toString();
			if (header.isEmpty()) {
				header = "header" + i;
			}

			if (rowData[i].toString().matches("[0-9.]*")) {
				if (i == 0) {
					query += " " + header + " real ";
					insertColumns += header;
					insertValues += " ? ";
				} else {
					query += ", " + header + " real ";
					insertColumns += ", " + header;
					insertValues += ", ? ";
				}
			} else if (rowData[i].toString().contains(":") && (rowData[i].toString().contains("/")
					|| rowData[i].toString().contains(".") || rowData[i].toString().contains("-"))) {
				if (i == 0) {
					query += " " + header + " datetime UNIQUE ";
					insertColumns += header;
					insertValues += " ? ";
				} else {
					query += ", " + header + " datetime  UNIQUE";
					insertColumns += ", " + header;
					insertValues += ", ? ";
				}
			} else {
				if (i == 0) {
					query += " " + header + " varchar(MAX)  ";
					insertColumns += header;
					insertValues += " ? ";
				} else {
					query += ", " + header + " varchar(MAX)  ";
					insertColumns += ", " + header;
					insertValues += ", ? ";
				}

			}

		}

		insertQuery += insertColumns + " ) values " + insertValues + " ) ";
		System.out.println("$$$ " + insertQuery);
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

			System.out.println("Query : " + query);
			stmt.executeUpdate(query);

		} catch (PException | SQLException e) {
			e.printStackTrace();
			throw new PException(e.getMessage());
		}
	}

	public String validateUser(String username, String password) throws PException {
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException("Invalid User !!!");
		}

	}

	public String register(String name, String email, String username, String password) throws PException {
		try {
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
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			throw new PException("Uable to get table list !!!");
		}
	}

	public String getColumnsList(String table, String db) throws PException {
		try {

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
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException(" Uable to get column list !!!");
		}

	}

	public Boolean setColumns(String dbName, String tableName, String columns, String chartDT) throws PException {
		try {

			Connection conn = DBConnection.getConnection();
			PreparedStatement ps;
			if (!columns.trim().isEmpty() || !tableName.trim().isEmpty() || !dbName.trim().isEmpty()) {
				System.out.println("Not Blank+columns");
				try {
					ps = conn.prepareStatement(
							"insert into Danpac.dbo.masterData (dbName,tableName,columnsName,chartDT,dt,url,dbInstanceName,userName,password) values (?,?,?,?,?,?,?,?,?)");
					ps.setString(1, dbName);
					ps.setString(2, tableName);
					ps.setString(3, columns);
					ps.setString(4, chartDT);
					ps.setTimestamp(5, new Timestamp(new Date().getTime()));
					ps.setString(6, DBConnection.getUrl());
					ps.setString(7, DBConnection.getDbInstanceName());
					ps.setString(8, DBConnection.getUserName());
					ps.setString(9, DBConnection.getPassword());
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
		} catch (SQLException e) {
			e.printStackTrace();
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
