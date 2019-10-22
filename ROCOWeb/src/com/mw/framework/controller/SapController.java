/**
 *
 */
package com.mw.framework.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mw.framework.commons.BaseController;
import com.mw.framework.sap.jco3.SAPConnect;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 *
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.SapController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-5-26
 *
 */
@Controller
@RequestMapping("/sap/*")
public class SapController extends BaseController{

	/**
	 * 访问SAP BAPI
	 * @throws JCoException 
	 */
	@RequestMapping(value = {"/one"}, method = RequestMethod.GET)
	public void exec(HttpSession session) throws JCoException{
		
		System.out.println("seesion ID:"+super.getSession().getId());
		//System.out.println("seesion ID:"+session.getId());
		
		long startTime = System.currentTimeMillis();
		JCoDestination connect = SAPConnect.getConnect();
		long sendTime = System.currentTimeMillis();
		System.out.println("建立连接耗时:"+(sendTime-startTime)+"毫秒");
		JCoFunction function = connect.getRepository().getFunction("ZRFC_PP_PC02");
		
		JCoTable table = function.getTableParameterList().getTable("S_ERDAT");
		table.appendRow();
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", "201505");
		
		function.execute(connect);
		long endTime = System.currentTimeMillis();
		System.out.println("执行BAPI(Z_RFC_REPORT_KHDJ)耗时:"+(endTime-sendTime)+"毫秒");
		System.out.println();
		//printResult(function);
	}
	
	@RequestMapping(value = {"/two"}, method = RequestMethod.GET)
	public void exec2() throws JCoException{
		System.out.println("seesion ID:"+super.getSession().getId());
		
		long startTime = System.currentTimeMillis();
		JCoDestination connect = SAPConnect.getConnect();
		long sendTime = System.currentTimeMillis();
		System.out.println("建立连接耗时:"+(sendTime-startTime)+"毫秒");
		JCoFunction function = connect.getRepository().getFunction("Z_RFC_REPORT_ZRE029");
		
		JCoParameterList tableParameterList = function.getTableParameterList();
		
		/*JCoTable table = tableParameterList.getTable("P_MONTH");
		table.appendRow();
		table.setValue("SIGN", "I");
		table.setValue("OPTION", "EQ");
		table.setValue("LOW", "201405");*/
		
		JCoParameterList importParameterList = function.getImportParameterList();
		importParameterList.setValue("S_SPOM", "201412");
		
		function.execute(connect);
		long endTime = System.currentTimeMillis();
		System.out.println("执行BAPI(Z_RFC_REPORT_ZRE029)耗时:"+(endTime-sendTime)+"毫秒");
		System.out.println();
		//printResult(function);
	}
	
	public void printResult(JCoFunction function){
		//获取返回值
		JCoParameterList exportParameterList = function.getExportParameterList();
		if(exportParameterList!=null){
			JCoFieldIterator fieldIterator = exportParameterList.getFieldIterator();
			while (fieldIterator.hasNextField()) {
				JCoField nextField = fieldIterator.nextField();
				System.out.println(nextField.getName()+"|"+ nextField.getValue());
			}
		}
		
		//获取表格数据
		JCoParameterList tableParameterList = function.getTableParameterList();//获取所有的表格
		for (JCoField jCoField : tableParameterList) {
			JCoTable table2 = tableParameterList.getTable(jCoField.getName());
			if (table2.getNumRows()>0) {
				table2.firstRow();
				for (int j = 0; j < table2.getNumRows(); j++,table2.nextRow()) {
					for (JCoField jCoField2 : table2) {
						Object value = table2.getValue(jCoField2.getName());
						System.out.println(jCoField2.getName()+"|"+jCoField2.getDescription()+"|"+value);
					}
				}
			}
		}
	}
}
