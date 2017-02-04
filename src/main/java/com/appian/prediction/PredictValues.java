package com.appian.prediction;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;

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

@Path("/predict")
@Produces(MediaType.APPLICATION_JSON)
public class PredictValues {

	SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

	@GET
	@Path("/predictbetween")
	public NavigableMap<Timestamp, Double> Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo) throws PException {
		Timestamp fromDate;
		Timestamp toDate;
		try {
			if (dateFrom == null || "".equals(dateFrom) || dateFrom.equals("null"))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());

			if (dateTo == null || "".equals(dateTo) || dateTo.equals("null"))
				toDate = new Timestamp(new Date().getTime());
			else
				toDate = new Timestamp(format.parse(dateTo).getTime());
			// System.out.println(fromDate + " : " + toDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		// System.out.println(fromDate + " : " + toDate);
		// System.out.println("!!--> " + dateFrom + " : " + dateFrom);

		String networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnA");

		Network networkA = ChartDB.map.get(networkName);

		networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnB");

		Network networkB = ChartDB.map.get(networkName);
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		ChartDB chartDB = new ChartDB(request);
		NavigableMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, toDate, networkA,
				(String) request.getSession().getAttribute("columnA"));
		// //System.out.println("columnA - Values : " + values.toString());

		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));
		// System.out.println(networkB.slidingWindowSize + " : network B : " +
		// networkB.toString());

		if (!((String) request.getSession().getAttribute("columnA"))
				.equals((String) request.getSession().getAttribute("columnB"))) {
			values = chartDB.getPredictedValues(fromDate, toDate, networkB,
					(String) request.getSession().getAttribute("columnB"));
			// System.out.println("columnB - Values : " + values.toString());
			if (values != null && values.size() > 0)
				chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"),
						(String) request.getSession().getAttribute("tableName"));
		}
		return values;
	}

	@GET
	@Path("/predictnvalues")
	public TreeMap<Timestamp, Double> PredictNValues(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("5") @QueryParam("numberOfValues") Integer numberOfValues) throws PException {
		Timestamp fromDate;
		try {
			if ("".equals(dateFrom))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());
			// System.out.println("fromDate : " + fromDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		String networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnA");
		Network networkA = ChartDB.map.get(networkName);
		networkName = (String) request.getSession().getAttribute("tableName")
				+ (String) request.getSession().getAttribute("columnB");
		Network networkB = ChartDB.map.get(networkName);
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		ChartDB chartDB = new ChartDB(request);
		TreeMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, numberOfValues, networkA,
				(String) request.getSession().getAttribute("columnA"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"),
					(String) request.getSession().getAttribute("tableName"));

		values = chartDB.getPredictedValues(fromDate, numberOfValues, networkB,
				(String) request.getSession().getAttribute("columnB"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"),
					(String) request.getSession().getAttribute("tableName"));
		/* network.lock.unlock(); */
		return values;
	}

}
