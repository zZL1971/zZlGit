//标准产品查询
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.myGoods.MyGoodsWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MyGoodsWindow',
			sourceShow:null,//显示来源
			matnr:null,
			formId:null,
			maximizable:true,
			height : 550,
			isBg:null,
			bgOrderType:null,
			width : document.body.clientWidth * 0.9,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			ortype:null,
			items:[
					{
						tbar : [
						     {
								xtype : 'button',
								text : '查询',
								itemId : 'query',
								iconCls:'table_search'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '确定',
						    	 itemId : 'comfirm',
						    	 icon:'resources/images/tick.png'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '新增散件包',
						    	 itemId : 'addMMSJ',
						    	 hidden:true,
						    	 icon:'resources/images/add.png'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '删除',
						    	 itemId : 'deleteMMSJ',
						    	 hidden:true,
						    	 icon:'resources/images/delete.png'
						     }
						    ]
						,
					    region:'north',
					    itemId:'MyGoodsMainFormView_itemId',
						xtype:'MyGoodsMainFormView'
					 },
					{
						xtype:'MyGoodsMainGridView',
						itemId:'MyGoodsMainGridView_itemId',
						region:'center'
					}
			      ]
			,
			listeners: {
				show : function(){
					var me = this;
					var isBg = me.isBg;
					var form = me.queryById('MyGoodsMainFormView_itemId');
	     			var formValues={};
	     			var _kunnr = me.kunnr;
	     			var _bgOrderType = me.bgOrderType;
					var ortype = form.getForm().findField("ortype");
					ortype.setValue(me.ortype);
				    ortype.hide();
				    var matnr = form.getForm().findField("matnr");
				    matnr.setValue(me.matnr);
				    matnr.hide();
				    
					formValues = form.getValues();
					me.down('grid').getStore().loadPage(1,{params:{kunnr:_kunnr,bgOrderType:_bgOrderType}});
					if(me.sourceShow=="newSaleContentWindow"){//新下单界面
						me.queryById('addMMSJ').show();
						me.queryById('deleteMMSJ').show();
					}else{
						me.queryById('MyGoodsMainGridView_itemId').down('actioncolumn').hide();
					}
					if(1==isBg){//补购下单
						me.queryById('addMMSJ').show();
						me.queryById('deleteMMSJ').show();
					}
				}
			}
		}
		
	);
