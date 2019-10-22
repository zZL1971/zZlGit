<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>${moduleTitle }</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<script type="text/javascript">
		var reportNo = "${reportNo}";
		var reportName = "${reportName}";
		
		function OnReady(id) {
			if(id=="AF"){
				var _build = AF.func("Build", "/core/report/t/"+reportNo);
				if(_build==1){
					//因supcan控件不支持问题，暂时屏蔽该功能
					//var menuMergecell = Ext.ComponentQuery.query("button[itemId=btn__mergecell]")[0];
					//menuMergecell[0].menu.menu.add({text:"3232323232"});
					
					/*for(col=AF.func("GetNextValidCol", ""); col!=""; col=AF.func("GetNextValidCol", col)) {
						var userProps = AF.func("GetColUserProps",col);
						if(userProps!=undefined && userProps.length>0){
							var userProp = userProps.split(",");
							for(var i=0;i<userProp.length;i++){
								if(userProp[i]=="mergecell"){
									var mergeCellStatus = AF.func("GetColProp",col+"\r\nmergecell");
									if(mergeCellStatus=="true"){
										AF.func("mergeSame", "col="+AF.func("GetColProp",col+"\r\nname"));
									}
								}
							}
						}else{
							menuMergecell.menu.add({checked:false,text:AF.func("GetColProp",col+"\r\nTitle"),indexName :AF.func("GetColProp",col+"\r\nname"),checkHandler:function(item, checked){if(checked){AF.func("mergeSame", "col="+item.indexName);}else{AF.func("demerge", "0\r\n"+item.indexName);}}});
						}
					}*/
				}
			}
		}

		function OnEvent(id, Event, p1, p2, p3, p4) {
			if (Event == "Load") {
				var rows = AF.func("getRows", "");
				var a = Ext.ComponentQuery.query('#southpanel')[0];
				a.getEl().setHTML("<div style='padding:0px;margin:0;color:blue;height:19px;line-height:21px;'>【Me报表引擎】为您找到相关结果共<strong>"+rows+"</strong>个</div>");
				
				for(col=AF.func("GetNextValidCol", ""); col!=""; col=AF.func("GetNextValidCol", col)) {
					var userProps = AF.func("GetColUserProps",col);
					var userProp = userProps.split(",");
					for(var i=0;i<userProp.length;i++){
						if(userProp[i]=="mergecell"){
							var mergeCellStatus = AF.func("GetColProp",col+"\r\nmergecell");
							if(mergeCellStatus=="true"){
								AF.func("mergeSame", "col="+AF.func("GetColProp",col+"\r\nname"));
							}
						}
					}
				}
			}else if(Event == "Clicked"){
				var text = AF.func("getCellData",p1+"\r\n "+p2);
				var img = AF.func("getColProp",p2+"\r\n isImage");
				if(img==1 && text.length>0){
					Ext.create('Ext.window.Window', {
					closeable : true,
					autoScroll:true,
					width : 800,
					height:600,
					constrainHeader: true,
					modal : true,
					items : [{   
						xtype: 'component',  
						autoEl: {   
  							  tag: 'img',
  							  src:text
							}   
						}]
				}).show();
				}
				
			}
		}
	</script>
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/resources/css/ext-all.css"/>
	<script type="text/javascript" src="/extjs/4.2.1/bootstrap.js"></script>
	<script type="text/javascript" src="/extjs/4.2.1/locale/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" charset="UTF-8" src="/supcan/binary/dynaload.js?87"></script>
	<script type="text/javascript" src="/app/index/${module }.js?20"></script>
  </head>
  <body>
 	<script>insertTreeList('test', '');</script>
  </body>
</html>
