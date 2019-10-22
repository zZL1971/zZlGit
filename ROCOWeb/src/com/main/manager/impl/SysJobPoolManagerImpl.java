package com.main.manager.impl;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.main.dao.CustHeaderDao;
import com.main.dao.CustLogisticsDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.dao.SaleOneCustDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.cust.CustLogistics;
import com.main.domain.cust.TerminalClient;
import com.main.domain.mm.ImosIdbext;
import com.main.domain.mm.ImosIdbwg;
import com.main.domain.mm.MaterialBujian;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sale.SaleLogistics;
import com.main.domain.sale.SaleOneCust;
import com.main.domain.sys.SysJobPool;
import com.main.domain.sys.SysJobPoolError;
import com.main.manager.SaleManager;
import com.main.manager.SysJobPoolManager;
import com.mw.framework.bean.Constants.Language;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.FieldFunction;
import com.mw.framework.utils.NumberUtils;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import roco.delivery.plugin.CalculateCapacity;
import roco.delivery.plugin.CalculateDelivery;

@Service("sysJobPoolManagerImpl")
@Transactional
public class SysJobPoolManagerImpl implements SysJobPoolManager {
	private static final Logger logger = LoggerFactory
			.getLogger(SysJobPoolManagerImpl.class);
	@Autowired
	private SysJobPoolDao soOrderPoolDao;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private CommonManager commonManager;
	@Autowired
	private MaterialHeadDao materialHeadDao;
	@Autowired
	private SaleOneCustDao saleOneCustDao;
	@Autowired
	private SysDataDictDao sysDataDictDao;
	@Autowired
	private SaleManager saleManager;
	@Autowired
	private CustHeaderDao custHeaderDao;
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	@Autowired
    private CustLogisticsDao custLogisticsDao;
	public SysJobPoolDao getSoOrderPoolDao() {
		return soOrderPoolDao;
	}

