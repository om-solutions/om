package com.appian.prediction;

import java.sql.SQLException;
import java.sql.Timestamp;
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

import com.appian.db.ChartDB;
import com.appian.exception.PException;
import com.appian.nn.Network;

@Path("/graph")
@Produces(MediaType.APPLICATION_JSON)
public class MeterGraphValues {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/meter")
	public String Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("value") String dateTo)
			throws PException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Timestamp fromDate;
		Timestamp toDate;

		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format1.parse(dateFrom).getTime());

			if (dateTo == null || "".equals(dateTo) || dateTo.equals("null"))
				toDate = new Timestamp(new Date().getTime());
			else
				toDate = new Timestamp(format1.parse(dateTo).getTime());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new PException("Invalid Date !!!");
		}
		ChartDB chartDB = new ChartDB(request);
		String meterJSONData = null;
		System.out.println("2 : " + dateFrom.toString() + " : " + dateTo.toString());

		meterJSONData = chartDB.getMeterGraphValues(fromDate, toDate);
		System.out.println(meterJSONData);

		return meterJSONData;
	}

	@GET
	@Path("/changeColumn")
	public String changeColumn(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("predicted") String predicted,
			@DefaultValue("") @QueryParam("proved") String proved) throws PException {
		Network network = (Network) request.getSession().getAttribute("NeuralNetwork");
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		if (predicted.equals(request.getSession().getAttribute("predicted"))
				&& proved.equals(request.getSession().getAttribute("proved")))
			return "true";

		request.getSession().setAttribute("predicted", predicted);
		request.getSession().setAttribute("proved", proved);
		/*
		 * request.getSession().setAttribute("dbName", predicted);
		 * request.getSession().setAttribute("tableName", predicted);
		 * request.getSession().setAttribute("chartDT", predicted);
		 */
		/*
		 * switch (column) { case "Pressure":
		 * request.getSession().setAttribute("predicted", "PPressure");
		 * request.getSession().setAttribute("proved", "MPressure"); break; case
		 * "Temprature": request.getSession().setAttribute("predicted",
		 * "PTemp"); request.getSession().setAttribute("proved", "MTemp");
		 * break; case "K_Factor":
		 * request.getSession().setAttribute("predicted", "Predicted_K_Factor");
		 * request.getSession().setAttribute("proved", "Proved_K_Factor");
		 * break; default: request.getSession().setAttribute("predicted",
		 * "PPressure"); request.getSession().setAttribute("proved",
		 * "MPressure"); }
		 */

		/* network.lock.unlock(); */
		TrainNetwork trainNetwork = new TrainNetwork();
		trainNetwork.Train(request, "", "");
		return "true";
	}

	@GET
	@Path("/meter2")
	public String Predict2(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("value") String predict) throws PException {
		System.out.println("!!! " + predict);

		Timestamp fromDate;
		Timestamp toDate;
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format1.parse(dateFrom).getTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		ChartDB chartDB = new ChartDB(request);
		String meterJSONData = null;
		System.out.println("!!!" + fromDate + " : " + predict);
		meterJSONData = chartDB.getMeterGraphWithPredictValues(fromDate, predict);
		System.out.println(meterJSONData);
		return meterJSONData;
	}

}
