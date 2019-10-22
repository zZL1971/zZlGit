package com.mw.framework.sap.jco3;

import java.util.Properties;

import com.mw.framework.sap.jco3.SAPDestinationDataProvider.MyDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class SAPConnect {
	static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
	private static MyDestinationDataProvider myProvider;
	static {
		myProvider = MyDestinationDataProvider.getInstance();
		Environment.registerDestinationDataProvider(myProvider);
	}

	static {
		Properties connectProperties = new Properties();
		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "172.16.3.10");// SAP 服务器地址
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");// 系统编号
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "600");// SAP集团
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, "zjava");// SAP用户名
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "654321");//
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "zh");// 登陆语言
		connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "100");// 最大连接数
		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "800");// 最大连接
		myProvider.addDestination(ABAP_AS_POOLED, connectProperties);
	}

	public static JCoDestination getConnect() {
		JCoDestination destination = null;
		try {
			destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
		} catch (Exception e) {
			System.out.println("请copy config/sapjco3.dll 文件到jdk/bin目录");
			// e.getCause();
		}
		return destination;
	}

	@Deprecated
	public static JCoFunction getFunction(String rfcName) throws JCoException {
		return getConnect().getRepository().getFunction(rfcName);// 链接SAP接口
	}
}
