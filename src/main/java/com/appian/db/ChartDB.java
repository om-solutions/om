package com.appian.db;

public class ChartDB {

	private String tableName;
	private String dbName;
	private String columns;
	private String chartDT;

	public ChartDB(String dbName, String tableName, String columns, String chartDT) {
		super();
		this.tableName = tableName;
		this.dbName = dbName;
		this.columns = columns;
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
