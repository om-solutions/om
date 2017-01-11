<%
session.setAttribute("userid", null);
session.invalidate();
response.sendRedirect("Plogin.jsp");
%>