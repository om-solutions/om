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
import com.appian.prediction.POJO.Column;
import com.google.gson.Gson;

@Path("/member")
@Produces(MediaType.APPLICATION_JSON)
public class Member {

	@GET
	@Path("/getCols")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCols(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("columns") String columns)
			throws ParseException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			SQLException {
		System.out.println("columns : " + columns);
		DBConnection dbConnection = new DBConnection(request);
		String cloumnList = dbConnection.getColumns();
		if (!cloumnList.isEmpty()) {
			request.getSession().setAttribute("chartDT", dbConnection.getChartDB().getChartDT());
			request.getSession().setAttribute("columns", dbConnection.getChartDB().getColumns());
			request.getSession().setAttribute("dbName", dbConnection.getChartDB().getDbName());
			request.getSession().setAttribute("tableName", dbConnection.getChartDB().getTableName());

			System.out.println("Clolumn List " + cloumnList);
			return cloumnList;
		} else {
			System.out.println("ERROR ::!!!!!!!!!");
			return "Error : List not saved";
		}
	}
}
