package com.appian.prediction;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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
import com.appian.nn.Network;

@Path("/predict")
@Produces(MediaType.APPLICATION_JSON)
public class PredictValues {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@GET
	@Path("/predictbetween")
	public TreeMap<Timestamp, Double> Predict(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
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
		try {
			values = chartDB.getPredictedValues(fromDate, toDate, network);
			if (values != null && values.size() > 0)
				chartDB.savePredictedValues(values);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			/* network.lock.unlock(); */
			return null;
		}
		/* network.lock.unlock(); */
		return values;
	}

	@GET
	@Path("/predictnvalues")
	public TreeMap<Timestamp, Double> PredictNValues(@Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("5") @QueryParam("numberOfValues") Integer numberOfValues) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Timestamp fromDate;
		try {
			if ("".equals(dateFrom))
				fromDate = new Timestamp(0);
			else
				fromDate = new Timestamp(format.parse(dateFrom).getTime());
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
		try {
			values = chartDB.getPredictedValues(fromDate, numberOfValues, network);
			if (values != null && values.size() > 0)
				chartDB.savePredictedValues(values);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			/* network.lock.unlock(); */
			return null;
		}
		/* network.lock.unlock(); */
		return values;
	}

}