	public void setSoOrderPoolDao(SysJobPoolDao soOrderPoolDao) {
		this.soOrderPoolDao = soOrderPoolDao;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public static Logger getLogger() {
		return logger;
	}

	public SaleHeaderDao getSaleHeaderDao() {
		return saleHeaderDao;
	}

	public void setSaleHeaderDao(SaleHeaderDao saleHeaderDao) {
		this.saleHeaderDao = saleHeaderDao;
	}

	public SaleItemDao getSaleItemDao() {
		return saleItemDao;
	}

	public void setSaleItemDao(SaleItemDao saleItemDao) {
		this.saleItemDao = saleItemDao;
	}

	public SaleOneCustDao getSaleOneCustDao() {
		return saleOneCustDao;
	}

	public void setSaleOneCustDao(SaleOneCustDao saleOneCustDao) {
		this.saleOneCustDao = saleOneCustDao;
	}

	public SysDataDictDao getSysDataDictDao() {
		return sysDataDictDao;
	}

	public void setSysDataDictDao(SysDataDictDao sysDataDictDao) {
		this.sysDataDictDao = sysDataDictDao;
	}

	public SaleManager getSaleManager() {
		return saleManager;
	}

	public void setSaleManager(SaleManager saleManager) {
		this.saleManager = saleManager;
	}

	public CustHeaderDao getCustHeaderDao() {
		return custHeaderDao;
	}

	public void setCustHeaderDao(CustHeaderDao custHeaderDao) {
		this.custHeaderDao = custHeaderDao;
	}

	public Message checkCreditBYCust(SysJobPool creditPool) {
		   Message msg = null;
		   Object postFlag="S";
		   Object message="";
		try {
				CommonManager commonManager = SpringContextHolder.getBean("commonManager");
		        JCoDestination connect = SAPConnect.getConnect();
		        if (creditPool.getNetwr() != null && creditPool.getNetwr() > 0) {
			        // 取客户信贷信息
			        JCoFunction functionXd  = connect.getRepository().getFunction("ZCREDIT_CHECKED_POST");
					JCoParameterList importParameterList = functionXd.getImportParameterList();
					
					//importParameterList.setValue("BUKRS",creditPool.getBukrs());
					importParameterList.setValue("BUKRS","3100");
					importParameterList.setValue("KUNNR",creditPool.getKunnr());
					importParameterList.setValue("ZUONR",creditPool.getZuonr());
					importParameterList.setValue("NETWR",creditPool.getNetwr());
		            functionXd.execute(connect);
		            JCoParameterList exportParameterList = functionXd.getExportParameterList();
		            postFlag = exportParameterList.getValue("POST_FLAG");
		            message = exportParameterList.getValue("E_MSG");
		        } 
	            if ("S".equals(postFlag)) {
	            	/*
	            	 * 查询bom是否已经创建
	            	 */
	            	/*System.out.println(creditPool.getZuonr()+":可以过账");
	            	com.sap.conn.jco.JCoFunction functionBom  = connect.getRepository().getFunction("ZRFC_SD_JJYY01");
	    			com.sap.conn.jco.JCoParameterList parameterList = functionBom.getImportParameterList();
	    			parameterList.setValue("I_VBELN",creditPool.getSapCode());
	    			functionBom.execute(connect);
	    			com.sap.conn.jco.JCoParameterList exportParameterList = functionBom.getExportParameterList();
	    			postFlag = exportParameterList.getValue("E_TYPE");
	    			message = exportParameterList.getValue("E_MSG1");
	    			if(StringUtils.isEmpty(postFlag)){
	    			}*/
	    			//Message ms = checkCreditBYCust1(creditPool);
    				creditPool.setJobStatus("B");
    				commonManager.save(creditPool);
    				msg=new Message("加入队列成功");
    				msg.setSuccess(true);
		         }
	            if(msg==null){//如果信贷没问题且bom已经创建 ,那么msg不会为空
	            	msg = new Message("JOB-FIN-500",message.toString());
			       	msg.setSuccess(false);
	            }
	            return msg;
      }catch(IllegalStateException ise){
      	ise.printStackTrace();
      	msg=new Message("JOB-FIN-500","订单释放成功!但是订单预计出货日期没有同步成功!");
      	msg.setSuccess(true);
      	return msg;
      }
      catch (Exception e) {
      	msg = new Message("JOB-FIN-500",e.getLocalizedMessage());
				e.printStackTrace();
				return msg;
			} 
	}
	
	public Map<String,Map<String,Integer>> calculateDelivery(String orderCode,String saleFor,boolean isRepair){
		String findSaleItemByOrderCodeSql = "SELECT SI.ORDER_CODE_POSEX,SH.ORDER_TYPE,SI.IS_STANDARD,MH.MATNR FROM SALE_HEADER SH LEFT JOIN SALE_ITEM SI ON SH.ID=SI.PID LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID=MH.ID WHERE SH.ORDER_CODE=? AND MH.SALE_FOR=? AND NVL(SI.STATE_AUDIT,'E')<>'QX'";
		StringBuffer findImosDeliverySqlZ=new StringBuffer("SELECT * FROM (SELECT * FROM Z_COMPONENT_DELIVERY_VIEW ZC WHERE ZC.ORDERID=? AND ZC.INFO1 NOT LIKE 'H%' ORDER BY ZC.DW_DELIVERY DESC) WHERE ROWNUM=1");//AND CD.INFO1 NOT LIKE 'H%'
		StringBuffer findImosDeliverySqlB=new StringBuffer("SELECT * FROM (SELECT * FROM Z_COMPONENT_DELIVERY_VIEW ZC WHERE ZC.ORDERID=? ORDER BY ZC.REPAIR_ORDER_DELIVERY DESC) WHERE ROWNUM=1");//AND CD.INFO1 NOT LIKE 'H%'
		String cpDeliverySqlZ="SELECT * FROM COMPONENT_CONFIG CD WHERE CD.MATERIAL_CODE=? AND CD.STAT='1' ORDER BY CD.DW_DELIVERY DESC";
		String cpDeliverySqlB="SELECT * FROM COMPONENT_CONFIG CD WHERE CD.MATERIAL_CODE=? AND CD.STAT='1' ORDER BY CD.REPAIR_ORDER_DELIVERY DESC";
		String moreInfo1="SELECT * FROM (SELECT SUBSTR(I.INFO1,0,1) AS INFO1 FROM IMOS_IDBEXT I WHERE I.ORDERID=? GROUP BY SUBSTR(I.INFO1,0,1) ) WHERE INFO1<>'H' AND INFO1 IS NOT NULL";
		String findLineSql="SELECT * FROM LINE_CONFIG CL WHERE CL.LINE_CODE=? AND CL.STAT='1'";
		StringBuffer orderByWhere=new StringBuffer();
		if(isRepair) {
			orderByWhere.append("ORDER BY CD.REPAIR_ORDER_DELIVERY DESC");
		}else {
			orderByWhere.append("ORDER BY CD.DW_DELIVERY DESC");
		}
		List<Map<String, Object>> saleItems = jdbcTemplate.queryForList(findSaleItemByOrderCodeSql,orderCode,saleFor);
		Integer maxDwDelivery = 0;
		Integer maxDnDelivery = 0;
		String line="";
		Map<String, Map<String, Integer>> deliverys=new HashMap<String, Map<String,Integer>>();
		for (Map<String, Object> saleItem : saleItems) {
			String orderCodePosex = ZStringUtils.resolverStr(saleItem.get("ORDER_CODE_POSEX"));
			String isStandard = ZStringUtils.resolverStr(saleItem.get("IS_STANDARD"));
			List<Map<String, Object>> componentConfigs = null;
			if("0".equals(isStandard)) {
				if(isRepair) {
					componentConfigs = jdbcTemplate.queryForList(findImosDeliverySqlB.toString(), orderCodePosex);
					if(componentConfigs.size()<=0) {
						break;
					}
					String repairOrderDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("REPAIR_ORDER_DELIVERY"));
					if(repairOrderDelivery!=null&&!"".equals(repairOrderDelivery)){
						if(maxDwDelivery<=Integer.parseInt(repairOrderDelivery)) {
							maxDwDelivery = Integer.parseInt(repairOrderDelivery);
							maxDnDelivery = Integer.parseInt(repairOrderDelivery);
						}
					}
				}else {
					List<Map<String, Object>> moreInfoList = jdbcTemplate.queryForList(moreInfo1, orderCodePosex);
					componentConfigs = jdbcTemplate.queryForList(findImosDeliverySqlZ.toString(), orderCodePosex);
					if(componentConfigs.size()<=0) {
						break;
					}
					String dwDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("DW_DELIVERY"));
					String dnDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("DN_DELIVERY"));
					if(dwDelivery!=null&&!"".equals(dwDelivery)&&dnDelivery!=null&&!"".equals(dnDelivery)) {
						if(maxDwDelivery<Integer.parseInt(dwDelivery)) {
							maxDwDelivery = Integer.parseInt(dwDelivery);
							maxDnDelivery = Integer.parseInt(dnDelivery);
							line = ZStringUtils.resolverStr(componentConfigs.get(0).get("INFO1"));
							line = line.substring(0,1);
						}
					}
					if(moreInfoList.size()>=2) {
						List<Map<String, Object>> lineConfigList = jdbcTemplate.queryForList(findLineSql,line);
						if(lineConfigList.size()>0&&maxDwDelivery<=12) {
							maxDwDelivery+=Integer.parseInt(ZStringUtils.resolverStr(lineConfigList.get(0).get("M_DELIVERY")));
							maxDnDelivery+=Integer.parseInt(ZStringUtils.resolverStr(lineConfigList.get(0).get("M_DELIVERY")));
						}
					}
				}
			}else if("1".equals(isStandard)) {///MATNR
				String matnr = ZStringUtils.resolverStr(saleItem.get("MATNR"));
				if(isRepair) {
					componentConfigs = jdbcTemplate.queryForList(cpDeliverySqlB, matnr);
				}else {
					componentConfigs = jdbcTemplate.queryForList(cpDeliverySqlZ, matnr);
				}
				maxDnDelivery = 8;
				maxDwDelivery = 8;
				if(isRepair) {
					maxDnDelivery = 2;
					maxDwDelivery = 2;
				}
				if(componentConfigs.size()>0) {
					String dwDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("DW_DELIVERY"));
					String dnDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("DN_DELIVERY"));
					if(dwDelivery!=null&&!"".equals(dwDelivery)&&dnDelivery!=null&&!"".equals(dnDelivery)) {
						maxDwDelivery = Integer.parseInt(dwDelivery);
						maxDnDelivery = Integer.parseInt(dnDelivery);
						if(isRepair) {
							String repairOrderDelivery = ZStringUtils.resolverStr(componentConfigs.get(0).get("REPAIR_ORDER_DELIVERY"));
							maxDwDelivery = Integer.parseInt(repairOrderDelivery);
							maxDnDelivery = Integer.parseInt(repairOrderDelivery);
						}
					}
				}
			}
			Map<String, Integer> delivery = new HashMap<String, Integer>();
			delivery.put("DW", maxDwDelivery);
			delivery.put("DN", maxDnDelivery);
			deliverys.put(orderCodePosex, delivery);
			maxDwDelivery = 0;
			maxDnDelivery = 0;
		}
		String posex="";
		int maxPosexDwDelivery = 0;
		for (Entry<String, Map<String, Integer>> delivery: deliverys.entrySet()) {
			Map<String, Integer> deliveryPosex = delivery.getValue();
			if(maxPosexDwDelivery<deliveryPosex.get("DW")) {
				maxPosexDwDelivery=deliveryPosex.get("DW");
				posex = delivery.getKey();
			}
		}
		Map<String, Map<String, Integer>> realDeliveryDay=new HashMap<String, Map<String,Integer>>();
		realDeliveryDay.put(saleFor , deliverys.get(posex));
		return realDeliveryDay;
	}
	
	
	/*public Map<String,Map<String,Integer>> calculateDelivery(String pid,String saleFor,boolean isRepair){
		Map<String, String[]> parameterMap=new HashMap<String, String[]>();
		parameterMap.put("ICEQsaleHeader__id", new String[] {pid});
		List<SaleItem> saleItemList = commonManager.queryByRange(SaleItem.class, parameterMap);
		List<SaleItem> newSaleItemList=new ArrayList<SaleItem>();
		for (SaleItem saleItem : saleItemList) {
			List<Map<String, Object>> materialSaleFor = jdbcTemplate.queryForList("SELECT * FROM MATERIAL_HEAD MH WHERE MH.ID=? AND MH.SALE_FOR=?",saleItem.getMaterialHeadId(),saleFor);
			if(materialSaleFor.size()>0) {
				newSaleItemList.add(saleItem);
			}
		}
		Map<String,Map<String,Integer>> deliveryMap=new HashMap<String,Map<String,Integer>>();
		Map<String,Map<String,Integer>> realDeliveryDay=new HashMap<String, Map<String, Integer>>();
		Map<String, Object> wLine = new HashMap<String,Object>();
		wLine.put("INFO1", "W");
		for (SaleItem saleItem : newSaleItemList) {
			int maxDnDelivery = 0;
			int maxDwDelivery = 0;
			if("0".equals(saleItem.getIsStandard())) {
				List<Map<String, Object>> imosInfoGroup = jdbcTemplate.queryForList("SELECT * FROM (SELECT SUBSTR(I.INFO1,0,1) AS INFO1 FROM IMOS_IDBEXT I WHERE I.ORDERID=? AND I.INFO1 IS NOT NULL) GROUP BY INFO1",new Object[] {saleItem.getOrderCodePosex()});
				imosInfoGroup.add(wLine);
				for (Map<String, Object> imos : imosInfoGroup) {
					if(imos==null) {
						continue;
					}
					String info1 = ZStringUtils.resolverStr(imos.get("INFO1"));
					parameterMap.clear();
					parameterMap.put("ICEQline", new String[] {info1});
					parameterMap.put("ICEQstat", new String[] {"1"});
					List<ComponentConfig> componentConfigList = commonManager.queryByRange(ComponentConfig.class, parameterMap);
					if(componentConfigList.size()<=0) {//如若产线标识码查询不出信息，则使用外购标识码
						parameterMap.clear();
						parameterMap.put("ICEQline", new String[] {info1});
						parameterMap.put("ICEQstat", new String[] {"1"});
						componentConfigList = commonManager.queryByRange(ComponentConfig.class, parameterMap);
					}
					if("W".equals(info1)) {
						for (ComponentConfig componentConfig : componentConfigList) {
							Integer outIdentifyList = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM IMOS_IDBEXT I WHERE I.ORDERID=? AND I.RENDER=? AND I.TYP='4'", Integer.class,saleItem.getOrderCodePosex(),componentConfig.getOutSourceIdentifyCode());
							if(outIdentifyList >0) {
								if(isRepair) {
									if(maxDwDelivery<componentConfig.getRepairOrderDelivery()) {
										maxDwDelivery = componentConfig.getRepairOrderDelivery();
										maxDnDelivery = componentConfig.getRepairOrderDelivery();
									}
								}else {
									if(maxDwDelivery<componentConfig.getDwDelivery()) {
										maxDwDelivery = componentConfig.getDwDelivery();
										maxDnDelivery = componentConfig.getDnDelivery();
									}
								}
							}
						}
					}else {
						List<Map<String, Object>> imosLines = jdbcTemplate.queryForList("SELECT I.ARTICLE_ID,I.INFO1 FROM IMOS_IDBEXT I WHERE I.ORDERID=? AND I.INFO1 LIKE ?",new Object[] {saleItem.getOrderCodePosex(),"%"+info1+"%"});
						for (ComponentConfig componentConfig : componentConfigList) {
							for (Map<String, Object> imosLine : imosLines) {
								String articleId = ZStringUtils.resolverStr(imosLine.get("ARTICLE_ID"));
								String line = ZStringUtils.resolverStr(imosLine.get("INFO1"));
								if(line.equals(componentConfig.getIdentifyCode())||line.equals(componentConfig.getOutSourceIdentifyCode())||(articleId!=null&&articleId.equals(componentConfig.getMaterialCode()))) {
									if(isRepair) {
										if(maxDwDelivery<componentConfig.getRepairOrderDelivery()) {
											maxDwDelivery = componentConfig.getRepairOrderDelivery();
											maxDnDelivery = componentConfig.getRepairOrderDelivery();
										}
									}else {
										if(maxDwDelivery<componentConfig.getDwDelivery()) {
											maxDwDelivery = componentConfig.getDwDelivery();
											maxDnDelivery = componentConfig.getDnDelivery();
										}
									}
								}
							}
						}
					}
					deliveryMap.put(saleItem.getOrderCodePosex(), getMaxDelivery(maxDnDelivery, maxDwDelivery));
				}
				Map<String, Integer> deliveryVal = deliveryMap.get(saleItem.getOrderCodePosex());
				if(imosInfoGroup.size()>=3) {
					Iterator<Map<String, Object>> iterators = imosInfoGroup.iterator();
					while(iterators.hasNext()) {
						Map<String, Object> map = iterators.next();
						Object hD = map.get("INFO1");
						if(hD!=null&&"H".equals(hD.toString())) {
							iterators.remove();
						}
					}
				}
				if(!isRepair&&imosInfoGroup.size()>=3) {
					int maxLineDelivery=0;
					if(deliveryVal.get("DN").intValue()<=8) {
						for (Map<String, Object> imos : imosInfoGroup) {//跨产线交期 叠加
							if(imos==null) {
								continue;
							}
							String info1 = ZStringUtils.resolverStr(imos.get("INFO1"));
							parameterMap.clear();
							parameterMap.put("ICEQlineCode", new String[] {info1});
							parameterMap.put("ICEQstat", new String[] {"1"});
							List<LineConfig> lineConfigList = commonManager.queryByRange(LineConfig.class, parameterMap);
							if(lineConfigList.size()>0) {
								if(maxLineDelivery<lineConfigList.get(0).getZmDelivery()) {
									maxLineDelivery = lineConfigList.get(0).getZmDelivery();
									if(isRepair) {
										maxLineDelivery = lineConfigList.get(0).getZmRepairOrder();
									}
								}
							}
						}
						Map<String, Integer> moreNewDelivery=new HashMap<String, Integer>();
						moreNewDelivery.put("DW", deliveryVal.get("DW")+maxLineDelivery);
						moreNewDelivery.put("DN", deliveryVal.get("DN")+maxLineDelivery);
						deliveryMap.put(saleItem.getOrderCodePosex(),moreNewDelivery);
					}
				}
			}else if("1".equals(saleItem.getIsStandard())) {
				parameterMap.clear();
				MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
				parameterMap.put("ICEQmaterialCode", new String[] {materialHead.getMatnr()});
				parameterMap.put("ICEQstat", new String[] {"1"});
				List<ComponentConfig> componentConfig = commonManager.queryByRange(ComponentConfig.class, parameterMap);
				maxDnDelivery = 8;
				maxDwDelivery = 8;
				if(isRepair) {
					maxDnDelivery = 2;
					maxDwDelivery = 2;
				}
				if(componentConfig.size()>0) {
					maxDwDelivery = componentConfig.get(0).getDwDelivery();
					maxDnDelivery = componentConfig.get(0).getDnDelivery();
				}
				deliveryMap.put(saleItem.getOrderCodePosex(), getMaxDelivery(maxDnDelivery, maxDwDelivery));
			}
		}
		Integer maxDwDeliveryDay=0;
		String posex="";
		Set<Entry<String, Map<String, Integer>>> delieveryEntrySet = deliveryMap.entrySet();
		for (Entry<String, Map<String, Integer>> entry : delieveryEntrySet) {
			Map<String, Integer> delivery = entry.getValue();
			Integer dwDelivery = delivery.get("DW");
			if(maxDwDeliveryDay<dwDelivery) {
				maxDwDeliveryDay = dwDelivery;
				posex = entry.getKey();
			}
		}
		realDeliveryDay.put(saleFor, deliveryMap.get(posex));
		return  realDeliveryDay;
	}*/

