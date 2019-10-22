Ext.define('SRM.view.trie.DictForm', {
	extend : 'Ext.window.Window',
	dictId:null,
	gridStore:null,
	alias : 'widget.trieDictWin',
	requires : ['Ext.form.*',"Ext.ux.form.DictCombobox"],
	initComponent : function() {

		var form = Ext.widget('form', {
			border : false,
			frame : false,
			bodyPadding : '5 5 0 5',
			itemId:'trieDictForm',
			//alias : 'widget.trieDictForm',
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
						name:'descZhCn',
						fieldLabel : '中文描述'
					},{
						name:'descEnUs',
						fieldLabel : '英文描述'
					},{
						name:'descZhTw',
						fieldLabel : '繁体描述'
					},{
						xtype:'dictcombobox',
						name:'stat',
						fieldLabel:'启用',
						dict:'T_OR_F'
					},{
						xtype : 'numberfield',
						name:'orderBy',
						fieldLabel : '排序号'
					}/*,{
						xtype : 'combobox',
						fieldLabel : '下拉框',
						store:Ext.create('SRM.store.trie.DictStore'),
						editable:false,
						listeners:{
							afterrender:function(combo,xx){
								var a = combo.getStore();
								combo.setValue(a.getAt(0).get("text"));
							}
						}
					}*/,{
						xtype : 'treepicker',
						name:'trieTree.id',
						fieldLabel : '父节点',
						displayField: 'descZhCn',
						store: Ext.data.StoreManager.lookup('trieTreeStore')
					}, {
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
				disabled: true,
				id:'dictFormSubmit',
            	formBind: true/*,
				handler : function() {
					form.submit({
						waitMsg : '请稍候',
						waitTitle : '正在验证登录',
						url : '/core/dd/save',
						success : function(form, action) {
							
						},
						failure : function(form, action) {
							
						}
					});
				}*/
			}]
		});
		
		if(this.dictId!=null){
			form.load({
				url:'/core/dd/get/'+this.dictId,
				//method:'POST',
				success:function(form,action){
                    //Ext.Msg.alert('提示',"加载成功");  
                },
                failure:function(form,action){
                    Ext.Msg.alert('提示',"失败原因是: "+action.result.errorMessage);  
                }
			});
		}
		
		Ext.apply(this, {
					//height : 280,
					width : 280,
					title : '字典编辑',
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