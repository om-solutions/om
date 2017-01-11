<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<!-- Styles -->
	<style type"text/css" >
	.amcharts-chart-div{display:none} a
	
	</style>
	<style>
#chartdiv {
	width: 100%;
	height: 500px;
}
</style>

	<!-- Resources -->
	<script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

	<script src="https://www.amcharts.com/lib/3/amcharts.js"></script>
	<script src="https://www.amcharts.com/lib/3/serial.js"></script>
	<script
		src="https://www.amcharts.com/lib/3/plugins/export/export.min.js"></script>
	<link rel="stylesheet"
		href="https://www.amcharts.com/lib/3/plugins/export/export.css"
		type="text/css" media="all" />
	<script src="https://www.amcharts.com/lib/3/themes/light.js"></script>

	<!-- Chart code -->
	<script>
		var data;
		$(document)
				.ready(
						function() {
							$.ajax({
										url : 'http://localhost:8080/Prediction/prediction/graph/meter',
										beforeSend : function(request) {
											request.setRequestHeader(
													"Authorization",
													"Negotiate");
										},
										type : 'GET',
										dataType : 'json',
										success : function(response) {
											data = response;
											console.log(response[0].actual)
											//chart.addListener("rendered", zoomChart);
											var chart = AmCharts
													.makeChart(
															"chartdiv",
															{
																"type" : "serial",
																"theme" : "light",
																"marginRight" : 40,
																"marginLeft" : 40,
																"autoMarginOffset" : 20,
																"mouseWheelZoomEnabled" : true,
																"dataDateFormat" : "YYYY-MM-DD",
																"valueAxes" : [ {
																	"id" : "v1",
																	"axisAlpha" : 0,
																	"position" : "left",
																	"ignoreAxisWidth" : true
																} ],
																"balloon" : {
																	"borderThickness" : 1,
																	"shadowAlpha" : 0
																},
																"graphs" : [ {
																	"id" : "g1",
																	"balloon" : {
																		"drop" : true,
																		"adjustBorderColor" : false,
																		"color" : "#ffffff"
																	},
																	"bullet" : "round",
																	"bulletBorderAlpha" : 1,
																	"bulletColor" : "#FFFFFF",
																	"bulletSize" : 5,
																	"hideBulletsCount" : 50,
																	"lineThickness" : 2,
																	"title" : "red line",
																	"useLineColorForBulletBorder" : true,
																	"valueField" : "value",
																	"balloonText" : "<span style='font-size:18px;'>[[value]]</span>"
																} ],
																"chartScrollbar" : {
																	"graph" : "g1",
																	"oppositeAxis" : false,
																	"offset" : 30,
																	"scrollbarHeight" : 80,
																	"backgroundAlpha" : 0,
																	"selectedBackgroundAlpha" : 0.1,
																	"selectedBackgroundColor" : "#888888",
																	"graphFillAlpha" : 0,
																	"graphLineAlpha" : 0.5,
																	"selectedGraphFillAlpha" : 0,
																	"selectedGraphLineAlpha" : 1,
																	"autoGridCount" : true,
																	"color" : "#AAAAAA"
																},
																"chartCursor" : {
																	"pan" : true,
																	"valueLineEnabled" : true,
																	"valueLineBalloonEnabled" : true,
																	"cursorAlpha" : 1,
																	"cursorColor" : "#258cbb",
																	"limitToGraph" : "g1",
																	"valueLineAlpha" : 0.2,
																	"valueZoomable" : true
																},
																"valueScrollbar" : {
																	"oppositeAxis" : false,
																	"offset" : 50,
																	"scrollbarHeight" : 10
																},
																"categoryField" : "date",
																"categoryAxis" : {
																	"parseDates" : true,
																	"dashLength" : 1,
																	"minorGridEnabled" : true
																},
																"export" : {
																	"enabled" : true
																},
																"dataProvider" : response
															});

											chart.addListener("rendered",
													zoomChart);

											function zoomChart() {
												chart
														.zoomToIndexes(
																chart.dataProvider.length - 40,
																chart.dataProvider.length - 1);
											}
										},
										error : function(error) {
											alert("API failure ");
										},
									});
						});
	</script>

	<!-- HTML -->
	<div id="chartdiv"></div>

</body>
</html>