/*	private Map<String,Integer> getMaxDelivery(int maxDnDelivery,
			int maxDwDelivery) {
		Map<String,Integer> delivery=new HashMap<String,Integer>();
		delivery.put("DW",maxDwDelivery);
		delivery.put("DN",maxDnDelivery);
		return delivery;
	}*/
	
	//定时器调用查询信贷额度，计算产能，财务释放
	public Message checkCreditBYCust1(SysJobPool creditPool) {
		synchronized (this) {
			Message msg = new Message("success");
			msg.setSuccess(false);
			Object postFlag = "S";
			Object message = "";
			try {
				JCoDestination connect = SAPConnect.getConnect();
				if (creditPool.getNetwr() != null && creditPool.getNetwr() > 0) {
					// 取客户信贷信息
					JCoFunction functionXd = connect.getRepository().getFunction(
							"ZCREDIT_CHECKED_POST");
					JCoParameterList importParameterList = functionXd
							.getImportParameterList();

					importParameterList.setValue("BUKRS", "3100");
					importParameterList.setValue("KUNNR", creditPool.getKunnr());
					importParameterList.setValue("ZUONR", creditPool.getZuonr());
					importParameterList.setValue("NETWR", creditPool.getNetwr());
					functionXd.execute(connect);
					JCoParameterList exportParameterList = functionXd
							.getExportParameterList();
					postFlag = exportParameterList.getValue("POST_FLAG");
					message = exportParameterList.getValue("E_MSG");
				}
				if ("S".equals(postFlag)) {

					// 释放金额
					// ZRFC_REL_REJECT_REASON 接口名
					// 输入参数
					// I_VBELN
					// SAP 订单号
					// 输出E_TYPE S-成功. E-失败
					// E_TYPE TYPE BAPI_MTYPE S-成功. E-失败
					// E_MSG1 TYPE CHAR255 拒绝原因修改返回消息
					// E_MSG2 TYPE CHAR255 交货日期返回消息
					List<SysJobPoolError> sysJobPoolErrors=new ArrayList<SysJobPoolError>();
					String orderCode = creditPool.getZuonr();
					List<SaleHeader> saleHeaders = saleHeaderDao.findByCode(orderCode);
					if(saleHeaders.size() == 1) {
						SaleHeader saleHeader = saleHeaders.get(0);
						// 只针对OR7 OR1 OR8 OR9的订单进行产能计算
						List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
						if(saleLogisticsList.size()>0) {
							HashSet<String> errorMeaasge=new HashSet<String>();
							for (SaleLogistics saleLogistics : saleLogisticsList) {
								Double totalPrice = jdbcTemplate.queryForObject("SELECT SUM(SI.TOTAL_PRICE) AS TOTAL_PTICE FROM SALE_ITEM SI LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID WHERE SI.PID=(SELECT SH.ID FROM SALE_HEADER SH WHERE SH.ORDER_CODE=?) AND NVL(SI.STATE_AUDIT,'E')<>'QX' AND MH.SALE_FOR=?",Double.class, creditPool.getZuonr(),saleLogistics.getSaleFor());
								//计算交期天数
								CalculateDelivery deliveryInstance=new CalculateDelivery(orderCode, saleLogistics.getSaleFor(), saleHeader.getOrderType()); 
								deliveryInstance.setJdbcTemplate(jdbcTemplate);
								deliveryInstance.calculateDelivery(true);
								if(deliveryInstance.getDnDelivery()<=0&&deliveryInstance.getDwDelivery()<=0) {
									throw new RuntimeException("交期天数计算出错");
								}
								//计算产能
								CalculateCapacity calculateCapacity=new CalculateCapacity(saleLogistics.getSaleFor(), orderCode,saleHeader.getOrderType(),jdbcTemplate);
								calculateCapacity.setDwDelivery(deliveryInstance.getDwDelivery()+1);
								calculateCapacity.setDnDelivery(deliveryInstance.getDnDelivery()+1);
								calculateCapacity.calculateCapacity();
								
								saleLogistics.setDeliveryDay(deliveryInstance.getDwDelivery().toString());
								saleLogistics.setZzjqlb(deliveryInstance.getZzjqlb());
								
								JCoFunction function = connect.getRepository().getFunction(
										"ZRFC_REL_REJECT_REASON");
								JCoTable table = function.getTableParameterList().getTable(
										"IT_TAB");
								JCoParameterList ipList = function.getImportParameterList();
								ipList.setValue("I_VBELN", saleLogistics.getSapCode());
								ipList.setValue("ZKUNNR", creditPool.getKunnr());
								ipList.setValue("ZZUONR", creditPool.getZuonr());
								ipList.setValue("ZNETWR", totalPrice);
								
								table.appendRow();
								table.setValue("MANDT", "800");
								table.setValue("VDATU", DateTools.formatDate(calculateCapacity.getPpcDate(),
										DateTools.defaultFormat));
								table.setValue("ZZSCDT", DateTools.formatDate(calculateCapacity.getPsDate(),
										DateTools.defaultFormat));
								table.setValue("BSTZD", String.valueOf(deliveryInstance.getDwDelivery()));
								table.setValue("ZZJQLB", deliveryInstance.getZzjqlb());
								function.execute(connect);
								JCoParameterList epList = function.getExportParameterList();
								Object flag = epList.getValue("E_TYPE");
								Object errMes = epList.getValue("E_MSG1");
								errorMeaasge.add((String)flag);
								if ("S".equals(flag) || "W".equals(flag)) {
									msg.setSuccess(true);
									saleLogistics.setPsDate(calculateCapacity.getPsDate());
									saleLogistics.setPpcDate(calculateCapacity.getPpcDate());
									commonManager.save(saleLogistics);
								}else {
									msg = new Message("JOB-FIN-500", errMes.toString());
									msg.setSuccess(false);
									return msg;
								}
							}
							if(errorMeaasge.size()>1) {
								if(errorMeaasge.contains("E")) {
									msg = new Message("JOB-FIN-500", "释放失败");
									msg.setSuccess(false);
								}
							}
						}else {
							msg = new Message("JOB-FIN-500", "交期未维护");
							msg.setSuccess(false);
						}
					}
				} else {
					msg = new Message("JOB-FIN-500", message.toString());
				}
				return msg;
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
				msg = new Message("JOB-FIN-500", "订单释放成功!但是订单预计出货日期没有同步成功!");
				msg.setSuccess(true);
				return msg;
			} catch (Exception e) {
				msg = new Message("JOB-FIN-500", e.getLocalizedMessage());
				e.printStackTrace();
				return msg;
			}
		}
		
	}

	private Date insertCapacityNumData(List<Map<String, Object>> sapZstPpRl01List) {
		Date currentDate = null;
		if(sapZstPpRl01List.size()<=0) {
			throw new TypeCastException("计算产能失败，工厂日历未维护，请同步或维护工厂日历");
		}
		Map<String, Object> sapZstPpRl01Map = sapZstPpRl01List.get(0);
		currentDate =(Date) sapZstPpRl01Map.get("WERKS_DATE");
		int result = jdbcTemplate
				.queryForObject(
						"SELECT COUNT(1) AS S FROM CAPACITY_G T WHERE T.SCHEDUL_DATE= ?",
						new Object[] { currentDate },
						Integer.class);
		if (result <= 0) {
			jdbcTemplate.update("INSERT INTO CAPACITY_G VALUES(?,0)",
					currentDate);
		}
		return currentDate;
	}
	
	@Override
	public Message sendMM(JCoDestination connect, SaleHeader saleHeader,
			List<SaleItem> saleItemList, String vkorg, String vtweg)
			throws JCoException {
		 Message msg = null;
	      JCoFunction functionMM = connect.getRepository().getFunction("ZRFC_MM_MM01");// SAP创建物料接口
          JCoTable itMatTable = functionMM.getTableParameterList().getTable("IT_MAT");// IMOS接口提供物料主数据结构
          JCoTable itImos01Table = functionMM.getTableParameterList().getTable("IT_IMOS01");// IMOS对接数据-主表-物料产品层次用
          // List<SaleItem> saleItemList = saleItemDao.findItemsByPid(saleHeader.getId());
          // saleItemDao.findItemsByPid(saleHeader.getId());
          // 需要创建的物料
          List<MaterialHead> createMaterialHeadList = new ArrayList<MaterialHead>();
          if (saleItemList != null && saleItemList.size() > 0) {
              // 订单行项目需要创建的物料ID
              Set<String> createMmHeadIdSet = new HashSet<String>();
              // 订单行项目的订单与行项目关联编号
              Set<String> orderCodePosexSet = new HashSet<String>();
              // key = orderCodePosex, value = 物料头的SerialNumber
              Map<String, Object> orderCodePosexMap_serialNumber = new HashMap<String, Object>();

              for (Iterator iterator = saleItemList.iterator(); iterator.hasNext();) {
                  SaleItem saleItem = (SaleItem) iterator.next();
                  if ("0".equals(saleItem.getIsStandard()) && !"QX".equals(saleItem.getStateAudit())) {// 非标物料
                      createMmHeadIdSet.add(saleItem.getMaterialHeadId());
                      orderCodePosexSet.add(saleItem.getOrderCodePosex());
                  }
              }
              createMaterialHeadList = saleManager.createQueryByIn(MaterialHead.class, "id", createMmHeadIdSet);
              for (MaterialHead materialHead : createMaterialHeadList) {
                  for (SaleItem saleItem : saleItemList) {
                      if (ZStringUtils.isNotEmpty(saleItem.getMaterialHeadId())
                              && ZStringUtils.isNotEmpty(materialHead.getId())
                              && saleItem.getMaterialHeadId().equals(materialHead.getId())
                              && !"QX".equals(saleItem.getStateAudit())) {
                          orderCodePosexMap_serialNumber.put(saleItem.getOrderCodePosex(), materialHead
                                  .getSerialNumber());
                          break;
                      }
                  }
                  itMatTable.appendRow();
                  // MATNR MATNR 物料号 CHAR 18
                  itMatTable.setValue("MATNR", materialHead.getMatnr());
                  // EXMAT ZEXMAT 外部物料号 CHAR 18
                  itMatTable.setValue("EXMAT", materialHead.getSerialNumber());
                  // VKORG VKORG 销售组织 CHAR 4
                  itMatTable.setValue("VKORG", vkorg);
                  // VTWEG VTWEG 分销渠道 CHAR 2
                  itMatTable.setValue("VTWEG", vtweg);
                  // MTART MTART 物料类型 CHAR 4
                  itMatTable.setValue("MTART", materialHead.getMtart());
                  // MAKTX MAKTX 物料描述（短文本） CHAR 40
                  itMatTable.setValue("MAKTX", materialHead.getMaktx());
                  // MATKL MATKL 物料组 CHAR 9
                  itMatTable.setValue("MATKL", materialHead.getMatkl());
                  // EXTWG EXTWG 外部物料组 CHAR 18
                  itMatTable.setValue("EXTWG", materialHead.getColor());
                  // BRGEW BRGEW 毛重 QUAN 13 3
                  itMatTable.setValue("BRGEW", materialHead.getBrgew());
                  // NTGEW NTGEW 净重 QUAN 13 3
                  itMatTable.setValue("NTGEW", materialHead.getNtgew());
                  // GEWEI GEWEI 重量单位 UNIT 3
                  itMatTable.setValue("GEWEI", materialHead.getGewei());
                  // VOLUM VOLUM 包数 QUAN 13 3
                  itMatTable.setValue("VOLUM", materialHead.getVolum());
                  // VOLEH VOLEH 体积单位 UNIT 3
                  itMatTable.setValue("VOLEH", materialHead.getVoleh());
                  // GROES GROES 大小/量纲 CHAR 32
                  itMatTable.setValue("GROES", materialHead.getGroes());
                  // ZXLCP ZXLCP 系列产品 NUMC 3
                  // ZCPLB ZCPLB 产品类别 NUMC 4
              }

              // List<ImosIdbext> imosIdbextList =
              // saleManager.createQueryByIn(ImosIdbext.class, "orderid",
              // createMmHeadIdSet);

			if (orderCodePosexSet.size() > 0) {
				StringBuffer sb = new StringBuffer();
				int i = 0;
				for (String str : orderCodePosexSet) {
					sb.append("'").append(str);
					if (i < orderCodePosexSet.size() - 1) {
						sb.append("',");
					} else {
						sb.append("'");
					}
					i++;
				}
				List<Map<String, Object>> imosIdbextList = jdbcTemplate
						.queryForList("select t.* from IMOS_IDBEXT t where t.orderid in ("+ sb.toString() + ")");
				for (Map<String, Object> map : imosIdbextList) {
                      // BeanUtils.tranMapToObj(map, imosIdbext);
                      itImos01Table.appendRow();
                      // POSEX POSEX 优先采购订单的项目号 CHAR 6
                      itImos01Table.setValue("POSEX",
                      		Integer.parseInt(map.get("ORDERID").toString().split(saleHeader.getOrderCode())[1]));
                      // EXMAT ZEXMAT 外部物料号 CHAR 18
                      itImos01Table.setValue("EXMAT",
                              orderCodePosexMap_serialNumber.get(map.get("ORDERID")) == null ? ""
                                      : orderCodePosexMap_serialNumber.get(map.get("ORDERID")).toString());
                      // ID ZID ID From Imos CHAR 30
                      itImos01Table.setValue("ID", map.get("ID") == null ? "" : map.get("ID").toString());
                      // RENDERPMAT ZRENDERPMAT IMOS->对应SAP物料号 CHAR 18
                      // TYP ZTYP 类型 CHAR 1
                      itImos01Table.setValue("TYP", map.get("TYP") == null ? "" : map.get("TYP").toString());
                      // PARTTYPE ZPARTTYPE 板件类型 INT4 10
                      if (ZStringUtils.isNotEmpty(map.get("PARTTYPE"))) {
                          itImos01Table.setValue("PARTTYPE", new Integer(map.get("PARTTYPE").toString()));
                      }
                      // NAME ZNAME1 名称1 CHAR 32
                      itImos01Table.setValue("NAME", map.get("NAME") == null ? "" : map.get("NAME").toString());
                      // NAME2 ZNAME2 名称2 CHAR 30
                      itImos01Table.setValue("NAME2", map.get("NAME2") == null ? "" : map.get("NAME2").toString());
                      // LENGTH ZLENGTH 长度 QUAN 13 3
                      if (ZStringUtils.isNotEmpty(map.get("LENGTH"))) {
                          itImos01Table.setValue("LENGTH", new Double(map.get("LENGTH").toString()));
                      }
                      // WIDTH ZWIDTH 宽度 QUAN 13 3
                      if (ZStringUtils.isNotEmpty(map.get("WIDTH"))) {
                          itImos01Table.setValue("WIDTH", new Double(map.get("WIDTH").toString()));
                      }
                      // THICKNESS ZTHICKNESS 厚度 QUAN 13 3
                      if (ZStringUtils.isNotEmpty(map.get("THICKNESS"))) {
                          itImos01Table.setValue("THICKNESS", new Double(map.get("THICKNESS").toString()));
                      }
                      // PARENTID ZPARENTID 父ID CHAR 30
                      itImos01Table.setValue("PARENTID", map.get("PARENTID") == null ? "" : map.get("PARENTID")
                              .toString());
                      // ARTICLE_ID ZARTICLE_ID 产品_编号 来自ERP中的板件编号 CHAR 18
                      itImos01Table.setValue("ARTICLE_ID", map.get("ARTICLE_ID") == null ? "" : map.get("ARTICLE_ID")
                              .toString());
                      // INFO1 ZINFO1 信息1-识别码 CHAR 15
                      itImos01Table.setValue("INFO1", map.get("INFO1") == null ? "" : map.get("INFO1").toString());
                      // INFO2 ZINFO2 信息2 CHAR 80
//                      if(map.get("INFO2") == null){
//                    	  itImos01Table.setValue("INFO2","");
//                      }else if(map.get("INFO2").equals("1")){
//                    	  itImos01Table.setValue("INFO2","左外切");
//                      }else if(map.get("INFO2").equals("0")){
//                    	  itImos01Table.setValue("INFO2","左内切");
//                      }
//                      if(map.get("INFO3") == null){
//                    	  itImos01Table.setValue("INFO3","");
//                      }else if(map.get("INFO3").equals("1")){
//                    	  itImos01Table.setValue("INFO3","右外切");
//                      }else if(map.get("INFO3").equals("0")){
//                    	  itImos01Table.setValue("INFO3","右内切");
//                      }
                      itImos01Table.setValue("INFO2", map.get("INFO2") == null ? "" : map.get("INFO2").toString());
                      itImos01Table.setValue("INFO3", map.get("INFO3") == null ? "" : map.get("INFO3").toString());
                      //itImos01Table.setValue("INFO2", map.get("INFO2") == null ? "" : map.get("INFO2").toString());
                      // INFO3 ZINFO3 信息3 CHAR 80
                      //itImos01Table.setValue("INFO3", map.get("INFO3") == null ? "" : map.get("INFO3").toString());
                      // INFO4 ZINFO4 信息4 CHAR 80
                      itImos01Table.setValue("INFO4", map.get("INFO4") == null ? "" : map.get("INFO4").toString());
                      // INFO5 ZINFO5 信息5 CHAR 80
                      itImos01Table.setValue("INFO5", map.get("INFO5") == null ? "" : map.get("INFO5").toString());
                      // COLOR1 ZCOLOR1 颜色1 CHAR 64
                      itImos01Table.setValue("COLOR1", map.get("COLOR1") == null ? "" : map.get("COLOR1").toString());
                      // COLOR2 ZCOLOR2 颜色2 CHAR 64
                      itImos01Table.setValue("COLOR2", map.get("COLOR2") == null ? "" : map.get("COLOR2").toString());
                  }
              }

          }
          int row = itMatTable.getNumRows();
          if (row > 0) {
              functionMM.execute(connect);
              JCoTable itReturnTable = functionMM.getTableParameterList().getTable("IT_RETURN");
              StringBuffer errSb = new StringBuffer();
              if (itReturnTable.getNumRows() > 0) {
                  itReturnTable.firstRow();
                  // List<String> list = new ArrayList<String>();
                  for (int i = 0; i < itReturnTable.getNumRows(); i++, itReturnTable.nextRow()) {
                      Object type = itReturnTable.getValue("TYPE");
                      Object message = itReturnTable.getValue("MESSAGE");
                      Object logNo = itReturnTable.getValue("LOG_NO");
                      Object messageV1 = itReturnTable.getValue("MESSAGE_V1");
                      if (type != null && "S".equals(type.toString())) {// 成功创建的物料
                          if (ZStringUtils.isNotEmpty(logNo) && ZStringUtils.isNotEmpty(messageV1)) {
                              for (Iterator iterator = createMaterialHeadList.iterator(); iterator.hasNext();) {
                                  MaterialHead materialHead = (MaterialHead) iterator.next();
                                  if (materialHead.getSerialNumber().equals(logNo.toString().trim())) {
                                      // 回写更新MATERIAL_HEAD的MATNR
                                      materialHead.setMatnr(messageV1.toString().trim());
                                      for (Iterator it = saleItemList.iterator(); it.hasNext();) {
                                          SaleItem saleItem = (SaleItem) it.next();
                                          System.out.println(messageV1.toString().trim());
                                          if (saleItem.getMaterialHeadId().equals(materialHead.getId())) {
                                              // 回写更新sale_item的matnr
                                        	  
                                              saleItem.setMatnr(messageV1.toString().trim());
                                          }
                                      }
                                  }
                              }
                          }
                          // // 回写更新MATERIAL_HEAD的MATNR
                          // String sql = "update MATERIAL_HEAD set MATNR='"
                          // + (ZStringUtils.isEmpty(messageV1) ? ""
                          // : messageV1.toString().trim())
                          // + "' where SERIAL_NUMBER='"
                          // + (ZStringUtils.isEmpty(logNo) ? "" : logNo
                          // .toString().trim()) + "'";
                          // // 回写更新sale_item的matnr
                          // String sql2 = "update sale_item set MATNR='"
                          // + (ZStringUtils.isEmpty(messageV1) ? ""
                          // : messageV1.toString().trim())
                          // + "' where MATERIAL_HEAD_ID in ("
                          // +
                          // "select id from MATERIAL_HEAD where SERIAL_NUMBER='"
                          // + (ZStringUtils.isEmpty(logNo) ? "" : logNo
                          // .toString().trim()) + "')";
                          // list.add(sql);
                          // list.add(sql2);

                          // // 回写更新MATERIAL_HEAD的MATNR
                          // int update = jdbcTemplate
                          // .update("update MATERIAL_HEAD set MATNR='"
                          // + (ZStringUtils.isEmpty(messageV1) ? ""
                          // : messageV1.toString()
                          // .trim())
                          // + "' where SERIAL_NUMBER='"
                          // + (ZStringUtils.isEmpty(logNo) ? ""
                          // : logNo.toString().trim())
                          // + "'");
                          // // 回写更新sale_item的matnr
                          // int update2 = jdbcTemplate
                          // .update("update sale_item set MATNR='"
                          // + (ZStringUtils.isEmpty(messageV1) ? ""
                          // : messageV1.toString()
                          // .trim())
                          // + "' where MATERIAL_HEAD_ID in ("
                          // +
                          // "select id from MATERIAL_HEAD where SERIAL_NUMBER='"
                          // + (ZStringUtils.isEmpty(logNo) ? ""
                          // : logNo.toString().trim())
                          // + "')");
                          // System.out.println("update=================>" +
                          // update);
                          // System.out.println("update2=================>" +
                          // update2);
                      } else {// 创建物料失败信息
                          errSb.append(message == null ? "" : message.toString()).append("<br/>");
                      }
                  }
                  // if (list.size() > 0) {
                  // String[] array = list.toArray(new String[list.size()]);
                  // int[] batchUpdate = jdbcTemplate.batchUpdate(array);
                  // for (int i : batchUpdate) {
                  // System.out.println("=================>" + i);
                  // }
                  // }
                 
              } else {
                  errSb.append("接口异常，没有返回信息");
              }
              // 有物料创建失败时返回页面，重新创建订单
              if (!errSb.toString().equals("")) {
                  msg = new Message("SALE-500", errSb.toString());
                  return msg;
              }
           
          }
          msg = new Message("OK");
		return msg;
	}
	@Override
	public Message sendSale(JCoDestination connect, SaleHeader saleHeader,
    		List<SaleItem> saleItemList,CustHeader custHeader,String userId) throws JCoException {
		 Message msg = null;
		 // 当前日期
        Date currDate = new Date();
        int count = 0;
        String jiaoQiTianShu = saleHeader.getJiaoQiTianShu();
        if (ZStringUtils.isNotEmpty(jiaoQiTianShu)) {
            count = Integer.parseInt(jiaoQiTianShu.trim());
        }
        /* long secondBetweenTwoDay = DateTools.getSecondBetweenTwoDay(DateTools.strToDate(DateTools.getDate()
                + " 12:00:00", DateTools.fullFormat), currDate);
        if (secondBetweenTwoDay > 0) {// 中午12点后加1
            count += 1;
        }*/
        StringBuffer sb = new StringBuffer();
        sb.append("select * from (");
        sb.append("select row_.*, rownum rownum_ from (");
        sb.append("select t.* from SAP_ZST_PP_RL01 t where t.work='X' and t.werks_date>=to_date('").append(
                DateTools.formatDate(currDate, DateTools.defaultFormat)).append(
                "','yyyy-mm-dd') order by t.werks_date");
        sb.append(") row_ where rownum <= ").append(count).append(") where rownum_ > 0 and rownum_ = ").append(
                count);
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sb.toString());
        Object werksDate = null;
        if (queryForList != null && queryForList.size() > 0) {
            werksDate = queryForList.get(0).get("WERKS_DATE");
        }
        JCoFunction function= connect.getRepository().getFunction("ZRFC_SD_CREAT_SO_NEW");// SAP创建销售订单接口 ZRFC_SD_CREAT_SO
        JCoStructure structure = function.getImportParameterList().getStructure("IM_S_HEAD");// 销售订单头结构
        //ZZAUFNR 内部订单号
		structure.setValue("ZZAUFNR", saleHeader.getZzaufnr());
		//ZSALE_TYPE 销售分类
		/*String sale_for="";
		if(saleHeader.getSaleFor()!=null&&!"".equals(saleHeader.getSaleFor())){
			if("0".equals(saleHeader.getSaleFor())){
				sale_for="YG";
			}else if("1".equals(saleHeader.getSaleFor())){
				sale_for="CG";
			}else if("3".equals(saleHeader.getSaleFor())){
				sale_for="MM";
			}
		}
		structure.setValue("ZSALE_TYPE",sale_for);*/
        // BSTNK BSTNK 客户采购订单编号 CHAR 20
        String orderCode = saleHeader.getOrderCode();
        String pOrderCode = saleHeader.getpOrderCode();
        String toushusql = "SELECT COUNT(SH.P_ORDER_CODE) AS TOUSUCISHU FROM SALE_HEADER SH WHERE SH.P_ORDER_CODE='"+pOrderCode+"'";
         List<Map<String, Object>> toushuList = jdbcTemplate.queryForList(toushusql);
         Map<String, Object> toushuMap  = toushuList.get(0);
         Object tousucishu = toushuMap.get("TOUSUCISHU");
         int toushu = Integer.parseInt(String.valueOf(tousucishu));
        structure.setValue("BSTNK", orderCode);
        //	ZCKDDBH -参考订单编号(补购)
        if(saleHeader.getpOrderCode()!=null&&""!=saleHeader.getpOrderCode()) {
        	structure.setValue("ZCKDDBH", pOrderCode);
        }
        //ZBJLSH-板件流水号
        if("OR4".equals(saleHeader.getOrderType())) {
        	structure.setValue("ZBJLSH", "B0"+toushu);
        }
        //是否通知客服检测
        if("1".equals(saleHeader.getIsKf())) {
        	structure.setValue("ZZKFJC", "X");
        }
        //是否通知质检检测
        if("1".equals(saleHeader.getIsQc())) {
        	structure.setValue("ZZQJK", "X");
        }
        //是否需要木架
        if("1".equals(saleHeader.getIsMj())) {
        	structure.setValue("ZZDMJ", "X");
        }
        
        if("UR2".equals(saleHeader.getUrgentType())) {
        	structure.setValue("ZZCLSX", "X");
        }
        // BSTDK BSTDK 客户采购订单日期 DATS 8
        structure.setValue("BSTDK", DateTools.formatDate(saleHeader.getOrderDate(), DateTools.YYMMDDFormat));
        // AUART AUART 销售凭证类型 CHAR 4
        structure.setValue("AUART", saleHeader.getOrderType());
        if (custHeader != null) {
            Set<CustItem> custItemSet = custHeader.getCustItemSet();
            for (Iterator iterator = custItemSet.iterator(); iterator.hasNext();) {
                CustItem custItem = (CustItem) iterator.next();
                if ("1".equals(custItem.getStatus())) {// 已经激活的
                    // ZZFDMC ZZFDMC 返点名称 CHAR 15
                	String fandian=sysDataDictDao.getDescForI18N("FAN_DIAN_NAME", custItem.getFanDianName(), Language.zh_CN);
                    structure.setValue("ZZFDMC", fandian==null?"":fandian);
                    break;
                }
            }
        }
        // VKORG VKORG 销售组织 CHAR 4
        structure.setValue("VKORG", custHeader.getVkorg());
        // VTWEG VTWEG 分销渠道 CHAR 2
        structure.setValue("VTWEG", custHeader.getVtweg());
        // SPART SPART 产品组 CHAR 2
        structure.setValue("SPART", "01");
        // KUNNR KUNNR 客户编号 CHAR 10
        structure.setValue("KUNNR", saleHeader.getShouDaFang());
        // KUSDF ZKUWE 送达方编号 CHAR 10
        if(saleHeader.getSongDaFang()!=null) {
        	structure.setValue("KUSDF", saleHeader.getSongDaFang());
        }

        // VDATU EDATU_VBAK 请求交货日期 DATS 8
        if (ZStringUtils.isNotEmpty(werksDate)) {
            structure.setValue("VDATU", DateTools.formatDate((Date) werksDate, DateTools.YYMMDDFormat));
        }
        
    	TerminalClient terminalClient = saleHeader.getTerminalClient();
    	// ZZNAME ZZNAME 客户姓名 CHAR 15
    	structure.setValue("ZZNAME", terminalClient.getName1());
    	// ZZGEND ZZGEND 客户性别 CHAR 2
    	if (ZStringUtils.isNotEmpty(terminalClient.getSex())) {
    		List<SysDataDict> sexSysDataDict = sysDataDictDao.findByTrieTreeKeyVal("SEX");
    		if (sexSysDataDict != null && sexSysDataDict.size() > 0) {
    			for (SysDataDict sysDataDict : sexSysDataDict) {
    				if (sysDataDict.getKeyVal().equals(terminalClient.getSex())) {
    					structure.setValue("ZZGEND", sysDataDict.getDescZhCn());
    				}
    			}
    		}
    	}
    	// ZZAGE ZZAGE 客户年龄 CHAR 3
    	structure.setValue("ZZAGE", terminalClient.getAge());
    	// ZZBITD ZZBITD 客户生日 DATS 8
    	if (terminalClient.getBirthday() != null)
    		structure.setValue("ZZBITD", DateTools.formatDate(terminalClient
    				.getBirthday(), DateTools.YYMMDDFormat));
    	// ZZPHON ZZPHON 客户电话 CHAR 16
    	structure.setValue("ZZPHON", terminalClient.getTel());
    	// ZZADRS ZZADRS 客户地址 CHAR 80
    	structure.setValue("ZZADRS", terminalClient.getAddress());
    	// ZZJEFW ZZJEFW 金额范围 NUMC 3
    	structure.setValue("ZZJEFW", terminalClient.getOrderPayFw());
    	// ZZHSTY ZZHSTY 客户户型 NUMC 3
    	structure.setValue("ZZHSTY", terminalClient.getHuXing());
    	// ZZREMK ZZREMK 备注 CHAR 80
    	structure.setValue("ZZREMK", terminalClient.getCustRemarks());
    	
    	// ZZSJHM ZZSJHM 审价员 NUMC 2
    	structure.setValue("ZZSJHM", saleHeader.getCheckPriceUser());
    	
    	if (ZStringUtils.isNotEmpty(userId)) {
    		// ZZCWHM ZZCWHM 财务确认人员 CHAR 12
    		structure.setValue("ZZCWHM", userId);
    	}
    	
    	// ZZYPFG ZZYPFG 是否样品 CHAR 1
    	if (ZStringUtils.isNotEmpty(saleHeader.getIsYp())) {
    		if ("1".equals(saleHeader.getIsYp())) {// 是
    			structure.setValue("ZZYPFG", "X");
    		} else if ("0".equals(saleHeader.getIsYp())) {// 否
    			structure.setValue("ZZYPFG", "");
    		}
    	}
    	// ZZAZFG ZZAZFG 是否需要安装 CHAR 1
    	if (ZStringUtils.isNotEmpty(terminalClient.getIsAnZhuang())) {
    		if ("1".equals(saleHeader.getIsYp())) {// 是
    			structure.setValue("ZZAZFG", "X");
    		} else if ("0".equals(saleHeader.getIsYp())) {// 否
    			structure.setValue("ZZAZFG", "");
    		}
    	}
   
        // 付款条件
        String fuFuanCond = saleHeader.getFuFuanCond();
        
        // ZZHDLX ZZHDLX 活动类型 NUMC 3
        structure.setValue("ZZHDLX", saleHeader.getHuoDongType());
        // ZZYSFG ZZYSFG 是否预收款50% CHAR 1
        structure.setValue("ZZYSFG", fuFuanCond);
        // ZZDRMK ZZDRMK 备注 CHAR 80
        structure.setValue("ZZDRMK", saleHeader.getRemarks());

        // ZZCJUN ZZCJUN 销售订单创建人 NUMC 2
        structure.setValue("ZZCJUN", saleHeader.getCreateUser());
        // ZZJQTS ZZJQTS 投诉日期 DATS 8
        structure.setValue("ZZJQTS", saleHeader.getJiaoQiTianShu());
        
     // ZZTSDT ZZTSDT 投诉日期 DATS 8
        structure.setValue("ZZTSDT", saleHeader.getCreateTime());
        List<SaleOneCust> saleOneCustList = saleOneCustDao.findSaleOneCustsByPid(saleHeader.getId());
        if (saleOneCustList != null && saleOneCustList.size() > 0) {
            JCoTable imTAddrsTable = function.getTableParameterList().getTable("IM_T_ADDRS");// 客户地址信息
            for (Iterator iterator = saleOneCustList.iterator(); iterator.hasNext();) {
                SaleOneCust saleOneCust = (SaleOneCust) iterator.next();
                imTAddrsTable.appendRow();
                // KUNNR KUNNR 客户编号 CHAR 10
                imTAddrsTable.setValue("KUNNR", saleOneCust.getKunnr());
                // ANRED ANRED 标题 CHAR 15
                imTAddrsTable.setValue("ANRED", saleOneCust.getAnred());
                // NAME1 NAME1_GP 名称 1 CHAR 35
                imTAddrsTable.setValue("NAME1", saleOneCust.getSaleOneCustName1());
                // STREET AD_STREET 街道 CHAR 60
                imTAddrsTable.setValue("STREET", saleOneCust.getSocAddress());
                // HOUSE_NUM1 AD_HSNM1 门牌号 CHAR 10
                // PSTLZ PSTLZ 邮政编码 CHAR 10
                imTAddrsTable.setValue("PSTLZ", saleOneCust.getPstlz());
                // ORT01 ORT01 城市 CHAR 25
                imTAddrsTable.setValue("ORT01", saleOneCust.getMcod3());
                // LAND1 LAND1 国家键值 CHAR 3
                imTAddrsTable.setValue("LAND1", saleOneCust.getLand1()==null?"CN":saleOneCust.getLand1());
                // REGIO REGIO 地区（省/自治区/直辖市、市、县） CHAR 3
                imTAddrsTable.setValue("REGIO", saleOneCust.getRegio());
                // TELF1 TELF1 第一个电话号 CHAR 16
                imTAddrsTable.setValue("TELF1", saleOneCust.getTelf1());
                // LZONE LZONE 发送货物的目的地运输区域 CHAR 10
                imTAddrsTable.setValue("LZONE", saleOneCust.getOrt02());
            }
        }
        // saleItemList = saleItemDao.findItemsByPid(saleId);
        if (saleItemList != null && saleItemList.size() > 0) {
            // 订单行项目包含的散件ID
            Set<String> sjHeadIdSet = new HashSet<String>();
            //订单行项目包含的费用化ID
//            Set<String> fyhHeadIdSet = new HashSet<String>();
            // 订单行项目包含的物料ID
            Set<String> mmHeadIdSet = new HashSet<String>();
            // 订单行项目的订单与行项目关联编号
            Set<String> orderCodePosexSet = new HashSet<String>();
            // 订单行项目包含的补件ID
            Set<String> bjIdSet = new HashSet<String>();
            // 订单行项目包含的我的商品ID
            Set<String> myGoodsIdSet = new HashSet<String>();
            JCoTable imTText = function.getTableParameterList().getTable("IM_T_TEXT");// 销售订单文本
            for (Iterator iterator = saleItemList.iterator(); iterator.hasNext();) {
                SaleItem saleItem = (SaleItem) iterator.next();
                // 取消状态不传SAP
                if (!"QX".equals(saleItem.getStateAudit())) {
                    // 物料说明(中文)
                    String maktx = saleItem.getMaktx();
                    if (ZStringUtils.isNotEmpty(maktx)) {
                        int totalLen = maktx.length();
                        int j = 1;
                        for (int i = 0; i < totalLen; i = i + 132) {
                            String currText = "";
                            if (maktx.length() > 132) {
                                currText = maktx.substring(i, i + 132);
                                maktx = maktx.substring(currText.length(), maktx.length());
                            } else {
                                currText = maktx;
                            }
                            imTText.appendRow();
                            // POSEX POSEX 优先采购订单的项目号 CHAR 6
                            imTText.setValue("POSEX", saleItem.getPosex());
                            // FORMAT_COL TDFORMAT 标记列 CHAR 2
                            imTText.setValue("FORMAT_COL", j);
                            // TEXT_LINE TDLINE 文本行 CHAR 132
                            imTText.setValue("TEXT_LINE", currText);
                            j++;
                        }
                    }
                    if(saleItem.getSanjianHeadId()!=null) {
                    	sjHeadIdSet.add(saleItem.getSanjianHeadId());
                    }
//                    if(saleItem.getExpenditureHeadId()!=null) {
//                    	fyhHeadIdSet.add(saleItem.getExpenditureHeadId());
//                    }
                    
                    mmHeadIdSet.add(saleItem.getMaterialHeadId());
                    //非标产品才需要传imos信息
                    //做个标识
                    if(!"1".equals(saleItem.getIsStandard())){
                    	orderCodePosexSet.add(saleItem.getOrderCodePosex());
                    }
                    bjIdSet.add(saleItem.getBujianId());
                    myGoodsIdSet.add(saleItem.getMyGoodsId());
                }
            }

            JCoTable imTImos01Table = function.getTableParameterList().getTable("IM_T_IMOS01");// IMOS对接数据-层级主表
            JCoTable imTImos09Table = function.getTableParameterList().getTable("IM_T_IMOS09");// IMOS对接数据-孔位
            if (orderCodePosexSet.size() > 0) {
                sb = new StringBuffer();
                int i = 0;
                for (String str : orderCodePosexSet) {
                    sb.append("'").append(str);
                    if (i < orderCodePosexSet.size() - 1) {
                        sb.append("',");
                    } else {
                        sb.append("'");
                    }
                    i++;
                }
                List<Map<String, Object>> imosIdbextList = jdbcTemplate
                        .queryForList("select t.* from IMOS_IDBEXT t where t.orderid in (" + sb.toString() + ")");
                if (imosIdbextList != null && imosIdbextList.size() > 0) {
                    Field[] declaredFields = ImosIdbext.class.getDeclaredFields();
                    Map<String, Class<?>> fieldMap = new HashMap<String, Class<?>>();
                    for (Field field : declaredFields) {
                        String name = field.getName();
                        Class<?> type = field.getType();
                        fieldMap.put(name, type);
                    }
                    for (Map<String, Object> map : imosIdbextList) {
                        imTImos01Table.appendRow();
                        // 通过反射生成
                        for (JCoField jCoField : imTImos01Table) {
                            String dbName = jCoField.getName();
                            String beanName = FieldFunction.dbField2BeanField(dbName);
                            if (!fieldMap.keySet().contains(beanName)) {
                                continue;
                            }
                            Class<?> class1 = fieldMap.get(beanName);
                            Object obj = map.get(dbName);
                            if (ZStringUtils.isEmpty(obj)) {
                                continue;
                            }
                            Object obj2 = null;
                            if (class1 == Timestamp.class || class1 == java.sql.Date.class
                                    || class1 == java.util.Date.class) {
                                obj2 = DateTools.strToDate(obj.toString(), DateTools.defaultFormat);
                            } else if (class1 == Double.class || class1 == double.class) {
                                obj2 = new Double(obj.toString());
                            } else if (class1 == Integer.class || class1.toString().equals("int")) {
                                obj2 = new Integer(obj.toString());
                            } else if (class1 == Long.class || class1.toString().equals("long")) {
                                obj2 = new Long(obj.toString());
                            } else {
                                obj2 = obj;
                            }
                            imTImos01Table.setValue(jCoField.getName(), obj2);
                        }
                        imTImos01Table.setValue("ID", map.get("ID"));
                        imTImos01Table.setValue("POSEX", Integer.parseInt(map.get("ORDERID").toString().split(
                                saleHeader.getOrderCode())[1]));
                    }
                }

                List<Map<String, Object>> imosIdbwgList = jdbcTemplate
                        .queryForList("select t.* from IMOS_IDBWG t where t.orderid in (" + sb.toString() + ")");
                if (imosIdbwgList != null && imosIdbwgList.size() > 0) {
                    Field[] declaredFields = ImosIdbwg.class.getDeclaredFields();
                    Map<String, Class<?>> fieldMap = new HashMap<String, Class<?>>();
                    for (Field field : declaredFields) {
                        String name = field.getName();
                        Class<?> type = field.getType();
                        fieldMap.put(name, type);
                    }
                    for (Map<String, Object> map : imosIdbwgList) {
                        imTImos09Table.appendRow();
                        // 通过反射生成
                        for (JCoField jCoField : imTImos09Table) {
                            String dbName = jCoField.getName();
                            String beanName = FieldFunction.dbField2BeanField(dbName);
                            if (!fieldMap.keySet().contains(beanName)) {
                                continue;
                            }
                            Class<?> class1 = fieldMap.get(beanName);
                            Object obj = map.get(dbName);
                            if (ZStringUtils.isEmpty(obj)) {
                                continue;
                            }
                            Object obj2 = null;
                            if (class1 == Timestamp.class || class1 == java.sql.Date.class
                                    || class1 == java.util.Date.class) {
                                obj2 = DateTools.strToDate(obj.toString(), DateTools.defaultFormat);
                            } else if (class1 == Double.class || class1 == double.class) {
                                obj2 = new Double(obj.toString());
                            } else if (class1 == Integer.class || class1.toString().equals("int")) {
                                obj2 = new Integer(obj.toString());
                            } else if (class1 == Long.class || class1.toString().equals("long")) {
                                obj2 = new Long(obj.toString());
                            } else {
                                obj2 = obj;
                            }
                            imTImos09Table.setValue(jCoField.getName(), obj2);
                        }
                        imTImos09Table.setValue("ID", map.get("ID"));
                        imTImos09Table.setValue("POSEX", Integer.parseInt(map.get("ORDERID").toString().split(
                                saleHeader.getOrderCode())[1]));
                    }
                }
            }

            JCoTable imTItemTable = function.getTableParameterList().getTable("IM_T_ITEM");// 行项目信息
            JCoTable imTPriceTable = function.getTableParameterList().getTable("IM_T_PRICE");// 价格信息
            JCoTable imTVcinfTable = function.getTableParameterList().getTable("IM_T_VCINF");// 销售订单物料配置信息
            JCoTable imMatPriceTable = function.getTableParameterList().getTable("IM_MAT_PRICE");// 产线价格信息
//            String orderType=saleHeader.getOrderType();
            
            List<MaterialHead> materialHeadList = saleManager.createQueryByIn(MaterialHead.class, "id", mmHeadIdSet);

            // 物料补件信息List
            List<MaterialBujian> materialBujianList = saleManager.createQueryByIn(MaterialBujian.class, "id",bjIdSet);
            // 物料附加信息List
            List<SaleItemFj> saleItemFjList = saleManager.createQueryByIn(SaleItemFj.class, "myGoodsId",myGoodsIdSet);
             
            // 散件bomb
            List<MaterialSanjianHead> mmSjHeadList =  saleManager.createQueryByIn(MaterialSanjianHead.class, "id",sjHeadIdSet);
            
            int data = Integer.parseInt(saleItemList.get(0).getPosex());
            //得到一个NumberFormat的实例
            NumberFormat nf = NumberFormat.getInstance();
            //设置是否使用分组
            nf.setGroupingUsed(false);
            //设置最大整数位数
            nf.setMaximumIntegerDigits(4);
            //设置最小整数位数    
            nf.setMinimumIntegerDigits(4);
            String posex = nf.format(data);
            for (Iterator iterator = saleItemList.iterator(); iterator.hasNext();) {
                SaleItem saleItem = (SaleItem) iterator.next();
                // 取消状态不传SAP
                if (!"QX".equals(saleItem.getStateAudit())) {
                    imTItemTable.appendRow();

                    // POSEX POSEX 优先采购订单的项目号 CHAR 6
                    imTItemTable.setValue("POSEX", saleItem.getPosex());
                    // ZZSPLIT_DATE 最后一次炸单日期 
                    List<Map<String,Object>> _list=jdbcTemplate.queryForList("select to_char(max(create_time),'yyyy-mm-dd') as create_time from imos_load_balance where order_code='"+saleHeader.getOrderCode()+posex+"' group by  order_code ");
                    if(_list!=null && _list.size()>0){
                    	imTItemTable.setValue("ZZSPLIT_DATE", (String)_list.get(0).get("CREATE_TIME"));
                    }
                    // KZKFG KZKFG 可配置的物料 CHAR 1
                    // KWMENG KWMENG 以销售单位表示的累计订单数量 QUAN 15 3
                    imTItemTable.setValue("KWMENG", saleItem.getAmount());
                    //传物料描述
                    imTItemTable.setValue("MAKTX", saleItem.getMaktx());
                    // MATNR MATNR 物料号 CHAR 18
                    imTItemTable.setValue("MATNR", saleItem.getMatnr());
                    //MAKTX MAKTX 物料描述
                    imTItemTable.setValue("MAKTX", saleItem.getMaktx());
                    if(saleItemFjList!=null && saleItemFjList.size()>0){
                    	 for (SaleItemFj saleItemFj : saleItemFjList) {
                             if (saleItemFj.getMyGoodsId().equals(saleItem.getMyGoodsId())) {
                                 // ZZAZDR ZZAZDR 安装位置 CHAR 20
                                 imTItemTable.setValue("ZZAZDR", saleItemFj.getZzazdr());
                                 break;
                             }
                         }
                    }

                    String kmein = "";
                    String kpein = "";
                    String konwa = "";

                    for (MaterialHead materialHead : materialHeadList) {
                        if (saleItem.getMaterialHeadId().equals(materialHead.getId())) {
                            if (ZStringUtils.isEmpty(saleItem.getMatnr())) {
                                // MATNR MATNR 物料号 CHAR 18
                                imTItemTable.setValue("MATNR", materialHead.getMatnr());
                            }
                            CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(custHeader.getKunnr(), materialHead.getSpart(),"3110");
                            if(custLogistics!=null) {
                            	if(("OR4".equals(saleHeader.getOrderType())||"OR3".equals(saleHeader.getOrderType()))&&saleHeader.getSongDaFang()!=null) {
                            		imTItemTable.setValue("KUSDF", saleHeader.getSongDaFang());
                            	}else {
                            		imTItemTable.setValue("KUSDF", custLogistics.getKunnrS());
                            	}
                            }else {
                            	//该客户无下该产品 权限
                            }
                            //产品组
                            imTItemTable.setValue("SPART", materialHead.getSpart());
                            //在下单的时候已赋值单位
                            String vrkme=saleItem.getUnit();
                            // VRKME VRKME 销售单位 UNIT 3
//                            imTItemTable.setValue("VRKME", materialHead.getMeins());
                            imTItemTable.setValue("MATKL", materialHead.getMatkl());
                            imTItemTable.setValue("VRKME", vrkme);
                            kmein = ZStringUtils.isEmpty(materialHead.getKmein()) ? "EA" : materialHead.getKmein();
                            kpein = ZStringUtils.isEmpty(materialHead.getKpein()) ? "1" : materialHead.getKpein();
                            konwa = ZStringUtils.isEmpty(materialHead.getKonwa()) ? "CNY" : materialHead.getKonwa();
                            // ZZCOMT ZZCOMT 颜色及材质 NUMC 3 ------字段取消
                           /* if(saleHeader.getSaleFor().equals("3")){
                            	String result=materialHead.getTextureOfMaterial().substring(0, materialHead.getTextureOfMaterial().indexOf("_"));
                                imTItemTable.setValue("ZZCOMT", materialHead.getTextureOfMaterial()==null?"":result
                                        + (materialHead.getColor()==null?(materialHead.getExtwg()==null?"":materialHead.getExtwg()):materialHead.getColor()));
                            }else{*/
                            if("999999".equals(materialHead.getMaterialColor())){
                            	imTItemTable.setValue("ZZCOMT", "999999");
                            }else{
                            	if(materialHead.getTextureOfMaterial()!=null&&materialHead.getColor()!=null){
                            		List<Map<String, Object>> list = jdbcTemplate.queryForList("select distinct tt.type from sys_trie_tree tt where tt.key_val='"+materialHead.getTextureOfMaterial()+"'");
                                	if(list.get(0).get("type")!=null&&list.get(0).get("type")!=""){
                                		List<Map<String, Object>> list1 = jdbcTemplate.queryForList("select dd.type from sys_data_dict dd left join sys_trie_tree tt on dd.trie_id=tt.id where tt.key_val='"+materialHead.getTextureOfMaterial()+"'and dd.key_val='"+materialHead.getColor()+"'");
                                		imTItemTable.setValue("ZZCOMT", list.get(0).get("type").toString()+list1.get(0).get("type").toString());
                                	}else{
                                		imTItemTable.setValue("ZZCOMT", materialHead.getTextureOfMaterial()==null?"":materialHead.getTextureOfMaterial()
                                                + (materialHead.getColor()==null?(materialHead.getExtwg()==null?"":materialHead.getExtwg()):materialHead.getColor()));
                                	}
                            	}
                            }
                            //}
                            // ZZWGFG ZZWGFG 是否外购物料 CHAR 1
                            imTItemTable.setValue("ZZWGFG", "1".equals(materialHead.getZzwgfg()) ? "X" : "");
                            // ZZTYAR ZZTYAR 投影面积平方数 QUAN 15 2
                            imTItemTable.setValue("ZZTYAR", materialHead.getZztyar());
                            // ZZZKAR ZZZKAR 板件展开面积平方数 QUAN 15 2
                            imTItemTable.setValue("ZZZKAR", materialHead.getZzzkar());
                            // ZZCPYT ZZCPYT 产品用途区分 NUMC 3
                            imTItemTable.setValue("ZZCPYT", materialHead.getZzcpyt()==null?"":materialHead.getZzcpyt());
                            // ZZCPDJ ZZCPDJ 产品等级 CHAR 2
                            imTItemTable.setValue("ZZCPDJ", materialHead.getZzcpdj());
                            //ZZCPXL ZZCPXL产品风格系列
                            imTItemTable.setValue("ZZCPXL", materialHead.getSeries()==null||materialHead.getSeries().equals("")?"999":materialHead.getSeries());
                            String materialHeadId = materialHead.getId();
                            List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT SIF.PRODUCT_SPACE FROM SALE_ITEM_FJ SIF WHERE SIF.MATERIAL_HEAD_ID='"+materialHeadId+"'");
                            String productSpace="99";
                            if(list.size()>0) {
                            	productSpace = (list.get(0).get("PRODUCT_SPACE")!=null)?list.get(0).get("PRODUCT_SPACE").toString():"99";
                            }
                            //ZZCPKJ ZZCPKJ 产品空间
                            imTItemTable.setValue("ZZCPKJ", productSpace);
                            //ZZHTLX 绘图类型 2016-06-13 加
                            if(materialHead.getDrawType()!=null){
                            	List<Map<String, Object>> hrlxs = jdbcTemplate.queryForList("select v.DESC_ZH_CN from sys_data_dict_view v where v.KEY_VAL='"+materialHead.getDrawType()+"' and v.TRIE_KEY_VAL='MATERIAL_DRAW_TYPE'");
	                           if(hrlxs.size()>0){
	                        	   String htlx=hrlxs.get(0).get("DESC_ZH_CN").toString();
	                        	   imTItemTable.setValue("ZZHTLX", htlx);
	                           }
	                           
                            }
                            //非标产品 物料审核员
                            if("0".equals(materialHead.getIsStandard())){
                            	 String sql="SELECT * FROM (select his.assignee_ assignee from act_hi_actinst his,act_ct_mapping acm where his.proc_inst_id_=ACM.PROCINSTID  AND HIS.ACT_ID_='usertask3' AND HIS.ACT_NAME_='物料审核' AND ACM.ID='"+saleItem.getId()+"' ORDER BY HIS.START_TIME_ DESC ) WHERE ROWNUM=1";
                              	 List<Map<String, Object>> actAssignee = jdbcTemplate.queryForList(sql);
                	             if(actAssignee.size()>0){
                	            	 // ZZADHM ZZADHM 审单员 CHAR 10
                                     imTItemTable.setValue("ZZADHM", actAssignee.get(0).get("ASSIGNEE"));
                	             }
                            }else{
                            	imTItemTable.setValue("ZZADHM", "");
                            }
                           
                            // ZZYMFS ZZYMFS 移门方数 QUAN 15 2
                            imTItemTable.setValue("ZZYMFS", materialHead.getZzymfs());
                            // ZZYMSS ZZYMSS 移门扇数 CHAR 6
                            imTItemTable.setValue("ZZYMSS", materialHead.getZzymss());
                            // ZZXSFS ZZXSFS 吸塑方数 QUAN 15 2
                            imTItemTable.setValue("ZZXSFS", materialHead.getZzxsfs());
                            //ZZZBJHD 板件厚度 
                            imTItemTable.setValue("ZZZBJHD", materialHead.getBoardThickness());
                            //ZZZBJYX 是否含有异型
                            imTItemTable.setValue("ZZZBJYX", StringUtils.isEmpty(materialHead.getHasPec())?0:Integer.parseInt(materialHead.getHasPec()));
                            
                            List<Map<String, Object>> materiaFileList = jdbcTemplate
                                    .queryForList("select * from ( select t.id,t.upload_file_name,t.upload_file_path from material_file t where t.file_type='PDF' and t.status is null and t.pid='"
                                            + saleItem.getMaterialHeadId()
                                            + "' order by create_time desc ) where rownum<=1");
                            if (materiaFileList != null && materiaFileList.size() > 0) {
                                Map<String, Object> fileObj = materiaFileList.get(0);
                                // ZFLWAY 路径
                                imTItemTable.setValue("ZFLWAY", fileObj.get("UPLOAD_FILE_PATH").toString());
                                // ZFLNAME 名称
                                imTItemTable.setValue("ZFLNAME", fileObj.get("UPLOAD_FILE_NAME").toString());
                            }
                            break;
                        }
                    }

                    if (materialBujianList != null && materialBujianList.size() > 0
                            && ZStringUtils.isNotEmpty(saleItem.getBujianId())) {
                        for (MaterialBujian materialBujian : materialBujianList) {
                            if (materialBujian.getId().equals(saleItem.getBujianId())) {
                                //  修改为补件表
                                // ZZTSNR ZZTSNR 投诉内容描述 CHAR 80
                                imTItemTable.setValue("ZZTSNR", materialBujian.getZztsnr());
                                // ZZEZX1 ZZEZX1 出错中心1 NUMC 2
                                imTItemTable.setValue("ZZEZX1", materialBujian.getZzezx1());
                                // ZZEZX2 ZZEZX2 出错中心2 NUMC 2
                                imTItemTable.setValue("ZZEZX2", materialBujian.getZzezx2());
                                // ZZEBM1 ZZEBM1 出错部门1 NUMC 2
                                imTItemTable.setValue("ZZEBM1", materialBujian.getZzebm1());
                                // ZZEBM2 ZZEBM2 出错部门2 NUMC 2
                                imTItemTable.setValue("ZZEBM2", materialBujian.getZzebm2());
                                // ZZECJ1 ZZECJ1 出错车间1 NUMC 2
                                imTItemTable.setValue("ZZECJ1", materialBujian.getZzecj1());
                                // ZZECJ2 ZZECJ2 出错车间2 NUMC 2
                                imTItemTable.setValue("ZZECJ2", materialBujian.getZzecj2());
                                // ZZESCX1 ZZESCX1 出错生产线1 NUMC 2
                                imTItemTable.setValue("ZZESCX1", materialBujian.getZzescx1());
                                // ZZESCX2 ZZESCX2 出错生产线2 NUMC 2
                                imTItemTable.setValue("ZZESCX2", materialBujian.getZzescx2());
                                // ZZRGX1 ZZRGX1 责任工序1 NUMC 2
                                imTItemTable.setValue("ZZRGX1", materialBujian.getZzrgx1());
                                // ZZRGX2 ZZRGX2 责任工序2 NUMC 2
                                imTItemTable.setValue("ZZRGX2", materialBujian.getZzrgx2());
                                // ZZELB1 ZZELB1 出错类别1 NUMC 2
                                imTItemTable.setValue("ZZELB1", materialBujian.getZzelb1());
                                // ZZELB2 ZZELB2 出错类别2 NUMC 2
                                imTItemTable.setValue("ZZELB2", materialBujian.getZzelb2());
                                //BGDISPO 产线
                                structure.setValue("BGDISPO", materialBujian.getBgdispo());
                                break;
                            }
                        }
                    }

                    kmein = ZStringUtils.isEmpty(kmein) ? "EA" : kmein;
                    kpein = ZStringUtils.isEmpty(kpein) ? "1" : kpein;
                    konwa = ZStringUtils.isEmpty(konwa) ? "CNY" : konwa;

                    Set<SaleItemPrice> saleItemPrices = saleItem.getSaleItemPrices();
                    if (saleItemPrices != null && saleItemPrices.size() > 0) {
                        for (SaleItemPrice saleItemPrice : saleItemPrices) {
                            imTPriceTable.appendRow();
                            // POSEX POSEX 优先采购订单的项目号 CHAR 6
                            imTPriceTable.setValue("POSEX", saleItem.getPosex());
                            // KSCHL KSCHL 条件类型 CHAR 4
                            imTPriceTable.setValue("KSCHL", saleItemPrice.getType());

                            String plusOrMinus = saleItemPrice.getPlusOrMinus();

                            if ("0".equals(plusOrMinus)) {// 减
                                // KBETR KBETR 价格( 条件金额或百分数 ) CURR 11 2
                                imTPriceTable.setValue("KBETR", NumberUtils.round(NumberUtils.subtract(0,
                                        saleItemPrice.getSubtotal()), 2));
                            } else {// 1是加
                                // KBETR KBETR 价格( 条件金额或百分数 ) CURR 11 2
                                imTPriceTable.setValue("KBETR", NumberUtils.round(saleItemPrice.getSubtotal(), 2));
                            }

                            // WAERS WAERS 货币码 CUKY 5
                            imTPriceTable.setValue("WAERS", konwa);
                            // KPEIN KPEIN 条件定价单位 DEC 5
                            imTPriceTable.setValue("KPEIN", kpein);
                            // KMEIN KMEIN 条件单位 UNIT 3
                            imTPriceTable.setValue("KMEIN", kmein);
                        }
                    }
                    List<Map<String, Object>> materialPriceLine = jdbcTemplate.queryForList("SELECT SUM(MP.TOTAL_PRICE) AS TOTALPRICE,MP.LINE ||'00' AS LINE FROM MATERIAL_PRICE MP WHERE MP.PID='"+saleItem.getId()+"' GROUP BY MP.LINE");
                    for (Map<String, Object> map : materialPriceLine) {
                    	String line= (map.get("LINE")!=null?String.valueOf(map.get("LINE")):"");
                    	if(!"00".equals(line)) {
                    		imMatPriceTable.appendRow();
                    		imMatPriceTable.setValue("POSEX", saleItem.getPosex());
                    		imMatPriceTable.setValue("KSCHL", "PR07");
                    		double totalPrice=(map.get("TOTALPRICE")!=null?Double.parseDouble(String.valueOf(map.get("TOTALPRICE"))):0.0);
                    		imMatPriceTable.setValue("KBETR", totalPrice);
                    		imMatPriceTable.setValue("WAERS", "CNY");
                    		imMatPriceTable.setValue("KPEIN",saleItem.getAmount());
                    		imMatPriceTable.setValue("KMEIN",saleItem.getUnit());
                    		imMatPriceTable.setValue("ZZPAR", line);
                    	}
					}
                    String materialPropertyItemInfo = saleItem.getMaterialPropertyItemInfo();
                    if (ZStringUtils.isNotEmpty(materialPropertyItemInfo)) {
                        // KZKFG KZKFG 可配置的物料 CHAR 1
                        imTItemTable.setValue("KZKFG", "X");

                        String[] split = materialPropertyItemInfo.split(",");
                        for (String str : split) {
                            if (ZStringUtils.isNotEmpty(str)) {
                                imTVcinfTable.appendRow();
                                String[] split2 = str.split(":");
                                // POSEX POSEX 优先采购订单的项目号 CHAR 6
                                imTVcinfTable.setValue("POSEX", saleItem.getPosex());
                                // ATINN ATINN 内部特性 NUMC 10
                                // ATNAM ATNAM 特征名称 CHAR 30
                                imTVcinfTable.setValue("ATNAM", split2[1]);
                                // ATBEZ ATBEZ 特性描述 CHAR 30
                                imTVcinfTable.setValue("ATBEZ", split2[2]);
                                // ATWRT ATWRT 特性值 CHAR 30
                                imTVcinfTable.setValue("ATWRT", split2[3]);
                                // ATWTB ATWTB 特性值文本 CHAR 30
                                imTVcinfTable.setValue("ATWTB", split2[4]);
                            }
                        }
                    }
                    
                    if(StringUtils.isEmpty(imTItemTable.getValue("VRKME")))
                    {
                    	imTItemTable.setValue("VRKME", "EA");
                    }
                }
                
            }
            if (mmSjHeadList != null && mmSjHeadList.size() > 0) {
            	int i = 0;
                for (MaterialSanjianHead materialSanjianHead : mmSjHeadList) {
//                    String POSEX = "10";
					String sql = "select SI.ORDER_CODE_POSEX from sale_item si where si.sanjian_head_id='"+materialSanjianHead.getId()+"'";
					String orderCodePosex = jdbcTemplate.queryForObject(sql, String.class);
					String POSEX = orderCodePosex.substring(orderCodePosex.length()-2);
                    Set<MaterialSanjianItem> materialSanjianItemSet = materialSanjianHead
                            .getMaterialSanjianItemSet();
                    
                    for (MaterialSanjianItem materialSanjianItem : materialSanjianItemSet) {
                        imTImos01Table.appendRow();
                        imTImos01Table.setValue("ID", ++i);
                        if("102999997".equals(materialSanjianHead.getMatnr())) {
                        	imTImos01Table.setValue("INFO1", "G01");
                        }else if ("102999998".equals(materialSanjianHead.getMatnr())) {
                        	imTImos01Table.setValue("INFO1", "H01");
                        	imTImos01Table.setValue("INFO4", "Z60");
                        }else if("102999996".equals(materialSanjianHead.getMatnr())||"102999995".equals(materialSanjianHead.getMatnr())) {
                        	imTImos01Table.setValue("INFO1", "H01");
                        }
                        
                        imTImos01Table.setValue("ORDERID", saleHeader.getOrderCode());
                        imTImos01Table.setValue("POSEX", POSEX);
                        imTImos01Table.setValue("TYP", "9");
                        imTImos01Table.setValue("PARENTID", "A");
                        // RENDER ZRENDER RENDER CHAR 18
                        imTImos01Table.setValue("PRICE", materialSanjianItem.getTotalPrice());
                        imTImos01Table.setValue("RENDER", materialSanjianItem.getMatnr());
                        imTImos01Table.setValue("NAME", materialSanjianItem.getMiaoshu());
                        imTImos01Table.setValue("CNT", materialSanjianItem.getAmount());
                    }
                }
            }

        }
        
        function.execute(connect);
        JCoTable reTable = function.getTableParameterList().getTable("EX_T_RETURN");
        JCoTable exTSoTable = function.getTableParameterList().getTable("EX_T_SO");
        // System.out.println(reTable);
        Map<String,String> sapCodeMap=new HashMap<String,String>();
        if(exTSoTable.getNumRows()>0) {
			for (int i = 0; i < exTSoTable.getNumRows(); i++, exTSoTable.nextRow()) {
				Object bstnk = exTSoTable.getValue("BSTNK");
				Object posex = exTSoTable.getValue("POSEX");
				Object vbeln = exTSoTable.getValue("VBELN");
				Object posnr = exTSoTable.getValue("POSNR");
				Object matnr = exTSoTable.getValue("MATNR");
				String orderCodePosex = String.valueOf(bstnk) + String.format("%04d", Integer.parseInt(String.valueOf(posex)));
				List<SaleItem> saleList = saleItemDao.findByOrderCodePosex(orderCodePosex);
				if(saleList.size() > 0) {
					SaleItem saleItem = saleList.get(0);
					saleItem.setSapCode(String.valueOf(vbeln));
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					sapCodeMap.put(materialHead.getSaleFor(),String.valueOf(vbeln));
					saleItem.setSapCodePosex(String.valueOf(posnr));
					//saleItem.setMatnr(String.valueOf(matnr));
					saleItemDao.save(saleItem);
				}else {
						 msg = new Message("SALE-500", "无对应物料信息");
				}	 
			}
        }
        if(sapCodeMap.size()>0) {
        	for (Entry<String, String> sapCodeentry : sapCodeMap.entrySet()) {
        		SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeader.getId(), sapCodeentry.getKey());
        		saleLogistics.setSapCode(sapCodeentry.getValue());
        		saleLogisticsDao.save(saleLogistics);
			}
        }
        StringBuffer errSb = new StringBuffer();
		if (reTable.getNumRows() > 0) {
		    reTable.firstRow();
		    for (int i = 0; i < reTable.getNumRows(); i++, reTable.nextRow()) {
		        Object type = reTable.getValue("TYPE");
		        Object id = reTable.getValue("ID");
		        Object message = reTable.getValue("MESSAGE");
		        if (i == 0) {
		            if (type != null && ("S".equals(type.toString())||"W".equals(type.toString()))) {// && "S".equals(type.toString())
		            	msg = new Message(message == null ? "" : message.toString());
		                /*msg.put("id", id == null ? "" : id.toString());
		                msg.put("yuJiDate", DateTools.formatDate((Date) werksDate, DateTools.defaultFormat));*/
		                return msg;
		            } else {
		                errSb.append(message == null ? "" : message.toString()).append("<br/>");
		            }
		        } else {
		            if (type != null && "E".equals(type.toString())) {
		                errSb.append(message == null ? "" : message.toString()).append("<br/>");
		            }
		        }
		    }
		} else {
		    errSb.append("接口异常，没有返回信息");
		}
        if (!errSb.toString().equals("")) {
            msg = new Message("SALE-500", errSb.toString());
        }
		return msg;
	}
	
	@Override
	public Message sendSaleByCode(String code, String userId) {
		  	Message msg = null;
	        try {
	        	List<SaleHeader> shs = saleHeaderDao.queryByNativeQuery("SELECT * FROM SALE_HEADER h where H.ORDER_CODE='"+code+"'");
	        	SaleHeader saleHeader=null;
	        	if(shs.size()<=0){
	        		return new Message("SALE-500","找不到该单号："+code);
	        	}else{
	        		saleHeader=shs.get(0);
	        	}
//	            SaleHeader saleHeader = saleHeaderDao.findOne(saleId);
/*	            if (ZStringUtils.isNotEmpty(saleHeader.getSapOrderCode())) {
	                msg = new Message("传输失败：<br/>订单" + saleHeader.getOrderCode() + "，已传输SAP产生了销售单号"
	                        + saleHeader.getSapOrderCode() + "！");
	                msg.put("id", saleHeader.getSapOrderCode());
	                return msg;
	            }*/
	            List<SaleItem> saleItemList = saleItemDao.findItemsByPid(saleHeader.getId());
	            /*String bukrs = "";// 公司代码
	            String vkorg = "3100";// 销售组织
	            String vtweg = "01";// 分销渠道
	            String spart = "01";// 产品组*/
	            CustHeader custHeader = null;
	            List<CustHeader> findByCode = custHeaderDao.findByCode(saleHeader.getShouDaFang());
	           /* for (SaleItem saleItem : saleItemList) {
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpart(saleHeader.getShouDaFang(), materialHead.getSpart());
					
				}*/
	            /*vkorg = ZStringUtils.isEmpty(custHeader.getVkorg()) ? "3100" : custHeader.getVkorg();
	            vtweg = ZStringUtils.isEmpty(custHeader.getVtweg()) ? "01" : custHeader.getVtweg();
	            spart = ZStringUtils.isEmpty(custHeader.getSpart()) ? "01" : custHeader.getSpart();
	            bukrs = ZStringUtils.isEmpty(custHeader.getBukrs()) ? "3000" : custHeader.getBukrs();*/
	            if (findByCode != null && findByCode.size() > 0) {
	                custHeader = findByCode.get(0);
	            }
	            JCoDestination connect = SAPConnect.getConnect();
	            //传送物料
//	            msg=this.sendMM(connect, saleHeader, saleItemList, vkorg, vtweg);
//	            if(msg.getSuccess()){
	            	//传送订单
//	            }
	            msg=this.sendSale(connect, saleHeader, saleItemList, custHeader, userId);
	        }catch (Exception e) {
				e.printStackTrace();
				return new Message("SALE-500",e.getLocalizedMessage());
			}
	            
		return msg;
	}
	 
}
