<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<jsp:include page="commonPublic.jsp"></jsp:include>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'dwrTest.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type='text/javascript' src='dwr/engine.js'></script>  
    <script type='text/javascript' src='dwr/util.js'></script>  
    <script type='text/javascript' src='dwr/interface/DWRHelper.js'></script>  
	
  </head>
  
  <!-- 通过 dwr.engine.setActiveReverseAjax(true); 启动该页面的Reverse Ajax功能  -->  
  <body onload="dwr.engine.setActiveReverseAjax(true);sendMessage();">  
    <p>备注: <input id="text" onkeypress="dwr.util.onReturn(event, sendMessage)" />    
    <input type="button" value="申请变更" onclick="sendMessage()" /></p>  
    <script type="text/javascript">  
        function sendMessage() {   
        	DWRHelper.addMessage(dwr.util.getValue("text")); 
        	if(dwr.util.getValue("text")!=""){
        		$("#msg_content").append("<a href='' target='_blank'><font color='blue'>"+dwr.util.getValue("text")+"</font></a><br/>");
        	}
        	Message.init();
        }   
    </script> 
    <hr/>  
    <select id="messages"></select>  
       
  </body>
   
</html>
