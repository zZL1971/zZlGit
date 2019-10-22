Ext.define("SMSWeb.view.spm.LookSalePrModView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.lookSalePrModView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			initComponent:function(){
				var me = this;
				
				//拒绝原因下拉
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
				        labelAlign: "right",
				        flex: 1,
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
						},{
							xtype:'textfield',
							name:'vbeln',
							readOnly:true,
							fieldLabel:'SAP编号'
						}]
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
							xtype:'dictcombobox',
							name:'zzysfg',
							readOnly:true,
							fieldLabel:'支付方式',
							dict:'PAY_TYPE'
						}]
					},{//行四
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'netwr',
							readOnly:true,
							fieldLabel:'订单金额'
						},{
							xtype:'textfield',
							name:'fuFuanMoney',
							readOnly:true,
							fieldLabel:'付款金额'
						}]
					},{//
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'oNetwr',
							readOnly:true,
							fieldLabel:'原订单总额'
						},{
							xtype:'textfield',
							name:'oFuFuanMoney',
							readOnly:true,
							fieldLabel:'原付款金额'
						}]
					},{//借贷
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'loanAmount',
							readOnly:true,
							fieldLabel:'借贷金额'
						},{
							xtype:'textfield',
							name:'vgbel',
							readOnly:true,
							fieldLabel:'借贷项编号'
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
							readOnly:true,
							dict:'REJECT_RE',
							flex:1
						},{
							xtype:'textfield',
							name:'tranState',
							fieldLabel:'更改方',
							readOnly:true
						},{
							xtype:'textfield',
							name:'id',
							readOnly:true,
							hidden:true,
							fieldLabel:'id'
						}]
					},{//
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'createUser',
							readOnly:true,
							fieldLabel:'更改人'
						},{
							xtype:'textfield',
							name:'createTime',
							readOnly:true,
							fieldLabel:'更改时间'
						}]
					}]
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
		                fields: ['stateAudit','id','posnr','mabnr',"kwmeng","arktx","abgru","pr00","pr01","pr02","pr05","pr03","pr04",
		                	//"zr01",
		                	//"zr02",
		                	"zr03",
		                	//"zr04",
		                	"zr05","old","zr07"],//定义字段
		                data:[],
		                listeners:{
		                	load:function(){
		                		
		                	}
		                }
		            },
		            columns: [//配置表格列
	                     {header: "", width: 40, dataIndex:'old', sortable:true,renderer:function(val){
		                	if(val=="1")
		                		return "<font color=red>旧</font>";
		                	else if(val==""||val==null||val==undefined)
		                		return "<font color=blue>新</font>";
		                	else if(val=="3")
		                		return "<font color=red>消</font>";
		                	else if(val=="4")
		                		return "<font color=green>改</font>";
		           			}
		                },
		           		{header: "序号",xtype:'rownumberer',width:50,align:'center'},
		           		{header: "状态", width:50, dataIndex:'stateAudit'},
		           		{header: "ID", width:50, dataIndex:'id',hidden:true},
		                {header: "行项", width:50, dataIndex:'posnr', sortable: true,align:'center'},
		                {header: "物料编码", width: 100, dataIndex:'mabnr'},
		                {header: "物料描述", width: 150, dataIndex:'arktx'},
		                {header: "数量", width: 50, dataIndex:'kwmeng'},
		                {header: "拒绝原因", width: 120, dataIndex:'abgru',renderer: function(value,metadata,record){  
			 				var rejectReStore = rejectReCombobox.getStore();
			 				var find = rejectReStore.findRecord('id',value); 
			 				if(find){
	 	            			return find.get('text');
	 	            		}
			                return value;  
		                }},
		                {header: "折后金额", width: 80, dataIndex:'pr00'},
		                {header: "商品原价", width: 80, dataIndex:'pr01'},
		                {header: "赠送(活动)", width: 90, dataIndex:'pr02'},
		                {header: "免费产品", width: 70, dataIndex:'pr05'},
		                {header: "产品折扣", width: 70, dataIndex:'pr03'},
		                {header: "活动折扣", width: 70, dataIndex:'pr04'},
		                //{header: "运输费(含税)", width: 90, dataIndex:'zr01'},
		                //{header: "返修费(含税)", width: 90, dataIndex:'zr02'},
		                {header: "安装服务费(含税)", width: 120, dataIndex:'zr03'},
		                //{header: "设计费(含税)", width: 100, dataIndex:'zr04'},
		                {header: "订单变更管理费(含税)", width: 140, dataIndex:'zr05'},
		                {header: "客服支持",width:100,dataIndex:'zr06'},
		                {header: "订单价格调整(含税)",width:100,dataIndex:'zr07'},
		            ],
		            listeners:{
		            	afterrender:function(ths){
		            		console.log(ths.getStore());
		            		
		            		 
		            	}
		            }
		        });
		   
		        me.items=[form_,grid_];
		        
		        grid_.getView().addRowCls(0,'x-grid-record-red');
		        me.callParent(arguments);
			}
		});