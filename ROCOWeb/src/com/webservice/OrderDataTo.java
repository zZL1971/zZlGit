package com.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name="OrderData")
public interface OrderDataTo {
	@WebMethod
	public String getOrderStatus(@WebParam(name="custId")String custId);
	
	@WebMethod
	public String wechatUserJurisdiction(@WebParam(name="userId")String userId);
}
