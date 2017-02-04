package com.appian.db;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.exception.PException;
import com.appian.nn.Network;

public class AdminDB {
	protected static String url;
	protected static String dbInstanceName;
	protected static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";;
	protected static String userName;
	protected static String password;
	protected static String chartDT;
	protected static String dbName;
	protected static String tableName;

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

	public AdminDB(HttpServletRequest request) {
		url = (String) request.getParameter("url");
		dbInstanceName = (String) request.getParameter("dbInstanceName");
		userName = (String) request.getParameter("userName");
		password = (String) request.getParameter("password");
		/*
		 * try { chartDB = new ChartDB(); } catch (InstantiationException |
		 * IllegalAccessException | ClassNotFoundException | SQLException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 * this.predictedColumnName = predicted == null ?
		 * chartDB.getColumns().split(",")[0] : predicted; this.provedColumnName
		 * = proved == null ? chartDB.getColumns().split(",")[0] : proved;
		 * this.chartDT = chartDT == null ? "" : chartDT; this.dbName = dbName
		 * == null ? "" : dbName; this.tableName = tableName == null ? "" :
		 * tableName;
		 */

		//System.out.println("DBConnection -> url : " + url + ", dbInstanceName : " + dbInstanceName);
	}

	public static Connection getConnection() throws PException {
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url + dbInstanceName, userName, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PException("Unabel to get Connection !!!");
		}

	}

	public String getDBList() throws PException {
		try {
			Connection connection = AdminDB.getConnection();
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
			Connection connection = AdminDB.getConnection();
			String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG='"
					+ db + "' ";
			PreparedStatement psDBList = connection.prepareStatement(sql);
			ResultSet rsDBList = psDBList.executeQuery();
			JSONArray jArray = new JSONArray();
			while (rsDBList.next()) {
				if (!rsDBList.getString("TABLE_NAME").startsWith("_")) {
					JSONObject json = new JSONObject();
					json.put("tableName", rsDBList.getString("TABLE_NAME"));
					jArray.put(json);
				}
			}
			return jArray.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException("Uable to get table list !!!");
		}
	}

	public String getColumnsList(String table, String db) throws PException {
		try {

			Connection connection = AdminDB.getConnection();
			String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table
					+ "' ORDER BY ORDINAL_POSITION ";

			//System.out.println("SQL : " + sql);
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

	public Boolean setColumns(String dbName, String tableName, String columns, String chartDT, String user)
			throws PException {
		try {

			Connection conn = AdminDB.getConnection();
			PreparedStatement ps;
			if (!columns.trim().isEmpty() || !tableName.trim().isEmpty() || !dbName.trim().isEmpty()) {
				//System.out.println("Not Blank+columns");
				try {
					//System.out.println("User -- > " + user);
					ps = conn.prepareStatement(
							"insert into Danpac.dbo.masterData (dbName,tableName,columnsName,chartDT,dt,url,dbInstanceName,userName,password,puser) values (?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, dbName);
					ps.setString(2, tableName);
					ps.setString(3, columns);
					ps.setString(4, chartDT);
					ps.setTimestamp(5, new Timestamp(new Date().getTime()));
					ps.setString(6, AdminDB.url);
					ps.setString(7, AdminDB.dbInstanceName);
					ps.setString(8, AdminDB.userName);
					ps.setString(9, AdminDB.password);
					ps.setString(10, user);
					ps.execute();
					ps.close();

					try {
						createTableCopy(tableName);
					} catch (Exception e) {
					}

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			} else {
				//System.out.println("Blank");
				return false;
			}
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException(" Uable to set column list !!!");
		}
	}

	public boolean createTableCopy(String table) throws PException {
		try {

			Connection connection = DBConnection.getConnection();
			String sql = "SELECT * INTO danpac.dbo._" + table + " FROM danpac.dbo." + table + " WHERE 1=2;";

			//System.out.println("SQL : " + sql);
			PreparedStatement psDBList = connection.prepareStatement(sql);
			psDBList.execute();

			return true;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw new PException(" Unable to create copy of table !!!");
		}

	}

}
