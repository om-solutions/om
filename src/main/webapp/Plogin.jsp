
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>Prediction Login</title>
<meta name="generator" content="Bootply" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<meta name="description"
	content="Example snippet for a Bootstrap login form modal" />
<link
	href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"
	rel="stylesheet">

<!--[if lt IE 9]>
          <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
<link rel="apple-touch-icon" href="/bootstrap/img/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72"
	href="/bootstrap/img/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114"
	href="/bootstrap/img/apple-touch-icon-114x114.png">










<!-- CSS code from Bootply.com editor -->

<style type="text/css">
.modal-footer {
	border-top: 0px;
}
</style>
</head>

<!-- HTML code from Bootply.com editor -->

<body>

	<!--login modal-->
	<div id="loginModal" class="modal show" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="text-center">Login</h1>
				</div>
				<div class="modal-body">
					<form class="form col-md-12 center-block">
						<div class="form-group">
							<input id="username" type="text" class="form-control input-lg"
								placeholder="User Name">
						</div>
						<div class="form-group">
							<input id="password" type="password"
								class="form-control input-lg" placeholder="Password">
						</div>
						<div class="checkbox-inline">
							<label> <input type="checkbox" id="asAdmin"
								name="asAdmin" data-size="small" value="Credit"
								data-on-text="Credit" data-on-color="success"
								data-off-text="Cash" data-off-color="warning" tabindex="13">As
								a admin
							</label>
						</div>

					</form>
					<div class="form-group">
						<button id="buttonLogin1" class="btn btn-primary btn-block">Sign
							In</button>
						<span class="pull-right"><a href="reg.jsp">Register</a></span>
					</div>
					<div class="modal-footer"></div>
				</div>
			</div>
		</div>

		<script type='text/javascript'
			src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>


		<script type='text/javascript'
			src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>







		<!-- JavaScript jQuery code from Bootply.com editor  -->

		<script type='text/javascript'>
			$( document ).ready( function () {

				$( "#buttonLogin1" ).click( function ( e ) {

					console.log( "button clicked" );
					$.ajax( {
						url : 'http://localhost:8080/Prediction/prediction/user/login?username=' + document.getElementById( "username" ).value + '&password=' + document.getElementById( "password" ).value,
						beforeSend : function ( request ) {
							console.log( "before send" );
							request.setRequestHeader( "Authorization", "Negotiate" );
						},
						type : 'GET',
						dataType : 'text',
						success : function ( response ) {

							console.log( response );
							if ( response == "True" ) {

								if ( $( "#asAdmin" ).prop( "checked" ) == true ) {
									sessionStorage.setItem( 'userid', 'userid' );
		<%session.setAttribute("userid", "user");%>
			window.location.href = "http://localhost:8080/Prediction/admin.jsp";
								} else {
									sessionStorage.setItem( 'userid', 'userid' );
		<%session.setAttribute("userid", "user");%>
			window.location.href = "http://localhost:8080/Prediction/welcome.jsp";

								}
							} else
								alert( "Try again" );

						},
						error : function ( error ) {
							alert( "API failure " );
						}
					} )
				} );
			} );
		</script>

		<style>
.ad {
	position: absolute;
	bottom: 70px;
	right: 48px;
	z-index: 992;
	background-color: #f3f3f3;
	position: fixed;
	width: 155px;
	padding: 1px;
}

.ad-btn-hide {
	position: absolute;
	top: -10px;
	left: -12px;
	background: #fefefe;
	background: rgba(240, 240, 240, 0.9);
	border: 0;
	border-radius: 26px;
	cursor: pointer;
	padding: 2px;
	height: 25px;
	width: 25px;
	font-size: 14px;
	vertical-align: top;
	outline: 0;
}

.carbon-img {
	float: left;
	padding: 10px;
}

.carbon-text {
	color: #888;
	display: inline-block;
	font-family: Verdana;
	font-size: 11px;
	font-weight: 400;
	height: 60px;
	margin-left: 9px;
	width: 142px;
	padding-top: 10px;
}

.carbon-text:hover {
	color: #666;
}

.carbon-poweredby {
	color: #6A6A6A;
	float: left;
	font-family: Verdana;
	font-size: 11px;
	font-weight: 400;
	margin-left: 10px;
	margin-top: 13px;
	text-align: center;
}
</style>
</body>
</html>