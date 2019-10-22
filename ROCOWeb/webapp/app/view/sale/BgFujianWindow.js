//附件界面
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.BgFujianWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.BgFujianWindow',
			sourceShow:null,//显示来源
			saleHeadId:null,//销售单主表id
			saleItemId:null,//销售行id
			flowInfo:null,//审核信息
			//loadStatus从哪个页面进入
			// 2：我的物品 显示非标产品 ,3:订单
			loadStatus:null,
			formId:null,
			matnr:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth *  0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			type:null,//OR3,OR4
			tbar : [],
			initComponent : function() {
//				alert("BJ"+this.loadStatus);
				var me = this;
				var _formId = me.formId;
				var _loadStatus = me.loadStatus;
				
				//saleHeadId获取订单审核信息
				if(me.saleHeadId!=null && me.saleHeadId.length>0){
					me.flowInfo = Ext.ux.DataFactory.getFlowActivityId(me.saleHeadId);
				}
				
				var tabpanel;
				var fileGrid;
				fileGrid = Ext.widget('grid',{
					itemId:'BJ_gridItem',
					title: '附件',
					border:false,
					viewConfig:{
					    enableTextSelection:true
					},
					   store : Ext.create("SMSWeb.store.sale.Store4BGMaterialBaseFile"),
				       selModel:{selType:'checkboxmodel',injectCheckbox:0},
				       columns : [
						          {text:'id',dataIndex:'id',width:0,hidden:true},
				                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer'},
				                  {text:'文件名称',dataIndex:'uploadFileNameOld',width:200,sortable: false,menuDisabled:true},
				                  {text:'备注',dataIndex:'remark',width:200,sortable: false,menuDisabled:true},
				                  {text:'上传人',dataIndex:'createUser',width:80,sortable: false,menuDisabled:true},
				                  {text:'上传日期',dataIndex:'createTime',width:140,sortable: false,menuDisabled:true},
				                  {text:'是否有效',dataIndex:'statusdesc',width:80,sortable: false,menuDisabled:true},
				                  {text:'文件下载',width:100,xtype:'actioncolumn',align:'center',icon:'/resources/images/down.png',
				                	  handler : function(grid,rowIndex,colIndex) {
											this.up('window').fireEvent('fileDownloadButtonClick',this.up('grid'),rowIndex,colIndex);
										},sortable: false,menuDisabled:true
				                  }
				                  ]
				});
				//add by Mark on 2016-04-12--end
				var filesTabpanel;
				filesTabpanel = Ext.widget('tabpanel',{
					title: '文件信息',
					itemId: 'filesTabpanel_ItemId',
					items:[fileGrid]
				});
				
				if('2'==_loadStatus){
					tabpanel = Ext.widget('tabpanel',{
						border : false,
						region:'center',
						itemId: 'tabpanel_ItemId',
						items:[fileGrid]
					});
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							region:'center',
							border : false,
							itemId: 'tabpanel_ItemId',
							items:[headForm,filesTabpanel,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							region:'center',
							border : false,
							itemId: 'tabpanel_ItemId',
							items:[headForm,filesTabpanel]
						});
						priceFileGrid.tab.hide();
					}
				}
				
				Ext.apply(me, {
					items : [tabpanel]
				});
				
				//加载数据
				if(_formId!=null){
					fileGrid.getStore().load({params:{'fileType':'BJ','pid':_formId}});
				}else{}
				me.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me = this;
				},
				close:function(){
				}
			}
			,
		});
