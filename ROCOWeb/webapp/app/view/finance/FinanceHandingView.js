Ext.define('SMSWeb.view.finance.FinanceHandingView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.financehandingview',
	layout : 'border',
	border : false,
	requires : ['Ext.ux.IFrame','Ext.ux.form.SearchForm',"Ext.ux.form.TableComboBox","Ext.ux.form.CustomCombobox","Ext.ux.form.DictCombobox","Ext.ux.form.TrieCombobox"],
	initComponent:function(){
		var me = this;
		
		var columns = [	
		    {xtype:'rownumberer',width:30,align:'right'},
		    { text: '订单ID', dataIndex: 'shId', width: 75, align: 'left',hidden:true}, 
			{ dataIndex:'bukrs', width:100, align:'left', text:'公司代码'}	,
		    { dataIndex:'kunnr', width:120,align:'center', text:'售达方(客户)'},
		    { dataIndex:'zuonr', width:130, align:'left', text:'订单号' },
		    { dataIndex:'netwr', width:100, align:'center',  text:'订单总额' },
		    { dataIndex:'jobStatusName', width:100, align:'center', text:'处理状态', filterable:true},
		    { dataIndex:'msg', width:200, align:'left', text:'处理信息' },
			{ dataIndex:'sourceTypeName', width:100, align:'center', text:'过账类型' },
			{ dataIndex:'createTime', xtype:'datecolumn', width:150, align:'center', text:'新增时间', format:'Y-m-d H:i:s' },
			{ dataIndex:'createUser',  width:100, align:'center', text:'创建人'},
        	{ dataIndex:'beginDate', width:150, xtype:'datecolumn', align:'left', text:'开始时间',  format:'Y-m-d H:i:s' },
			{ dataIndex:'endDate', width:150, xtype:'datecolumn', text:'结束时间',  format:'Y-m-d H:i:s' },
			{ dataIndex:'num', width:80, align:'left', text:'执行次数' },
			{ dataIndex:'mark', width:200, align:'left', text:'备注' },
			{ dataIndex:'batch', width:80, align:'left',text:'批次'}
			
		];
	
		var fields = [
		    {name:'shId'},
			{name:'bukrs'},
			{name:'kunnr'},
			{name:'zuonr'},
			{name:'netwr'},
			{name:'jobStatusName'},
			{name:'msg'},
			{name:'sourceTypeName'},
			{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
			{name:'createUser'},
			{name:'beginDate',type:'date',dateFormat:'Y-m-d H:i:s'},
			{name:'endDate',type:'date',dateFormat:'Y-m-d H:i:s'},
			{name:'num'},
			{name:'mark'},
			{name:'batch'}
		];
		
		var dataUrl = "/main/sale/financeHanding/datalist";
		//加载数据
		var store = Ext.create("Ext.data.Store",{
			fields:fields,
			proxy:{
				type:'ajax',
				url:dataUrl,
		        headers:{
					"Content-Type":"application/json; charset=utf-8"        
		        },
				reader:{
					type:'json',
					root:'content',
					idProperty:'uuid',
					totalProperty :'totalElements'
				},
				listeners:{  
			        exception:Ext.ux.DataFactory.exception
			    }
			},
			autoLoad:true
		});
		var sm = Ext.create('Ext.selection.CheckboxModel');
		var grid = Ext.widget('grid',{
			xtype:'grid',
			id:'finance_grid_id',
			loadMask: true,
			plugins: 'bufferedrenderer',
			enableKeyNav : true,
			selModel: sm,
			columnLines : true,
			border : false,
			style:'border-top:1px solid #C0C0C0;',
			viewConfig:{
			    enableTextSelection:true //可以复制单元格文字
			},
			store:store,
			region : 'center',
			columns:columns,
			dockedItems:[{
				xtype:'pagingtoolbar',
				store:store,
				dock:'bottom',
				displayInfo:true,
				displayMsg:"显示 {0} -{1}条，共{2} 条",
				border:false,
				items:['-','每页',{
					xtype:'combobox',
					editable : false,
					width:55,
					listeners:{
						 'render':function(comboBox){ 
						 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
						 	comboBox.setValue(this.store.pageSize);
						 },
					  	 'select':function(comboBox){ 
					  	 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
					  	 	grid.getStore().pageSize = comboBox.getValue();
					  	 	grid.getStore().load({params:{start:0,limit:comboBox.getValue()}});
					  	 }
					},
					store:Ext.create('Ext.data.Store',{
				        fields:['id','name'],
				        data:
				        [
				            {'id':25,'name':25},
				            {'id':50,'name':50},
				            {'id':100,'name':100},
				            {'id':200,'name':200},
				            {'id':500,'name':500}
				        ]
				    }),
				    displayField:'name',
				    valueField:'id'
				},'条']
			}],
		});
		
		var searchForm = Ext.widget('searchform',{
			//xtype:'searchform',
			region : 'north',
			items:[{
				flex:'1',
				layout:'hbox',
				xtype:'container',
				items:[{
					fieldLabel:'订单号',
					name:'zuonr',
					xtype:'textfield'
				},{
					fieldLabel:'售达方(客户)',
					name:'kunnr',
					xtype:'textfield'
				},{
					xtype:'dictcombobox',
					name:'jobStatus',
					fieldLabel:'处理状态',
//					multiSelect:true,
					dict:'JOB_POOL_STA'
				}]
			},{
				flex:'1',
				layout:'hbox',
				xtype:'container',
				items:[{
					fieldLabel:'创建人',
					name:'createUser',
					xtype:'textfield'
				},{
					fieldLabel:'新增日期[S]',
					name:'createTime',
					xtype:'datefield',
					format:'Y-m-d'
				},{
					fieldLabel:'新增日期[E]',
					name:'createTime',
					xtype:'datefield',
					format:'Y-m-d'
				}]
			}]
		});
		
		
		me.tbar =[{
			text:'查询',
			handler:function(){
				grid.getStore().load({params:searchForm.getSearchs()});
			}
		},{
			text:'释放订单',
			handler:function(){
				if(grid.getSelectionModel().getSelection().length>0){
					var saleHeadIds;
				
	        		var ses=grid.getSelectionModel().getSelection();
	        	
	        		for(var i=0;i<ses.length;i++){
	        			var id=ses[i].data.shId;
	        			if(saleHeadIds){
	        				saleHeadIds=saleHeadIds+":"+id;
	        			}else{
	        				saleHeadIds=id;
	        			}
	        		}
	        		console.log(saleHeadIds);
	        		Ext.MessageBox.confirm('提示信息',"确定要释放所选订单？",function(btn){
				        if(btn=='yes'){
		        			Ext.Ajax.request({
			        			url : '/main/sale/batch/validateTranSap/'+saleHeadIds,
								async : false,
								method : 'GET',
								dataType : "json",
								contentType : "application/json",
								callback : function(options, success, response) {
									var jsonResult = Ext.decode(response.responseText);
									if(!jsonResult.success){
										Ext.Msg.show({
											title:"错误提示["+jsonResult.errorCode+"]:",
											icon:Ext.Msg.ERROR,
											msg:jsonResult.errorMsg,
											buttons:Ext.Msg.OK
										});
									}else{
										Ext.Msg.alert("温馨提示", '执行成功');
									}
								},
								success : function(response, opts) {
									var values = Ext.decode(response.responseText);
									console.log(values);	
								},
								failure : function(response, opts) {
									var values = Ext.decode(response.responseText);
									console.log(values);
									Ext.Msg.alert("can't", 'error');
								}
							});
				        }else{
				        	console.log("取消");
				        }
				    });

				}else{
					Ext.Msg.alert('提示', '至少选中一条记录');
					return false;
				}	
			}
		}];
		
		me.items=[searchForm,grid];
		
		me.callParent(arguments);
	},
});
