Ext.define("SMSWeb.view.spm.SalePricePolicyView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.SalePricePolicyView',// 面板的别名
			layout:'border',
			region:'center',
			autoScroll:true,
			border:false,
			tools:[{
				xtype:'textfield',
				name:'sale_order',
				fieldLabel:'SAP订单编号'
			},{
				xtype:'textfield',
				name:'discount_style',
				fieldLabel:'折扣类型'
			},{
				labelAlign: 'left',
				type:'search',
				itemId:'spm_search'
			}],
			initComponent:function(){
				var me = this;
				
				var rejectReCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
					name:'abgru',
					dict:'REJECT_RE'
				});
				
				//订单抬头
				var form_ = Ext.widget('form',{
					bodyPadding : '5 5 0 5',
					region:'north',
					fieldDefaults : {
						labelWidth: 80,
				        labelAlign: "left",
				        flex: 2,
				        margin: 5
					},
					items:[{//行一
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'KUNNR',
							readOnly:true,
							fieldLabel:'客户编码'
						},{
							xtype:'textfield',
							name:'NAME1',
							readOnly:true,
							fieldLabel:'客户名称'
						}]
					},{//行二
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'dictcombobox',
							name:'DISCOUNT_STYLE',
							readOnly:true,
							fieldLabel:'折扣类型',
							dict:'ORDER_TYPE'
						},{
							xtype:'dictcombobox',
							name:'ISABLE',
							readOnly:true,
							fieldLabel:'是否启用',
							dict:'ORDER_TYPE'
						}]
					},{//行三
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'START_TIME',
							readOnly:true,
							fieldLabel:'开始时间'
						},{
							xtype:'textfield',
							name:'END_TIME',
							readOnly:true,
							fieldLabel:'结束时间'
						}]
					},{//行四
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'SALE_ORDER',
							readOnly:true,
							fieldLabel:'关联订单号'
						}]
					}]
				});
				
		        me.items=[form_];
		        me.callParent(arguments);
			}
		});