<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<link rel="stylesheet" type="text/css"
			href="/SRM/extjs/4.2.1/resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css"
			href="/SRM/resource/css/ext-custom.css" />
		<script type="text/javascript" src="/SRM/extjs/4.2.1/bootstrap.js"></script>
		<script type="text/javascript"
			src="/SRM/extjs/4.2.1/locale/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="/SMSWeb/app/index/CmoApp.js"></script>
		<base href="<%=basePath%>">



		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

	</head>

	<body>

	</body>
</html>
