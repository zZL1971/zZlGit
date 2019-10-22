package com.mw.framework.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.UUIDUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class LoadShipmentDataQuartzJob {
	private static final Logger logger = LoggerFactory
			.getLogger(LoadShipmentDataQuartzJob.class);

	public void run() {
		try {
			SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");

			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");
			Date beginDate = new Date();
			Calendar cld = Calendar.getInstance();
			cld.setTime(beginDate);
			// 连接sap
			JCoDestination connect = SAPConnect.getConnect();
			// 调用sap方法，获取出库产品
			JCoFunction function = connect.getRepository().getFunction(
					"ZRFC_PP_CH01");
			//cld.add(Calendar.DAY_OF_MONTH, -1);
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(beginDate);
			String remindTime = null;
			String remindNo="240";
			calendar.add(Calendar.HOUR,
					calendar.get(Calendar.HOUR) - Integer.parseInt(remindNo));
			remindTime = sim.format(calendar.getTime());
			while (!remindTime.equals(sim.format(cld.getTime()))) {
				System.out.println(sim.format(cld.getTime()));
				// 由于客户和客服都是查历史纪录的,所以每天只需要同步前一天的数据.
				// cld.setTime(new Date());
				// cld.add(Calendar.DAY_OF_MONTH, -1);
				// 添加参数
				JCoParameterList importParameterList = function
						.getImportParameterList();
				importParameterList.setValue("S_ERDAT",
				 //"20171222"
						sim.format(cld.getTime())
				// sim.format(new Date())
						);
				// sap方法执行
				function.execute(connect);
				// 获取返回数据
				JCoTable jcoTable = function.getTableParameterList().getTable(
						"EX_T_CH01");
				// 检索返回数据中，是否有已经存在DB中的，如果有，那么忽略，如果没有，那么存入db中
				for (int index = 0; index < jcoTable.getNumRows(); index++) {
					jcoTable.setRow(index);
					Object outputTime = jcoTable.getValue("ERDAT");
					Object sapCode = jcoTable.getValue("VBELN");
					Object posex = jcoTable.getValue("POSNR");
					Object generalSum = jcoTable.getValue("ZTYIS");
					Object carcaseSum = jcoTable.getValue("ZGUIS");
					Object shutterSum = jcoTable.getValue("ZMENS");
					Object backboardSum = jcoTable.getValue("ZBEIS");
					Object partsSum = jcoTable.getValue("ZPEIS");
					Object metalsSum = jcoTable.getValue("ZWUJS");
					Object slidDoorSum = jcoTable.getValue("ZYIMS");
					Object plasticSum = jcoTable.getValue("ZXISS");
					Object decorationSum = jcoTable.getValue("ZZSJS");

					Object name = jcoTable.getValue("NAME1");
					Object custName = jcoTable.getValue("ZZNAME");
					Object custPhone = jcoTable.getValue("ZZPHON");
					// 装车单号
					Object ZJIAN = jcoTable.getValue("ZJIAN");

					// 获取收货人和联系方式
					if (outputTime != null) {
						String consignee = "";
						String consigneePhone = "";
						String kunnr = "";
						String kunnrName = "";
						List<Map<String, Object>> list = jdbcTemplate
								.queryForList("select c.namev,c.telf1,c.kunnr,(select z.name1 from cust_header z where z.kunnr=c.kunnr and rownum=1) as name1 from cust_contacts c where c.id=(select id from (select nvl((select cc.id from cust_contacts cc where cc.abtnr='0008' and cc.pid=ch.id and rownum=1),(select cc.id from cust_contacts cc where cc.abtnr='0001'and cc.pid=ch.id and rownum=1)) as id from cust_header ch where ch.kunnr=( select shou_da_fang from sale_header where sap_order_code='"
										+ +Integer.parseInt(sapCode.toString())
										+ "'and rownum = 1)) where id is not null )");
						for (Map<String, Object> map : list) {
							consignee = map.get("namev") == null ? "" : map
									.get("namev").toString();
							consigneePhone = map.get("telf1") == null ? ""
									: map.get("telf1").toString();
							kunnr = map.get("kunnr") == null ? "" : map.get(
									"kunnr").toString();
							kunnrName = map.get("name1") == null ? "" : map
									.get("name1").toString();
						}

						// 如果订单已经存在，那么不存入DB
						List<Map<String, Object>> saleShip = jdbcTemplate.queryForList(
								"select * from SALE_SHIPMENT_LOG ssl where ssl.sap_code='"
										+ Integer.parseInt(sapCode.toString())
										+ "' and ssl.posex='"
										+ Integer.parseInt(posex.toString())+"' ");//and ssl.ZJIAN='" + ZJIAN + "'
						if (saleShip.size() > 0) {
							for (Map<String, Object> map : saleShip) {
								Object $zjian = map.get("ZJIAN");
								if(!ZJIAN.equals($zjian)){
									int result = jdbcTemplate.update("DELETE SALE_SHIPMENT_LOG SL WHERE SL.SAP_CODE='"+Integer.parseInt(sapCode.toString())+"' AND SL.POSEX='"+Integer.parseInt(posex.toString())+"' AND SL.ZJIAN='"+ZJIAN+"'");
									if(result>0){
										saveSaleShipment(sim, jdbcTemplate, outputTime,
												sapCode, posex, generalSum, carcaseSum,
												shutterSum, backboardSum, partsSum,
												metalsSum, slidDoorSum, plasticSum,
												decorationSum, name, custName, custPhone,
												ZJIAN, consignee, consigneePhone, kunnr,
												kunnrName);
									}
								}else{
									continue;
								}
							}
						} else {
							saveSaleShipment(sim, jdbcTemplate, outputTime,
									sapCode, posex, generalSum, carcaseSum,
									shutterSum, backboardSum, partsSum,
									metalsSum, slidDoorSum, plasticSum,
									decorationSum, name, custName, custPhone,
									ZJIAN, consignee, consigneePhone, kunnr,
									kunnrName);
						}
					}
				}
				cld.add(Calendar.DAY_OF_MONTH, -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveSaleShipment(SimpleDateFormat sim,
			JdbcTemplate jdbcTemplate, Object outputTime, Object sapCode,
			Object posex, Object generalSum, Object carcaseSum,
			Object shutterSum, Object backboardSum, Object partsSum,
			Object metalsSum, Object slidDoorSum, Object plasticSum,
			Object decorationSum, Object name, Object custName,
			Object custPhone, Object ZJIAN, String consignee,
			String consigneePhone, String kunnr, String kunnrName) {
		List<Map<String, Object>> _list = jdbcTemplate
				.queryForList("select order_code from sale_header where sap_order_code='"
						+ Integer.parseInt(sapCode
								.toString()) + "'");
		String orderCode = "";
		if (_list != null && _list.size() > 0) {
			orderCode = _list.get(0).get("ORDER_CODE")
					.toString();
		}
		// DB存入操作
		jdbcTemplate
				.update(" insert into SALE_SHIPMENT_LOG(id,create_time,create_user,row_status,update_time,update_user,BACKBOARD_SUM,CARCASE_SUM,CUST_NAME,CUST_PHONE,DECORATION_SUM,GENERAL_SUM,METALS_SUM,NAME,ORDER_CODE,OUTPUT_TIME,PARTS_SUM,PLASTIC_SUM,POSEX,SAP_CODE,SHUTTER_SUM,SLIDDOOR_SUM,consignee,consignee_phone,kunnr,kunnr_name,ZJIAN) values('"
						+ UUIDUtils.base58Uuid()
						+ "',sysdate,'admin',1,sysdate,'admin',"
						+ backboardSum
						+ ","
						+ carcaseSum
						+ ",'"
						+ custName
						+ "','"
						+ custPhone
						+ "',"
						+ decorationSum
						+ ","
						+ generalSum
						+ ","
						+ metalsSum
						+ ",'"
						+ name
						+ "','"
						+ orderCode
						+ "',to_date('"
						+ sim.format(outputTime)
						+ "','yyyymmdd'),"
						+ partsSum
						+ ","
						+ plasticSum
						+ ",'"
						+ Integer
								.parseInt(posex.toString())
						+ "','"
						+ Integer.parseInt(sapCode
								.toString())
						+ "',"
						+ shutterSum
						+ ","
						+ slidDoorSum
						+ ",'"
						+ consignee
						+ "','"
						+ consigneePhone
						+ "','"
						+ kunnr
						+ "','"
						+ kunnrName
						+ "','"
						+ ZJIAN
						+ "')");
	}
}
