<!DOCTYPE html>
<html lang="en">
<head>
<title>Prediction</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.min.css">


<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/amcharts.js"></script>
<script src="js/serial.js"></script>
<script src="js/light.js"></script>
<script src="js/export.min.js"></script>
<script language="javascript" type="text/javascript"
	src="js/datetimepicker.js"></script>
<link rel="icon" type="image/x-icon" href="js/images/favicon.ico">
<link rel="stylesheet" href="css/export.css" type="text/css" media="all" />
<script language="javascript" type="text/javascript" src="js/moment.js"></script>
<link rel="stylesheet"
	href="./css/bootstrap-material-datetimepicker.css" />

<link href='http://fonts.googleapis.com/css?family=Roboto:400,500'
	rel='stylesheet' type='text/css'>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">

<script type="text/javascript"
	src="js/bootstrap-material-datetimepicker.js"></script>

<style>
/* Remove the navbar's default margin-bottom and rounded borders */
.navbar {
	margin-bottom: 0;
	border-radius: 0;
}

/* Set height of the grid so .sidenav can be 100% (adjust as needed) */
.row.content {
	height: 450px
}

/* Set gray background color and 100% height */
.sidenav {
	padding-top: 20px;
	background-color: #f1f1f1;
	height: 100%;
}

/* Set black background color, white text and some padding */
footer {
	background-color: #555;
	color: white;
	padding: 15px;
}

/* On small screens, set height to 'auto' for sidenav and grid */
@media screen and (max-width: 767px) {
	.sidenav {
		height: auto;
		padding: 15px;
	}
	.row.content {
		height: auto;
	}
}
</style>
</head>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<div class="collapse navbar-collapse" id="myNavbar">
				<ul class="nav navbar-nav">
					<li class="active"><h1>
							<a>Prediction</a>
						</h1></li>
					<!-- <li><a href="#">About</a></li>
						<li><a href="#">Projects</a></li>
						<li><a href="#">Contact</a></li> -->
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="http://localhost:8080/Prediction/logout.jsp"><span
							class="glyphicon glyphicon-log-in"></span> Logout</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container-fluid text-center">
		<div class="row content">
			<div class="col-sm-1 sidenav">
				<!-- 				<p> -->
				<!-- 					<a href="#">Link</a> -->
				<!-- 				</p> -->
				<!-- 				<p> -->
				<!-- 					<a href="#">Link</a> -->
				<!-- 				</p> -->
				<!-- 				<p> -->
				<!-- 					<a href="#">Link</a> -->
				<!-- 				</p> -->
			</div>
			<div class="col-sm-8 text-left " style="margin-top: 10px;">
				<div class="col-xs-12" style="height: 30px;">
					Currently Predicting : <select style="margin-right: 50px;"
						id="predictedColumn">
						<!-- <option value="K_Factor">K_Factor</option>
						<option value="Temprature">Temprature</option>
						<option value="Pressure">Pressure</option> -->
					</select> Currently Proved : <select style="margin-right: 50px;"
						id="provedColumn">
						<!-- <option value="K_Factor">K_Factor</option>
						<option value="Temprature">Temprature</option>
						<option value="Pressure">Pressure</option> -->
					</select> Refresh After : <select id="refreshInterval"
						class="refreshInterval" value="1">
						<option value="0" selected>Off</option>
						<option value="1">30 sec</option>
						<option value="2">1 min</option>
						<option value="6">3 min</option>

					</select>
				</div>
				<input type="hidden" id="filter" class="form-control floating-label"
					placeholder="To Date Time" value="1">

				<div class="row" style="margin-top: 10px;">
					<div class="col-xs-1 pull-left">
						<button class="btn btn-primary" id="btnLoadPrev"
							onclick="loadPreviousGraph()" style="enable: false;">Load
							Previous</button>
					</div>
					<div class="col-xs-2 pull-right">
						<button class="btn btn-primary" id="btnLoadNext"
							onclick="loadNextGraph()">Load Next</button>
					</div>
				</div>
				<style>
