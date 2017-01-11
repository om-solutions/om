package com.appian.prediction.POJO;

public class Column {

	private String columnName;
	private Boolean isNull;
	private String dataType;

	public Column(String columnName, Boolean isNull, String dataType) {
		super();
		this.columnName = columnName;
		this.isNull = isNull;
		this.dataType = dataType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(Boolean isNull) {
		this.isNull = isNull;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
