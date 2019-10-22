Ext.define("SMSWeb.view.main.MainView", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.MainView',// 面板的别名
	layout : 'border',
	border : false,
	scrollable:true,
	/*
	 * defaults: { split: true, collapsible: true },
	 */
	items : [ {
		region : 'center',
		xtype : 'tabpanel',
		bodyBorder : false,
		border : false,
		layout:'fit',
		items : [ {
			title : '我的工作台',
			layout:'fit',
			autoScroll : true,
			items:[{xtype:'WorkSpace',layout:'vbox'}]
		}]
	}, {
		region : 'west',
		border : false,
		split : true,
		layout : 'fit',
		width : 220,
		items : [ {
			title : '导航菜单',
			border : false,
			xtype : 'TreeView'
		} ]
	}, {
		region : 'north',
		xtype : 'maintop'
	} /*
		 * ,{ region: 'south', xtype:'toolbar', alias : 'widget.mainbottom',
		 * items:[{ xtype:'label', text:USER_AGENT
		 *  }] }
		 *//*
						 * { xtype:'tabpanel', region:'center', items:[{ title :
						 * '我的工作台', xtype : 'panel', layout : 'anchor', items : [{
						 * anchor : '20% 100%', layout:'border', maxWidth:300,
						 * bodyStyle :"border:none;border-right: 1px solid
						 * #99bce8;", items:[{ region:'center', xtype : 'panel',
						 * layout : 'accordion', border:false, items : [{
						 * border:false, title : '导航菜单', xtype : 'TreeView' }]
						 * },{ xtype : 'panel', region:'south', height:180,
						 * bodyStyle :"padding :5 10 5
						 * 10;margin:0;border:none;border-top: 1px solid
						 * #99bce8;", html : '<div><p></p><center><img
						 * src="resources/logo/roco.gif" width="90%"/></center>' + '<div
						 * style="padding:10px;"><font color=#ccc>System：</font><font
						 * color=blue>'+Sys.system+'</font><br/>' + '<font
						 * color=#ccc>Browser：</font><font
						 * color=blue>'+Sys.browser+'</font><br/>'+ '<font
						 * color=#ccc>Login User：</font><font
						 * color=blue>'+CURR_USER_LOGIN_NO+'</font></div></div>' }] }] }] } , {
						 * region:'south', border:false, xtype : 'panel',
						 * bodyStyle : { padding : '2px', margin:0 }, html : '<center>©
						 * 2014 Information Consulting Co., Ltd. Shenzhen SPRO</center>' }
						 */]
});