package com.appian.db;

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

import com.appian.nn.Network;

public class DBConnection {
	protected static String url = "jdbc:sqlserver://localhost:1433";
	protected static String dbInstanceName = ";databaseName=Danpac;instance=SQLEXPRESS";
	protected static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	protected static String userName = "sa";
	protected static String password = "root";
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

	public DBConnection(HttpServletRequest request) {
		String predicted = (String) request.getSession().getAttribute("predicted");
		String proved = (String) request.getSession().getAttribute("proved");
		String chartDT = (String) request.getSession().getAttribute("chartDT");
		String dbName = (String) request.getSession().getAttribute("dbName");
		String tableName = (String) request.getSession().getAttribute("tableName");
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

		System.out.println("DBConnection -> dbName : " + dbName + ", tableName : " + tableName);
	}

	public static Connection getConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(driver).newInstance();
		Connection conn = DriverManager.getConnection(url + dbInstanceName, userName, password);
		return conn;
	}

	public static String insertRowTable(String[] rowData)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn;
		int count = 0;
		conn = DBConnection.getConnection();

		PreparedStatement ps = conn.prepareStatement(insertQuery);
		for (int i = 0; i < columnCount; i++) {
			ps.setString(i + 1, rowData[i].toString());
		}
		count = ps.executeUpdate();
		ps.close();
		conn.close();
		// return jArray.toString();

		return count > 0 ? "true" : "false";

	}

	public static void createReplaceTable(String fileName, String[] headerData, String[] rowData) {
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

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("INFO : " + e.getMessage());

		}
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
						"insert into Danpac.dbo.masterData (dbName,tableName,columnsName,chartDT,dt,url,dbInstanceName,userName,password) values (?,?,?,?,?,?,?,?,?)");
				ps.setString(1, dbName);
				ps.setString(2, tableName);
				ps.setString(3, columns);
				ps.setString(4, chartDT);
				ps.setTimestamp(5, new Timestamp(new Date().getTime()));
				ps.setString(6, DBConnection.url);
				ps.setString(7, DBConnection.dbInstanceName);
				ps.setString(8, DBConnection.userName);
				ps.setString(9, DBConnection.password);
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

}
