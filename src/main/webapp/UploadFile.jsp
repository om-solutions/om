<%@page import="com.appian.prediction.Admin"%>
<%@page import="javassist.bytecode.stackmap.BasicBlock.Catch"%>
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

<%@ page import="com.appian.exception.PException"%>


<%
	try {
		// Apache Commons-Fileupload library classes
		//System.out.println("Start");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//System.out.println("1");
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//System.out.println("2");
		if (!ServletFileUpload.isMultipartContent(request)) {
			//System.out.println("sorry. No file uploaded");
			return;
		}
		//System.out.println("3");

		// parse request

		//List items = sfu.parseRequest(request);

		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);	
		for (FileItem item : items) {
			String fileName = item.getName();
			String crunchifyCSV = item.getString();
			ArrayList<String> crunchifyResult = new ArrayList<String>();			
			if (crunchifyCSV != null) {
				String lines[] = crunchifyCSV.split("\\r?\\n");
				//System.out.println("lines.length : " + lines.length);
				for (int j = 0; j < lines.length; j++) {
					if (j == 0) {
						String[] headerData = lines[j].split("\\s*,\\s*");
						String[] rowData = lines[j + 1].split("\\s*,\\s*");
						DBConnection.createReplaceTable(fileName, headerData, rowData);

					} else {
						String[] rowData = lines[j].split("\\s*,\\s*");
						try {
							DBConnection.insertRowTable(rowData);
						} catch (Exception e) {
							try {
								DBConnection.updateRowTable(rowData);
							} catch (Exception ex) {

							}
						}
					}
				}

			}
			
			//DBConnection.TrainDB(fileName.toString(),session.getAttribute("pUser").toString());
		}
		
		session.setAttribute("status", "File Uploaded Successfully !!!");
		//	out.println("File Uploaded Successfully.");
%>
<script type="text/javascript">
	window.location.href = "admin.jsp";
</script>
<%
	} catch (Exception ex) {
		//System.out.println("99999 : " + ex);
		session.setAttribute("status",
				"Unable to upload the file!! Please check the file name and column names.");
%><script type="text/javascript">
	window.location.href = "admin.jsp";
</script>
<%
	}
%>