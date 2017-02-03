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

import com.appian.db.ChartDB;
import com.appian.db.DBConnection;
import com.appian.exception.PException;
import com.appian.prediction.POJO.Column;
import com.google.gson.Gson;

@Path("/member")
@Produces(MediaType.APPLICATION_JSON)
public class Member {

	@GET
	@Path("/getCols")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCols(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("columns") String columns)
			throws PException {
		System.out.println("getCols : " + columns);
		ChartDB chartDB = new ChartDB(request);
		String cloumnList = chartDB.getColumns(request,(String) request.getSession().getAttribute("pUser"));
		if (!cloumnList.isEmpty()) {
			/*request.getSession().setAttribute("chartDT", ChartDB.chartDT);
			request.getSession().setAttribute("columns", ChartDB.columns);
			request.getSession().setAttribute("dbName", ChartDB.dbName);
			request.getSession().setAttribute("tableName", ChartDB.tableName);
			request.getSession().setAttribute("dbInstanceName", ChartDB.dbInstanceName);
			request.getSession().setAttribute("password", ChartDB.password);
			request.getSession().setAttribute("userName", ChartDB.userName);
			request.getSession().setAttribute("url", ChartDB.url);

			request.getSession().setAttribute("columnA", ChartDB.columns.split(",")[0]);
			request.getSession().setAttribute("columnB", ChartDB.columns.split(",")[0]);*/

			System.out.println("Clolumn List " + cloumnList);
			return cloumnList;
		} else {
			System.out.println("ERROR ::!!!!!!!!!");
			return "Error : List not saved";
		}
	}
}
