<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; utf-8" />
<title>图片变换效果</title>
<script src="/plugins/imageTrans/CJL.0.1.min.js"></script>
<script src="/plugins/imageTrans/ImageTrans.js"></script>
</head>
<body>
<style>
body{height:100%; overflow:hidden; margin:0px; padding:0px;}
#idContainer{height:100%; background:#CCC; position:absolute; width:100%;} 
</style>
<center>
<div id="idContainer">
  </div>
  <div style="position:fixed; top: 10px; left: 10px; ">
  <input id="idLeft" type="button" value="向左旋转" />
<input id="idRight" type="button" value="向右旋转" />
<input id="idVertical" type="button" value="垂直翻转" />
<input id="idHorizontal" type="button" value="水平翻转" />
<input id="idReset" type="button" value="重置" />
<input id="idCanvas" type="button" value="使用Canvas" />
<br>
<input id="idSrc" type="hidden" value="${uuid }" />
  </div>
<script>

(function(){
	//console.log("document.body.clientHeight"+document.body.clientHeight);
	//console.log("document.body.clientWidth"+document.body.clientWidth);
	//document.getElementById("idContainer").style.height= document.body.clientHeight+"px";
	//document.getElementById("idContainer").style.width= document.body.clientWidth+"px";
	
     var id=document.getElementById("idSrc").value;
     var src_="/main/sysFile/fileDownload?id="+id;
     
	var container = $$("idContainer"), 
	//src = "http://images.cnblogs.com/cnblogs_com/cloudgamer/143727/r_mm14.jpg",
	src=src_,
	options = {
		onPreLoad: function(){
			container.style.backgroundImage = "url('/extjs/4.2.1/packages/ext-theme-access/build/resources/images/grid/loading.gif')";
			 
			console.log("container.clientWidth"+container.clientWidth);
			console.log("container.clientHeight"+container.clientHeight);
		},
		onLoad: function(){ 
			container.style.backgroundImage = ""; 
		},
		onError: function(err){ container.style.backgroundImage = "";
		 alert("找不到图片"); 
		}
	},
	it = new ImageTrans( container, options);
	it.load(src);
	//垂直翻转
	$$("idVertical").onclick = function(){ it.vertical(); }
	//水平翻转
	$$("idHorizontal").onclick = function(){ it.horizontal(); }
	//左旋转
	$$("idLeft").onclick = function(){ it.left(); }
	//右旋转
	$$("idRight").onclick = function(){ it.right(); }
	//重置
	$$("idReset").onclick = function(){ it.reset(); }
//换图
//$$("idLoad").onclick = function(){ it.load( $$("idSrc").value ); }
//Canvas
$$("idCanvas").onclick = function(){
	if(this.value == "默认模式"){
		this.value = "使用Canvas"; delete options.mode;
	}else{
		this.value = "默认模式"; options.mode = "canvas";
	}
	it.dispose();
	it = new ImageTrans( container, options );
	it.load(src);
}

})()
</script>
</center>
</body>
</html>
