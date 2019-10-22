function OnReady(id) {
	console.log(id);
	var a_ = parent.Ext?Ext.ComponentQuery.query('uxsupcangrid'):null;
	if(a_){
		if(a_.length>0){
			a_[0].fireEvent('onReady',id);	
			return;
		}
	}else{
		console.log('OnReady没有找到对应组件');
	}
}

function OnEvent(id, Event, p1, p2, p3, p4) {
	console.log(Event);
	var a_ = parent.Ext?Ext.ComponentQuery.query('uxsupcangrid'):null;
	if(a_){
		if(a_.length>0){
			a_[0].fireEvent('onEvent',id);	
			return;
		}
	}else{
		console.log('OnEvent没有找到对应组件');
	}
}