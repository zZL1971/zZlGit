package com.mw.framework.quartz.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.sap.jco3.SAPConnect;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class SyncABankSapQuartzJob {
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	/**
	 * 同步sap,获取凭证信息		
	 * @return
	 * @throws ParseException 
	 */
	public void run(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String today=df.format(new Date());
		System.out.println("同步sap获取银行凭证-----同步时间："+today);// new Date()为获取当前系统时间
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.DATE,   1); 
		//yesterday此为前天
		String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime()); 
		Calendar   cal1   =   Calendar.getInstance();  
		cal1.add(Calendar.DATE,   -2); 
		//yesterday此为前天
		String yesterday1 = new SimpleDateFormat( "yyyy-MM-dd ").format(cal1.getTime()); 
		JCoDestination connect = SAPConnect.getConnect();
		//连接sap
		JCoFunction function=null;
		String kunnr2=null;
		String tradeAmount2=null;
		String name2=null;
		String jrnNo=null;
		String BKTXT=null;
		String sql1 ="select ch.kunnr,th.acc_name1,th.trade_date,th.trade_amount,ch.name1,th.jrn_no,cb.bank_status from cust_header ch "
			+"left join cust_bank cb on ch.id=cb.cust_id left join cust_trade_header th on th.trade_acc_no=cb.bank_id where cb.bank_id is not null "
			+ " and th.trade_time between date '"+yesterday1+"' and date '"+yesterday+"'";
		//		String sql1 ="select ch.kunnr,th.trade_date,th.trade_amount,ch.name1,th.jrn_no from cust_header ch "
		//				+"left join cust_trade_header th on th.trade_acc_no=ch.trade_accno where th.jrn_no='415444001'";
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sql1);
		for (Map<String, Object> map : totalElements) {
			kunnr2=(String) map.get("KUNNR");
			String accName1=(String) map.get("ACC_NAME1");
			String tradeDate2=(String)map.get("TRADE_DATE");
			String year=tradeDate2.substring(0, 4);
			String month=tradeDate2.substring(4, 6);
			tradeAmount2=map.get("TRADE_AMOUNT").toString();
			name2=(String) map.get("NAME1");
			jrnNo=(String)map.get("JRN_NO");
			String bankStatus=(String)map.get("BANK_STATUS");
			if("NH".equals(bankStatus)){
				BKTXT="预收款:"+name2+"预付款";
			}else{
				BKTXT="预收"+name2+"款,"+jrnNo+"-"+accName1;
			}
			try {
				function = connect.getRepository().getFunction("ZRFC_FI_U801");
				function.getImportParameterList().setValue("I_AD_TYPE", "4");
				JCoStructure structure = function.getImportParameterList().getStructure("S_AD_BKPF");
				structure.setValue("BLDAT", tradeDate2);//交易日期
				structure.setValue("BLART", "DZ");
				structure.setValue("BUKRS", "3100");
				structure.setValue("BUDAT", tradeDate2);//交易日期
				structure.setValue("GJAHR", year);//交易年
				structure.setValue("MONAT", month);//交易月
				structure.setValue("USNAM", "abc");//打款人姓名
				structure.setValue("WAERS", "CNY");
				structure.setValue("BKTXT", BKTXT);//信息摘要
				structure.setValue("PPNAM", "admin");//操作人姓名
				structure.setValue("ZVOUCHER",jrnNo);//交易流水号，唯一标识
				//连接sap
				JCoTable table2 = function.getTableParameterList().getTable("T_AD_BSEG");
				table2.appendRow();
				table2.setValue("BUZEI", "10");           
				table2.setValue("SHKZG", "S");            
				table2.setValue("BSCHL", "40");   
				if("NH".equals(bankStatus)){
					table2.setValue("HKONT", "1002020300");
				}else{
					table2.setValue("HKONT", "1002040300");
				}
				table2.setValue("USNAM", "abc");//打款人姓名
				table2.setValue("BUKRS", "3100");   
				table2.setValue("SGTXT", BKTXT);//信息摘要     
				table2.setValue("WRBTR", tradeAmount2);//金额         
				table2.setValue("ZZCC", "103");           
				table2.setValue("PRCTR", "9999999999");
				table2.setValue("WAERS", "CNY");
				table2.appendRow();
				table2.setValue("BUZEI", "20");           
				table2.setValue("SHKZG", "H");            
				table2.setValue("BSCHL", "19");           
				table2.setValue("UMSKZ", "S");            
				table2.setValue("HKONT", "2203010100");   
				table2.setValue("USNAM", "abc");//打款人姓名
				table2.setValue("BUKRS", "3100");   
				table2.setValue("SGTXT", BKTXT);//信息摘要         
				table2.setValue("WRBTR", tradeAmount2);//金额         
				table2.setValue("WAERS", "CNY");
				table2.setValue("CUSTOMER", kunnr2);
				function.execute(connect);
				//凭证信息
				String belnr=function.getExportParameterList().getString("E_BELNR");
				//标识
				String status=function.getExportParameterList().getString("E_STATUS");
				//返回信息
				String message=function.getExportParameterList().getString("E_MESSAGE");
				System.out.println(jrnNo+status+belnr+message);
				if(belnr!=null&&!belnr.isEmpty()){
					String sql2="update cust_trade_header th set th.resumo= '" +belnr+"' where th.jrn_no= '" +jrnNo+"'";
					jdbcTemplate.execute(sql2);
				}
			} catch (JCoException e) {
				e.printStackTrace();
			}
		}
		System.out.println("sap同步结束");
	}

}
