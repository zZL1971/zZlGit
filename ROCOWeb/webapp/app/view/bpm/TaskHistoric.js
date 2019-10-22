Ext.define('SMSWeb.view.bpm.TaskHistoric', {
	extend : 'Ext.window.Window',
	alias : 'widget.TaskHistoric',
	layout : 'fit',
	id:'TaskHistoric',
	minWidth : 700,
	minHeight:300,  
	title:'流程历史记录',
	modal : true,
	resizable:false,
	procinstid:'',//流程
	initComponent:function(){
		var me=this;
		
		me.width = document.body.clientWidth * 0.7;
		me.height = document.body.clientHeight*0.7;
		me.items=[{
	       	 xtype:'uxgrid',
	    	 id:'taskHistoricGridId',
	    	 headerModule:'SYS_BPM_HI',
			 domain:'BPMHistoricTaskInstance',
			 dataRoot:'data',
			 dataUrl:'/core/bpm/historic'
		}];
        me.listeners={
    			show:function(){
    				if(me.procinstid){
    					var flowGrid =Ext.getCmp("taskHistoricGridId");
    					flowGrid.getStore().load({params:{id:me.procinstid}});
    				}
    			
    			}
    		};
	    this.callParent(arguments);
	    }

	});
