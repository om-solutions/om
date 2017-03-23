<!DOCTYPE html>
<html lang="en">
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="css/bootstrap.min.css">
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap-notify.js"></script>
<script src="js/bootstrap-notify.min.js"></script>
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

	<div class="col-sm-2 sidenav"></div>

	<div class="row-left"></div>

	<ul class="nav nav-tabs">
		<li class="active"><a data-toggle="tab" href="#selection">Selection
		</a></li>
		<li><a data-toggle="tab" href="#loadcsv">Load CSV</a></li>
	</ul>

	<div class="tab-content">

		<div id="selection" class="tab-pane fade in active">
			<br>
			<div class="container text-center">
				<div class="alert alert-info" style="text-align: center;" id="alt">
					<%=session.getAttribute("status") == null ? "" : session.getAttribute("status")%></div>
				<%
					session.removeAttribute("status");
				%>
			</div>
			<div class="container text-center">
				<div class="row-left">
					<div class="col-sm-4 left">
						<h5>Database URL</h5>
					</div>
					<div class="col-sm-4 left">
						<input type="text" class="form-control" id="url">
					</div>
				</div>
			</div>
			<div class="container text-center">
				<div class="row-left">
					<div class="col-sm-4 left">
						<h5>DB Instance</h5>
					</div>
					<div class="col-sm-4 left">
						<input type="text" class="form-control" id="dbInstanceName">
					</div>
				</div>
			</div>
			<div class="container text-center">
				<div class="row-left">
					<div class="col-sm-4 left">
						<h5>User Name</h5>
					</div>
					<div class="col-sm-4 left">
						<input type="text" class="form-control" id="userName">
					</div>
				</div>
			</div>
			<div class="container text-center">
				<div class="row-left">
					<div class="col-sm-4">
						<h5>Password</h5>
					</div>
					<div class="col-sm-4">
						<input type="password" class="form-control" id="password">
					</div>
				</div>

			</div>

			<div class="container text-center">
				<div class="row-left">

					<div class="col-sm-8 left">
						<button type="button" class="btn btn-success" id="getCon"
							name="getCon" onclick="connectDB()">Connect</button>
					</div>
				</div>

			</div>


			<div class="container text-center">
				<div class="row-left">
					<div class="col-sm-2 left">
						<h5>Select Database</h5>
					</div>
					<div class="col-sm-2 left">
						<select name="dbSelector" id="dbSelector"
							class="btn btn-primary dropdown-toggle" data-style="btn-info"></select>
					</div>
				</div>
				<div class="row-left">
					<div class="col-sm-2">
						<h5>Select Table</h5>
					</div>
					<div class="col-sm-2">
						<select name="tableSelector" id="tableSelector"
							class="btn btn-primary dropdown-toggle" data-style="btn-info"></select>
					</div>
				</div>

			</div>
			<div class="container text-center">
				<div class="row">
					<div class="col-sm-8" style="text-align: center;">
						<h2>Basic Table</h2>
					</div>
					<div class="row">

						<script>
							
						<%int var = 1;
			out.println(var < 10 ? "0" + var : var);%>
							$( '#tableSelector' ).change( function () {
								$( "#btSave" ).addClass( "disabled" );
								var selectedTable = $( this ).find( "option:selected" ).text();
								var selectedDb = $( '#dbSelector' ).find( "option:selected" ).text();
								loadColumns( "http://localhost:8080/Prediction/prediction/admin/columns?dbName=" + selectedDb + "&tableName=" + selectedTable );
							} );
							$( '#dbSelector' ).change( function () {
								var selectedText = $( this ).find( "option:selected" ).text();
								loadTable( "http://localhost:8080/Prediction/prediction/admin/table?dbName=" + selectedText );
								$( "#tableSelector" ).empty();
							} );

							$( document ).ready( function () {
						<%if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == "")) {%>
							alert( "Please Login!!!" );
								window.location = "Plogin.jsp";
						<%} else {
				//System.out.println(" - - >" + (session.getAttribute("userid")));%>
							loadConnection( "http://localhost:8080/Prediction/prediction/admin/getCon" );
						<%}%>
							} );

							function loadConnection () {
								$( '#dbSelector' ).children().remove();
								$.ajax( {
									url : "http://localhost:8080/Prediction/prediction/admin/loadCon",
									type : 'GET',
									dataType : 'json',
									success : function ( response ) {

										for ( var i = 0; i < response.length; i++ ) {
											//alert("1221212");
											var obj = response[ i ];
											document.getElementById( 'url' ).value = obj[ "url" ];
											document.getElementById( 'dbInstanceName' ).value = obj[ "dbInstanceName" ];
											document.getElementById( 'userName' ).value = obj[ "userName" ];
											document.getElementById( 'daysToPredict' ).value = obj[ "daysToPredict" ];

											//document.getElementById( 'password' ).value = obj[ "password" ];

										}
									},
									error : function ( error ) {
										alert( "Unable to load Connection !!!" );
									},
								} );
							}

							function connectDB () {
								if ( document.getElementById( 'dbInstanceName' ).value === "" || document.getElementById( 'userName' ).value === "" || document.getElementById( 'password' ).value === "" || document.getElementById( 'url' ).value === "" ) {
									alert( "Some field are blank !!! " );
								} else {
									$( '#dbSelector' ).children().remove();
									$.ajax( {
										url : "http://localhost:8080/Prediction/prediction/admin/dbCon?url=" + document.getElementById( 'url' ).value + "&dbInstanceName=" + document.getElementById( 'dbInstanceName' ).value + "&userName=" + document.getElementById( 'userName' ).value + "&password="
												+ document.getElementById( 'password' ).value,
										type : 'GET',
										dataType : 'json',
										success : function () {
											//alert(JSON.stringify(response));
											loadDatabase( "http://localhost:8080/Prediction/prediction/admin/database" );

										},
										error : function ( error ) {
											alert( "Unable to connect to database, Please check the setting " );
										},
									} );
								}
								document.getElementById( 'password' ).value = "";
							}

							function loadDatabase ( url ) {
								$.ajax( {
									url : url,
									type : 'GET',
									dataType : 'json',
									success : function ( response ) {
										//alert(JSON.stringify(response));
										$( '#dbSelector' ).children().remove();
										for ( var i = 0; i < response.length; i++ ) {
											var obj = response[ i ];
											for ( var key in obj ) {
												var attrName = key;
												var attrValue = obj[ key ];
												//alert(attrValue);
												$( '#dbSelector' ).append( $( '<option>', {
													value : attrValue,
													text : attrValue
												} ) );

											}
										}

									},
									error : function ( error ) {
										alert( "Unable to load database !!!" );
									},
								} );
							}

							function loadTable ( url ) {
								$.ajax( {
									url : url,
									type : 'GET',
									dataType : 'json',
									success : function ( response ) {
										//alert(JSON.stringify(response));
										for ( var i = 0; i < response.length; i++ ) {
											var obj = response[ i ];
											for ( var key in obj ) {
												var attrName = key;
												var attrValue = obj[ key ];
												//alert(attrValue);
												$( '#tableSelector' ).append( $( '<option>', {
													value : attrValue,
													text : attrValue
												} ) );

											}
										}

									},
									error : function ( error ) {
										alert( "Unable to load tables" );
									},
								} );
							}

							function loadColumns ( url ) {
								$.ajax( {
									url : url,
									type : 'GET',
									dataType : 'json',
									success : function ( response ) {
										//alert(JSON.stringify(response));
										var a = 'txtFlowTemperature';
										$( "#POIBody" ).children().remove();
										for ( var i = 0; i < response.length; i++ ) {
											//alert("1221212");
											var obj = response[ i ];
											var columnName = obj[ "columnName" ];
											var dataType = obj[ "dataType" ];
											var isNull = obj[ "isNull" ];
											addRow( columnName, dataType, isNull, i );

											//alert("!!!!  : "+$( '#txt'+columnName ).val())
										}

									},
									error : function ( error ) {
										alert( "Unable to retrieve column list !!!" );
									},
								} );
							}
							function addRow ( columnName, dataType, isNull, i ) {
								if ( dataType === 'real' || dataType === 'int' || dataType === 'float' || dataType === 'datetime' )
									if ( dataType === 'datetime' ) {
										$( "#POITable" )
												.append(
														'<tr><td style="text-align: left;"></td><td style="text-align: left;">'
																+ columnName
																+ '</td> <td style="text-align: left;">'
																+ dataType
																+ '</td><td style="text-align: left;" >'
																+ isNull
																+ '</td><td style="text-align: left;" > <div id="select1" class="product-options " data-toggle="buttons"><input type="radio" checked="checked" name="chartDT" value='+columnName+' ></div></td>NA<td style="text-align: left;" >NA</td><td style="text-align: left;" >NA</td></tr>' );
										$( "#btSave" ).removeClass( "disabled" );
									} else {
										$( "#POITable" )
												.append(
														'<tr><td style="text-align: left;"><input class="messageCheckbox" type="checkbox" value='+columnName+' id='+columnName+'></td><td style="text-align: left;">'
																+ columnName
																+ '</td> <td style="text-align: left;">'
																+ dataType
																+ '</td><td style="text-align: left;" >'
																+ isNull
																+ '</td><td style="text-align: left;" > <div id="select1" class="product-options " data-toggle="buttons"><input type="radio" name="chartDT" value='+columnName+' ></div></td> <td style="text-align: left;" > <select class="selectpicker" name="opt'+columnName+'" id="opt'+columnName+'">  <option><</option>  <option>></option> <option><=</option> <option>>=</option>	</select> </td> <td style="text-align: left;" > <div id="select1" class="product-options " data-toggle="buttons"><input type="number" name="txt'+columnName+'" id="txt'+columnName+'" placeholder="Number Only" name='+columnName+' ></div></td></tr>' );
									}

							}
							function insRow () {
								var x = document.getElementById( 'POITable' );
								// deep clone the targeted row
								var new_row = x.rows[ 1 ].cloneNode( true );
								// get the total number of rows
								var len = x.rows.length;
								// set the innerHTML of the first row 
								new_row.cells[ 0 ].innerHTML = len;

								// grab the input from the first cell and update its ID and value
								var inp1 = new_row.cells[ 1 ].getElementsByTagName( 'input' )[ 0 ];
								inp1.id += len;
								inp1.value = '';

								// grab the input from the first cell and update its ID and value
								var inp2 = new_row.cells[ 2 ].getElementsByTagName( 'input' )[ 0 ];
								inp2.id += len;
								inp2.value = '';

								// append the new row to the table
								x.appendChild( new_row );
							}
							function saveColumns () {
								var allColumns = "";
								var dbName = "";
								var tableName = "";
								var jArray = [];

								var dbName = $( '#dbSelector' ).find( "option:selected" ).text();
								var tableName = $( '#tableSelector' ).find( "option:selected" ).text();

								$( "input[type=checkbox]:checked" ).each( function () {

									alert( "Inner1 : " + $( this ).val() );
									alert( "Inner2 : " +  $( '#opt' + $( this ).val() ).find( "option:selected" ).text());
									alert( "Inner3 : " + $( '#txt' + $( this ).val() ).val() );
									
									jArray.push( {
										"column" : $( this ).val(),
										"operator" :  $( '#opt' + $( this ).val() ).find( "option:selected" ).text(),
										"notifyWhen" :  $( '#txt' + $( this ).val() ).val()
									} );

									if ( allColumns == "" ) {
										allColumns = $( this ).val();

									} else {
										allColumns += ',' + $( this ).val();
									} 									//alert( "Inner4 : " + JSON.stringify( jArray ) );

								} );

								alert( JSON.stringify( jArray ) );
								var chartDT = $( 'input[name=chartDT]:checked' ).val();

								if ( chartDT == null ) {
									alert( "Plese Select Date\Time Cloumn !!!" );
								} else {
									$.ajax( {
										url : "http://localhost:8080/Prediction/prediction/admin/saveCols?columns=" + JSON.stringify( jArray ) + "&dbName=" + dbName + "&tableName=" + tableName + "&chartDT=" + chartDT,
										type : 'GET',
										success : function ( response ) {
											alert( response );
										},
										error : function ( error ) {
											alert( "Try again : " + JSON.stringify( error ) );
										},
									} );
								}

							}
						</script>
					</div>
				</div>
			</div>
			<div class="container text-center">
				<div class="row">
					<div class="col-sm-1"></div>
					<div class="col-sm-8">

						<table class="table" id="POITable" name="POITable" align="center">
							<thead>
								<tr>
									<th>is?</th>
									<th>Column Name</th>
									<th>Data Type</th>
									<th>is Null</th>
									<th>is datetime</th>
									<th>Operator</th>
									<th>Notify When</th>

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
			<footer class="container-fluid text-center">
				<div class="row">
					<button type="button" class="btn btn-success disabled" id="btSave"
						name="btSave" onclick="saveColumns()">Save</button>
				</div>
			</footer>

		</div>
		<div id="loadcsv" class="tab-pane fade">
			<div class="container text-center">
				<form id="form1" enctype="multipart/form-data"
					action="UploadFile.jsp" method="post">
					<table>
						<br>
						<br>
						<tr>
							<td style="padding-right: 20px">Days to be predicted</td>
							<td style="padding-right: 20px"><input align="center"
								name="daysToPredict" id="daysToPredict" type="number" min="0"
								max="365" class="btn btn-default" /></td>
							<td style="padding-right: 20px"><input align="center"
								name="btnDaysToPredict" id="btnDaysToPredict" type="button"
								value="Save" class="btn btn-default" /></td>
						</tr>
						<tr>
							<td style="padding-right: 20px">Browse File</td>
							<td style="padding-right: 20px"><input align="center"
								type="file" name="csvfile" accept=".csv" class="btn btn-default" />
							<td style="padding-right: 20px"><input align="center"
								type="submit" value="Upload File" class="btn btn-default" /></td>
						</tr>
					</table>
					<p />


				</form>
			</div>
		</div>

	</div>

	<script>
		$( '#btnDaysToPredict' ).on( 'click', function ( e ) {

			var x = $( "#daysToPredict" ).val();
			$.notify( {
				// options
				message : 'Hello World'
			}, {
				// settings
				type : 'danger'
			} );

			//alert("asadasd : "+x);		
			$.ajax( {
				url : "http://localhost:8080/Prediction/prediction/admin/saveDays?daysToPredict=" + x,
				type : 'GET',
				success : function ( response ) {
					alert( response );
				},
				error : function ( error ) {
					alert( "Try again : " + JSON.stringify( error ) );
				},
			} );

		} )
	</script>
</body>
</html>

