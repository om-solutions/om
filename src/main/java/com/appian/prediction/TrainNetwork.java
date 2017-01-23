package com.appian.prediction;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

@Path("/train")
public class TrainNetwork {

	public static int slidingWindowSize = 5;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String Train(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo) throws PException {
		Timestamp fromDate;
		Timestamp toDate;

		Network network = (Network) request.getSession().getAttribute("NeuralNetwork");

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "Dates not in correct format";
		}
		if (network == null) {
			network = new Network(slidingWindowSize);
			/*
			 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS))
			 * return "Cannot obtain lock"; } catch (InterruptedException e2) {
			 * return "Cannot obtain lock"; }
			 */
			request.getSession().setAttribute("NeuralNetwork", network);
		}
		network.clearTrainingSet();
		ChartDB chartDB = new ChartDB(request);
		try {
			ArrayList<Double> values = chartDB.getActualValuesAndSetNormalizationFactors(network, fromDate, toDate);
			ArrayList<Integer> missingValues = network.trainNetworkFromData(values);
			HashMap<Integer, Double> predicted = network.addMissingValues(values, missingValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* network.lock.unlock(); */
		return "NetworkTrained" + fromDate + toDate;
	}
}
