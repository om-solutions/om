
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
/*
/* Created by Filipe Pina
 * Specific styles of signin, register, component
 */
/*
 * General styles
 */
body, html {
	height: 100%;
	background-repeat: no-repeat;
	background-color: #d3d3d3;
	font-family: 'Oxygen', sans-serif;
}

.main {
	margin-top: 70px;
}

h1.title {
	font-size: 50px;
	font-family: 'Passion One', cursive;
	font-weight: 400;
}

hr {
	width: 10%;
	color: #fff;
}

.form-group {
	margin-bottom: 15px;
}

label {
	margin-bottom: 15px;
}

input, input::-webkit-input-placeholder {
	font-size: 11px;
	padding-top: 3px;
}

.main-login {
	background-color: #fff;
	/* shadows and rounded borders */
	-moz-border-radius: 2px;
	-webkit-border-radius: 2px;
	border-radius: 2px;
	-moz-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
	-webkit-box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
	box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.main-center {
	margin-top: 30px;
	margin: 0 auto;
	max-width: 330px;
	padding: 40px 40px;
}

.login-button {
	margin-top: 5px;
}

.login-register {
	font-size: 11px;
	text-align: center;
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
					<h1 class="text-center">Registration</h1>
				</div>
				<div class="modal-body">
					<form class="form col-md-12 center-block">

						<div class="form-group">
							<label for="name" class="cols-sm-2 control-label">Your
								Name</label>
							<div class="cols-sm-10">
								<div class="input-group">
									<span class="input-group-addon"><i class="fa fa-user fa"
										aria-hidden="true"></i></span> <input type="text"
										class="form-control" name="name" id="name"
										placeholder="Enter your Name" />
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="email" class="cols-sm-2 control-label">Your
								Email</label>
							<div class="cols-sm-10">
								<div class="input-group">
									<span class="input-group-addon"><i
										class="fa fa-envelope fa" aria-hidden="true"></i></span> <input
										type="text" class="form-control" name="email" id="email"
										placeholder="Enter your Email" />
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="username" class="cols-sm-2 control-label">Username</label>
							<div class="cols-sm-10">
								<div class="input-group">
									<span class="input-group-addon"><i
										class="fa fa-users fa" aria-hidden="true"></i></span> <input
										type="text" class="form-control" name="username" id="username"
										placeholder="Enter your Username" />
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="password" class="cols-sm-2 control-label">Password</label>
							<div class="cols-sm-10">
								<div class="input-group">
									<span class="input-group-addon"><i
										class="fa fa-lock fa-lg" aria-hidden="true"></i></span> <input
										type="password" class="form-control" name="password"
										id="password" placeholder="Enter your Password" />
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="confirm" class="cols-sm-2 control-label">Confirm
								Password</label>
							<div class="cols-sm-10">
								<div class="input-group">
									<span class="input-group-addon"><i
										class="fa fa-lock fa-lg" aria-hidden="true"></i></span> <input
										type="password" class="form-control" name="confirm"
										id="confirm" placeholder="Confirm your Password" />
								</div>
							</div>
						</div>

						<div class="form-group ">

							<button type="button" onclick="doReg()"
								class="btn btn-primary btn-lg btn-block login-button">Register</button>
						</div>
						<div class="login-register">
							<a href="Plogin.jsp">Login</a></span>
						</div>
					</form>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>

	<script type='text/javascript' src="js/jquery.min.js"></script>


	<script type='text/javascript' src="js/bootstrap.min.js"></script>







	<!-- JavaScript jQuery code from Bootply.com editor  -->

	<script type='text/javascript'>
		$(document).ready(function() {

		});

		function doReg() {
			console.log('http://localhost:8080/Prediction/prediction/user/register?name=' + document.getElementById("name").value + '&email=' + document.getElementById("email").value
					+ '&username=' + document.getElementById("username").value + '&password='
					+ document.getElementById("password").value + '&confirm=' + document.getElementById("confirm").value);

			/* var name = document.getElementById("name").value;
			var email = document.getElementById("email").value;
			var username = document.getElementById("username").value;
			var password = document.getElementById("password").value;
			var confirm = document.getElementById("confirm").value;
			 */
			$
					.ajax({
						url : 'http://localhost:8080/Prediction/prediction/user/register?name=' + document.getElementById("name").value + '&email=' + document.getElementById("email").value
								+ '&username=' + document.getElementById("username").value + '&password='
								+ document.getElementById("password").value + '&confirm=' + document.getElementById("confirm").value,
						type : 'GET',
						dataType : 'text',
						success : function(response) {
							console.log(response);
							if(response=="true")
							{
								alert("Registration Sucessfull !!!");
								window.location= "Plogin.jsp";
							}
							else
								alert("Try Again");

						},
						error : function(error) {
							alert("API failure ");
						},
					});

		}
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