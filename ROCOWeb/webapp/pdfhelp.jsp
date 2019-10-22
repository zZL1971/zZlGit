<%@ page language="java" import="java.util.*,com.mw.framework.utils.*,java.security.interfaces.*" pageEncoding="UTF-8"%>
<%
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String itemid = request.getParameter("itemid");
		System.out.println("itemid="+itemid);
		String pdfname = "";
		if(itemid.equals("a")){
			 pdfname = "ABC.pdf";
		}else{
			 pdfname = "11.pdf";	
		}
		
		System.out.println("pdfname="+pdfname);
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>帮助文档</title>

	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">

<script type="text/javascript" src="/security/pdfobject.js">
</script>

<style type="text/css">
		<!--
		#pdf {
			width: 800px;
			height: 800px;
			margin: 2em auto;
			border: 1px solid #65452;
		}
		
		#pdf p {
			padding: 1em;
		}
		
		#pdf object {
			display: block;
			border: solid 1px #123;
		}
		-->
</style>

<script type="text/javascript">
    
    var cgg  = "<%=pdfname%>";
 
window.onload = function() {
  
	var success = new PDFObject({
		//url : "./books/6.0user.pdf",
		url : cgg,
		//url : cgg,
		pdfOpenParams : {
			scrollbars : '0',
			toolbar : '0',
			statusbar : '0'
		}
	}).embed("pdf");
};
</script>

</head>
<body>
	<div id="pdf">
		<a href=""></a>
	</div>
</body>
</html>