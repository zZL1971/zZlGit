Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewCustWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewCustWindow',
			formId:null,
			saleFlag:null,
			queryBgType:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			autoScroll:true,
			layout:'border',
			tbar : [{
						xtype : 'button',
						text : '查询',
						id : 'queryCustInfo',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : '确定',
						id : 'confirmCustInfo'
					}],
			initComponent : function() {

				 var me = this;
				 var form = Ext.widget('form',{
					   bodyStyle : "padding-left:10px;padding-top:10px;",
		    		   region:'north',
			    	   itemId:'newCustFromWindow',
			    	   bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   autoScroll:true,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   }/*,
			    	   items : [
			    		   	{
			                    xtype:'textfield',
			                    fieldLabel: '客户编号',
			                    name: 'user_id'
			                }
						]*/
			    	   
				 });
				var custInfoStore = Ext.create("SMSWeb.store.sale.CustInfoStore");
				/*itemGrid变更子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.CustInfoStore',
						store : custInfoStore,
						itemId:'newCustGridWindow',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						autoScroll:true,
						frame:false,
						border:0,
						style:'border-top:1px solid #C0C0C0;',
						//selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:30,align:'right'},
									 {text:'客户ID',dataIndex:'cust_id',width:80,menuDisabled:true},
									 {text:'名字',dataIndex:'name',width:120,menuDisabled:true},
									 {text:'性别',dataIndex:'sex',width:80,menuDisabled:true},
									 {text:'生日',dataIndex:'birthday',width:120,menuDisabled:true},
									 {text:'电话',dataIndex:'tel',width:110,menuDisabled:true},
									 {text:'经手人',dataIndex:'jingShouRen',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
									 {text:'户型',dataIndex:'huXing',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
									 {text:'是否样板房',dataIndex:'isYangBan',width:100,menuDisabled:true},
									 {text:'是否需要安装',dataIndex:'isAnZhuang',width:100,menuDisabled:true},
									 {text:'楼层',dataIndex:'floor',width:120,menuDisabled:true},
									 {text:'安装地址',dataIndex:'address',width:120,menuDisabled:true},
						           ],
						//selType : 'cellmodel',
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
									{
									    enableKeyNav : true,
										clicksToEdit : 2
						})]
				});
				/*itemGrid变更子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				this.callParent(arguments);
			}
		});
