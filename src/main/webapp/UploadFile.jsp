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

<HTML>

<BODY bgcolor="#99CCCC">
	<p></p>
	<br>

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
			System.out.println("4" + items.size());
			for (FileItem item : items) {
				//System.out.println("5"+item.getString());
				System.out.println("5" + item.getSize());
				System.out.println("5" + item.getContentType());
				System.out.println("5" + item.getFieldName());
				System.out.println("5" + item.getName());
				System.out.println("5" + item.getClass());
				System.out.println("5" + item.getOutputStream());
				String fileName = item.getName();
				String crunchifyCSV = item.getString();
				ArrayList<String> crunchifyResult = new ArrayList<String>();

				if (crunchifyCSV != null) {
					String lines[] = crunchifyCSV.split("\\r?\\n");
					for (int j = 0; j < lines.length; j++) {
						System.out.println(j + " : " + lines[j].toString());
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

			/* 
			BufferedReader reader = new BufferedReader(new FileReader(<<your file>>));
			List<String> lines = new ArrayList<>();
			String line = null;
			while ((line = reader.readLine()) != null) {
			lines.add(line);
			}
			*/

			//System.out.println("items : " + items.toString());

			FileItem PartNo = (FileItem) items.get(0);
			String photoid = PartNo.getString();
			//System.out.println("photoid : " + photoid);
			FileItem SerialNo = (FileItem) items.get(1);
			String phototitle = SerialNo.getString();
			//System.out.println("SerialNo : " + SerialNo);
			System.out.println("phototitle : " + phototitle);
			// get uploaded file
			FileItem file = (FileItem) items.get(2);

			// Connect to Oracle

			Connection con = DBConnection.getConnection();
			con.setAutoCommit(false);

			PreparedStatement ps = con
					.prepareStatement("insert into danpac.dbo.InventoryDB_Main(PartNo,SerialNo) values(?,?)");
			ps.setString(1, photoid);
			ps.setString(2, phototitle);
			// size must be converted to int otherwise it results in error
			ps.setBinaryStream(3, file.getInputStream(), (int) file.getSize());
			ps.executeUpdate();
			con.commit();
			con.close();
			out.println("File Uploaded Successfully.");
		} catch (

		Exception ex) {
			out.println("Error --> " + ex);
		}
	%>