<%@ page language="java" import="java.util.*,com.mw.framework.utils.*,java.security.interfaces.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object currentUser = request.getSession().getAttribute("CURR_USER_ID");

String relogin = request.getParameter("relogin");
if(relogin==null){
	relogin="false";
}
HashMap<String, Object> map = RSAUtils.getKeys();    
//生成公钥和私钥    
RSAPublicKey publicKey = (RSAPublicKey) map.get("public");    
RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");  
  
session.setAttribute("privateKey", privateKey);//私钥保存在session中，用于解密  

//公钥信息保存在页面，用于加密  
String publicKeyExponent = publicKey.getPublicExponent().toString(16);  
String publicKeyModulus = publicKey.getModulus().toString(16);  

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>ROCO 门店接单系统v1.0</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<link rel="shortcut icon" href="/resources/images/favicon.ico" type="image/vnd.microsoft.icon">
	<link rel="icon" href="/resources/images/favicon.ico" type="image/vnd.microsoft.icon">
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/resources/css/ext-all-neptune.css"/>
	<script type="text/javascript" src="/security/security.js">
</script>
	<script type="text/javascript">
		var curruser = "<%=currentUser%>",relogin=<%=relogin%>;
		var CURR_USER_LOGIN_NO = parent.top.CURR_USER_LOGIN_NO;
		if(CURR_USER_LOGIN_NO==null){
			CURR_USER_LOGIN_NO = "";
		}
		if(curruser!="null"){
			location.href="/core/main/";
		}
		
		
	    RSAUtils.setMaxDigits(200);
	    
	    function k(k,x,y){var a=new RSAUtils.getKeyPair((relogin?x:"<%=publicKeyExponent%>"),"",(relogin?y:"<%=publicKeyModulus%>"));var b=RSAUtils.encryptedString(a,k);return b}
	</script>
	<script type="text/javascript" src="/extjs/4.2.1/bootstrap.js"></script>
	<script type="text/javascript" src="/extjs/4.2.1/locale/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="/app/index/Login.js?20"></script>
	<script type="text/javascript" src="/jquery/jquery-1.11.1.min.js"></script>
	<style type="text/css">
		.x-form-code{width: 73px;height: 23px;line-height:17px;vertical-align: middle;cursor: pointer;float: left;margin-left: 2px;}
		body{
			background-color: #ccc;
		}
		.x-mask{
			background:none;
		}
	</style>
  </head>
  
  <body>
  </body>
</html>