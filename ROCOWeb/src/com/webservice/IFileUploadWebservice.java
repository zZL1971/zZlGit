package com.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.mw.framework.model.CXFOrderModel;

@WebService(name="importFile2020")
/*@SOAPBinding(style=SOAPBinding.Style.RPC)
@MTOM*/
public interface IFileUploadWebservice {

	@WebMethod
	public String upload(@WebParam(name="info") CXFOrderModel info);
	
}
