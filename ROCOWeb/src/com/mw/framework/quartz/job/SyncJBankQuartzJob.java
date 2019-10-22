package com.mw.framework.quartz.job;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.abc.AbchinaException;
import com.abc.DefaultAbchinaClient;
import com.abc.request.AbchinaApiPaymentDebitCardListRequest;
import com.abc.response.AbchinaDomainTokenAchieveResponse;
import com.abc.sign.TrxException;
import com.main.util.MakeOrderNumUtil;
import com.mw.framework.bean.Message;
import com.mw.framework.domain.CustAguasPassadas;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.DateTools;
public class SyncJBankQuartzJob {
	//同步建行数据
	@Autowired
	private CommonManager commonManager;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	final static String CUST_ID="GD44000009178969501";
	final static String USER_ID="WLPT01";
	final static String PASSWORD="111111";
	final static String PAY_ACCNO="44050155150700000084";
	//@SuppressWarnings("unchecked")
	public void run() throws UnknownHostException, ParseException, IOException, DocumentException{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		String date = df.format(new Date());
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.DATE,   -1);  
		String yesterday = new SimpleDateFormat( "yyyyMMdd ").format(cal.getTime()); 
		// 拼接xml报文
		int count=Integer.MAX_VALUE;
		for (int i = 1; i <= count; i++) {
			if(!(i>count)){
				StringBuilder sb = new StringBuilder();
				String num = MakeOrderNumUtil.makeOrderNum(true);
				sb.append("<?xml version='1.0' encoding='GB2312' standalone='yes' ?>");
				sb.append("<TX>");
				sb.append("<REQUEST_SN>" + num + "</REQUEST_SN>");// 请求序列码
				sb.append("<CUST_ID>" + CUST_ID + "</CUST_ID>");// 客户号
				sb.append("<USER_ID>" + USER_ID + "</USER_ID>");// 操作员号
				sb.append("<PASSWORD>" + PASSWORD + "</PASSWORD>");// 密码
				sb.append("<TX_CODE>6WY101</TX_CODE>");// 交易请求码
				sb.append("<LANGUAGE>CN</LANGUAGE>");// 语言
				sb.append("<TX_INFO>");
				sb.append("<STARTDATE>"+yesterday+"</STARTDATE>");// 开始时间
				sb.append("<ENDDATE>"+date+"</ENDDATE>");//结束时间
				sb.append("<ACCNO1>"+PAY_ACCNO+"</ACCNO1>");//账号
				sb.append("<PAGE>"+i+"</PAGE>");
				sb.append("<TOTAL_RECORD>200</TOTAL_RECORD>");//每页记录数
				sb.append("</TX_INFO>");
				sb.append("</TX>");
				// 向服务器端发送请求，服务器IP地址和服务器监听的端口号//172.16.9.2
				Socket client = new Socket("172.16.3.248", 8082);
				//此处添加连接时长，超时结束
				//client.setSoTimeout(500000);
				// 通过printWriter 来向服务器发送消息
				PrintWriter printWriter = new PrintWriter(client.getOutputStream());
				System.out.println("建行连接已建立...");
				// 发送消息
				printWriter.println(sb);
				printWriter.flush();
				// InputStreamReader是低层和高层串流之间的桥梁
				// client.getInputStream()从Socket取得输入串流
				InputStreamReader streamReader = new InputStreamReader(client.getInputStream(),"GBK");
				// 链接数据串流，建立BufferedReader来读取，将BufferReader链接到InputStreamReder
				BufferedReader reader = new BufferedReader(streamReader);
				StringBuffer content = new StringBuffer();
				String ch=null;
				while ((ch = reader.readLine()).length()>0) {
					content.append( ch);
				}
				reader.close();
				String advice=content.toString();
//				String utf8 = new String(advice.getBytes("GB18030")); 
//				String unicode = new String(utf8.getBytes(), "GB2312"); 
//				String gbk = new String(unicode.getBytes("GB2312"));
				org.dom4j.Document doc=DocumentHelper.parseText(advice);
				Element rootElt = doc.getRootElement(); // 获取根节点
				Iterator<Element> TX_INFO = rootElt.elementIterator("TX_INFO"); // 获取根节点下的子节点TX_INFO
				while (TX_INFO.hasNext()) {
					Element recordEle = TX_INFO.next();
					if(!(count==Integer.parseInt(recordEle.elementTextTrim("TOTAL_PAGE")))){
						count = Integer.parseInt(recordEle.elementTextTrim("TOTAL_PAGE")); // 拿到TX_INFO节点下的子节点TOTAL_PAGE值
					}
					Iterator<Element> DETAILLIST = recordEle.elementIterator("DETAILLIST"); // 获取子节点TX_INFO下的子节点DETAILLIST
					// 遍历TX_INFO节点下的DETAILLIST节点
					while (DETAILLIST.hasNext()) {
						Element recordEle1 = DETAILLIST.next();
						Iterator<Element> DETAILINFO = recordEle1.elementIterator("DETAILINFO"); // 获取子节点DETAILLIST下的子节点DETAILINFO
						while(DETAILINFO.hasNext()){
							Element recordEle2 = (Element) DETAILINFO.next();
							String FLAG1=recordEle2.elementTextTrim("FLAG1");//借贷标志
							if(FLAG1.equals("1")){
								//如果借贷标志=1则为转入流水
								String tradeDate1 = recordEle2.elementTextTrim("TRANDATE");//交易日期
								String tradeDate=tradeDate1.replaceAll("/", "");
								Double tradeAmount =Double.parseDouble(recordEle2.elementTextTrim("AMT")); //金额 
								String timer = tradeDate1+" "+recordEle2.elementTextTrim("TRANTIME");//交易时间
								timer=timer.replace("/", "-");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date tradeTime= sdf.parse(timer);
								String jrnNo = recordEle2.elementTextTrim("TRAN_FLOW");//交易流水号
								String tradeAccNo = recordEle2.elementTextTrim("RLTV_ACCNO");//关联账号
								String accName1 = recordEle2.elementTextTrim("ACC_NAME1");//打款人
								String accno2 = recordEle2.elementTextTrim("ACCNO2");//打款帐号
								//绑定付款人信息
								String payer="";
								String sql1="select distinct ch.payer from cust_trade_header ch where ch.trade_acc_no='"+tradeAccNo+"'";
								List<Map<String,Object>> list=jdbcTemplate.queryForList(sql1);
								if(list.size()>0){
									payer =(String)list.get(0).get("PAYER");
								}
								//保存到数据库
								String sql="SELECT COUNT(1) FROM CUST_TRADE_HEADER TH WHERE TH.JRN_NO= '"+jrnNo+"'";
								int count1 = jdbcTemplate.queryForObject(sql, Integer.class);
								if(count1==0){
									CustAguasPassadas aguasPassadas=new CustAguasPassadas(tradeAccNo, jrnNo, tradeDate, tradeTime, tradeAmount, "JH",payer,accName1,accno2);
									System.out.println(aguasPassadas);
									commonManager.save(aguasPassadas);
								}
							}else{
								//同步0条数据
							}
						}
					}
				}
			}
		}}
}