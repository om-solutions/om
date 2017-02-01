package com.appian.prediction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.appian.db.ChartDB;
import com.appian.db.DBConnection;

public class PredictionLoader implements ServletContextListener {
	/** The servlet context with which we are associated. */
	private ServletContext context = null;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log("Context destroyed");
		this.context = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();
try{
		Connection dbConnection = DBConnection.getConnection();
		String sql = "select columnsName,tableName , pUser from Danpac.dbo.masterData where     dt  IN (select  MAX(dt) as dt  from Danpac.dbo.masterData group by puser)";
		System.out.println("Inside Context : " + sql);
		PreparedStatement psDBList;

		psDBList = dbConnection.prepareStatement(sql);

		ResultSet rsDBList = psDBList.executeQuery();
		if (rsDBList.next()) {
			String columns = rsDBList.getString("columnsName");
			String tableName = rsDBList.getString("tableName");

			List<String> items = Arrays.asList(columns.split(","));
			Iterator<String> i = items.iterator();
			while (i.hasNext()) {

				String column = i.next();

				TrainNetwork trainNetwork = new TrainNetwork();
				if (!ChartDB.map.containsKey(tableName + column))
					trainNetwork.Train(null, format.format(new Date(0)), format.format(new Date()), column, tableName);
			}
		}}
		catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void log(String message) {
		if (context != null) {
			context.log("MyServletContextListener: " + message);
		} else {
			System.out.println("MyServletContextListener: " + message);
		}
	}
}