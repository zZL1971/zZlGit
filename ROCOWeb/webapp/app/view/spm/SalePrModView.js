Ext.define("SMSWeb.view.spm.SalePrModView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.salePrModView',// 面板的别名
			layout:'border',
			region:'center',
			autoScroll:true,
			border:false,
			tools:[{
				xtype:'textfield',
				name:'bstkd',
				fieldLabel:'订单编号'
			},/*{
				xtype:'textfield',
				name:'vbeln',
				fieldLabel:'SAP编号'
			},*/{
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
							name:'bstkd',
							readOnly:true,
							fieldLabel:'订单编号'
						}/*,{
							xtype:'textfield',
							name:'vbeln',
							readOnly:true,
							fieldLabel:'SAP编号'
						}*/]
					},{//行二
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'dictcombobox',
							name:'auart',
							readOnly:true,
							fieldLabel:'订单类型',
							dict:'ORDER_TYPE'
						},{
							xtype:'textfield',
							name:'bstdk',
							readOnly:true,
							fieldLabel:'订单日期'
						}]
					},{//行三
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'dictcombobox',
							name:'zterm',
							readOnly:true,
							fieldLabel:'付款条件',
							dict:'FU_FUAN_COND'
						},{
							xtype:'textfield',
							name:'netwr',
							readOnly:true,
							fieldLabel:'订单金额'
						}]
					},{//行四
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'dictcombobox',
							name:'zzysfg',
							readOnly:true,
							fieldLabel:'支付方式',
							dict:'PAY_TYPE'
						},{
							xtype:'textfield',
							name:'fuFuanMoney',
							readOnly:true,
							fieldLabel:'付款金额'
						}]
					},{//借贷
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'vgbel',
							readOnly:true,
							fieldLabel:'借贷项编号'
						},{
							xtype:'textfield',
							name:'loanAmount',
							readOnly:true,
							fieldLabel:'借贷金额'
						}]
					},{//行五
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'kunnr',
							readOnly:true,
							fieldLabel:'售达方'
						},{
							xtype:'textfield',
							name:'name1',
							readOnly:true,
							fieldLabel:'售达方名称'
						}]
					},{//行六
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'zzname',
							readOnly:true,
							fieldLabel:'客户名称'
						},{
							xtype:'textfield',
							name:'zzphon',
							readOnly:true,
							fieldLabel:'联系方式'
						}]
					},{//行七隐藏
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'dictcombobox',
							name:'abgru',
							fieldLabel:'拒绝原因',
							dict:'REJECT_RE',
							flex:1
						},{
							xtype:'textfield',
							name:'oNetwr',
							readOnly:true,
							hidden:true,
							fieldLabel:'原来的订单总额'
						},{
							xtype:'textfield',
							name:'oFuFuanMoney',
							readOnly:true,
							hidden:true,
							fieldLabel:'原来的付款金额'
						},{
							xtype:'textfield',
							name:'id',
							readOnly:true,
							hidden:true,
							fieldLabel:'id'
						}]
					}]
				});
				var status=Ext.create('Ext.ux.form.DictCombobox',{
					dict:'MATERIAL_STATE_AUDIT'
				});
				//订单明细
				var grid_ = Ext.create('Ext.grid.Panel',{
		            border:false,
		            region:true,
		            region:'center',
		            plugins:[  
		                     Ext.create('Ext.grid.plugin.CellEditing',{  
		                         clicksToEdit:1 //设置单击单元格编辑  
		                     })  
		            ],
		            viewConfig: {
		                forceFit : true,
		                enableTextSelection:true,
		                stripeRows: true,//在表格中显示斑马线
		                getRowClass: function(record) { 
		                    return record.get('old') == 1 ? 'boy-row' : 'gril-row';
		                } 
		            },
		            store: {//配置数据源
		                fields: [{name:'id'},
		                         {name:'stateAudit'},
		                         {name:'posnr'},
		                         {name:'mabnr'},
		                         {name:'kwmeng'},
		                         {name:'arktx'},
		                         {name:'abgru'},
		                         {name:'oldAbgru'},
		                         {name:'pr00',type:'float'},
		                         {name:'pr01',type:'float'},
		                         {name:'pr02',type:'float'},
		                         {name:'pr05',type:'float'},
		                         {name:'pr03',type:'float'},
		                         {name:'pr04',type:'float'},
		                         //{name:'zr01',type:'float'},
		                         //{name:'zr02',type:'float'},
		                         {name:'zr03',type:'float'},
		                         //{name:'zr04',type:'float'},
		                         {name:'zr06',type:'float'},
		                         {name:'zr07',type:'float'},
		                         {name:'zr05',type:'float'}],//定义字段
		                data:[],
		                sorters:[{property:"posnr",direction:"ASC"}] //给定一个默认的排序方式 
		            },
		            columns: [//配置表格列
		           		{xtype:'rownumberer',width:30,align:'right'},
		           		{header: "ID", width:50, dataIndex:'id', sortable: true,hidden:true},
		           		{header: "状态", width:90, dataIndex:'stateAudit',renderer:function(value,metadata,record){
		           			var find=status.getStore().findRecord('id',value,0,false,true,true);
		           			if(find){
		           				return find.get('text');
		           			}
		           			return value;
		           		}},
		                {header: "行项", width:60, dataIndex:'posnr', sortable: true},
		                {header: "物料编码", width: 100, dataIndex:'mabnr'},
		                {header: "物料描述", width: 150, dataIndex:'arktx'},
		                {header: "数量", width: 50, dataIndex:'kwmeng'},
		                {header: "拒绝原因", width: 120, dataIndex:'abgru',editor: rejectReCombobox,
				        	 renderer: function(value,metadata,record){  
					 				var rejectReStore = rejectReCombobox.getStore();
					 				var find = rejectReStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;  
				        }},
				        {header: "原来拒绝原因", width:50, dataIndex:'oldAbgru',hidden:true},
		                {header: "折后金额", width: 80, dataIndex:'pr00', sortable: true},
		                {header: '<font color="#2522C9">商品原价</font>', width: 80, dataIndex:'pr01',editor: {
		                	 xtype: 'numberfield',
		                     minValue: 0,
		                     allowBlank: false
		                }},
		                {header: '<font color="#2522C9">赠送(活动)</font>', width: 90, dataIndex:'pr02', editor: {
		                	 xtype: 'numberfield',
		                     minValue: 0,
		                     allowBlank: false
		                }},
		                {header: '<font color="#2522C9">免费产品</font>', width: 73, dataIndex:'pr05',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},
		                {header: '<font color="#2522C9">产品折扣</font>', width: 73, dataIndex:'pr03',editor: {
		                	 xtype: 'numberfield',
		                     minValue: 0,
		                     maxValue: 1,
		                    allowBlank: false
		                }},
		                {header: '<font color="#2522C9">活动折扣</font>', width: 73, dataIndex:'pr04',editor: {
		                	 xtype: 'numberfield',
		                     minValue: 0,
		                     maxValue: 1,
		                    allowBlank: false
		                }},
		                {header: '<font color="#2522C9">订单价格调整(含税)</font>', width: 140, dataIndex:'zr07',editor: {
		                	 xtype: 'numberfield',
		                    allowBlank: false
		                }},
		               /* {header: '<font color="#2522C9">运输费(含税)</font>', width: 95,hidden:true, dataIndex:'zr01',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},*/
		                /*{header: '<font color="#2522C9">返修费(含税)</font>', width: 95,hidden:true, dataIndex:'zr02',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},*/
		                {header: '<font color="#2522C9">安装服务费(含税)</font>', width: 125, dataIndex:'zr03',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},
		                /*{header: '<font color="#2522C9">设计费(含税)</font>',hidden:true, width: 100, dataIndex:'zr04',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},*/
		                {header: '<font color="#2522C9">订单变更管理费(含税)</font>', width: 140, dataIndex:'zr05',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }},
		                {header: '<font color="#2522C9">客服支持</font>', width: 90, dataIndex:'zr06',editor: {
		                	xtype: 'numberfield',
		                    minValue: 0,
		                    allowBlank: false
		                }}
		            ]
		        });
		   
		        me.items=[form_,grid_];
		        me.callParent(arguments);
			}
		});