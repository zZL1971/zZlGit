package com.main.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.dao.SaleLogisticsDao;
import com.main.domain.sale.SaleLogistics;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.json.filter.annotation.IgnoreProperties;
import com.mw.framework.json.filter.annotation.IgnoreProperty;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.model.JdbcExtGridBean;

@RequestMapping("/sale/log/*")
@Controller
public class SaleLogisticsController extends BaseController{

	private static final Logger logger=Logger.getLogger(SaleLogisticsController.class);
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	@Autowired
	private CommonManager commonManager;
	@Autowired
	private SysTrieTreeDao sysTrieTreeDao;
	@Autowired
	private SysDataDictDao sysDataDictDao;
	
	@RequestMapping(value="/findSaleLogisticsStoreInfo",method=RequestMethod.GET)
	@IgnoreProperties(value={@IgnoreProperty(name = {  "saleHeader"}, pojo = SaleLogistics.class)})
	@ResponseBody
	public JdbcExtGridBean findSaleLogisticsStoreInfo() {
		String pid = super.getRequest().getParameter("pid");
		List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(pid);
//		System.out.println(saleLogisticsList.get(0).getPpcDate());
		return new JdbcExtGridBean(1, saleLogisticsList.size(), saleLogisticsList.size(),
				saleLogisticsList);
	}
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public Message update() {
		Message msg=null;
		String id = this.getRequest().getParameter("id");
		String val = this.getRequest().getParameter("val");
		String name = this.getRequest().getParameter("name");
		String orderType = this.getRequest().getParameter("orderType");
		SaleLogistics saleLogistics=commonManager.getById(id, SaleLogistics.class);
		Class<? extends SaleLogistics> saleLog = saleLogistics.getClass();
		SaleLogistics controlSaleLog=new SaleLogistics();
		Class<? extends SaleLogistics> cls = controlSaleLog.getClass();
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = saleLog.getDeclaredMethods();
		if(name!=null&&!"".equals(name)) {
			if("deliveryDay".equals(name)) {
				if("buDan".equals(orderType)||"OR3".equals(orderType)||"OR4".equals(orderType)) {
					SysTrieTree sysTrieTree = sysTrieTreeDao.findByKeyVal("JIAO_QI_TIAN_SHU_B");
					if(sysTrieTree!=null) {
						List<SysDataDict> sysDataDictList = sysDataDictDao.findByTrieTreeIdAndStat(sysTrieTree.getId());
						for (SysDataDict sysDataDict : sysDataDictList) {
							if(sysDataDict.getKeyVal().equals(val)) {
								saleLogistics.setDeliveryIdentify(sysDataDict.getType());
								saleLogistics.setZzjqlb(sysDataDict.getTypeKey());
								break;
							}
						}
					}
				}else {
					SysTrieTree sysTrieTree = sysTrieTreeDao.findByKeyVal("JIAO_QI_TIAN_SHU");
					if(sysTrieTree!=null) {
						List<SysDataDict> sysDataDictList = sysDataDictDao.findByTrieTreeIdAndStat(sysTrieTree.getId());
						for (SysDataDict sysDataDict : sysDataDictList) {
							if(sysDataDict.getKeyVal().equals(val)) {
								saleLogistics.setDeliveryIdentify(sysDataDict.getType());
								saleLogistics.setZzjqlb(sysDataDict.getTypeKey());
								break;
							}
						}
					}
				}
			}
			for (Field field : fields) {
				String fieldName = field.getName();
				if(name.equals(fieldName)&&!fieldName.equals("serialVersionUID")) {
					String setMethod="set"+
							name.substring(0, 1).toUpperCase()+name.substring(1,name.length());
					try {
						Method method = cls.getMethod(setMethod, field.getType());
						method.invoke(controlSaleLog, val);
						for (Method meth : methods) {
							String methodName = meth.getName();
							if(methodName.startsWith("set")) {
								if(methodName.indexOf(setMethod)==0) {
									meth.invoke(saleLogistics, val);
									break;
								}
							}
						}
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}else {
			
		}
		commonManager.save(saleLogistics);
		msg=new Message("XML-UPDATE-200","修改成功");
		return msg;
	}
}
