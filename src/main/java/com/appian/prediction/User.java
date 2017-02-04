package com.appian.prediction;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class User {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public String Login(@Context HttpServletRequest request, @DefaultValue("0") @QueryParam("username") String username,
			@DefaultValue("0") @QueryParam("password") String password,
			@DefaultValue("0") @QueryParam("admin") Boolean isAdmin) throws PException {
		DBConnection dbConnection = new DBConnection();
		String status = null;
		if (isAdmin)
			status = dbConnection.validateAdmin(username, password);
		else
			{status = dbConnection.validateUser(username, password);
			ChartDB chartDB = new ChartDB(request);
			}
		//System.out.println("Login() : " + status);
		request.getSession().setAttribute("pUser", username);

		
		//if (chartDB != null) {
			/*
			 * request.getSession().setAttribute("chartDT", chartDB.chartDT);
			 * request.getSession().setAttribute("columns", chartDB.columns);
			 * request.getSession().setAttribute("dbName", chartDB.dbName);
			 * request.getSession().setAttribute("tableName",
			 * chartDB.tableName);
			 * request.getSession().setAttribute("dbInstanceName",
			 * chartDB.dbInstanceName);
			 * request.getSession().setAttribute("password", chartDB.password);
			 * request.getSession().setAttribute("userName", chartDB.userName);
			 * request.getSession().setAttribute("url", chartDB.url);
			 */
		//}

		return status;
	}

	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	public String Register(@Context HttpServletRequest request) throws PException {
		DBConnection dbConnection = new DBConnection();
		String msg = null;

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");
		if (password.equals(confirm)) {
			msg = dbConnection.register(name, email, username, password);
		}
		request.getSession().setAttribute("pUser", username);

		return msg;
	}

}
