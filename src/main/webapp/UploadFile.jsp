<%@page import="com.appian.prediction.PredictValues"%>
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
<%@ page import="org.json.JSONObject"%>


<%
	try {
		// Apache Commons-Fileupload library classes
		System.out.println("Start");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		System.out.println("1");
		ServletFileUpload sfu = new ServletFileUpload(factory);
		System.out.println("2");
		if (!ServletFileUpload.isMultipartContent(request)) {
			//System.out.println("sorry. No file uploaded");
			return;
		}
		System.out.println("3");

		// parse request

		//List items = sfu.parseRequest(request);

		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

		System.out.println("Item Size  : " + items.size());

		for (FileItem item : items) {
			String fileName = item.getName();
			System.out.println("File Name : " + fileName);
			session.setAttribute("fName", fileName);
			System.out.println("crunchifyCSV : " + item.getName());
			String crunchifyCSV = item.getString();
			ArrayList<String> crunchifyResult = new ArrayList<String>();
			if (crunchifyCSV != null) {
				System.out.println("crunchifyCSV : " + crunchifyCSV.toString());
				String lines[] = crunchifyCSV.split("\\r?\\n");
				System.out.println("lines.length : " + lines.length);
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

			try{
			ServletContext context = getServletContext();
			System.out.println("getContextPath() : " + context.getContextPath());
			String tableName = fileName.replace(".csv", "");
			System.out.println("##### Max Date " + DBConnection.getMaxDate(tableName));
			JSONObject jsonObj = DBConnection.getMaxDate(tableName);
			request.setAttribute("dateFrom", jsonObj.get("startdt"));
			request.setAttribute("dateTo", jsonObj.get("enddt"));

			PredictValues predictValues = new PredictValues();
			predictValues.PrePredict(request, jsonObj.get("startdt").toString(),
					jsonObj.get("enddt").toString(), tableName);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			

		}

		//##### Max Date {"startdt":"2009-12-01 06:15:00.0","enddt":"2010-03-01 06:15:00.0"}

		//rd.include(request, response);

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