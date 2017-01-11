<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>


	<style>
#chartdiv {
	width: 100%;
	height: 500px;
}
</style>

	<!-- Resources -->
	<script type="text/javascript" src="js/jquery.min.js"></script>
	<script src="js/amcharts.js"></script>
	<script src="js/serial.js"></script>
	<script src="js/light.js"></script>
	<script src="js/export.min.js"></script>
	<link rel="stylesheet" href="css/export.css" type="text/css"
		media="all" />
	<script language="javascript" type="text/javascript"
		src="js/datetimepicker.js">
		//Date Time Picker script- by TengYong Ng of http://www.rainforestnet.com
		//Script featured on JavaScript Kit (http://www.javascriptkit.com)
		//For this script, visit http://www.javascriptkit.com
	</script>
	<script language="javascript" type="text/javascript" src="js/moment.js"></script>

	<div>
		<input id="dateFrom" type="text" size="25"><a
			href="javascript:NewCal('dateFrom','ddmmmyyyy',true,24)"><img
			src="js/images/cal.gif" width="16" height="16" border="0"
			alt="Pick a date"></a> <input id="dateTo" type="text" size="25"><a
			href="javascript:NewCal('dateTo','ddmmmyyyy',true,24)"><img
			src="js/images/cal.gif" width="16" height="16" border="0"
			alt="Pick a date"></a> <input id="btnLoad" type="button"
			onclick="showGraph()">
	</div>
	<!-- Chart code -->

	<script>
		function showGraph() {
			var aa =document.getElementById("dateFrom").value;
			var d = Date.parseDate("2005-10-05 12:13 am", "Y-m-d g:i a");
			//alert(document.getElementById("dateFrom").value);
			
			//document.getElementById("dateFrom").value
			//document.getElementById("dateFrom").value
			var date1 = Date.Parse("5-Oct-2016 02:42:37");
			var date2 = Date.Parse("5-Oct-2016 02:42:37");

			loadChart(moment(date1).format("YYYY-MM-DD HH:mm:ss"),
					moment(date2).format("YYYY-MM-DD HH:mm:ss"));
		}

		var data;
		var chartData;
		$(document).ready(function() {
			var dd = new Date();
			var dd1 = new Date();
			dd1.setMonth(dd.getMonth() - 1);
			var CurrentDate = moment(dd).format("YYYY-MM-DD HH:mm:ss");
			var PreMonthDate = moment(dd).format("YYYY-MM-DD HH:mm:ss");

			loadChart(CurrentDate, PreMonthDate);
		});

		function loadChart(CurrentDate, PreMonthDate) {
			$
					.ajax({
						url : 'http://localhost:8080/Prediction/prediction/graph/meter',
						data : 'dateFrom=' + CurrentDate + '&dateTo='
								+ PreMonthDate,
						beforeSend : function(request) {
							request.setRequestHeader("Authorization",
									"Negotiate");
						},
						type : 'GET',
						dataType : 'json',
						success : function(response) {
							//data = response;
							chartData = generateChartData(response);

							console.log(response[0].actual)
							//chart.addListener("rendered", zoomChart);
							var chart = AmCharts.makeChart("chartdiv", {
								"type" : "serial",
								"theme" : "light",
								"legend" : {
									"useGraphSettings" : true
								},
								"dataProvider" : chartData,
								"synchronizeGrid" : true,
								"valueAxes" : [ {
									"id" : "v1",
									"axisColor" : "#FF6600",
									"axisThickness" : 2,
									"axisAlpha" : 1,
									"position" : "left"
								}, {
									"id" : "v2",
									"axisColor" : "#FCD202",
									"axisThickness" : 2,
									"axisAlpha" : 1,
									"position" : "right"
								}, {
									"id" : "v3",
									"axisColor" : "#B0DE09",
									"axisThickness" : 2,
									"gridAlpha" : 0,
									"offset" : 50,
									"axisAlpha" : 1,
									"position" : "left"
								} ],
								"graphs" : [ {
									"valueAxis" : "v1",
									"lineColor" : "#556B2F",
									"bullet" : "round",
									"bulletBorderThickness" : 1,
									"hideBulletsCount" : 30,
									"title" : "Actual",
									"valueField" : "actual",
									"fillAlphas" : 0
								}, {
									"valueAxis" : "v2",
									"lineColor" : "#00FF00",
									"bullet" : "square",
									"bulletBorderThickness" : 1,
									"hideBulletsCount" : 30,
									"title" : "Predicted",
									"valueField" : "predicted",
									"fillAlphas" : 0
								}, {
									"valueAxis" : "v3",
									"lineColor" : "#8B4513",
									"bullet" : "triangleUp",
									"bulletBorderThickness" : 1,
									"hideBulletsCount" : 30,
									"title" : "Error",
									"valueField" : "error",
									"fillAlphas" : 0
								} ],
								"chartScrollbar" : {},
								"chartCursor" : {
									"cursorPosition" : "mouse"
								},
								"categoryField" : "datetime",
								"categoryAxis" : {
									"parseDates" : true,
									"axisColor" : "#DADADA",
									"minorGridEnabled" : true
								},
								"export" : {
									"enabled" : true,
									"position" : "bottom-right"
								}
							});

							chart.addListener("dataUpdated", zoomChart);
							zoomChart();

							function generateChartData(response) {
								return response;
							}

							function zoomChart() {
								chart.zoomToIndexes(
										chart.dataProvider.length - 20,
										chart.dataProvider.length - 1);
							}
						},
						error : function(error) {
							alert("API failure ");
						},
					});
		}
	</script>

	<!-- HTML -->
	<div id="chartdiv"></div>

</body>
</html>