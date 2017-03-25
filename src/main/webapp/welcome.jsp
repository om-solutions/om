<!DOCTYPE html>
<html lang="en">
<head>
<title>Insight Engine</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.min.css">

<link rel="stylesheet" href="css/bootstrap.min.css">
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/amcharts.js"></script>
<script src="js/serial.js"></script>
<script src="js/light.js"></script>
<script src="js/export.min.js"></script>
<script src="js/bootstrap-notify.js"></script>
<script src="js/bootstrap-notify.min.js"></script>
<script src="js/datetimepicker.js"></script>
<script src="js/bootstrap-table.min.js"></script>
<link href="css/bootstrap-table.min.css" rel="stylesheet"
	type="text/css" />

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
					Column A : <select style="margin-right: 50px;" id="selColumnA">
						<!-- <option value="K_Factor">K_Factor</option>
						<option value="Temprature">Temprature</option>
						<option value="Pressure">Pressure</option> -->
					</select> Column B : <select style="margin-right: 50px;" id="selColumnB">
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

				<script type="text/javascript">
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
										
										var prev_valA;

										$('#selColumnA').focus(function() {
										    prev_valA = $(this).val();
										}).change(function() {
											if(document.getElementById("selColumnA").value === document.getElementById("selColumnB").value){
												alert("Both Columns can not be same !!!");
												 $(this).val(prev_valA);
											        //alert('unchanged');
											        return false; 
											} 
											else{$.ajax({
														url : 'http://localhost:8080/Prediction/prediction/graph/changeColumn?columnA='
																+ document
																		.getElementById("selColumnA").value
																+ '&columnB='
																+ document
																		.getElementById("selColumnB").value,
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
																	.getElementById("selColumnA").value,document
																	.getElementById("selColumnB").value);
														
															
														
														},
														error : function(
																error) {
															
															alert("API failure ");
														}
													})
										}										   
										});
										
										
								
					
					var prev_valB;
					
					$('#selColumnB').focus(function() {
						prev_valB = $(this).val();
					}).change(function() {
						
						if(document.getElementById("selColumnA").value === document.getElementById("selColumnB").value){
							alert("Both Columns can not be same !!!");
							 $(this).val(prev_valB);
						        //alert('unchanged');
						        return false; 
						} 
						else{$.ajax({
											url : 'http://localhost:8080/Prediction/prediction/graph/changeColumn?columnA='
													+ document
															.getElementById("selColumnA").value
													+ '&columnB='
													+ document
															.getElementById("selColumnB").value,
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
												console.log(response);
									
												loadChart(
														'http://localhost:8080/Prediction/prediction/graph/meter',
														null,
														null,document
														.getElementById("selColumnA").value,document
														.getElementById("selColumnB").value);
												
											},
											error : function(
													error) {
									
												alert("API failure ");
											}
										})
								}});

					
								});
							
				

					function refreshDropdownA() {
											
						//var oldVal= document.getElementById("selColumnA").value
						//$("#selColumnA").empty();

						var columnName = "<%=session.getAttribute("columns")%>";
						//salert(columnName);
						var str_array = columnName.split(',');
							for (var i = 0; i < str_array.length; i++) {
								/* if(str_array[i]!=document.getElementById("selColumnB").value){
									//alert("A : "+str_array[i]);
									if(str_array[i]==oldVal)
								$('#selColumnA').append($('<option selected>', {value : str_array[i],text : str_array[i]}));
									else */
									$('#selColumnA').append($('<option>', {value : str_array[i],text : str_array[i]}));
								//}								
						}
						
						
					}
					function refreshDropdownB() {
						//var oldVal= document.getElementById("selColumnB").value
						//$("#selColumnB").empty();
						var columnName = "<%=session.getAttribute("columns")%>";
						//alert(columnName);
						var str_array = columnName.split(',');
							for (var i = 0; i < str_array.length; i++) {

								/* if(str_array[i]!=document.getElementById("selColumnA").value){
								//	alert("B : "+str_array[i]);*/
								if(i==1)
								{	$('#selColumnB').append($('<option>', {value : str_array[i],text : str_array[i]}));								
								$('#selColumnB').val(str_array[i]);
								}
								
								else 
									$('#selColumnB').append($('<option>', {value : str_array[i],text : str_array[i]}));
								//}								
						}
						
						
					}
					
					
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
						} else{
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
									var columnName = obj["columns"];
									//alert(columnName);
									var tableName = obj["tableName"];
									var dbName = obj["dbName"];
									// 									alert(columnName + " : " + tableName
									// 											+ " : " + dbName);
									refreshDropdownA();
									refreshDropdownB();

								}
								loadChart(
										'http://localhost:8080/Prediction/prediction/graph/meter',
										null, null,document
										.getElementById("selColumnA").value,document
										.getElementById("selColumnB").value);
							},
							error : function(error) {
								
								alert("Try again");
							},
						});
					}

					

					function loadChart(url, CurrentDate, PreMonthDate) {
						var d1 = Date.parse(CurrentDate);
						var d2 = Date.parse(PreMonthDate);
					if(d2<d1)
						{
						alert("To date must be gretter than from date !!!");
						}else{
						
					
				<%if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == "")) {%>
					alert("Please Login!!!");
						window.location = "Plogin.jsp";
				<%} else {
					
					
					
					
				//System.out.println("-->" + (session.getAttribute("chartDT")) + " : " + (session.getAttribute("columns"))
					//	+ " : " + (session.getAttribute("dbName")));
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
							document.getElementById( "btnLoad" ).disabled = true;
							$.ajax( {
								url : url,
								data : 'dateFrom=' + CurrentDate + '&value=' + PreMonthDate,
								beforeSend : function ( request ) {
									request.setRequestHeader( "Authorization", "Negotiate" );
								},
								type : 'GET',
								dataType : 'json',
								success : function ( response ) {
									//( JSON.stringify( response ) );

									if ( response.length == 0 ) {
										alert( "No Value Available !!!" );
									} else {
										//data = response;				
										//document.getElementById( "table" ).deleteTHead();
										// 									var rows = "<thead><tr><th data-field="+chartDT+">" + chartDT + "</th><th data-field=" + document.getElementById( "selColumnA" ).value + ">" + document.getElementById( "selColumnA" ).value + "</th><th data-field=" + "_"
										// 											+ document.getElementById( "selColumnA" ).value + ">" + "_" + document.getElementById( "selColumnA" ).value + "</th><th data-field=" + document.getElementById( "selColumnB" ).value + ">" + document.getElementById( "selColumnB" ).value
										// 											+ "</th><th data-field=" + "_" + document.getElementById( "selColumnB" ).value + ">" + "_" + document.getElementById( "selColumnB" ).value + "</th></tr></thead>";
										// 									//var rows = "<thead><tr><th >DateTime</th><th >asdas</th><th>asasasa</th><th >zzzzz</th><th >qqqq</th></tr></thead>";
										// 									var table = document.createElement( 'table' );
										// 									table.innerHTML = rows;
										// 									document.getElementById( "table" ).appendChild( table.firstChild );

										ShowAlertsFromJSON( response );
										CreateTableFromJSON( response );
										document.getElementById( "btnLoad" ).disabled = false;
										/* 	$( '#table' ).bootstrapTable( 'destroy' );

											$( '#table' ).bootstrapTable( {
												data : response
											} ); */
										//alert( "table called" );
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
														"offset" : 50,
														"position" : "left"
													}, {
														"id" : "v3",
														"axisColor" : "#ff0000",
														"axisThickness" : 2,
														"gridAlpha" : 0,
														"offset" : 100,
														"axisAlpha" : 1,
														"position" : "left"
													}, {
														"id" : "v4",
														"axisColor" : " #FF0099",
														"axisThickness" : 2,
														"gridAlpha" : 1,
														"axisAlpha" : 1,
														"position" : "right"
													}, {
														"id" : "v5",
														"axisColor" : " #990033",
														"axisThickness" : 2,
														"gridAlpha" : 1,
														"offset" : 50,
														"axisAlpha" : 1,
														"position" : "right"
													}, {
														"id" : "v6",
														"axisColor" : " #0000ff",
														"axisThickness" : 2,
														"gridAlpha" : 0,
														"offset" : 100,
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
														"title" : document.getElementById( "selColumnA" ).value,
														"valueField" : document.getElementById( "selColumnA" ).value,
														"fillAlphas" : 0
													}, {
														"valueAxis" : "v2",
														"lineColor" : "#00FF00",
														"bullet" : "square",
														"bulletBorderThickness" : 1,
														"hideBulletsCount" : 30,
														"title" : "_" + document.getElementById( "selColumnA" ).value,
														"valueField" : "_" + document.getElementById( "selColumnA" ).value,
														"fillAlphas" : 0
													}, {
														"valueAxis" : "v3",
														"lineColor" : "#ff0000",
														"bullet" : "triangleUp",
														"bulletBorderThickness" : 1,
														"hideBulletsCount" : 30,
														"title" : "err%_" + document.getElementById( "selColumnA" ).value,
														"valueField" : "err%_" + document.getElementById( "selColumnA" ).value,
														"fillAlphas" : 0
													}

													, {
														"valueAxis" : "v4",
														"lineColor" : "#FF0099",
														"bullet" : "round",
														"bulletBorderThickness" : 1,
														"hideBulletsCount" : 30,
														"title" : document.getElementById( "selColumnB" ).value,
														"valueField" : document.getElementById( "selColumnB" ).value,
														"fillAlphas" : 0
													}, {
														"valueAxis" : "v5",
														"lineColor" : "#990033",
														"bullet" : "square",
														"bulletBorderThickness" : 1,
														"hideBulletsCount" : 30,
														"title" : "_" + document.getElementById( "selColumnB" ).value,
														"valueField" : "_" + document.getElementById( "selColumnB" ).value,
														"fillAlphas" : 0
													}, {
														"valueAxis" : "v6",
														"lineColor" : "#0000ff",
														"bullet" : "triangleUp",
														"bulletBorderThickness" : 1,
														"hideBulletsCount" : 30,
														"title" : "err%_" + document.getElementById( "selColumnB" ).value,
														"valueField" : "err%_" + document.getElementById( "selColumnB" ).value,
														"fillAlphas" : 0
													}

											],
											"chartScrollbar" : {},
											"chartCursor" : {

												"categoryBalloonDateFormat" : "YYYY-MM-DD JJ:NN",
												"cursorPosition" : "mouse",
												"selectWithoutZooming" : true,
												"listeners" : [
													{
														"event" : "selected",
														"method" : function ( event ) {
															var start = new Date( event.start );
															var end = new Date( event.end );
															document.getElementById( 'info' ).innerHTML = "Selected: " + start.toLocaleTimeString() + " -- " + end.toLocaleTimeString()
														}
													}
												]
											},
											"categoryField" : chartDT,
											"categoryAxis" : {
												"parseDates" : true,
												"axisColor" : "#DADADA",
												"minPeriod" : "mm",
												"minorGridEnabled" : true

											},
											"export" : {
												"enabled" : true,
												"dateFormat" : "YYYY-MM-DD JJ:NN:SS.Q",
												"position" : "bottom-right"
											}
										} );

										chart.addListener( "dataUpdated", zoomChart );
										zoomChart();

										function generateChartData ( response ) {
											return response;
										}

										function zoomChart () {
											chart.zoomToIndexes( chart.dataProvider.length - 40, chart.dataProvider.length - 1 );

										}
									}

								},
								error : function ( error ) {
									document.getElementById( "btnLoad" ).disabled = false;
									alert( "Try again" );
								},
							} );
				<%}
			}%>
					}
					}

					function ShowAlertsFromJSON ( tableData ) {
						
					<%-- 	var optColumnA = "<%=session.getAttribute("optColumnA")%>";
						var whnColumnA = "<%=session.getAttribute("whnColumnA")%>";
						var optColumnB = "<%=session.getAttribute("optColumnB")%>";
						var whnColumnB = "<%=session.getAttribute("whnColumnB")%>";
						
					
						var jsonData = JSON.parse( JSON.stringify( tableData ) );
						for ( var i = 0; i < jsonData.length; i++ ) {
							//alert(JSON.stringify(jsonData));
							var counter = JSON.stringify( jsonData[i] );
							//alert("QWE : "+JSON.stringify(counter));
							
							
						} --%>
					}
			

					function CreateTableFromJSON ( tableData ) {
						//console.log("2312"+ JSON.stringify( tableData ) );
						// EXTRACT VALUE FOR HTML HEADER. 
						// ('Book ID', 'Book Name', 'Category' and 'Price')
						
						var optColumnA = "<%=session.getAttribute("optColumnA")%>";
						var whnColumnA = "<%=session.getAttribute("whnColumnA")%>";
						var optColumnB = "<%=session.getAttribute("optColumnB")%>";
						var whnColumnB = "<%=session.getAttribute("whnColumnB")%>";
						console.log ("In CreateTableFromJSON optColumn : "+optColumnA +", whnColumnA : "+whnColumnA+", optColumnB : "+optColumnB +"whnColumnB : "+whnColumnB);
									
						var col = [];
						for ( var i = 0; i < tableData.length; i++ ) {
							for ( var key in tableData[ i ] ) {
								if ( col.indexOf( key ) === -1 ) {
									col.push( key );
								}
							}
						}

						// CREATE DYNAMIC TABLE.
						var table = document.createElement( "table" );
						table.classList.add( 'table' );

						// CREATE HTML TABLE HEADER ROW USING THE EXTRACTED HEADERS ABOVE.

						var tr = table.insertRow( -1 ); // TABLE ROW.

						for ( var i = 0; i < col.length; i++ ) {
							var th = document.createElement( "th" ); // TABLE HEADER.							
							th.innerHTML = col[ i ];
							tr.appendChild( th );
						}

						// ADD JSON DATA TO THE TABLE AS ROWS.						
						//consle.log( "tableData : " + tableData );
						//consle.log( "col[ j ] : " + col[ j ] );

						for ( var i = 0; i < tableData.length; i++ ) {

							tr = table.insertRow( -1 );

							for ( var j = 0; j < col.length; j++ ) {
								var tabCell = tr.insertCell( -1 );
								tabCell.innerHTML = tableData[ i ][ col[ j ] ] == undefined ? "-" : tableData[ i ][ col[ j ] ];
								
								
								if ( col[ j ] == document.getElementById( "selColumnA" ).value ) {
									console.log(col[ j ]  + " :  "+document.getElementById( "selColumnA" ).value);
									console.log(isVoilated( tableData[ i ][ col[ j ] ], optColumnA, whnColumnA ));
									if ( isVoilated( tableData[ i ][ col[ j ] ], optColumnA, whnColumnA ) ) {										
										console.log(  col[ j ] +" is :  " +tableData[ i ][ col[ j ] ] + optColumnA +whnColumnA);										
										$.notify( {
											// options
											message :  col[ j ] +" is Voilated !!! \n  " +tableData[ i ][ col[ j ] ] + optColumnA +whnColumnA
										}, {
											// settings
											type : 'danger',
											placement: {
													from: "bottom",
													align: "right"
												},
											timer: 20000,											
											newest_on_top: true,
											
										} );

										
										}
										
									
								}
								if ( col[ j ] == document.getElementById( "selColumnB" ).value ) {
									console.log(col[ j ]  + " :  "+document.getElementById( "selColumnB" ).value);
									console.log(isVoilated( tableData[ i ][ col[ j ] ], optColumnB, whnColumnB ));
									if ( isVoilated( tableData[ i ][ col[ j ] ], optColumnB, whnColumnB ) ) {

										console.log(  col[ j ] +" is :  " +tableData[ i ][ col[ j ] ] + optColumnB +whnColumnB);
										
										$.notify( {
											// options
											message :  col[ j ] +" is Voilated !!! \n  " +whnColumnB + optColumnB +tableData[ i ][ col[ j ] ] 
										}, {
											// settings
											type : 'danger',
											placement: {
													from: "bottom",
													align: "right"
												},
											timer: 20000,											
											newest_on_top: true,											
										} );

										
										}
									}
								}

							}						

						// FINALLY ADD THE NEWLY CREATED TABLE WITH JSON DATA TO A CONTAINER.
						var divContainer = document.getElementById( "showData" );
						divContainer.innerHTML = "";
						divContainer.appendChild( table );
					}
				
					function isVoilated ( value, optColumn, whnColumn ) {				
						console.log("isVoilation -> "+value+ " : " + optColumn+ " : " + whnColumn);
						switch(optColumn) {
						    case '<':
						        return whnColumn<value;
						        break;
						    case '>':
						    	return whnColumn>value;
						        break;
						    case '<=':
						    	return whnColumn<=value;
						        break;
						    case '>=':
						    	return whnColumn>=value;
						        break;
						    default:
						        return false;						        
						}

					}
				</script>

				<!-- HTML -->
				<div id="chartdiv"></div>
				<br> <br>
				<p>
				<h1>Prediction Table</h1>
				<p>
				<div id="showData" class="table-responsive"></div>

				<!-- 				<div class="table-responsive"> -->
				<!-- 					<table class="table" id="table"> -->
				<!-- 					</table> -->
				<!-- 				</div> -->

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
			</div>

		</div>
	</div>

</body>
</html>

