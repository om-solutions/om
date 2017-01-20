<%@page import="java.util.ArrayList"%>
<%@page import="com.appian.db.DBConnection"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.List"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page import="javax.servlet.http.HttpServlet"%>
<%@ page import="javax.servlet.http.HttpServletRequest"%>
<%@ page import="javax.servlet.http.HttpServletResponse"%>
<%@ page import="org.apache.commons.fileupload.FileItem"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page
	import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>



<%
	try {
		// Apache Commons-Fileupload library classes
		System.out.println("Start");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		System.out.println("1");
		ServletFileUpload sfu = new ServletFileUpload(factory);
		System.out.println("2");
		if (!ServletFileUpload.isMultipartContent(request)) {
			System.out.println("sorry. No file uploaded");
			return;
		}
		System.out.println("3");

		// parse request

		//List items = sfu.parseRequest(request);

		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		for (FileItem item : items) {
			String fileName = item.getName();
			String crunchifyCSV = item.getString();
			ArrayList<String> crunchifyResult = new ArrayList<String>();

			if (crunchifyCSV != null) {
				String lines[] = crunchifyCSV.split("\\r?\\n");
				for (int j = 0; j < lines.length; j++) {
					if (j == 0) {
						String[] headerData = lines[j].split("\\s*,\\s*");
						String[] rowData = lines[j + 1].split("\\s*,\\s*");

						DBConnection.createReplaceTable(fileName, headerData, rowData);

					} else {
						String[] rowData = lines[j].split("\\s*,\\s*");
						DBConnection.insertRowTable(rowData);
					}
				}

			}
		}

		//	out.println("File Uploaded Successfully.");
%>
<script type="text/javascript">
	window.location.href = "admin.jsp?status=File Uploaded Successfully !!!";
</script>
<%
	} catch (Exception ex) {
		if(ex.getCause().toString().contains("already"))
		
		//	out.println("Error --> " + ex);
%><script type="text/javascript">
		window.location.href = "admin.jsp?status="+;
	</script>
<%
	}
%>