package com.appian.prediction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appian.db.AdminDB;
import com.appian.db.ChartDB;
import com.appian.db.DBConnection;
import com.appian.exception.PException;
import com.appian.util.AppianUtil;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class Admin {
	private static AdminDB adminDb = null;

	@GET
	@Path("/dbCon")
	@Produces(MediaType.APPLICATION_JSON)
	public void dbCon(@Context HttpServletRequest request) throws PException {

		adminDb = new AdminDB(request);
		if (adminDb != null) {
			Database(request);
		}
		// throw new PException("Unable to get connection !!");

	}

	@GET
	@Path("/saveDays")
	@Produces(MediaType.APPLICATION_JSON)
	public String saveDays(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("daysToPredict") String daysToPredict) {
		try {
			//System.out.println("daysToPredict : " + daysToPredict);
			Connection connection = DBConnection.getConnection();

			String sql = "update Danpac.dbo.masterData set daysToPredict='" + daysToPredict
					+ "' where dt in (select TOP(1) dt  from Danpac.dbo.masterData order by dt desc)";

			PreparedStatement countRecord;
			countRecord = connection.prepareStatement(sql);
			//System.out.println(sql);
			Integer count = countRecord.executeUpdate();
			if (count > 0)
				return "true";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "false";
	}

	@GET
	@Path("/loadCon")
	@Produces(MediaType.APPLICATION_JSON)
	public String loadCon(@Context HttpServletRequest request) throws PException {
		try {
			Connection connection = DBConnection.getConnection();
			String sql = "select url,dbInstanceName,dbName,tableName,columnsName,chartDt,userName,password,daysToPredict from Danpac.dbo.masterData order by dt desc ";
			// System.out.println("SQL : " + sql);
			PreparedStatement psDBList;

			psDBList = connection.prepareStatement(sql);
			JSONArray jArray = new JSONArray();
			ResultSet rsDBList = psDBList.executeQuery();
			if (rsDBList.next()) {
				JSONObject json = new JSONObject();
				json.put("url", rsDBList.getString("url"));
				json.put("columns", AppianUtil.extractColumns(rsDBList.getString("columnsName")));
				json.put("tableName", rsDBList.getString("tableName"));
				json.put("dbName", rsDBList.getString("dbName"));
				json.put("chartDT", rsDBList.getString("chartDT"));
				json.put("dbInstanceName", rsDBList.getString("dbInstanceName"));
				json.put("userName", rsDBList.getString("userName"));
				json.put("daysToPredict", rsDBList.getString("daysToPredict"));
				jArray.put(json);
			} else {

				// System.out.println("5");
				JSONObject json = new JSONObject();
				json.put("url", DBConnection.getUrl());
				json.put("dbInstanceName", DBConnection.getDbInstanceName());
				json.put("dbName", DBConnection.getDbName());
				json.put("tableName", DBConnection.getTableName());
				json.put("userName", DBConnection.getUserName());
				json.put("password", DBConnection.getPassword());
				json.put("daysToPredict", DBConnection.getDaysToPredict());

				jArray.put(json);
				// System.out.println("ELSE JSON : " + jArray.toString());

			}

			return jArray.toString();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new PException("No connection record found !!!");
		}

	}

	@GET
	@Path("/database")
	@Produces(MediaType.APPLICATION_JSON)
	public String Database(@Context HttpServletRequest request) throws PException {
		// adminDb = new AdminDB(request);
		String dbList = adminDb.getDBList();
		// System.out.println(dbList);
		return dbList;
	}

	@GET
	@Path("/table")
	@Produces(MediaType.APPLICATION_JSON)
	public String Table(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("dbName") String db)
			throws PException {
		// System.out.println("table : " + db);
		// adminDb = new AdminDB(request);
		String dbList = adminDb.getTableList(db);
		// System.out.println(dbList);
		return dbList;
	}

	@GET
	@Path("/columns")
	@Produces(MediaType.APPLICATION_JSON)
	public String Columns(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("tableName") String table,
			@DefaultValue("") @QueryParam("dbName") String db) throws PException {
		// System.out.println("table : " + table);
		// AdminDB dbConnection = new AdminDB(request);
		String dbList = adminDb.getColumnsList(table, db);
		// System.out.println(dbList);
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
		// //System.out.println("columns : " + columns);
		// AdminDB dbConnection = new AdminDB(request);
		// to make JSON to Sting
		// columns = AppianUtil.extractColumns(columns);
		//System.out.println("columns : " + columns);
		//System.out.println("dbName : " + dbName);
		//System.out.println("tableName : " + tableName);
		//System.out.println("columns : " + columns);
		//System.out.println("chartDT : " + chartDT);

		if (adminDb.setColumns(dbName, tableName, columns, chartDT,
				(String) request.getSession().getAttribute("pUser"))) {
			// System.out.println("Saved!!!!!!!!!!");
			try {
				columns = AppianUtil.extractColumns(columns);
				String[] s = columns.trim().split(",");
				for (String column : s)
					if (!ChartDB.map.contains(tableName + column)) {
						request.getSession().setAttribute("dbName", dbName);
						request.getSession().setAttribute("tableName", tableName);
						request.getSession().setAttribute("chartDT", chartDT);
						new TrainNetwork().Train(request, PredictionLoader.format.format(new Date(0)),
								PredictionLoader.format.format(new Date()), column, tableName);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "List saved successful";
		} else {
			// System.out.println("ERROR ::!!!!!!!!!");
			return "Error : List not saved";
		}

	}

	// call train network
}
