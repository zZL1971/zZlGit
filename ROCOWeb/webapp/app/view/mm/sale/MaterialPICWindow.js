Ext.tip.QuickTipManager.init();
Ext.define(
				"SMSWeb.view.mm.sale.MaterialPICWindow",
				{
					//extend : 'Ext.grid.Panel',
					extend : 'Ext.window.Window',
					alias : 'widget.MaterialPICWindow',
					enableKeyNav : true,
					columnLines : true,
					filepath : null,
					height : 500,
					//width : document.body.clientWidth *  0.5,
					width : 500,
					matnr : null,

					id : 'MaterialPICWindow',
					viewConfig : {
						enableTextSelection : true
					}
					/*,items:[
							{      
							    xtype: 'box', //或者xtype: 'component',   
							    id:'tupian',
							    width: 100, //图片宽度      
							    height: 200, //图片高度      
							    autoEl: {      
							        tag: 'img',    //指定为img标签      
							        src: 'resources/images/add.png'    //指定url路径
							       // src: filepath
							    }   
							    //html:'<img id="checkpic" src="picAction.do?method=getPicForDataBase&recodId='+thisbehaviorcount+'" style="width:430px;height:430px;"  onclick="showBigPic()" />'  
							    html:'<img id="checkpic" src="'+filepath+'" style="width:430px;height:430px;"  onclick="showBigPic()" />'
							}  
					        ]*/,
					initComponent : function() {
					        	
//						function showBigPic() {
//							var com = document.getElementById("checkpic");
//							window.open(com.src,"image",'fullscreen=1,top=0,left=0,height=600,width=1000, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no');
//						}

						var headForm;
						var me = this;
						var _filepath = me.filepath;
						headForm = Ext.widget('form',
										{
											//title:'图片',
											items : [ {
												xtype : 'box', //或者xtype: 'component',   
												id : 'tupian',
												width : 500, //图片宽度      
												height : 500, //图片高度      
												//html:'<img id="checkpic" src="picAction.do?method=getPicForDataBase&recodId='+thisbehaviorcount+'" style="width:430px;height:430px;"  onclick="showBigPic()" />'  
												html : '<img id="checkpic" src="' + _filepath + '"  style="width:500px;height:500px;"   onclick="showBigPic()" />'
											} ]
										});

						var me = this;
						//Ext.apply(this);
						Ext.apply(me, {
							items : [ headForm ]
						});
						me.callParent(arguments);
					}
				});