#chartdiv {
	width: 100%;
	height: 500px;
}
</style>



				<!-- Chart code -->

				<script>
					$(document)
							.ready(
									function() {

										$('#dateFrom1')
												.bootstrapMaterialDatePicker(
														{
															format : 'DD-MMM-YYYY hh:mm:ss'
														});

										$('#dateFrom')
												.bootstrapMaterialDatePicker(
														{
															format : 'DD-MMM-YYYY hh:mm:ss',

														});

										$('#dateTo')
												.bootstrapMaterialDatePicker(
														{
															format : 'DD-MMM-YYYY hh:mm:ss',

														});

										$("#predictedColumn")
												.change(
														function(e) {														
															$
																	.ajax({
																		url : 'http://localhost:8080/Prediction/prediction/graph/changeColumn?predicted='
																				+ document
																						.getElementById("predictedColumn").value
																				+ '&proved='
																				+ document
																						.getElementById("provedColumn").value,
																		beforeSend : function(
																				request) {																			
																			request
																					.setRequestHeader(
																							"Authorization",
																							"Negotiate");
																		},
																		type : 'GET',
																		dataType : 'text',
																		success : function(
																				response) {
																			console
																					.log(response);
																			loadChart(
																					'http://localhost:8080/Prediction/prediction/graph/meter',
																					null,
																					null,document
																					.getElementById("predictedColumn").value,document
																					.getElementById("provedColumn").value);
																		},
																		error : function(
																				error) {
																			alert("API failure ");
																		}
																	})
														});
									});
					
					$("#provedColumn")
					.change(
							function(e) {
								console
										.log("column button clicked");
								$
										.ajax({
											url : 'http://localhost:8080/Prediction/prediction/graph/changeColumn?predicted='
													+ document
															.getElementById("predictedColumn").value
													+ '&proved='
													+ document
															.getElementById("provedColumn").value,
											beforeSend : function(
													request) {
												console
														.log("before send");
												request
														.setRequestHeader(
																"Authorization",
																"Negotiate");
											},
											type : 'GET',
											dataType : 'text',
											success : function(
													response) {
												console
														.log(response);
												loadChart(
														'http://localhost:8080/Prediction/prediction/graph/meter',
														null,
														null,document
														.getElementById("predictedColumn").value,document
														.getElementById("provedColumn").value);
											},
											error : function(
													error) {
												alert("API failure ");
											}
										})
							});


					function loadPreviousGraph() {

						/* alert("-------------"
								+ document.getElementById("dateFrom").value); */

						if (document.getElementById("dateFrom").value == '') {
							alert('Please search first');
						} else {
							if (document.getElementById("filter").value == 1) {
								var date1 = new Date(document
										.getElementById("dateFrom").value);
								date1.setDate(date1.getDate() - 7);
								document.getElementById("dateFrom").value = formatDate(date1);
								showGraph();
							} else {
								var date1 = new Date(document
										.getElementById("dateFrom1").value);
								date1.setDate(date1.getDate() - 7);
								document.getElementById("dateFrom1").value = formatDate(date1);
								showGraph2();
							}
						}

					}

					function loadNextGraph() {
						/* alert("-------------"
								+ document.getElementById("dateTo").value);
						 */

						if (document.getElementById("dateTo").value == '') {
							alert('Please search first');
						} else {
							if (document.getElementById("filter").value == 1) {
								var date1 = new Date(document
										.getElementById("dateTo").value);
								date1.setDate(date1.getDate() + 7);
								document.getElementById("dateTo").value = formatDate(date1);
								showGraph();
							} else {
								showGraph2();
							}
						}
					}

					function formatDate(myDate) {
						var abbrMonths = [ "Jan", "Feb", "Mar", "Apr", "May",
								"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
						var abbrDays = [ "Sun", "Mon", "Tue", "Wed", "Thu",
								"Fri", "Sat" ];

						function zeroPadding(val) {
							return val.toString().length === 1 ? "0" + val
									: val;
						}

						return myDate.getDate() + "-"
								+ (abbrMonths[myDate.getMonth()]) + "-"
								+ myDate.getFullYear() + " "
								+ zeroPadding(myDate.getHours()) + ":"
								+ zeroPadding(myDate.getMinutes()) + ":"
								+ zeroPadding(myDate.getSeconds());
					}

					$(".refreshInterval").change(function() {

						display();
					});

					function display() {
						//alert("Display");
						try {
							/* if (typeof (document.getElementById("filter").value) !== 'undefined') {
								alert("Please Search First !!!");
							} else { */
							if ($("#refreshInterval").val() == '0') {
								clearTimeout(t);
							} else {
								/* alert("123 : "
										+ document.getElementById("filter").value); */
								if (document.getElementById("filter").value == 1) {
									//alert("11111");
									showGraph();
								} else {
									//alert("22222");
									showGraph2();
								}
								/* alert(document.getElementById("filter").value
										+ ":::"
										+ parseInt($("#refreshInterval").val())) */
								t = setTimeout("display()", parseInt($(
										"#refreshInterval").val()) * 30000)
							}
							//}
						} catch (e) {
							alert(e);
						}
					}
					function showGraph() {

						// 						alert(document.getElementById("dateFrom").value+" : "+
						// 								document.getElementById("dateTo").value)");

						if (document.getElementById("dateFrom").value == ''
								|| document.getElementById("dateTo").value == '') {
							alert("Please enter values");
						} else {
							loadChart(
									'http://localhost:8080/Prediction/prediction/graph/meter',
									document.getElementById("dateFrom").value,
									document.getElementById("dateTo").value);
							document.getElementById("filter").value = 1;
						}
					}
					function showGraph2() {
						//alert(document.getElementById("dateFrom1").value +" : "+document.getElementById("predict").value);
						if (document.getElementById("dateFrom1").value == ''
								|| document.getElementById("predict").value == '') {
							alert("Please enter values");
						} else {
							loadChart(
									'http://localhost:8080/Prediction/prediction/graph/meter2',
									document.getElementById("dateFrom1").value,
									document.getElementById("predict").value);
							document.getElementById("filter").value = 2;
						}
					}

					var data;
					var chartData;
					$(document)
							.ready(
									function() {
										loadColumns("http://localhost:8080/Prediction/prediction/member/getCols");

									});

					function loadColumns(url) {
						$.ajax({
							url : url,
							type : 'GET',
							dataType : 'json',
							success : function(response) {
								//alert(JSON.stringify(response));
								$("#POIBody").children().remove();
								for (var i = 0; i < response.length; i++) {
									var obj = response[i];
									var columnName = obj["columnsName"];
									var tableName = obj["tableName"];
									var dbName = obj["dbName"];
									// 									alert(columnName + " : " + tableName
									// 											+ " : " + dbName);
									addRow(columnName, tableName, dbName);

								}
								loadChart(
										'http://localhost:8080/Prediction/prediction/graph/meter',
										null, null,document
										.getElementById("predictedColumn").value,document
										.getElementById("provedColumn").value);
							},
							error : function(error) {
								alert("Try again");
							},
						});
					}

					function addRow(columnName, dataType, isNull) {

						var str_array = columnName.split(',');
						for (var i = 0; i < str_array.length; i++) {

							$('#predictedColumn').append($('<option>', {
								value : str_array[i],
								text : str_array[i]
							}));
							$('#provedColumn').append($('<option>', {
								value : str_array[i],
								text : str_array[i]
							}));

						}
					}

					function loadChart(url, CurrentDate, PreMonthDate) {
				<%if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == "")) {%>
					alert("Please Login!!!");
						window.location = "Plogin.jsp";
				<%} else {
				System.out.println("-->" + (session.getAttribute("chartDT")) + " : " + (session.getAttribute("columns"))
						+ " : " + (session.getAttribute("dbName")));
				String chartDT = session.getAttribute("chartDT").toString();
				String columns = session.getAttribute("columns").toString();
				String dbName = session.getAttribute("dbName").toString();
				String tableName = session.getAttribute("tableName").toString();

				if ((session.getAttribute("chartDT") == null) || (session.getAttribute("columns") == null)
						|| (session.getAttribute("dbName") == null) || (session.getAttribute("tableName") == null)) {%>
					alert("Chart Not loaded properly ! Login Again !!" );
						
				<%} else {%>
						var chartDT = '<%=chartDT%>';
						var columns =  '<%=columns%>';
						var dbName =  '<%=dbName%>';
						var tableName =  '<%=tableName%>';
						$.ajax( {
							url : url,
							data : 'dateFrom=' + CurrentDate + '&value=' + PreMonthDate,
							beforeSend : function ( request ) {
								request.setRequestHeader( "Authorization", "Negotiate" );
							},
							type : 'GET',
							dataType : 'json',
							success : function ( response ) {
								//alert(JSON.stringify(response));
								if ( response.length == 0 ) {
									alert( "No Value Available !!!" );
								} else {
									//data = response;
									chartData = generateChartData( response );
									//alert(response);
									//chart.addListener("rendered", zoomChart);
									var chart = AmCharts.makeChart( "chartdiv", {
										"type" : "serial",
										"theme" : "light",
										"legend" : {
											"useGraphSettings" : true
										},
										"dataProvider" : chartData,
										"synchronizeGrid" : true,
										"valueAxes" : [
												{
													"id" : "v1",
													"axisColor" : "#FF6600",
													"axisThickness" : 2,
													"axisAlpha" : 1,
													"position" : "left"
												}, {
													"id" : "v2",
													"axisColor" : "#00FF00",
													"axisThickness" : 2,
													"axisAlpha" : 1,
													"position" : "right"
												}
										],
										"graphs" : [
												{
													"valueAxis" : "v1",
													"lineColor" : "#FF6600",
													"bullet" : "round",
													"bulletBorderThickness" : 1,
													"hideBulletsCount" : 30,
													"title" : document.getElementById( "predictedColumn" ).value,
													"valueField" : document.getElementById( "predictedColumn" ).value,
													"fillAlphas" : 0
												}, {
													"valueAxis" : "v2",
													"lineColor" : "#00FF00",
													"bullet" : "square",
													"bulletBorderThickness" : 1,
													"hideBulletsCount" : 30,
													"title" : document.getElementById( "provedColumn" ).value,
													"valueField" : document.getElementById( "provedColumn" ).value,
													"fillAlphas" : 0
												}
										],
										"chartScrollbar" : {},
										"chartCursor" : {
											"cursorPosition" : "mouse"
										},
										"categoryField" : chartDT,
										"categoryAxis" : {
											"parseDates" : true,
											"axisColor" : "#DADADA",
											"minorGridEnabled" : true
										},
										"export" : {
											"enabled" : true,
											"position" : "bottom-right"
										}
									} );

									chart.addListener( "dataUpdated", zoomChart );
									zoomChart();
									chart.autoMargins = false;
									function generateChartData ( response ) {
										return response;
									}

									function zoomChart () {
										chart.zoomToIndexes( chart.dataProvider.length - 400, chart.dataProvider.length - 1 );

									}
								}
							},
							error : function ( error ) {
								alert( "Try again" );
							},
						} );
				<%}
			}%>
					}
				</script>

				<!-- HTML -->
				<div id="chartdiv"></div>

			</div>
			<div class="col-sm-3 sidenav">
				<div class="panel-group" id="accordion">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" data-parent="#accordion"
									href="#collapse1">Predict Between</a>
							</h4>
						</div>
						<div id="collapse1" class="panel-collapse collapse in">
							<div class="panel-body">
								<div class="row" style="margin-top: 10px;">
									<!-- <div class="col-xs-3" style="margin-top: 3px;">
										<dt class="bg-success text-left">From :</dt>
									</div> -->
									<div class="col-xs-13">
										<dt class="bg-success text-left">
											<input id="dateFrom" class="form-control floating-label"
												placeholder="From Date Time">
											<!-- <a
												href="javascript:NewCal('dateFrom','ddmmmyyyy',true,24)"><img
												src="js/images/cal.gif" width="16" height="16" border="2"
												alt="Pick a date"></a> -->
										</dt>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<!-- <div class="col-xs-3" style="margin-top: 3px;">
										<dt class="bg-success text-left">To :</dt>
									</div> -->
									<div class="col-xs-13">
										<dt class="bg-success text-left">
											<input id="dateTo" class="form-control floating-label"
												placeholder="To Date Time">
											<!--<a -->
											<!-- 												href="javascript:NewCal('dateTo','ddmmmyyyy',true,24)"><img -->
											<!-- 												src="js/images/cal.gif" width="16" height="16" border="0" -->
											<!-- 												alt="Pick a date"></a> -->
										</dt>
									</div>

								</div>

								<div class="row pull-right" style="margin-top: 10px;">
									<div class="col-xs-1">
										<button class="btn btn-success" id="btnLoad" type="button"
											onclick="showGraph()">Go</button>
									</div>
								</div>

							</div>
						</div>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" data-parent="#accordion"
									href="#collapse2">Predict N Values</a>
							</h4>
						</div>
						<div id="collapse2" class="panel-collapse collapse ">
							<div class="panel-body">
								<div class="row" style="margin-top: 10px;">
									<div class="col-xs-13">
										<dt class="bg-success text-left">
											<input id="dateFrom1" class="form-control floating-label"
												placeholder="From Date Time">
											<!-- <a
												href="javascript:NewCal('dateFrom1','ddmmmyyyy',true,24)"><img
												src="js/images/cal.gif" width="16" height="16" border="2"
												alt="Pick a date"></a> -->
										</dt>
									</div>
								</div>
								<div class="row" style="margin-top: 10px;">
									<div class="col-xs-4">
										<dt class="bg-success text-left">Count :</dt>
									</div>
									<div class="col-xs-13">
										<dt class="bg-success text-left">
											<input type="text" size="5"
												onkeypress='return event.charCode >= 48 && event.charCode <= 57'
												id="predict">
										</dt>
									</div>

								</div>

								<div class="row pull-right" style="margin-top: 10px;">
									<div class="col-xs-1">
										<button class="btn btn-success" id="btnLoad" type="button"
											onclick="showGraph2()">Go</button>
									</div>
								</div>

							</div>
						</div>
					</div>
				</div>
				<!-- <div class="well">
					<p>ADS</p>
				</div>
				<div class="well">
					<p>ADS</p>
				</div> -->
			</div>
		</div>
	</div>

</body>
</html>

