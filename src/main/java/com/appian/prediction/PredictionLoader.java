package com.appian.prediction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONObject;

import com.appian.db.ChartDB;
import com.appian.db.DBConnection;
import com.appian.nn.Network;
import com.appian.util.AppianUtil;
import com.google.gson.Gson;
import com.sun.jmx.snmp.Timestamp;

public class PredictionLoader implements ServletContextListener {
	/** The servlet context with which we are associated. */
	private ServletContext context = null;
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log("Context destroyed");
		this.context = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();
		try {
			Connection dbConnection = DBConnection.getConnection();
			String sql = "select [dt],[dbName],[tableName] ,[columnsName],[chartDT],[url],[dbInstanceName],[userName],[password],[pUser] from Danpac.dbo.masterData where dt is not null and columnsname is not null";
			// System.out.println("Inside Context : " + sql);
			PreparedStatement psDBList;

			psDBList = dbConnection.prepareStatement(sql);

			ResultSet rsDBList = psDBList.executeQuery();
			while (rsDBList.next()) {
				JSONObject jobj = new JSONObject();
				jobj.put("url", rsDBList.getString("url"));
				jobj.put("columns", AppianUtil.extractColumns(rsDBList.getString("columnsName")));
				jobj.put("tableName", rsDBList.getString("tableName"));
				jobj.put("dbName", rsDBList.getString("dbName"));
				jobj.put("dbInstanceName", rsDBList.getString("dbInstanceName"));
				jobj.put("userName", rsDBList.getString("userName"));
				jobj.put("password", rsDBList.getString("password"));
				jobj.put("chartDT", rsDBList.getString("chartDT"));

				System.out.println("111111 : " + rsDBList.getString("columnsName"));
				String columns = AppianUtil.extractColumns(rsDBList.getString("columnsName"));
				String tableName = rsDBList.getString("tableName");
				System.out.println("222222 : " + columns);
				List<String> items = Arrays.asList(columns.split(","));
				Iterator<String> i = items.iterator();

				ChartDB chartDB = new ChartDB(jobj);
				while (i.hasNext()) {
					String column = i.next();
					TrainNetwork trainNetwork = new TrainNetwork();
					Network network = null;
					chartDB.columnA = column;
					if (!ChartDB.map.containsKey(tableName + column)) {
						trainNetwork.Train(jobj, format.format(new Date(0)), format.format(new Date()), column,
								tableName);
					}

				}

			}

			dbConnection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void log(String message) {
		if (context != null) {
			context.log("MyServletContextListener: " + message);
		} else {
			// System.out.println("MyServletContextListener: " + message);
		}
	}
}