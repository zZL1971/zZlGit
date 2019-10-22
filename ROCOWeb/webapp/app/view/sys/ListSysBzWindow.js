Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sys.ListSysBzWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.ListSysBzWindow',
			formId:null,
			maximizable:true,
			height : 400,
			width : document.body.clientWidth * 0.5,
			modal:true,
			autoScroll:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			initComponent : function() {
				var me = this;
				var sysBzStore = Ext.create("SMSWeb.store.sys.SysBzStore");
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.SysBzGridView',
						store : sysBzStore,
						itemId:'sysBzGrid',
						enableKeyNav : true,
						columnLines : true,
//						title: '产品明细',
				        border:false,
						region:'center',
						columns : [
									 {xtype:'rownumberer',width:30,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'流向',dataIndex:'text',width:100,menuDisabled:true},
									 {text:'备注',dataIndex:'remark',width:300,menuDisabled:true},
									 {text:'操作人',dataIndex:'createUser',width:100,menuDisabled:true},
									 {text:'操作时间',dataIndex:'createTime',width:200,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d H:i:s'}
						           ]
				});
				
				//生成页面
				Ext.apply(this, {
					items : [itemGrid]
				});
				
				this.callParent(arguments);
			}
		});
