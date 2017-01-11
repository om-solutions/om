<%@ page import ="java.sql.*" %>
<%
	String url = "jdbc:sqlserver://localhost:1433";
	String dbName = ";databaseName=Danpac;instance=SQLEXPRESS";
	String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String userName = "sa";
	String password = "root";
    String user = request.getParameter("uname");    
    String pwd = request.getParameter("pass");
    String fname = request.getParameter("fname");
    String lname = request.getParameter("lname");
    String email = request.getParameter("email");
    Class.forName(driver).newInstance();
	Connection conn = DriverManager.getConnection(url + dbName, userName, password);
    Statement st = conn.createStatement();
    //ResultSet rs;
    int i = st.executeUpdate("insert into danpac.dbo.members(first_name, last_name, email, uname, pass, regdate) values ('" + fname + "','" + lname + "','" + email + "','" + user + "','" + pwd + "', GETDATE())");
    if (i > 0) {
        //session.setAttribute("userid", user);
        response.sendRedirect("welcome.jsp");
       // out.print("Registration Successfull!"+"<a href='index.jsp'>Go to Login</a>");
    } else {
        response.sendRedirect("index.jsp");
    }
%>