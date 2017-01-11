package com.appian.prediction;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.appian.db.DBConnection;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class User {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public String Login(@Context HttpServletRequest request, @DefaultValue("0") @QueryParam("username") String username,
			@DefaultValue("0") @QueryParam("password") String password) throws ParseException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		DBConnection dbConnection = new DBConnection(request);
		String status = null;
		status = dbConnection.validateUser(username, password);
		System.out.println("Authentication : " + status);
		request.getSession().setAttribute("user", username);
		TrainNetwork trainNetwork=new TrainNetwork();
		trainNetwork.Train(request, format.format(new Date(0)), format.format(new Date()));
		return status;
	}

	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	public String Register(@Context HttpServletRequest request) throws ParseException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		DBConnection dbConnection = new DBConnection(request);
		String msg = null;

		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");
		if (password.equals(confirm)) {
			msg = dbConnection.register(name, email, username, password);
		}
		request.getSession().setAttribute("user", username);
		return msg;
	}

}
