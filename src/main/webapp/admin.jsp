<!DOCTYPE html>
<html lang="en">
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style>
/* Remove the navbar's default margin-bottom and rounded borders */
.navbar {
	margin-bottom: 0;
	border-radius: 0;
}

/* Add a gray background color and some padding to the footer */
footer {
	background-color: #f2f2f2;
	padding: 25px;
}

.carousel-inner img {
	width: 100%; /* Set width to 100% */
	margin: auto;
	min-height: 200px;
}

/* Hide the carousel text when the screen is less than 600 pixels wide */
@media ( max-width : 600px) {
	.carousel-caption {
		display: none;
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
	<div class="container text-center">
		<div class="row">
			<div class="col-sm-2">
				<h5>Select Database</h5>
				<select name="dbSelector" id="dbSelector" class="selectpicker"
					data-style="btn-info"></select>
			</div>
			<div class="col-sm-2">

				<h5>Select Table</h5>
				<select name="tableSelector" id="tableSelector" class="selectpicker"
					data-style="btn-info"></select>
			</div>
			<div class="col-sm-8" style="text-align: left;">
				<h2>Basic Table</h2>
			</div>



		</div>


	</div>

	<div class="container text-center">
		<div class="row">
			<div class="col-sm-1">

				<script>
					
				<%int var = 1;
			out.println(var < 10 ? "0" + var : var);%>
					$('#tableSelector')
							.change(
									function() {
										var selectedTable = $(this).find(
												"option:selected").text();
										var selectedDb = $('#dbSelector').find(
												"option:selected").text();
										loadColumns("http://localhost:8080/Prediction/prediction/admin/columns?dbName="
												+ selectedDb
												+ "&tableName="
												+ selectedTable);
									});
					$('#dbSelector')
							.change(
									function() {
										var selectedText = $(this).find(
												"option:selected").text();
										loadTable("http://localhost:8080/Prediction/prediction/admin/table?dbName="
												+ selectedText);
										$("#tableSelector").empty();
									});

					$(document)
							.ready(
									function() {
				<%if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == "")) {%>
					alert("Please Login!!!");
										window.location = "Plogin.jsp";
				<%} else {%>
					loadDatabase("http://localhost:8080/Prediction/prediction/admin/database");
				<%}%>
					});

					function loadDatabase(url) {
						$.ajax({
							url : url,
							type : 'GET',
							dataType : 'json',
							success : function(response) {
								//alert(JSON.stringify(response));
								for (var i = 0; i < response.length; i++) {
									var obj = response[i];
									for ( var key in obj) {
										var attrName = key;
										var attrValue = obj[key];
										//alert(attrValue);
										$('#dbSelector').append($('<option>', {
											value : attrValue,
											text : attrValue
										}));

									}
								}

							},
							error : function(error) {
								alert("Try again");
							},
						});
					}

					function loadTable(url) {
						$.ajax({
							url : url,
							type : 'GET',
							dataType : 'json',
							success : function(response) {
								//alert(JSON.stringify(response));
								for (var i = 0; i < response.length; i++) {
									var obj = response[i];
									for ( var key in obj) {
										var attrName = key;
										var attrValue = obj[key];
										//alert(attrValue);
										$('#tableSelector').append(
												$('<option>', {
													value : attrValue,
													text : attrValue
												}));

									}
								}

							},
							error : function(error) {
								alert("Try again");
							},
						});
					}

					function loadColumns(url) {
						$.ajax({
							url : url,
							type : 'GET',
							dataType : 'json',
							success : function(response) {
								//alert(JSON.stringify(response));
								$("#POIBody").children().remove();
								for (var i = 0; i < response.length; i++) {
									//alert("1221212");
									var obj = response[i];
									var columnName = obj["columnName"];
									var dataType = obj["dataType"];
									var isNull = obj["isNull"];
									addRow(columnName, dataType, isNull);

								}

							},
							error : function(error) {
								alert("Try again");
							},
						});
					}
					function addRow(columnName, dataType, isNull) {
						if (dataType === 'datetime') {
							$("#POITable")
									.append(
											'<tr><td style="text-align: left;"><input class="messageCheckbox" type="checkbox" value='+columnName+' id='+columnName+'></td><td style="text-align: left;">'
													+ columnName
													+ '</td> <td style="text-align: left;">'
													+ dataType
													+ '</td><td style="text-align: left;" >'
													+ isNull
													+ '</td><td style="text-align: left;" > <div id="select1" class="product-options " data-toggle="buttons"><input type="radio" checked="checked" name="chartDT" value='+columnName+' ></div></tr>');
						} else {
							$("#POITable")
									.append(
											'<tr><td style="text-align: left;"><input class="messageCheckbox" type="checkbox" value='+columnName+' id='+columnName+'></td><td style="text-align: left;">'
													+ columnName
													+ '</td> <td style="text-align: left;">'
													+ dataType
													+ '</td><td style="text-align: left;" >'
													+ isNull
													+ '</td><td style="text-align: left;" > <div id="select1" class="product-options " data-toggle="buttons"><input type="radio" name="chartDT" value='+columnName+' ></div></tr>');
						}

					}
					function insRow() {
						var x = document.getElementById('POITable');
						// deep clone the targeted row
						var new_row = x.rows[1].cloneNode(true);
						// get the total number of rows
						var len = x.rows.length;
						// set the innerHTML of the first row 
						new_row.cells[0].innerHTML = len;

						// grab the input from the first cell and update its ID and value
						var inp1 = new_row.cells[1]
								.getElementsByTagName('input')[0];
						inp1.id += len;
						inp1.value = '';

						// grab the input from the first cell and update its ID and value
						var inp2 = new_row.cells[2]
								.getElementsByTagName('input')[0];
						inp2.id += len;
						inp2.value = '';

						// append the new row to the table
						x.appendChild(new_row);
					}
					function saveColumns() {
						var allColumns = "";
						var dbName = "";
						var tableName = "";

						var dbName = $('#dbSelector').find("option:selected")
								.text();
						var tableName = $('#tableSelector').find(
								"option:selected").text();

						$("input[type=checkbox]:checked").each(function() {
							if (allColumns == "")
								allColumns = $(this).val();
							else
								allColumns += ',' + $(this).val();
						});

						var chartDT = $('input[name=chartDT]:checked').val();

						if (chartDT == null) {
							alert("Plese Select Date\Time Cloumn !!!");
						} else {
							$
									.ajax({
										url : "http://localhost:8080/Prediction/prediction/admin/saveCols?columns="
												+ allColumns
												+ "&dbName="
												+ dbName
												+ "&tableName="
												+ tableName
												+ "&chartDT="
												+ chartDT,
										type : 'GET',
										success : function(response) {
											alert(response);
										},
										error : function(error) {
											alert("Try again : "
													+ JSON.stringify(error));
										},
									});
						}

					}
				</script>
			</div>
			<div class="col-sm-8">


				<p>The .table class adds basic styling (light padding and only
					horizontal dividers) to a table:</p>
				<table class="table" id="POITable" name="POITable">
					<thead>
						<tr>
							<th>is?</th>
							<th>Column Name</th>
							<th>Data Type</th>
							<th>is Null</th>

						</tr>
					</thead>
					<tbody id="POIBody">
						<!-- <tr>
							<td><input type="checkbox" value=""></td>
							<td>John</td>
							<td>Doe</td>
							<td id="btnAdd" class="button-add" onclick="insertRow();">add</td>
						</tr>
						<tr>
							<td><input type="checkbox" value=""></td>
							<td>Mary</td>
							<td>Moe</td>
							<td id="btnAdd" class="button-add" onclick="insertRow();">add</td>
						</tr>
						<tr>
							<td><input type="checkbox" value=""></td>
							<td>July</td>
							<td>Dooley</td>
							<td id="btnAdd" class="button-add" onclick="insertRow();">add</td>
						</tr> -->
					</tbody>
				</table>

			</div>
		</div>
	</div>
	<br>

	<footer class="container-fluid text-center">
		<p>
			<button type="button" class="btn btn-success" onclick="saveColumns()">Save</button>
		</p>
	</footer>

</body>
</html>
