package com.appian.prediction;

import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.appian.db.DBConnection;
import com.appian.exception.PException;
import com.appian.prediction.POJO.Column;
import com.google.gson.Gson;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class Admin {

	@GET
	@Path("/database")
	@Produces(MediaType.APPLICATION_JSON)
	public String Database(@Context HttpServletRequest request) throws PException {
		DBConnection dbConnection = new DBConnection(request);
		String dbList = dbConnection.getDBList();
		System.out.println(dbList);
		return dbList;
	}

	@GET
	@Path("/table")
	@Produces(MediaType.APPLICATION_JSON)
	public String Table(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("dbName") String db)
			throws PException {
		System.out.println("table : " + db);
		DBConnection dbConnection = new DBConnection(request);
		String dbList = dbConnection.getTableList(db);
		System.out.println(dbList);
		return dbList;
	}

	@GET
	@Path("/columns")
	@Produces(MediaType.APPLICATION_JSON)
	public String Columns(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("tableName") String table,
			@DefaultValue("") @QueryParam("dbName") String db) throws PException {
		System.out.println("table : " + table);
		DBConnection dbConnection = new DBConnection(request);
		String dbList = dbConnection.getColumnsList(table, db);
		System.out.println(dbList);
		return dbList;
	}

	@GET
	@Path("/saveCols")
	@Produces(MediaType.TEXT_PLAIN)
	public String saveColumns(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("columns") String columns,
			@DefaultValue("") @QueryParam("dbName") String dbName,
			@DefaultValue("") @QueryParam("tableName") String tableName,
			@DefaultValue("") @QueryParam("chartDT") String chartDT) throws PException {
		// System.out.println("columns : " + columns);
		DBConnection dbConnection = new DBConnection(request);
		if (dbConnection.setColumns(dbName, tableName, columns, chartDT)) {
			System.out.println("Saved!!!!!!!!!!");
			return "List saved successful";
		} else {
			System.out.println("ERROR ::!!!!!!!!!");
			return "Error : List not saved";
		}
	}
}
