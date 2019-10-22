Ext.apply(Ext.form.field.VTypes, {
	ipaddr:function(v){
		return /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(v);
	},
	ipaddrText:'必须输入正确的IP地址',
	ipaddrMask:/[\d\.]/i,
	daterange : function(val, field) {
		var date = field.parseDate(val);

		if (!date) {
			return false;
		}
		if (field.startDateField
				&& (!this.dateRangeMax || (date.getTime() != this.dateRangeMax
						.getTime()))) {
			var start = field.up('form').down('#'
					+ field.startDateField);
			start.setMaxValue(date);
			start.validate();
			this.dateRangeMax = date;
		} else if (field.endDateField
				&& (!this.dateRangeMin || (date.getTime() != this.dateRangeMin
						.getTime()))) {
			var end = field.up('form').down('#' + field.endDateField);
			end.setMinValue(date);
			end.validate();
			this.dateRangeMin = date;
		}
		/*
		 * Always return true since we're only using this vtype to set
		 * the min/max allowed values (these are tested for after the
		 * vtype test)
		 */
		return true;
	},

	daterangeText : '开始时间必须小于结束日期',//Start date must be less than end date

	password : function(val, field) {
		if (field.initialPassField) {
			var pwd = field.up('form').down('#'
					+ field.initialPassField);
			return (val == pwd.getValue());
		}
		return true;
	},

	passwordText : '密码不匹配',//Passwords do not match
	
	phone : function(val, field) { 
	    var str=val;
	    //var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	    var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$/;
	    return reg.test(str);
	},
    phoneText : "号码不匹配!",
    contactCN : function(val, field) { 
	    var reg=/^((((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8})|(010|02[012345789]{1}|0[35789]{1}[12356789]{1}[0-9]{1}|066[0-3]{1}|069[12]{1}|048[23]{1}|042[179]{1}|04[157]{1}[0-9]{1}|043[1-9]{1})-[0-9]{7,8}|(852|853|00852|00853)-[0-9]{8}|(010|02[012345789]{1}|0[35789]{1}[12356789]{1}[0-9]{1}|066[0-3]{1}|069[12]{1}|048[23]{1}|042[179]{1}|04[157]{1}[0-9]{1}|043[1-9]{1})-[0-9]{7,8}-(\d{4}|\d{3}|\d{2}|\d{1})|(852|853|00852|00853)-[0-9]{8}-(\d{4}|\d{3}|\d{2}|\d{1})|[0-9]{7,8}-(\d{4}|\d{3}|\d{2}|\d{1}))$/;
	    return reg.test(val);
	},
    contactCNText : "联系方式号码不匹配，请输入正确的号码，如18688888888、020-12345678、020-12345678-1234!"
});
