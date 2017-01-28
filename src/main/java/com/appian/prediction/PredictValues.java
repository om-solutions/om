package com.appian.prediction;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

	@GET
	@Path("/predictbetween")
	public TreeMap<Timestamp, Double> Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo) throws PException {
		Timestamp fromDate;
		Timestamp toDate;
		try {
			if ("".equals(dateFrom))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());

			if ("".equals(dateTo))
				toDate = new Timestamp(new Date().getTime());
			else
				toDate = new Timestamp(format.parse(dateTo).getTime());

			System.out.println(fromDate + " : " + toDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
			return null;
		}
		Network network = (Network) request.getSession().getAttribute("NeuralNetwork");
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		ChartDB chartDB = new ChartDB(request);
		TreeMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, toDate, network,
				(String) request.getSession().getAttribute("columnA"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"));

		values = chartDB.getPredictedValues(fromDate, toDate, network,
				(String) request.getSession().getAttribute("columnB"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"));

		/* network.lock.unlock(); */
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
			System.out.println("fromDate : " + fromDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		Network network = (Network) request.getSession().getAttribute("NeuralNetwork");
		/*
		 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS)) return
		 * null; } catch (InterruptedException e1) { return null; }
		 */
		ChartDB chartDB = new ChartDB(request);
		TreeMap<Timestamp, Double> values = null;

		values = chartDB.getPredictedValues(fromDate, numberOfValues, network,
				(String) request.getSession().getAttribute("columnA"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnA"));

		values = chartDB.getPredictedValues(fromDate, numberOfValues, network,
				(String) request.getSession().getAttribute("columnB"));
		if (values != null && values.size() > 0)
			chartDB.savePredictedValues(values, (String) request.getSession().getAttribute("columnB"));
		/* network.lock.unlock(); */
		return values;
	}

}
