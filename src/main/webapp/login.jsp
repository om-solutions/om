<%@ page import ="java.sql.*" %>
<%

	String url = "jdbc:sqlserver://localhost:1433";
	String dbName = ";databaseName=Danpac;instance=SQLEXPRESS";
	String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	String userName = "sa";
	String password = "root";
    String userid = request.getParameter("uname");    
    String pwd = request.getParameter("pass");
    Class.forName(driver).newInstance();
   	Connection conn = DriverManager.getConnection(url + dbName, userName, password);
    Statement st = conn.createStatement();
    ResultSet rs;
    rs = st.executeQuery("select * from danpac.dbo.members where uname='" + userid + "' and pass='" + pwd + "'");
    if (rs.next()) {
        session.setAttribute("userid", userid);
        //out.println("welcome " + userid);
        //out.println("<a href='logout.jsp'>Log out</a>");
        response.sendRedirect("success.jsp");
    } else {
        out.println("Invalid password <a href='index.jsp'>try again</a>");
    }
%>