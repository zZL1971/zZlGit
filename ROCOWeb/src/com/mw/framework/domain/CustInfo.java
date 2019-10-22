package com.mw.framework.domain;

import java.util.List;

public class CustInfo {

	/*"return_code": "SUCCESS",*/
   
	private String return_code;
	private String return_msg;
	private List<Cust> return_data;
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public List<Cust> getReturn_data() {
		return return_data;
	}
	public void setReturn_data(List<Cust> return_data) {
		this.return_data = return_data;
	}
	
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	@Override
	public String toString() {
		return "[return_code=" + return_code + ", return_msg="
				+ return_msg + ", return_data=" + return_data + "]";
	}

}
