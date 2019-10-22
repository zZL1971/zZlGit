Ext.define('SMSWeb.view.report.ReportView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.reportView',
	layout : 'border',
	requires : ['Ext.ux.form.TableComboBox', 'Ext.ux.form.SearchForm'],
	tbar : [{
		xtype : 'button',
		text : 'Query',
		id : 'queryReport',
		tooltip : 'Query',
		iconCls : 'table_search'
	}/*,'-',{
		text:'Merge Cells',
		itemId:'btn__mergecell',
		menu:[]
	}*/,"-",{
		xtype : 'button',
		text : 'Merge Cell',
		id : 'mergecell',
		tooltip : 'MergeCell',
		iconCls : 'table_mergecell'
	},{
		xtype : 'button',
		text : 'Demerge',
		id : 'demerge',
		tooltip : 'DeMerge',
		iconCls : 'table_demerge'
	}],
	initComponent:function(){
		var result;
		Ext.Ajax.request({
			url:"/core/report/s/"+reportNo,
			async:false,
			success:function(response,opts){
				result = Ext.decode(response.responseText);
			},
			failure:function(response,opts){
				Ext.Msg.alert("错误代码:"+response.status,response.responseText);
			}
		});
			
		var north = {
			region : 'north',
			itemId:'reportFormView',
			xtype : 'searchform',
			border : false,
			bodyStyle : "background-color: #D0DEF0;padding:5px;",
			fieldDefaults : {
				labelWidth : 100,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;'
			},
			items : result
		};
		this.items=[north, {
			region : 'center',
			//contentEl :'treelist'
			html:insertTreeListForExtjs('AF', 'Border=none;Hue=Lilian;')
			//html:'sadf'
		}, {
			region : 'south',
			border : false,
			xtype : 'panel',
			itemId:'southpanel',
			minHeight:21,
			bodyStyle : {
				padding : '2px',
				margin : 0
			},
			html : '没有记录'
		}];
		this.callParent(arguments);
	}
});