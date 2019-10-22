Ext.define("SMSWeb.view.spm.LookSalePricePolicyView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.lookSalePricePolicyView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			requires : ['Ext.ux.form.SearchForm',"Ext.ux.form.TableComboBox","Ext.ux.form.CustomCombobox","Ext.ux.form.DictCombobox","Ext.ux.form.TrieCombobox"],
			
			initComponent:function(){
		
		
		var form = Ext.widget('form',{
			bodyPadding : '5 5 0 5',
			
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
							name:'kunnr',
							//readOnly:true,
							fieldLabel:'客户编码'
						},{
							xtype:'textfield',
							name:'name1',
							//readOnly:true,
							fieldLabel:'客户名称'
						}]
					},{//行二
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[discountstyleCombobox,isableCombobox]
					},{//行三
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
							items:[{
	                    xtype:'datefield',
	                    fieldLabel: '开始日期',
	                    name: 'starttime',
	                    format :'Y-m-d'
	                },{
	                    xtype:'datefield',
	                    fieldLabel: '结束日期',
	                    name: 'endtime',
	                    format :'Y-m-d'
		            }]
					},{//行四
						xtype:'container',
						layout:'hbox',//此布局是将所有子组件在容器中水平排列
						items:[{
							xtype:'textfield',
							name:'saleorder',
							//readOnly:true,
							fieldLabel:'关联订单号'
						}]
					},{
						title : "系统信息",
						xtype : "fieldset",
						bodyPadding : 5,
						collapsible : true,
						collapsed : true, // 默认收缩
						defaults : {
							labelSeparator : "：",
							labelWidth : 65,
							anchor : '100%',
							//width : 300,
							fieldStyle:'background:#E6E6E6'
						},
						defaultType : "textfield",
						items : [{
									fieldLabel : 'ID',
									name : 'id',
									readOnly : true
								}, {
									fieldLabel : '创建人',
									name : 'createUser',
									readOnly : true
								}, {
									fieldLabel : '创建时间',
									name : 'createTime',
									xtype:'datefield',
									format:'Y-m-d H:i:s'
									,readOnly : true
								}, {
									fieldLabel : '更新人',
									name : 'updateUser',
									readOnly : true
								}, {
									fieldLabel : '更新时间',
									name : 'updateTime',
									xtype:'datefield',
									format:'Y-m-d H:i:s'
									,readOnly : true
								}]
						}],
			buttons:[{
				text:'确认',
				handler:function(){
					form.submit({
						url : '/core/bpm/taskDistribution/save',
						success : function(form, action) {
							grid.getStore().load({params:searchForm.getSearchs()});
							window.close();
						},
						failure : function(formx, action) {
							
						}
					});
				}
			}]
		});
		
		//form.loadRecord(record);
		
		var window = Ext.widget('window',{
			title:'任务重排',
			modal : true,
			items:[form]
		});
		
		window.show();
	}
		
	
		});