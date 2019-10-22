Ext.define('SRM.view.trie.TrieForm', {
	extend : 'Ext.window.Window',
	trieRecord:null,
	trieId:null,
	orderby:1,
	pid: null,
	alias : 'widget.trieTrieWin',
	requires : ['Ext.form.*'],
	initComponent : function() {
		var form = Ext.widget('form', {
			border : false,
			frame : false,
			bodyPadding : '5 5 0 5',
			itemId:'trieTrieForm',
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 65,
				//labelStyle : 'font-weight:bold'//,
				width : 240
			},
			defaultType: "textfield",
			defaults : {
				margins : '0'
			},
			reader: {
			    type:'json',
			    root:'data',
			    model: 'SRM.model.trie.GridModel'
			},
			items : [{
						name:'keyVal',
						fieldLabel : '字典编码',
						blankText : '字典编码不能为空',
						allowBlank : false
					},{
						name:'type',
						fieldLabel : '类型'
					},{
						name:'icon',
						xtype:'hiddenfield',
						fieldLabel : '图片'
					},{
						name:'descZhCn',
						fieldLabel : '中文描述'
					},{
						name:'descEnUs',
						fieldLabel : '英文描述'
					},{
						name:'descZhTw',
						fieldLabel : '繁体描述'
					},{
						xtype : 'numberfield',
						name:'orderBy',
						minValue:0,
						fieldLabel : '排序号'
					},{
						xtype : 'treepicker',
						name:'parent.id',
						fieldLabel : '父节点',
						displayField: 'descZhCn',
						store: Ext.create('Ext.data.TreeStore',{
							fields: ['id','descZhCn'],
							root: {
								text: 'wwww',
								expanded: true
							},
							proxy: {
								type: 'ajax',
								url: '/core/trie/tree/root',
								reader: {
									type: 'json'
								}
							}
						})
						
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
			buttons : [{
				text : '保存',
				//disabled: true,
				id:'trieFormSubmit',
            	formBind: true/*,
				handler : function() {
					this.up("form").getForm().submit({
						url : '/core/trie/save',
						success : function(form, action) {
							alert(4);
						},
						failure : function(form, action) {
							alert(5);
						}
					});
					
					alert(8);
					
					
				}*/
			}]
		});
		
		if(this.trieRecord!=null){
			form.loadRecord(this.trieRecord);
		}
		
		if(this.trieId!=null){
			form.load({
				url:'/core/trie/'+this.trieId,
				//method:'POST',
				success:function(f,action){
					var result = Ext.decode(action.response.responseText);
					f.findField('parent.id').setValue(result.data.pid);
					f.findField('icon').setValue(result.data.icon);
                    //Ext.Msg.alert('提示',"加载成功");
                },
                failure:function(form,action){
                	var result = Ext.decode(action.response.responseText);
                    Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
                }
			});
		}else{
			form.getForm().findField('orderBy').setValue(this.orderby);
		}
		
		if(this.pid!=null){
			form.getForm().findField('parent.id').setValue(this.pid);
		}
		
		Ext.apply(this, {
					//height : 280,
					width : 280,
					title : '字典索引编辑',
					closable : true,
					iconCls: 'func_default',
					layout : 'fit',
					modal : true,
					//autoScroll : true,
					//plain : true,
					//resizable : true,
					border : false,
					items : form
				});
		this.callParent(arguments);
	}
});