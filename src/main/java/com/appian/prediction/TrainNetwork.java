package com.appian.prediction;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.appian.db.ChartDB;
import com.appian.exception.PException;
import com.appian.nn.Network;
import com.appian.util.AppianUtil;
import com.google.gson.Gson;

@Path("/train")
public class TrainNetwork {

	public static int slidingWindowSize = 5;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public Network Train(JSONObject jobj, @DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo,
			@DefaultValue("") @QueryParam("coloumn") String coloumn, String tableName) throws PException {
		Timestamp fromDate;
		Timestamp toDate;
		Network network = null;

		coloumn = AppianUtil.extractColumns(coloumn);
		System.out.println("!!!!Columns : " + coloumn);
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
			return null;
		}

		network = new Network(slidingWindowSize);
		network.clearTrainingSet();
		ChartDB chartDB = new ChartDB(jobj);
		try {
			ArrayList<Double> values = chartDB.getActualValuesAndSetNormalizationFactors(network, fromDate, toDate,
					coloumn);
			ArrayList<Integer> missingValues = network.trainNetworkFromData(values);
			HashMap<Integer, Double> predicted = network.addMissingValues(values, missingValues);
			ChartDB.map.put(tableName + coloumn, network);
			// System.out.println("ChartDB : " + chartDB.map.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		chartDB.columnA = coloumn;
		chartDB.initialSaveValues(network, coloumn);
		return network;
	}

	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Network Train(@Context HttpServletRequest request, @DefaultValue("") @QueryParam("dateFrom") String dateFrom,
			@DefaultValue("") @QueryParam("dateTo") String dateTo,
			@DefaultValue("") @QueryParam("coloumn") String coloumn, String tableName) throws PException {
		Timestamp fromDate;
		Timestamp toDate;
		Network network = null;
		try {
			network = (Network) request.getSession().getAttribute("NeuralNetwork");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
			return null;
		}
		if (network == null) {

			network = new Network(slidingWindowSize);
			/*
			 * try { if(!network.lock.tryLock(5000l, TimeUnit.MILLISECONDS))
			 * return "Cannot obtain lock"; } catch (InterruptedException e2) {
			 * return "Cannot obtain lock"; }
			 */
			try {
				request.getSession().setAttribute("NeuralNetwork", network);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		network.clearTrainingSet();
		ChartDB chartDB = new ChartDB(request);
		try {
			ArrayList<Double> values = chartDB.getActualValuesAndSetNormalizationFactors(network, fromDate, toDate,
					coloumn);
			ArrayList<Integer> missingValues = network.trainNetworkFromData(values);
			HashMap<Integer, Double> predicted = network.addMissingValues(values, missingValues);
			ChartDB.map.put(tableName + coloumn, network);
		} catch (Exception e) {
			e.printStackTrace();
		}
		chartDB.columnA = coloumn;
		chartDB.initialSaveValues(network, coloumn);
		/* network.lock.unlock(); */
		return network;
	}
}
