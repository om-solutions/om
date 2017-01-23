package com.appian.csv;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.appian.db.ChartDB;
import com.appian.db.DBConnection;
import com.appian.exception.PException;

@Path("/csv")
@Produces(MediaType.APPLICATION_JSON)
public class csv {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/upload")
	@Produces(MediaType.TEXT_PLAIN)
	public String Login(@Context HttpServletRequest request, @DefaultValue("0") @QueryParam("username") String username,
			@DefaultValue("0") @QueryParam("password") String password) throws PException {
		DBConnection dbConnection = new DBConnection(request);
		String status = null;
		status = dbConnection.validateUser(username, password);
		System.out.println("Authentication : " + status);
		request.getSession().setAttribute("user", username);

		ChartDB chartDB = new ChartDB();
		if (chartDB != null) {
			request.getSession().setAttribute("chartDT", ChartDB.chartDT);
			request.getSession().setAttribute("columns", ChartDB.columns);
			request.getSession().setAttribute("dbName", ChartDB.dbName);
			request.getSession().setAttribute("tableName", ChartDB.tableName);
			request.getSession().setAttribute("dbInstanceName", ChartDB.dbInstanceName);
			request.getSession().setAttribute("password", ChartDB.password);
			request.getSession().setAttribute("userName", ChartDB.userName);
			request.getSession().setAttribute("url", ChartDB.url);
		}
		/// TrainNetwork trainNetwork = new TrainNetwork();
		// trainNetwork.Train(request, format.format(new Date(0)),
		/// format.format(new Date()));
		return status;
	}

}
