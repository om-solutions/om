<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Example</title>
</head>
<body>
	<form method="post" action="login.jsp">
		<center>
			<table border="1" width="30%" cellpadding="3">
				<thead>
					<tr>
						<th colspan="2">Login Here</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>User Name</td>
						<td><input type="text" name="uname" value="" /></td>
					</tr>
					<tr>
						<td>Password</td>
						<td><input type="password" name="pass" value="" /></td>
					</tr>
					<tr>
						<td><input type="submit" value="Login" /></td>
						<td><input type="reset" value="Reset" /></td>
					</tr>
					<tr>
						<td colspan="2">Yet Not Registered!! <a href="reg.jsp">Register
								Here</a></td>
					</tr>
				</tbody>
			</table>
		</center>
	</form>


	<script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
	<script type="text/javascript">
		function display() {
			$("h1").html(parseInt($("#inc").val()) + parseInt($("h1").html()));
			t = setTimeout("display()", parseInt($("#int").val()) * 1000)
		}
		function pauseT() {
			clearTimeout(t);
		}
		function continueT() {
			t = setTimeout("display()", parseInt($("#int").val()) * 1000)
		}
	</script>
	Increment Value
	<input type="text" id="inc"></input> Interval in Second
	<input type="text" id="int"></input>
	<input type="button" onclick="display()" value="Start" />
	<h1>0</h1>
	<button onclick="continueT()">Continue</button>
	<button onclick="pauseT()">Pause</button>
	
	
	<select class="refreshInterval" value="1">
    <option value="0">Off</option>
    <option value="0.5" selected>30 sec</option>
    <option value="1">1 min</option>
    <option value="2">2 min</option>
    <option value="5">5 min</option>
</select>
	<script type="text/javascript">
	
	ref = setInterval(showDashboard, 1000) 

$(".refreshInterval").change(function() {    
    var interval = parseFloat( $(this).val() ) * 60 * 1000;
    clearInterval(ref);
    console.log("New Interval: " + interval);
    if (interval > 0) {
        ref = setInterval(showDashboard, interval)     
    }
});


function showDashboard() {
    console.log(Math.random());
}
</script>
</body>
</html>