package com.webservice;

import java.net.MalformedURLException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.rpc.ServiceException;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.validation.BindingResult;



@WebService(name = "saveBase2020")
public interface IOrder2020 {
	/**
	 * @param orderCode
	 * @param material
	 * @return
	 * @throws ServiceException 
	 * @throws MalformedURLException 
	 */
	@WebMethod
	public String kitUpload(@WebParam(name="data")String data,@WebParam(name="fileBase64") String fileBase64) throws ServiceException,MalformedURLException;
	//
}
