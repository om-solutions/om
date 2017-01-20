package com.appian.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChartDB {

	private static String tableName;
	private static String dbName;
	private static String columns;
	private static String chartDT;

	public ChartDB() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		super();

		Connection connection = DBConnection.getConnection();
		String sql = "select dbname,tableName,columnsName,chartDt from Danpac.dbo.masterData order by dt desc ";

		System.out.println("SQL : " + sql);
		PreparedStatement psDBList = connection.prepareStatement(sql);
		ResultSet rsDBList = psDBList.executeQuery();
		if (rsDBList.next()) {

			this.columns = rsDBList.getString("columnsName");
			this.tableName = rsDBList.getString("tableName");
			this.dbName = rsDBList.getString("dbname");
			this.chartDT = rsDBList.getString("chartDT");

			System.out.println("JSON : " + this);
			System.out.println("JSON  columns: " + this.columns);
			System.out.println("JSON  tableName: " + this.tableName);
			System.out.println("JSON  dbName: " + this.dbName);
			System.out.println("JSON  chartDT: " + this.chartDT);

		}

	}

	public ChartDB(String dbname, String tableName, String columnsName, String chartDT) {
		this.columns = columnsName;
		this.tableName = tableName;
		this.dbName = dbname;
		this.chartDT = chartDT;
	}

	public String getTableName() {
		return tableName;
	}

	public String getDbName() {
		return dbName;
	}

	public String getColumns() {
		return columns;
	}

	public String getChartDT() {
		return chartDT;
	}

}
