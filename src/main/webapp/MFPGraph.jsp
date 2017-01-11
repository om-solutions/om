<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.min.css">
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<!-- Resources -->
<script src="js/amcharts.js"></script>
<script src="js/serial.js"></script>
<script src="js/light.js"></script>
<script src="js/export.min.js"></script>
<script language="javascript" type="text/javascript"
	src="js/datetimepicker.js"></script>
<link rel="icon" type="image/x-icon" href="js/images/favicon.ico">
<link rel="stylesheet" href="css/export.css" type="text/css" media="all" />
<script language="javascript" type="text/javascript" src="js/moment.js"></script>
<title>Insert title here</title>
</head>
<body>


	<style>
#chartdiv {
	width: 100%;
	height: 500px;
}
</style>


	<div class="container-fluid" >
		<div class="row well" >
			<div class="col-sm-2">
				<input id="dateFrom" type="text" size="21"><a
					href="javascript:NewCal('dateFrom','ddmmmyyyy',true,24)"><img
					src="js/images/cal.gif" width="16" height="16" border="0"
					alt="Pick a date"></a>
			</div>
			<div class="col-sm-2">
				<input id="dateTo" type="text" size="21"><a
					href="javascript:NewCal('dateTo','ddmmmyyyy',true,24)"><img
					src="js/images/cal.gif" width="16" height="16" border="0"
					alt="Pick a date"></a>
			</div>
			<div class="col-sm-1">
				<button class="btn btn-success" id="btnLoad" type="button" onclick="showGraph()">Go</button>
			</div>
		</div>

		<div class="row well">
			<div class="col-sm-2">
				<input id="dateFrom1" type="text" size="21"><a
					href="javascript:NewCal('dateFrom1','ddmmmyyyy',true,24)"><img
					src="js/images/cal.gif" width="16" height="16" border="0"
					alt="Pick a date"></a>
			</div>
			<div class="col-sm-2">
				<input type="text" size="21"
					onkeypress='return event.charCode >= 48 && event.charCode <= 57'
					id="predict"></a>
			</div>
			<div class="col-sm-1">

				<button class="btn btn-success" id="btnLoad" type="button" onclick="showGraph2()">Go</button>
			</div>
		</div>
	</div>
	<!-- Chart code -->

	<script>
		function showGraph() {

			loadChart(
					'http://localhost:8080/Prediction/prediction/graph/meter',
					document.getElementById("dateFrom").value, document
							.getElementById("dateTo").value);
		}

		function showGraph2() {

			loadChart(
					'http://localhost:8080/Prediction/prediction/graph/meter2',
					document.getElementById("dateFrom1").value, document
							.getElementById("predict").value);
		}

		var data;
		var chartData;
		$(document)
				.ready(
						function() {
							loadChart(
									'http://localhost:8080/Prediction/prediction/graph/meter',
									null, null);
						});

		function loadChart(url, CurrentDate, PreMonthDate) {
			$.ajax({
				url : url,
				data : 'dateFrom=' + CurrentDate + '&dateTo=' + PreMonthDate,
				beforeSend : function(request) {
					request.setRequestHeader("Authorization", "Negotiate");
				},
				type : 'GET',
				dataType : 'json',
				success : function(response) {
					//data = response;
					chartData = generateChartData(response);
					//alert(response);
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
							"axisColor" : "#556B2F",
							"axisThickness" : 2,
							"axisAlpha" : 1,
							"position" : "left"
						}, {
							"id" : "v2",
							"axisColor" : "#00FF00",
							"axisThickness" : 2,
							"axisAlpha" : 1,
							"position" : "right"
						}, {
							"id" : "v3",
							"axisColor" : "#8B4513",
							"axisThickness" : 2,
							"gridAlpha" : 0,
							"offset" : 50,
							"axisAlpha" : 1,
							"position" : "left"
						}, {
							"id" : "v4",
							"axisColor" : " #ff3300",
							"axisThickness" : 2,
							"gridAlpha" : 0,
							"offset" : 50,
							"axisAlpha" : 1,
							"position" : "right"
						}, {
							"id" : "v5",
							"axisColor" : "#ff00ff",
							"axisThickness" : 2,
							"gridAlpha" : 0,
							"offset" : 100,
							"axisAlpha" : 1,
							"position" : "left"
						}, {
							"id" : "v6",
							"axisColor" : "#00ccff",
							"axisThickness" : 2,
							"gridAlpha" : 0,
							"offset" : 100,
							"axisAlpha" : 1,
							"position" : "right"
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
						}, {
							"valueAxis" : "v4",
							"lineColor" : " #ff3300",
							"bullet" : "triangleUp",
							"bulletBorderThickness" : 1,
							"hideBulletsCount" : 30,
							"title" : "Temp",
							"valueField" : "temp",
							"fillAlphas" : 0
						}, {
							"valueAxis" : "v5",
							"lineColor" : " #ff00ff",
							"bullet" : "triangleUp",
							"bulletBorderThickness" : 1,
							"hideBulletsCount" : 30,
							"title" : "Pressure",
							"valueField" : "pressure",
							"fillAlphas" : 0
						}, {
							"valueAxis" : "v6",
							"lineColor" : " #00ccff",
							"bullet" : "triangleUp",
							"bulletBorderThickness" : 1,
							"hideBulletsCount" : 30,
							"title" : "Flow Rate",
							"valueField" : "flowrate",
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
						chart.zoomToIndexes(chart.dataProvider.length - 20,
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