Ext.define('SMSWeb.view.finance.FinanceView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.financeview',
	layout : 'border',
	border : false,
	requires : ['Ext.ux.IFrame','Ext.ux.form.SearchForm',"Ext.ux.form.TableComboBox","Ext.ux.form.CustomCombobox","Ext.ux.form.DictCombobox","Ext.ux.form.TrieCombobox"],
	initComponent:function(){
		var me = this;
		
		var columns = [	
		    {xtype:'rownumberer',width:30,align:'right'},
		    { xtype:'actioncolumn',text:'审批',width:40,align: 'center',items:[{
				icon: '/resources/images/remarks1.png',
				tooltip:"快速审批",
                handler: function(grid, rowIndex, colIndex) {
                	var datas=grid.getStore().getRange(rowIndex);
                	var ids="";
					var taskIds="";
					var assignees="";
					//console.info(datas[0].data);
					for(var i=0;i<datas.length;i++){
						if(ids==""){
							ids=datas[i].data.id;
							taskIds=datas[i].data.taskId;
							var assignee=datas[i].data.assignee;
							if(!assignee){
								assignees="X";
							}
							
						}else{
							ids=ids+","+datas[i].data.id;
							var tId=datas[i].data.taskId;
							var assignee=datas[i].data.assignee;
							if(!assignee){
								assignee="X";
							}
							taskIds=taskIds+","+tId;
							assignees=assignees+","+assignee;
						}
						
					}
					var tsWin = Ext.widget('taskApprove',{uuidVal:ids,v_taskIds:taskIds,v_assignees:assignees});
					tsWin.show();
                }
			}]},
		    { text: 'ID', dataIndex: 'id', width: 75, align: 'left',hidden:true}, 
		    { text: '下载', dataIndex: 'fcount', width: 50, align: 'center',renderer: function(value,metadata,record){  
 			 	if(value){
 			 		var url=basePath+"main/sysFile/fileDownload?id="+value;
 			 		var str='<a href="javascript:void(0)" onClick="SMSWeb_controller_finance_FinanceController_fileDownloadByOrderId(\''+record.data.id+'\''+',\''+record.data.orderCode+'\''+',\''+record.data.shouDaFang+'\')">下载</a>';
 			 		return str;
 			 	}
                return "";  
			}},
			{ text: '下载次数', dataIndex: 'loadTime', width: 75, align: 'right' }	,
		    { text: '订单编号', dataIndex: 'orderCode', width: 130, align: 'left' },
		    { text: '推单状态(SAP	)', dataIndex: 'sapStatus', width: 120, align: 'left'},
		    { text: '过账状态', dataIndex: 'billName', width: 100, align: 'left' },
		    { text: '客户编号', dataIndex: 'shouDaFang', align: 'left', width: 80,renderer : function(v) { 
		    	return "<a href=\"javascript:SMSWeb_controller_finance_FinanceController_showCustWin('" + v + "')\">" + v + "</a>"; 
		    } },
		    {text:'错误信息',dataIndex:'errorMsg',align:'left',width:300},
		    { text: '客户名称', dataIndex: 'name1', align: 'left', minWidth: 90 },
		    { text: '地区', groupable:false, dataIndex: 'regio', align: 'center', width: 70 ,editor:Ext.create('Ext.ux.form.DictCombobox',{
        		dict:'REGIO'}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
        	{text:'大区',dataIndex:'bzirk',align:'right',width:90,editor:Ext.create('Ext.ux.form.DictCombobox',{
        		dict:'BZIRK'}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
		    { text: '订单类型', groupable:false, dataIndex: 'orderType', align: 'left', minWidth: 80 ,editor:Ext.create('Ext.ux.form.DictCombobox',{
        		dict:'ORDER_TYPE'}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
			{ text: '订单日期',dataIndex: 'orderDate', align: 'center',xtype: 'datecolumn',format:'Y-m-d'},
			{ text: '订单总额', dataIndex: 'orderTotal', align: 'right', minWidth: 80,
				renderer: function(value,metadata,record){
					var val=value.toFixed(2);
					return val;
				}},
			{ text: '付款条件', groupable:false, dataIndex: 'fuFuanCond', align: 'center', minWidth: 80 ,editor:Ext.create('Ext.ux.form.DictCombobox',{
        		dict:'FU_FUAN_COND'}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
        	{ text: '付款总额', dataIndex: 'fuFuanMoney', align: 'right', minWidth: 80,
				renderer: function(value,metadata,record){
					var val=value.toFixed(2);
					return val;
				}},
        	{ text: '处理时效', groupable:false, dataIndex: 'handleTime', align: 'center', width: 80 ,editor:Ext.create('Ext.ux.form.DictCombobox',{
        		dict:'HANDLE_TIME'}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
			{ text: '审批用户组', dataIndex: 'groupId', align: 'left', minWidth: 80, hidden:true},
			{ text: '审批用户', dataIndex: 'assignee', width: 75, align: 'center' },
			{ text: '任务编号', dataIndex: 'taskId', width: 75, align: 'left' },
			{ text: '领取时间',dataIndex: 'claimTime',width: 150, align: 'center',xtype: 'datecolumn',format:'Y-m-d H:i:s'}
			
		];
	
		var fields = [
		    {name:'id'},
			{name:'orderCode'},
			{name:'sapStatus'},
			{name:'billName'},
			{name:'errorMsg'},
			{name:'shouDaFang'},
			{name:'name1'},
			{name:'regio'},
			{name:'bzirk'},
			{name:'orderType'},
			{name:'orderDate',type:'date',dateFormat:'Y-m-d H:i:s'},
			{name:'orderTotal'},
			{name:'fuFuanCond'},
			{name:'fuFuanMoney'},
			{name:'handleTime'},
			{name:'groupId'},
			{name:'assignee'},
			{name:'taskId'},
			{name: 'claimTime',type:'date',dateFormat:'Y-m-d H:i:s'},
			{name:'fcount'},
			{name:'loadTime'}
		];
		
		var dataUrl = "/core/bpm/finance/datalist";
		
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
					fieldLabel:'订单编号',
					name:'orderCode',
					xtype:'textfield'
				},{
					xtype:'dictcombobox',
					name:'orderType',
					fieldLabel:'订单类型',
					dict:'ORDER_TYPE'
				},{
					xtype:'dictcombobox',
					name:'regio',
					fieldLabel:'地区',
//					multiSelect:true,
					dict:'REGIO'
				},{
					xtype:'dictcombobox',
					name:'bzirk',
					fieldLabel:'大区',
					dict:'BZIRK'
				}]
			},{
				flex:'1',
				layout:'hbox',
				xtype:'container',
				items:[{
					fieldLabel:'客户编码',
					name:'shouDaFang',
					xtype:'textfield'
				},{
					fieldLabel:'客户名称',
					name:'name1',
					xtype:'textfield'
				},{
					xtype:'dictcombobox',
					name:'handleTime',
					fieldLabel:'处理时效',
					dict:'HANDLE_TIME'
				}]
			}]
		});
		
		
		me.tbar =[{
			text:'查询',
			handler:function(){
				grid.getStore().load({params:searchForm.getSearchs()});
			}
		},{
			text:'批量下载',
			handler:function(){
				var fgrid=Ext.getCmp("finance_grid_id");
				if(fgrid.getSelectionModel().getSelection().length>0){
					var ids=new Array();
					var warnStr="";
	        		var ses=fgrid.getSelectionModel().getSelection();
	        		var j=0;
	        		for(var i=0;i<ses.length;i++){
	        			var id=ses[i].data.id;
	        			var orderCode=ses[i].data.orderCode;
	        			var shouDaFang=ses[i].data.shouDaFang;
	        			var fcount=ses[i].data.fcount;
	        			if(fcount==0||fcount==null||fcount=='0'){
	        				warnStr=warnStr+orderCode+"<br/>";
	        				continue;
	        			}else{
	        				var param=id+":"+shouDaFang+"_"+orderCode;
		        			ids[j]=param;
		        			j++;
	        			}
	        			
	        		}
	        		if(warnStr!=""){
						warnStr="您选中的：<br/>"+warnStr+"没有文件可下载，将会被忽略，其他的将会正常！你确定要下载？"
					}else{
						warnStr='确定要下载所选订单文件？';
					}
					Ext.MessageBox.confirm('提示信息',warnStr,function(btn){
						        if(btn=='yes'){
						        	if(ids.length<1){
					        			Ext.Msg.alert("提示信息","没有文件可下载");		
					        		}else{
					        			window.location.href = basePath+"main/sysFile/fileloadBatch/"+ids;
					        			fgrid.getSelectionModel().clearSelections();
					        			fgrid.getView().refresh();
					        			setTimeout(function(){
					        				fgrid.getStore().load({
											params : searchForm.getSearchs()
										});},1500);
					        			
					        		}
						        }else{
						        	console.log("取消");
						        }
						    });

				}else{
					Ext.Msg.alert('提示', '至少选中一条记录');
					return false;
				}	
			}
		},{
			text:'批量审批',
			handler:function(){
				var fgrid=Ext.getCmp("finance_grid_id");
				if(fgrid.getSelectionModel().getSelection().length>0){
					var mappingIds=new Array();
	        		var mappingNos=new Array();
	        		var currentflows=new Array();
	        		var nextflows=new Array();
	        		var ses=fgrid.getSelectionModel().getSelection();
	        		var warnStr="";
	        		var str="";
	        		var j=0;
	        		for(var i=0;i<ses.length;i++){
	        			var sapStatus=ses[i].data.sapStatus;
	        			var orderCode=ses[i].data.orderCode;
	        			var id=ses[i].data.id;
	        			var taskId=ses[i].data.taskId;
	        			var assignee=ses[i].data.assignee;
	        			var orderType=ses[i].data.orderType;
	        			var fcount=ses[i].data.fcount;
	        			var loadTime=ses[i].data.loadTime;
//	        			console.log(ses[i].data);
//	        			debugger;
	        			if(sapStatus==null||sapStatus==undefined||sapStatus=="推单失败"){//如果SAP号为空则不能提交
	        				warnStr=warnStr+orderCode+"<br/>";
	        				continue;
	        			}else if(("undefined"==typeof(loadTime)||loadTime==null||""==loadTime)&&!(fcount==0||fcount==null||fcount=='0')){
	        				str=str+orderCode+"<br/>";
	        			}else{
	        				mappingIds[j]=id;
		        			mappingNos[j]=orderCode;
		        			currentflows[j]=taskId;
		        			if('OR3'==orderType||'OR4'==orderType){
		        				nextflows[j]='flow11';
		        			}else{
		        				nextflows[j]='flow18';
		        			}
		        			j++;
	        			}	
	        		}
	        		if(str!=""){
	        			Ext.Msg.alert('提示', "您选中的：<br/>"+str+"未下载凭证");
	        				return;
	        		}
					if(warnStr!=""){
						warnStr="您选中的：<br/>"+warnStr+"SAP编号为空，将会被忽略，其他的将会提交！你确定要提交？"
					}else{
						warnStr='确定要审批所选订单？';
					}
					Ext.MessageBox.confirm('提示信息',warnStr,function(btn){
						        if(btn=='yes'){
					        		if(mappingNos.length<1){
					        			Ext.Msg.alert("提示信息","没有有效订单可审批");	
					        			
					        		}else{
					        			Ext.Ajax.request({
						        			url : '/core/bpm//commitBatch',
											async : false,
											jsonData : {
												mappingIds : mappingIds,
												mappingNos : mappingNos,
												currentflows:currentflows,
												nextflows:nextflows,
												desc:'',
												errType:'',
												errDesc:''
											},
											method : 'POST',
											dataType : "json",
											contentType : "application/json",
											callback : function(options, success, response) {
												if (!success) {
													Ext.Msg.show({
														title : "错误代码:S-500",
														icon : Ext.Msg.ERROR,
														msg : "链接服务器失败，请稍后再试或联系管理员!",
														buttons : Ext.Msg.OK
													});
												}
											},
											success : function(response, opts) {
												var values = Ext.decode(response.responseText);
												console.log(values);	
												var msgs=values.data;
												var str="";
												for(i in msgs){
													console.log(msgs[i]);
													str=str+msgs[i].msg+"<br/>";
												}
												Ext.Msg.alert("提示信息", str);											
												fgrid.getStore().load({
													params : searchForm.getSearchs()
												});
												window.close();
											},
											failure : function(response, opts) {
												var values = Ext.decode(response.responseText);
												console.log(values);
												Ext.Msg.alert("can't", 'error');
											}
										});
					        		}
						        }else{
						        	console.log("取消");
						        }
						    });

				}else{
					Ext.Msg.alert('提示', '至少选中一条记录');
					return false;
				}	
			}
		},{
			text:'已处理',
			handler:function(){
				var fgrid=Ext.getCmp("finance_grid_id");
				if(fgrid.getSelectionModel().getSelection().length>0){
					var orderCodes=new Array();
					var warrOrderCode="";
					var ses=fgrid.getSelectionModel().getSelection();
					var j = 0;
					for(var i=0;i<ses.length;i++){
	        			var billName=ses[i].data.billName;
	        			var orderCode=ses[i].data.orderCode;
	        			if(!"需手动过账"==billName){
	        				warrOrderCode += orderCode+",";
	        			}else{
	        				orderCodes[j]=orderCode;
	        				j++;
	        			}
					}
					if(warrOrderCode!=""){
						warrOrderCode = "您选中的"+warrOrderCode+"非失败订单已被过滤，其他订单将被系统处理！";
					}else{
						warrOrderCode = "确认要处理已选中订单吗？";
					}
					Ext.MessageBox.confirm('提示信息',warrOrderCode,function(btn){
						if(btn=="yes"){
							if(orderCodes.length<1){
			        			Ext.Msg.alert("提示信息","没有有效订单可审批");	
			        			
			        		}else{
			        			Ext.Ajax.request({
				        			url : '/core/bpm/manageErrorOrder',
									async : false,
									jsonData : {
										mappingIds : orderCodes
									},
									method : 'POST',
									dataType : "json",
									contentType : "application/json",
									callback : function(options, success, response) {
										if (!success) {
											Ext.Msg.show({
												title : "错误代码:S-500",
												icon : Ext.Msg.ERROR,
												msg : "链接服务器失败，请稍后再试或联系管理员!",
												buttons : Ext.Msg.OK
											});
										}
									},
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);
										var msgs=values.success;
										if(msgs){
											Ext.MessageBox.alert("提示信息",values.msg);
										}								
										fgrid.getStore().load({
											params : searchForm.getSearchs()
										});
										window.close();
									},
									failure : function(response, opts) {
										var values = Ext.decode(response.responseText);
										console.log(values);
										Ext.Msg.alert("can't", 'error');
									}
								});
			        		}
						}
					});
				}
			}
		},{
        	text : '导出',
        	id:'export',
        	icon:'/resources/images/down.png',
        	handler:function(){
        		var tmpgrid = Ext.getCmp('finance_grid_id');
				Ext.MessageBox.confirm("温馨提示", "导出到Excel", function (btn) {
					if(btn=="yes"){
						//用grid导出excel
						ExportExcelByGrid(tmpgrid);
					}
				});
        	}
        }];
		
		me.items=[searchForm,grid];
		
		me.callParent(arguments);
	},
});
