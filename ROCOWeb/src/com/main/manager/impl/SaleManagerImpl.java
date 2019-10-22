package com.main.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import com.main.bean.SaleBean;
import com.main.controller.BgController;
import com.main.dao.CustHeaderDao;
import com.main.dao.CustItemDao;
import com.main.dao.CustLogisticsDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.MaterialPriceDao;
import com.main.dao.MaterialSanjianHeadDao;
import com.main.dao.PriceConditionDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemFjDao;
import com.main.dao.SaleItemPriceDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.cust.CustLogistics;
import com.main.domain.cust.TerminalClient;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sale.SaleLogistics;
import com.main.domain.sale.SaleOneCust;
import com.main.manager.MyGoodsManager;
import com.main.manager.SaleManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.domain.SysUser;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.manager.impl.CommonManagerImpl;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.NumberUtils;
import com.mw.framework.utils.StringUtil;
import com.mw.framework.utils.ZStringUtils;

import net.sf.json.JSONObject;

@Service("saleManager")
@Transactional
public class SaleManagerImpl extends CommonManagerImpl implements SaleManager {
	private static final Logger logger = LoggerFactory
			.getLogger(SaleManagerImpl.class);
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private PriceConditionDao priceConditionDao;
	@Autowired
	private SaleItemPriceDao saleItemPriceDao;
	@Autowired
	private SerialNumberManager serialNumberManager;
	/*@Autowired
	private MyGoodsDao myGoodsDao;*/

	@Autowired
	private CustItemDao custItemDao;
	@Autowired
	private CustLogisticsDao custLogisticsDao;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private MaterialHeadDao materialHeadDao;
	@Autowired
	protected MyGoodsManager myGoodsManager;
    @Autowired
    private SaleItemFjDao saleItemFjDao;
    @Autowired
    private MaterialSanjianHeadDao materialSanjianHeadDao;
    @Autowired
    private MaterialPriceDao materialPriceDao;
    @Autowired
    private SaleLogisticsDao saleLogisticsDao;
    @Autowired
    private SysTrieTreeDao sysTrieTreeDao;
    @Autowired
    private SysDataDictDao sysDataDictDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private CustHeaderDao custHeaderDao;
    @Autowired
    private SysActCTMappingDao sysActCTMappingDao;
    @Autowired
    private TaskService taskService;
  /*  @Autowired
    private MaterialSanjianHeadDao materialSanjianHeadDao;*/
	  
	@Override
	public SaleHeader saveFB(SaleHeader saleHeader, SaleBean saleBean,String createUser) {
		List<SaleItem> saleItemList = saleBean.getSaleItemList();
		SysUser sysUser = sysUserDao.findOne(createUser);
		CustHeader custHeader = sysUser.getCustHeader();
		// insert or update item foreach
		// double saleTotalPrice = 0;
		Map<String,String> saleForMap=new HashMap<String, String>();
		for (SaleItem saleItem : saleItemList) {
			if (!"QX".equals(saleItem.getStateAudit())) {
				saleItem.setSaleHeader(saleHeader);
				saleItem.setStatus("1");
				List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
				if (saleItem.getId() == null || "".equals(saleItem.getId())) {
					// saleItemPrices = getSaleItemPrice(saleItem);
				} else {
					saleItemPrices = saleItemPriceDao
							.querySaleItemPrice(saleItem.getId());
				}
				// saleItemPriceDao.save(saleItemPrices);
				Double totalPrice = 0.0;
				if(saleItemPrices.size()>0) {
					totalPrice = calculationPrice(saleItemPrices, saleItem
							.getAmount(), true, saleHeader.getShouDaFang());
					Set<SaleItemPrice> saleItemPriceSet = new HashSet<SaleItemPrice>(
							saleItemPrices);
					saleItem.setSaleItemPrices(saleItemPriceSet);
				}
				if("1".equals(saleItem.getIsStandard())) {
					List<MaterialHead> materialHead = materialHeadDao.findStandardByMatnr(saleItem.getMatnr());
					if(materialHead.size()>0) {
						totalPrice = materialHead.get(0).getKbetr();
					}
				}
				saleItem.setTotalPrice(totalPrice);
				// saleTotalPrice = NumberUtils.add(saleTotalPrice,
				// totalPrice.doubleValue());
				
				
				//更新附加信息（安装位置）
		      String orderType=saleHeader.getOrderType();
		      if("OR1".equals(orderType)||"OR7".equals(orderType)||"OR8".equals(orderType)||"OR9".equals(orderType)||"OR2".equals(orderType)){
					//非标
					if("0".equals(saleItem.getIsStandard())&&saleItem.getRemark()!=null){
						String sql="update sale_item_fj f set f.zzazdr='"+saleItem.getRemark()+"' where  f.create_user='"+createUser+"' and f.material_head_id='"+saleItem.getMaterialHeadId()+"'";
						jdbcTemplate.update(sql);
					}else if("1".equals(saleItem.getIsStandard())&&saleItem.getRemark()!=null){//标准产品安装位置
						SaleItemFj saleItemFj =new SaleItemFj();
						saleItemFj.setZzazdr(saleItem.getRemark());
						saleItemFj.setMaterialHeadId(saleItem.getMaterialHeadId());
						saleItemFj.setMyGoodsId(saleItem.getMaterialHeadId());
		                saleItemFj = saleItemFjDao.save(saleItemFj);
					}
			 }
		     MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
		     if(materialHead==null) {
		    	 throw new TypeCastException("数据异常");
		     }
		     saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
		      
		     saleHeader.getSaleItemSet().add(saleItem);
			}
		}
		/*Set<SaleLogistics> saleLogisticsList=new HashSet<SaleLogistics>();
		for (Entry<String, String> map : saleForMap.entrySet()) {
			String saleHeaderId = saleHeader.getId();
			saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
			String saleFor=map.getKey();
			String spart=map.getValue();
			SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeaderId, saleFor);
			if(saleLogistics == null) {
				saleLogistics = new SaleLogistics();
			}
			saleLogistics.setSaleFor(saleFor);
			CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(saleHeader.getShouDaFang(), spart,"3110");
			saleLogistics.setKunnrS(custLogistics.getKunnrS());
			saleLogistics.setSaleHeader(saleHeader);
			saleLogisticsList.add(saleLogistics);
		}
		saleHeader.setSaleLogisticsSet(saleLogisticsList);*/
		
		// 针对 没有审绘的订单 行号重排
		if(saleHeader.getOrderCode() !=null &&!"".equals(saleHeader.getOrderCode())) {
			SysActCTMapping actCtMapping = sysActCTMappingDao.createQuery("from SysActCTMapping sa where sa.id='"+saleHeader.getId()+"'").get(0);
			Task task = taskService.createTaskQuery().processInstanceId(actCtMapping.getProcinstid()).singleResult();
			if(task.getAssignee() == null&& "".equals(task.getAssignee())) {
				List<SaleItem> saleItems = saleItemDao.findByItemsAndStateAudit(saleHeader.getOrderCode(), "B");
				if(saleItems.size()<=0) {
					//重排 行号
					saleItemList = resetOrderPosexForSaleFor(saleItemList,saleForMap);
				}
			}
		}else {
			// 重排行号
			saleItemList = resetOrderPosexForSaleFor(saleItemList,saleForMap);
		}
		// saleHeader.setOrderTotal(saleTotalPrice);

		// delete item foreach
		// if (saleBean.getDelItems() != null && saleBean.getDelItems().size() >
		// 0)
		// saleItemDao.delete(saleBean.getDelItems());

		List<SaleOneCust> saleOneCustList = saleBean.getSaleOneCustList();
		if(saleOneCustList!=null){
			for (SaleOneCust saleOneCust : saleOneCustList) {
				saleOneCust.setSaleHeader(saleHeader);
				saleHeader.getSaleOneCustSet().add(saleOneCust);
			}
		}

		TerminalClient terminalClient = saleBean.getTerminalClient();
		terminalClient.setSaleHeader(saleHeader);
		saleHeader.setTerminalClient(terminalClient);

		String id = saleHeader.getId();
		if (ZStringUtils.isEmpty(id)) {
			String orderCode = saleHeader.getOrderCode();
			String orderType = saleHeader.getOrderType();
			String pOrderCode = saleHeader.getpOrderCode();
			if (ZStringUtils.isEmpty(orderCode)
					&& ZStringUtils.isEmpty(pOrderCode)
					&& "OR4".equals(orderType)) {
				//生成免费临时单号
				String curSerialNumberFullYY = "LD"+serialNumberManager.curSerialNumberFullYY("LD", 8);
				saleHeader.setOrderCode(curSerialNumberFullYY);
			}
		}
		SaleHeader save = saleHeaderDao.save(saleHeader);
		Set<SaleItem> newSaleItem = save.getSaleItemSet();
		NO_MATERIAL:for (SaleItem item : newSaleItem) {
			if(item.getId()!=null&&!"".equals(item.getId())) {
				List<Map<String, Object>> fpItems = jdbcTemplate.queryForList("SELECT SD.KEY_VAL FROM SYS_TRIE_TREE ST RIGHT JOIN SYS_DATA_DICT SD ON ST.ID = SD.TRIE_ID WHERE ST.KEY_VAL=? AND SD.STAT=?", "F_P_ITEM","1");
				for (Map<String, Object> fpItem : fpItems) {
					String keyVal = ZStringUtils.resolverStr(fpItem.get("KEY_VAL"));
					if(keyVal.equals(item.getMatnr())) {
						continue NO_MATERIAL;
					}
				}
				if("OR2".equals(saleHeader.getOrderType())) {//OR2 需要保存散件价格
					List<MaterialPrice> materialPrices = materialPriceDao.findByPid(item.getId());
					if(materialPrices.size()>0) {
						this.deleteMaterialPrice(materialPrices);
					}
			    	 MaterialSanjianHead materialSanjianHead = materialSanjianHeadDao.findOne(item.getSanjianHeadId());
			    	 Set<MaterialSanjianItem> materialSanjianItems = materialSanjianHead.getMaterialSanjianItemSet();
			    	 for (MaterialSanjianItem materialSanjianItem: materialSanjianItems) {
			    		 String matnr = materialSanjianItem.getMatnr();//获取物料编码
			    		 MaterialHead materialHead = materialHeadDao.findMaterialHeadByMatnr(matnr,custHeader.getVkorg(),custHeader.getVtweg());
			    		 if(materialHead==null) {
			    			 throw new TypeCastException("物料信息不存在");
			    		 }
			    		 String matkl = materialHead.getMatkl();//获取 物料组
			    		 List<Map<String, Object>> sysDataDicts = jdbcTemplate.queryForList("SELECT ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN,COUNT(SD.ID) AS TOTAL FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE SD.KEY_VAL='"+matkl+"' AND SD.STAT <>'0' GROUP BY ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN HAVING DECODE(ST.KEY_VAL,'102999995','1','102999996','1','102999997','1','0')>=1");
			    		 if(sysDataDicts.size()>1||sysDataDicts.size()<=0) {
			    			//返回配置报错信息
							throw new TypeCastException("物料组存在多个");
			    		 }
			    		 Map<String, Object> sysDataDict = sysDataDicts.get(0);
		    			 String line = (sysDataDict.get("TYPE_KEY")!=null?String.valueOf(sysDataDict.get("TYPE_KEY")):"");
		    			 String type = (sysDataDict.get("DESC_ZH_CN")!=null?String.valueOf(sysDataDict.get("DESC_ZH_CN")):"");
		    			 if(""==line) {
		    				 //返回报错信息
		    				 throw new TypeCastException("请完善相应配置（产线）");
		    			 }
		    			 String miaoshu = materialSanjianItem.getMiaoshu();//获取描述信息
		    			 Integer amount = materialSanjianItem.getAmount();//获取数量
		    			 Double price = materialSanjianItem.getPrice();//获取单价
		    			 Double zhekou = materialSanjianItem.getZhekou();//获取折扣信息
		    			 Double sjtotalPrice = materialSanjianItem.getTotalPrice();//获取总价
		    			 String unit = materialHead.getMeins();//获取单位
		    			 MaterialPrice materialPrice=new MaterialPrice();
		    			 materialPrice.setType(type);
		    			 materialPrice.setAmount(amount);
		    			 materialPrice.setLine(line);
		    			 materialPrice.setName(miaoshu);
		    			 materialPrice.setUnitPrice(price);
		    			 materialPrice.setTotalPrice(sjtotalPrice);
		    			 materialPrice.setUnit(unit);
		    			 materialPrice.setArea((double)amount);
		    			 materialPrice.setRebate(zhekou);
		    			 materialPrice.setNetPrice(sjtotalPrice*zhekou);
		    			 materialPrice.setPid(item.getId());
		    			 materialPriceDao.save(materialPrice);
					}
			     }
			}
		}
		/*List<SaleItem> findItemsByPid = saleItemDao
		.findItemsByPid(save.getId());
		long posex = 10;
		for (Iterator iterator = findItemsByPid.iterator(); iterator.hasNext();) {
			SaleItem saleItem = (SaleItem) iterator.next();
			if (ZStringUtils.isEmpty(saleItem.getPosex())) {
				saleItem.setPosex(posex + "");
			} else {
				posex = Long.parseLong(saleItem.getPosex());
			}
			if (ZStringUtils.isNotEmpty(save.getOrderCode())
					&& ZStringUtils.isEmpty(saleItem.getOrderCodePosex())) {
				saleItem.setOrderCodePosex(save.getOrderCode()
						+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
			}
			posex += 10;
		}*/
		return save;
	}
	private List<SaleItem> resetOrderPosexForSaleFor(List<SaleItem> saleItemList, Map<String, String> saleForMap) {
		// TODO Auto-generated method stub
		TreeMap<String, String> saleForTreeMap = new TreeMap<String, String>(saleForMap);
		int posex = 10;
		for (Entry<String, String> entry : saleForTreeMap.entrySet()) {
			String key = entry.getKey();
			for (SaleItem saleItem : saleItemList) {
				if(saleItem.getMaterialHeadId()!=null && !"".equals(saleItem.getMaterialHeadId())) {
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					if(key.equals(materialHead.getSaleFor())) {
						saleItem.setPosex(String.valueOf(posex));
						posex+=10;
					}
				}
			}
		}
		return saleItemList;
	}
	/**
	 * 删除 原 物料价格数据
	 * @param materialPrices 物料价格源数据
	 */
	@Override
	public void deleteMaterialPrice(List<MaterialPrice> materialPrices) {
		materialPriceDao.delete(materialPrices);
	}
	@Override
//	@Transactional(propagation=Propagation.NESTED)
	public SaleHeader save(SaleHeader saleHeader, SaleBean saleBean) {
		if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())){
			List<SaleItem> saleItemList = saleBean.getSaleItemList();
			Map<String,String> saleForMap=new HashMap<String, String>();
			for (SaleItem saleItem : saleItemList) {
				if (!"QX".equals(saleItem.getStateAudit())) {
					saleItem.setSaleHeader(saleHeader);
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					materialHead.setRowStatus("1");
					if("0".equals(materialHead.getIsStandard())) {
						if(materialHead.getMatkl()!=null&&!"".equals(materialHead.getMatkl())) {
							if("0".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())) {
								materialHead.setMatnr("102999999");
							}else if ("1".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())){
								materialHead.setMatnr("102999993");
							}else {
								SysDataDict sysDataDict = sysDataDictDao.findByKeyVal(materialHead.getMatkl());
								materialHead.setMatnr(sysDataDict.getTypeDesc());
							}
						}
					}
					if(materialHead.getSaleFor()!=null&&!"".equals(materialHead.getSaleFor())) {
			    		SysTrieTree saleForSysTrieTree = sysTrieTreeDao.findByKeyVal("SALE_FOR");
			    		if(saleForSysTrieTree!=null) {
			    			List<SysDataDict> saleForSysDataDictList = sysDataDictDao.findByTrieTreeId(saleForSysTrieTree.getId());
			    			for (SysDataDict sysDataDict : saleForSysDataDictList) {
			    				if(String.valueOf(materialHead.getSaleFor()).equals(sysDataDict.getKeyVal())) {
			    					materialHead.setSpart(sysDataDict.getTypeKey());
			    					break;
			    				}
			    			}
			    		}
			    	}
					if(materialHead!=null) {
						saleItem.setMatnr(materialHead.getMatnr());
					}
					materialHeadDao.save(materialHead);
					saleItem.setStatus("1");
					List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
					if (saleItem.getId() == null || "".equals(saleItem.getId())) {
						// saleItemPrices = getSaleItemPrice(saleItem);
					} else {
						saleItemPrices = saleItemPriceDao
								.querySaleItemPrice(saleItem.getId());
					}
					Double totalPrice = calculationPrice(saleItemPrices, saleItem
							.getAmount(), true, saleHeader.getShouDaFang());
					Set<SaleItemPrice> saleItemPriceSet = new HashSet<SaleItemPrice>(
							saleItemPrices);
					saleItem.setSaleItemPrices(saleItemPriceSet);
					saleItem.setTotalPrice(totalPrice);
					saleHeader.getSaleItemSet().add(saleItem);
					MaterialHead wlMaterialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					if(wlMaterialHead==null) {
						throw new TypeCastException("数据异常");
					}
					saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
				}
			}
			
			if(saleItemList.size()<=0) {
				 List<SaleItem> slaeItemList1 = saleItemDao.findItemsByPid(saleHeader.getId());
//				Set<SaleItem> saleItemSet = saleHeader.getSaleItemSet();
				for(SaleItem saleItem : slaeItemList1) {
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
				}
			}
			//添加物流信息
			Set<SaleLogistics> saleLogisticsList=new HashSet<SaleLogistics>();
			for (Entry<String, String> map : saleForMap.entrySet()) {
				String saleHeaderId = saleHeader.getId();
				saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
				String saleFor=map.getKey();
				String spart=map.getValue();
				SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeaderId, saleFor);
				if(saleLogistics == null) {
					saleLogistics = new SaleLogistics();
				}
				saleLogistics.setSaleFor(saleFor);
//				CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(saleHeader.getShouDaFang(), spart,"3110");
				if(saleHeader.getSongDaFang()!=null) {
					saleLogistics.setKunnrS(saleHeader.getSongDaFang());
					saleLogistics.setSaleHeader(saleHeader);
					saleLogisticsList.add(saleLogistics);
				}
			}
			saleHeader.setSaleLogisticsSet(saleLogisticsList);
			saleLogisticsDao.save(saleLogisticsList);
			List<SaleOneCust> saleOneCustList = saleBean.getSaleOneCustList();
			if(saleOneCustList!=null){
				for (SaleOneCust saleOneCust : saleOneCustList) {
					saleOneCust.setSaleHeader(saleHeader);
					saleHeader.getSaleOneCustSet().add(saleOneCust);
				}
			}
			TerminalClient terminalClient = saleBean.getTerminalClient();
			terminalClient.setSaleHeader(saleHeader);
			saleHeader.setTerminalClient(terminalClient);
			
			String id = saleHeader.getId();
			if (ZStringUtils.isEmpty(id)) {
				String orderCode = saleHeader.getOrderCode();
				String orderType = saleHeader.getOrderType();
				String pOrderCode = saleHeader.getpOrderCode();
				if (ZStringUtils.isEmpty(orderCode)
						&& ZStringUtils.isEmpty(pOrderCode)
						&& "OR4".equals(orderType)) {
					//生成免费临时单号
					String curSerialNumberFullYY = "LD"+serialNumberManager.curSerialNumberFullYY("LD", 8);
					saleHeader.setOrderCode(curSerialNumberFullYY);
				}
			}
			SaleHeader save = saleHeaderDao.save(saleHeader);
			Set<SaleItem> saleitemSet= save.getSaleItemSet();
			List<MaterialPrice> materialPrices=new ArrayList<MaterialPrice>();
			for (SaleItem saleItem : saleitemSet) {
				//计算行项目价格 （标准）
	            jdbcTemplate.update("UPDATE SALE_ITEM S SET S.TOTAL_PRICE=S.AMOUNT*DECODE(S.SANJIAN_HEAD_ID,NULL,NVL((SELECT H.kbetr FROM Material_Head H WHERE H.IS_STANDARD=1 AND H.ID=S.MATERIAL_HEAD_ID),0),NVL((SELECT SUM(I.TOTAL_PRICE) FROM MATERIAL_SANJIAN_ITEM I WHERE I.PID=S.SANJIAN_HEAD_ID),0)) WHERE S.IS_STANDARD=1 AND  S.ID='"+saleItem.getId()+"'");
	            if(saleItem.getOrtype()!=null&&!"".equals(saleItem.getOrtype())&&saleItem.getSanjianHeadId()!=null) {
	            	//OR10 需要保存散件价格
	            	MaterialSanjianHead materialSanjianHead = materialSanjianHeadDao.findOne(saleItem.getSanjianHeadId());
	            	Set<MaterialSanjianItem> materialSanjianItems = materialSanjianHead.getMaterialSanjianItemSet();
	            	List<MaterialPrice> materialPriceList = materialPriceDao.findByPid(saleItem.getId());
//	            	materialPriceDao.delete(materialPriceList);
	            	if(materialPriceList.size()>0) {
	            		this.deleteMaterialPrice(materialPriceList);
	            	}
	            	for (MaterialSanjianItem materialSanjianItem: materialSanjianItems) {
	            		String orderType = saleItem.getOrtype();
	            		MaterialPrice materialPrice=new MaterialPrice();
	            		if(("OR3".equals(orderType)||"OR4".equals(orderType))) {
	            			//String matnr = materialSanjianItem.getMatnr();//获取物料编码
	            			//MaterialHead materialHead = materialHeadDao.findMaterialHeadByMatnr(matnr);
	            			String miaoshu = materialSanjianItem.getMiaoshu();//获取描述信息
	            			Integer amount = materialSanjianItem.getAmount();//获取数量
	            			Double price = materialSanjianItem.getPrice();//获取单价
	            			Double zhekou = materialSanjianItem.getZhekou();//获取折扣信息
	            			Double sjtotalPrice = materialSanjianItem.getTotalPrice();//获取总价
	            			//String unit = materialHead.getMeins();//获取单位
	            			String typeSql = "SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE SD.TRIE_ID='5iEbUW7W1zs5gXNTcamvNF'AND SD.KEY_VAL='"+materialSanjianItem.getChanxian()+"'";
	            			List<Map<String, Object>> typeList = jdbcTemplate.queryForList(typeSql);
	            			if(typeList.size()<=0) {
	            				throw new TypeCastException("请完善相应配置（产线）");
	            			}
	            			String chanXian = materialSanjianItem.getChanxian();
	            			materialPrice.setType((typeList.get(0).get("DESC_ZH_CN")!=null)?typeList.get(0).get("DESC_ZH_CN").toString():"");
	            			materialPrice.setAmount(amount);
	            			materialPrice.setLine(chanXian);
	            			materialPrice.setName(miaoshu);
	            			materialPrice.setUnitPrice(price);
	            			materialPrice.setTotalPrice(sjtotalPrice);
	            			materialPrice.setArea((double)amount);
	            			materialPrice.setRebate(zhekou);
	            			materialPrice.setNetPrice(sjtotalPrice*zhekou);
	            			materialPrice.setUnit(materialSanjianItem.getFyhmeins());
	            			materialPrice.setPid(saleItem.getId());
	            			materialPriceDao.save(materialPrice);
	            		}else {
	            			//增加产线价格
	            			String matnr = materialSanjianItem.getMatnr();//获取物料编码
	            			String kunnr  = saleHeader.getShouDaFang();
	            			CustHeader CustHeader = custHeaderDao.finByKunnr(kunnr);
	            			MaterialHead materialHead = materialHeadDao.findMaterialHeadByMatnr(matnr,CustHeader.getVkorg(),CustHeader.getVtweg());
	            			if(materialHead==null) {
	            				throw new TypeCastException("物料信息不存在");
	            			}
	            			String matkl = materialHead.getMatkl();//获取 物料组
	            			List<Map<String, Object>> sysDataDicts = jdbcTemplate.queryForList("SELECT ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN,COUNT(SD.ID) AS TOTAL FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE SD.KEY_VAL='"+matkl+"' AND SD.STAT='1' GROUP BY ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN HAVING DECODE(ST.KEY_VAL,'102999995','1','102999996','1','102999997','1','0')>=1");
							if(sysDataDicts.size()>1||sysDataDicts.size()<=0) {
								//返回配置报错信息
								throw new TypeCastException("物料组存在多个");
							}
	            			Map<String, Object> sysDataDict = sysDataDicts.get(0);
	            			String line = (sysDataDict.get("TYPE_KEY")!=null?String.valueOf(sysDataDict.get("TYPE_KEY")):"");
	            			String type = (sysDataDict.get("DESC_ZH_CN")!=null?String.valueOf(sysDataDict.get("DESC_ZH_CN")):"");
	            			if(""==line) {
	            				//返回报错信息
	            				throw new TypeCastException("请完善相应配置（产线）");
	            			}
	            			String miaoshu = materialSanjianItem.getMiaoshu();//获取描述信息
	            			Integer amount = materialSanjianItem.getAmount();//获取数量
	            			Double price = materialSanjianItem.getPrice();//获取单价
	            			Double zhekou = materialSanjianItem.getZhekou();//获取折扣信息
	            			Double sjtotalPrice = materialSanjianItem.getTotalPrice();//获取总价
	            			String unit = materialHead.getMeins();//获取单位
	            			//MaterialPrice materialPrice=new MaterialPrice();
	            			materialPrice.setType(type);
	            			materialPrice.setAmount(amount);
	            			materialPrice.setLine(line);
	            			materialPrice.setName(miaoshu);
	            			materialPrice.setUnitPrice(price);
	            			materialPrice.setTotalPrice(sjtotalPrice);
	            			materialPrice.setUnit(unit);
	            			materialPrice.setArea((double)amount);
	            			materialPrice.setRebate(zhekou);
	            			materialPrice.setNetPrice(sjtotalPrice*zhekou);
	            			materialPrice.setUnit(unit);
	            			materialPrice.setPid(saleItem.getId());
	            			materialPriceDao.save(materialPrice);
	            		}
	            		materialPrices.add(materialPrice);
	            	}
	            }
				//保存产线价格
				if(materialPrices.size()>0) {
					materialPriceDao.save(materialPrices);
				}
			}
			List<SaleItem> findItemsByPid = saleItemDao
					.findItemsByPid(save.getId());
			long posex = 10;
			for (Iterator iterator = findItemsByPid.iterator(); iterator.hasNext();) {
				SaleItem saleItem = (SaleItem) iterator.next();
				if (ZStringUtils.isEmpty(saleItem.getPosex())) {
					saleItem.setPosex(posex + "");
				} else {
					posex = Long.parseLong(saleItem.getPosex());
				}
				if (ZStringUtils.isNotEmpty(save.getOrderCode())
						&& ZStringUtils.isEmpty(saleItem.getOrderCodePosex())) {
					saleItem.setOrderCodePosex(save.getOrderCode()
							+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
				}
				posex += 10;
			}
			return save;
		
		}else{
			
			List<SaleItem> saleItemList = saleBean.getSaleItemList();
			Map<String,String> saleForMap=new HashMap<String, String>();
			// insert or update item foreach
			// double saleTotalPrice = 0;
			List<SaleItem> oldSaleItemList = saleItemDao.findItemsByPid(saleHeader.getId());
			for (SaleItem saleItem : oldSaleItemList) {
				if (!"QX".equals(saleItem.getStateAudit())) {
					for (SaleItem saleItem2 : saleItemList) {
						if(saleItem2.getId()!=null&&saleItem2.getId().equals(saleItem.getId())) {
							saleItem.setAmount(saleItem2.getAmount());
						}
					}
					saleItem.setSaleHeader(saleHeader);
					MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					if("0".equals(materialHead.getIsStandard())) {
						if(materialHead.getMatkl()!=null&&!"".equals(materialHead.getMatkl())) {
							if("0".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())) {
								materialHead.setMatnr("102999999");
							}else if ("1".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())){
								materialHead.setMatnr("102999993");
							}else {
								SysDataDict sysDataDict = sysDataDictDao.findByKeyVal(materialHead.getMatkl());
								materialHead.setMatnr(sysDataDict.getTypeDesc());
							}
							/*SysDataDict sysDataDict = sysDataDictDao.findByKeyVal(materialHead.getMatkl());
							materialHead.setMatnr(sysDataDict.getTypeDesc());*/
						}
					}
					if(materialHead.getSaleFor()!=null&&!"".equals(materialHead.getSaleFor())) {
						SysTrieTree saleForSysTrieTree = sysTrieTreeDao.findByKeyVal("SALE_FOR");
						if(saleForSysTrieTree!=null) {
							List<SysDataDict> saleForSysDataDictList = sysDataDictDao.findByTrieTreeId(saleForSysTrieTree.getId());
							for (SysDataDict sysDataDict : saleForSysDataDictList) {
								if(String.valueOf(materialHead.getSaleFor()).equals(sysDataDict.getKeyVal())) {
									materialHead.setSpart(sysDataDict.getTypeKey());
									break;
								}
							}
						}
					}
					if(materialHead!=null) {
						saleItem.setMatnr(materialHead.getMatnr());
					}
					materialHeadDao.save(materialHead);
					saleItem.setStatus("1");
					List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
					if (saleItem.getId() == null || "".equals(saleItem.getId())) {
						// saleItemPrices = getSaleItemPrice(saleItem);
					} else {
						saleItemPrices = saleItemPriceDao
						.querySaleItemPrice(saleItem.getId());
					}
					// saleItemPriceDao.save(saleItemPrices);
					Double totalPrice = calculationPrice(saleItemPrices, saleItem
							.getAmount(), true, saleHeader.getShouDaFang());
					Set<SaleItemPrice> saleItemPriceSet = new HashSet<SaleItemPrice>(
							saleItemPrices);
					saleItem.setSaleItemPrices(saleItemPriceSet);
					saleItem.setTotalPrice(totalPrice);
					// saleTotalPrice = NumberUtils.add(saleTotalPrice,
					// totalPrice.doubleValue());
					saleHeader.getSaleItemSet().add(saleItem);
					MaterialHead wlMaterialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
					if(wlMaterialHead==null) {
						throw new TypeCastException("数据异常");
					}
					saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
				}
			}
			//添加物流信息
			Set<SaleLogistics> saleLogisticsList=new HashSet<SaleLogistics>();
			for (Entry<String, String> map : saleForMap.entrySet()) {
				String saleHeaderId = saleHeader.getId();
				saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
				String saleFor=map.getKey();
				String spart=map.getValue();
				SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeaderId, saleFor);
				if(saleLogistics == null) {
					saleLogistics = new SaleLogistics();
				}
				saleLogistics.setSaleFor(saleFor);
				CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(saleHeader.getShouDaFang(), spart,"3110");
				if(custLogistics!=null) {
					saleLogistics.setKunnrS(custLogistics.getKunnrS());
					saleLogistics.setSaleHeader(saleHeader);
					saleLogisticsList.add(saleLogistics);
				}
			}
			saleHeader.setSaleLogisticsSet(saleLogisticsList);
			saleLogisticsDao.save(saleLogisticsList);
			// saleHeader.setOrderTotal(saleTotalPrice);
			
			// delete item foreach
			// if (saleBean.getDelItems() != null && saleBean.getDelItems().size() >
			// 0)
			// saleItemDao.delete(saleBean.getDelItems());
			
			List<SaleOneCust> saleOneCustList = saleBean.getSaleOneCustList();
			if(saleOneCustList!=null){
				for (SaleOneCust saleOneCust : saleOneCustList) {
					saleOneCust.setSaleHeader(saleHeader);
					saleHeader.getSaleOneCustSet().add(saleOneCust);
				}
			}
			TerminalClient terminalClient = saleBean.getTerminalClient();
			terminalClient.setSaleHeader(saleHeader);
			saleHeader.setTerminalClient(terminalClient);
			
			String id = saleHeader.getId();
			if (ZStringUtils.isEmpty(id)) {
				String orderCode = saleHeader.getOrderCode();
				String orderType = saleHeader.getOrderType();
				String pOrderCode = saleHeader.getpOrderCode();
				if (ZStringUtils.isEmpty(orderCode)
						&& ZStringUtils.isEmpty(pOrderCode)
						&& "OR4".equals(orderType)) {
					//生成免费临时单号
					String curSerialNumberFullYY = "LD"+serialNumberManager.curSerialNumberFullYY("LD", 8);
					saleHeader.setOrderCode(curSerialNumberFullYY);
				}
			}
			SaleHeader save = saleHeaderDao.save(saleHeader);
			Set<SaleItem> saleitemSet= save.getSaleItemSet();
			List<MaterialPrice> materialPrices=new ArrayList<MaterialPrice>();
			for (SaleItem saleItem : saleitemSet) {
				//计算行项目价格 （标准）
				jdbcTemplate.update("UPDATE SALE_ITEM S SET S.TOTAL_PRICE=S.AMOUNT*DECODE(S.SANJIAN_HEAD_ID,NULL,NVL((SELECT H.kbetr FROM Material_Head H WHERE H.IS_STANDARD=1 AND H.ID=S.MATERIAL_HEAD_ID),0),NVL((SELECT SUM(I.TOTAL_PRICE) FROM MATERIAL_SANJIAN_ITEM I WHERE I.PID=S.SANJIAN_HEAD_ID),0)) WHERE S.IS_STANDARD=1 AND  S.ID='"+saleItem.getId()+"'");
				if(saleItem.getOrtype()!=null&&!"".equals(saleItem.getOrtype())&&saleItem.getSanjianHeadId()!=null) {
					//OR10 需要保存散件价格
					MaterialSanjianHead materialSanjianHead = materialSanjianHeadDao.findOne(saleItem.getSanjianHeadId());
					Set<MaterialSanjianItem> materialSanjianItems = materialSanjianHead.getMaterialSanjianItemSet();
					List<MaterialPrice> materialPriceList = materialPriceDao.findByPid(saleItem.getId());
//            	materialPriceDao.delete(materialPriceList);
					if(materialPriceList.size()>0) {
						this.deleteMaterialPrice(materialPriceList);
					}
					for (MaterialSanjianItem materialSanjianItem: materialSanjianItems) {
						String orderType = saleItem.getOrtype();
						MaterialPrice materialPrice=new MaterialPrice();
						if(("OR3".equals(orderType)||"OR4".equals(orderType))) {
							//String matnr = materialSanjianItem.getMatnr();//获取物料编码
							//MaterialHead materialHead = materialHeadDao.findMaterialHeadByMatnr(matnr);
							String miaoshu = materialSanjianItem.getMiaoshu();//获取描述信息
							Integer amount = materialSanjianItem.getAmount();//获取数量
							Double price = materialSanjianItem.getPrice();//获取单价
							Double zhekou = materialSanjianItem.getZhekou();//获取折扣信息
							Double sjtotalPrice = materialSanjianItem.getTotalPrice();//获取总价
							//String unit = materialHead.getMeins();//获取单位
							String typeSql = "SELECT SD.DESC_ZH_CN FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE SD.TRIE_ID='5iEbUW7W1zs5gXNTcamvNF'AND SD.KEY_VAL='"+materialSanjianItem.getChanxian()+"'";
							List<Map<String, Object>> typeList = jdbcTemplate.queryForList(typeSql);
							if(typeList.size()<=0) {
								throw new TypeCastException("请完善相应配置（产线）");
							}
							String chanXian = materialSanjianItem.getChanxian();
							materialPrice.setType((typeList.get(0).get("DESC_ZH_CN")!=null)?typeList.get(0).get("DESC_ZH_CN").toString():"");
							materialPrice.setAmount(amount);
							materialPrice.setLine(chanXian);
							materialPrice.setName(miaoshu);
							materialPrice.setUnitPrice(price);
							materialPrice.setTotalPrice(sjtotalPrice);
							materialPrice.setArea((double)amount);
							materialPrice.setRebate(zhekou);
							materialPrice.setNetPrice(sjtotalPrice*zhekou);
							materialPrice.setUnit(materialSanjianItem.getFyhmeins());
							materialPrice.setPid(saleItem.getId());
							materialPriceDao.save(materialPrice);
						}else {
							//增加产线价格
							String matnr = materialSanjianItem.getMatnr();//获取物料编码
							String kunnr  = saleHeader.getShouDaFang();
							CustHeader CustHeader = custHeaderDao.finByKunnr(kunnr);
							MaterialHead materialHead = materialHeadDao.findMaterialHeadByMatnr(matnr,CustHeader.getVkorg(),CustHeader.getVtweg());
							if(materialHead==null) {
								throw new TypeCastException("物料信息不存在");
							}
							String matkl = materialHead.getMatkl();//获取 物料组
							List<Map<String, Object>> sysDataDicts = jdbcTemplate.queryForList("SELECT ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN,COUNT(SD.ID) AS TOTAL FROM SYS_TRIE_TREE ST LEFT JOIN SYS_DATA_DICT SD ON ST.ID=SD.TRIE_ID WHERE SD.KEY_VAL='"+matkl+"' AND SD.STAT='1' GROUP BY ST.KEY_VAL,SD.KEY_VAL,SD.TYPE_KEY,SD.DESC_ZH_CN HAVING DECODE(ST.KEY_VAL,'102999995','1','102999996','1','102999997','1','0')>=1");
							if(sysDataDicts.size()>1||sysDataDicts.size()<=0) {
								//返回配置报错信息
								throw new TypeCastException("物料组存在多个");
							}
							Map<String, Object> sysDataDict = sysDataDicts.get(0);
							String line = (sysDataDict.get("TYPE_KEY")!=null?String.valueOf(sysDataDict.get("TYPE_KEY")):"");
							String type = (sysDataDict.get("DESC_ZH_CN")!=null?String.valueOf(sysDataDict.get("DESC_ZH_CN")):"");
							if(""==line) {
								//返回报错信息
								throw new TypeCastException("请完善相应配置（产线）");
							}
							String miaoshu = materialSanjianItem.getMiaoshu();//获取描述信息
							Integer amount = materialSanjianItem.getAmount();//获取数量
							Double price = materialSanjianItem.getPrice();//获取单价
							Double zhekou = materialSanjianItem.getZhekou();//获取折扣信息
							Double sjtotalPrice = materialSanjianItem.getTotalPrice();//获取总价
							String unit = materialHead.getMeins();//获取单位
							//MaterialPrice materialPrice=new MaterialPrice();
							materialPrice.setType(type);
							materialPrice.setAmount(amount);
							materialPrice.setLine(line);
							materialPrice.setName(miaoshu);
							materialPrice.setUnitPrice(price);
							materialPrice.setTotalPrice(sjtotalPrice);
							materialPrice.setUnit(unit);
							materialPrice.setArea((double)amount);
							materialPrice.setRebate(zhekou);
							materialPrice.setNetPrice(sjtotalPrice*zhekou);
							materialPrice.setUnit(unit);
							materialPrice.setPid(saleItem.getId());
							materialPriceDao.save(materialPrice);
						}
						materialPrices.add(materialPrice);
					}
				}
				//保存产线价格
				if(materialPrices.size()>0) {
					materialPriceDao.save(materialPrices);
				}
			}
			/*List<SaleItem> findItemsByPid = saleItemDao
			.findItemsByPid(save.getId());
			long posex = 10;
			for (Iterator iterator = findItemsByPid.iterator(); iterator.hasNext();) {
				SaleItem saleItem = (SaleItem) iterator.next();
				if (ZStringUtils.isEmpty(saleItem.getPosex())) {
					saleItem.setPosex(posex + "");
				} else {
					posex = Long.parseLong(saleItem.getPosex());
				}
				if (ZStringUtils.isNotEmpty(save.getOrderCode())
						&& ZStringUtils.isEmpty(saleItem.getOrderCodePosex())) {
					saleItem.setOrderCodePosex(save.getOrderCode()
							+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
				}
				posex += 10;
			}*/
			return save;
		}
	}

	public Double calculationPrice(List<SaleItemPrice> saleItemPrices, int num,
			boolean updateFlag, String shoudafang) {
		Double totalGrid = 0.0;
		Double subtotal = 0.0;
		Double subtotalGrid = 0.0;
		Double total = 0.0;

		Double pr04Total = 0.0;
		String orderType = "";
		Date orderDate = new Date();
		if(saleItemPrices!=null&&saleItemPrices.size()>0)
		{
			SaleItemPrice saleItemPrice1=saleItemPrices.get(0);
			if(saleItemPrice1.getSaleItem()!=null||saleItemPrice1.getId()!=null)
			{
				String sql="";
				if(saleItemPrice1.getSaleItem()!=null)
				{
					sql = "select c.order_type,c.order_date from sale_Item b,sale_header c "
						+ "where  b.pid=c.id  and b.id='"
						+ saleItemPrice1.getSaleItem().getId() + "'";
				}else
				{
					sql = "select c.order_type,c.order_date from sale_Item_Price a,sale_Item b,sale_header c "
						+ "where  a.sale_itemid=b.id and b.pid=c.id  and a.id='"
						+ saleItemPrice1.getId() + "'";
				}
				List<Map<String, Object>> query = jdbcTemplate
						.queryForList(sql);
				
				if (query.size() > 0) {
					orderType = (String) query.get(0).get("ORDER_TYPE");
					orderDate=(Date) query.get(0).get("ORDER_DATE");
				}
			}
		}
		

		// 获取售达方折扣
		CustItem custItem = getCustItem(shoudafang,orderDate);

		Double shengYu = custItem.getShengYu() == null ? 0.0 : custItem
				.getShengYu();
		for (SaleItemPrice saleItemPrice : saleItemPrices) {
			String plusOrMinus = saleItemPrice.getPlusOrMinus();
			String condition = saleItemPrice.getCondition();
			Double conditionValue = saleItemPrice.getConditionValue() == null ? 0.0
					: saleItemPrice.getConditionValue();

			if ("PR04".equals(saleItemPrice.getType())) {
				if (!"OR4".equals(orderType)) {
					//针对单个行项目修改的折扣信息去做
					conditionValue=saleItemPrice.getConditionValue()==null?0.0:saleItemPrice.getConditionValue();
					//conditionValue = custItem.getZheKou() == null ? 0.0
					//		: custItem.getZheKou();
					if (saleItemPrice.getId() != null) {
						SaleItemPrice saleItemPriceOld = getSaleItemPrice(saleItemPrice
								.getId());
						pr04Total = saleItemPriceOld.getTotal() == null ? 0.0
								: saleItemPriceOld.getTotal();
					}
				}else
				{
					conditionValue = 0.0;
					subtotal = 0.0;
					total = 0.0;
				}

			}
			String isTakeNum = saleItemPrice.getIsTakeNum();
			subtotal = this.conOperation(subtotalGrid, condition,
					conditionValue);

			if ("1".equals(isTakeNum)) {

				total = subtotal * num;
			} else {
				total = subtotal;
			}

			// 获取客户折扣

			if ("PR04".equals(saleItemPrice.getType())) {
				Double difference = NumberUtils.subtract(total, pr04Total);
				if (custItem.getShengYu() != null && difference != 0.0
						&& difference <= shengYu && updateFlag) {
					custItem.setShengYu(NumberUtils.subtract(custItem
							.getShengYu(), difference));
					custItem.setYuJi(NumberUtils.add(custItem.getYuJi(),
							difference));
				} else if (shengYu < difference) {
					if (updateFlag) {
						custItem.setShengYu(NumberUtils.add(custItem
								.getShengYu(), pr04Total));
						custItem.setYuJi(NumberUtils.add(custItem.getYuJi(),
								pr04Total));
					}
					conditionValue = 0.0;
					subtotal = 0.0;
					total = 0.0;
				}
			}

			if ("1".equals(plusOrMinus)) {
				totalGrid = totalGrid + total;
				subtotalGrid = subtotalGrid + subtotal;
			} else {
				totalGrid = totalGrid - total;
				subtotalGrid = subtotalGrid - subtotal;
			}

			if (updateFlag) {
				saleItemPrice.setConditionValue(NumberUtils.round(
						conditionValue, 2));
				saleItemPrice.setSubtotal(NumberUtils.round(subtotal, 2));
				saleItemPrice.setTotal(NumberUtils.round(total, 2));
				if (custItem.getId() != null) {
					custItemDao.save(custItem);
				}
			}
		}

		return totalGrid;
	}

	public Double conOperation(Double subtotal, String condition,
			Double conditionValue) {
		if ("1".equals(condition)) {
			return conditionValue;
		} else if ("2".equals(condition)) {
			return conditionValue;
		} else if ("3".equals(condition)) {
			return subtotal * conditionValue;
		} else if ("4".equals(condition)) {
			return subtotal / conditionValue;
		} else {
			return 0.0;
		}
	}

	public CustItem getCustItem(String shoudafang) {
		// 获取售达方折扣
		List<CustItem> custItems = custItemDao.findItemsByCode1(shoudafang);
		CustItem custItem = new CustItem();
		if (custItems.size() > 0) {
			custItem = custItems.get(0);
		}
		return custItem;
	}
	public CustItem getCustItem(String shoudafang,Date orderDate) {
		// 获取售达方折扣
		List<CustItem> custItems = custItemDao.findItemsByCode1(shoudafang,orderDate);
		CustItem custItem = new CustItem();
		if (custItems.size() > 0) {
			custItem = custItems.get(0);
		}
		return custItem;
	}

	public SaleItemPrice getSaleItemPrice(String id) {
		SaleItemPrice saleItemPrice = saleItemPriceDao.findOne(id);
		return saleItemPrice;
	}

	@Override
	public List<SaleItemPrice> querySaleItemPrice(String pid) {
		return saleItemPriceDao.querySaleItemPrice(pid);
	}

	@Override
	public void deleteSaleItemByIds(String[] ids, String custCode) {
		CustItem custItems = getCustItem(custCode);

		List<Map<String, Object>> list = jdbcTemplate
				.queryForList("select TOTAL from sale_item_price where type='PR04' and SALE_ITEMID in("
						+ StringUtil.arrayToString(ids) + ")");
		Double totalDouble = 0.0;
		for (Map<String, Object> map : list) {
			totalDouble += Double.parseDouble(map.get("TOTAL").toString());
		}
		custItems.setShengYu(NumberUtils.add(custItems.getShengYu(),
				totalDouble));
		jdbcTemplate.execute("delete sale_item_price where SALE_ITEMID in("
				+ StringUtil.arrayToString(ids) + ")");
		/*** 删除冗余数据 ***/
		myGoodsManager.deleteDataBySaleItems(ids);
		/*** 删除冗余数据 ***/
		super.delete(ids, SaleItem.class);
	}

	@Override
	public List<SaleItemPrice> getSaleItemPrice(SaleItem saleItem) {
		List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
		String isStandard = saleItem.getIsStandard();

		List<PriceCondition> priceConditions = priceConditionDao
				.queryPriceCondition();
		for (PriceCondition priceCondition : priceConditions) {
			SaleItemPrice saleItemPrice = new SaleItemPrice();

			saleItemPrice.setType(priceCondition.getType());
			saleItemPrice.setPlusOrMinus(priceCondition.getPlusOrMinus());
			saleItemPrice.setIsTakeNum(priceCondition.getIsTakeNum());
			saleItemPrice.setOrderby(priceCondition.getOrderby());
			saleItemPrice.setCondition(priceCondition.getCondition());

			if ("1".equals(isStandard)
					&& "PR01".equals(priceCondition.getType())) {
				MaterialHead materialHead = materialHeadDao.findOne(saleItem
						.getMaterialHeadId());
				// 配置属性vc价格
				// !StringUtils.isEmpty(saleItem.getMaterialPropertyItemId())
				if (!StringUtils
						.isEmpty(saleItem.getMaterialPropertyItemInfo())
						&& !StringUtils.isEmpty(saleItem.getMaterialPrice())) {
					Double materialPrice = saleItem.getMaterialPrice();

					saleItemPrice.setConditionValue(materialHead.getKbetr()
							+ materialPrice);
				} else if (!StringUtils.isEmpty(saleItem.getSanjianHeadId())) {
					Double total = 0.0;
					List<MaterialSanjianItem> list = myGoodsManager
							.getMaterialSanjianItemList(saleItem
									.getSanjianHeadId());
					for (MaterialSanjianItem materialSanjianItem : list) {
						// MaterialHead sanjian =
						// materialHeadDao.findOne(materialSanjianItem.getMaterialHeadId());
						// Double kbetr = sanjian.getKbetr();
						Double kbetr = materialSanjianItem.getPrice();
						if (ZStringUtils.isEmpty(kbetr)) {
							kbetr = 0.0;
						}

						Integer amount = materialSanjianItem.getAmount();
						if (ZStringUtils.isEmpty(amount)) {
							amount = 1;
						}

						Double zhekou = materialSanjianItem.getZhekou();
						if (ZStringUtils.isEmpty(zhekou) || zhekou <= 0) {
							zhekou = 1.0;
							if (zhekou >= 0 && zhekou <= 1) {

							} else {
								zhekou = 1.0;
							}
						}

						Double rowTotal = kbetr * amount * zhekou;

						materialSanjianItem.setTotalPrice(rowTotal);
						total += rowTotal;
					}
					saleItemPrice.setConditionValue(total);
				} else {// 物料价格
					saleItemPrice.setConditionValue(materialHead.getKbetr());
				}

			} else {
				saleItemPrice.setConditionValue(priceCondition
						.getConditionValue());
			}
			saleItemPrice.setSaleItem(saleItem);
			saleItemPrices.add(saleItemPrice);
		}
		return saleItemPrices;
	}

	@Override
	public void deleteSaleByIds(String[] ids) {
		jdbcTemplate.update("delete ACT_CT_MAPPING where ID in("
				+ StringUtil.arrayToString(ids) + ")");

		for (String id : ids) {
			SaleHeader saleHeader = saleHeaderDao.findOne(id);

			Set<SaleItem> saleItemSet = saleHeader.getSaleItemSet();
			List<String> saleItemIdLists = null;
			if (saleItemSet != null) {
				saleItemIdLists = new ArrayList<String>();

				for (Iterator iterator = saleItemSet.iterator(); iterator
						.hasNext();) {
					SaleItem saleItem = (SaleItem) iterator.next();
					String saleItemId = saleItem.getId();
					saleItemIdLists.add(saleItemId);
				}

				String[] saleItemIds = saleItemIdLists
						.toArray(new String[saleItemIdLists.size()]);
				/*** 删除冗余数据 ***/
				myGoodsManager.deleteDataBySaleItems(saleItemIds);
				/*** 删除冗余数据 ***/
			}
			// 删除主表
			saleHeaderDao.delete(saleHeader);
		}
	}

	@Override
	public Double getFuFuanMoney(SaleHeader saleHeader) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		double fuFuanMoney = 0;
		Double salerTotal = 0.0;
		for (SaleItem saleItem : saleHeader.getSaleItemSet()) {
			if ("QX".equals(saleItem.getStateAudit())) {
				String sql = "select TOTAL from SALE_ITEM_PRICE where TYPE='PR04' and SALE_ITEMID='"+ saleItem.getId() + "'";
				List<Map<String, Object>> queryForIdList = jdbcTemplate.queryForList(sql);
				if (queryForIdList.size() > 0) {
					Map<String, Object> map = queryForIdList.get(0);
					Double pr04Total =  Double.parseDouble(map.get("TOTAL").toString());
					if(pr04Total>0)
					{
						CustItem custItem = getCustItem(saleHeader.getShouDaFang());
						
						custItem.setShengYu(NumberUtils.add(custItem.getShengYu(), pr04Total));
						custItem.setYuJi(NumberUtils.subtract(custItem.getYuJi(),pr04Total));
					}
					
				}
				
				jdbcTemplate.update("delete SALE_ITEM_PRICE where SALE_ITEMID ='"
								+ saleItem.getId() + "'");
			} else {
				saleItem.setSaleHeader(saleHeader);
				saleItem.setStatus("1");
				List<SaleItemPrice> saleItemPrices = new ArrayList<SaleItemPrice>();
				if (saleItem != null
						&& saleItem.getSaleItemPrices().size() == 0) {
					saleItemPrices = getSaleItemPrice(saleItem);
				} else {
					SaleItemPriceDao saleItemPriceDao = SpringContextHolder
							.getBean("saleItemPriceDao");
					saleItemPrices = saleItemPriceDao
							.querySaleItemPrice(saleItem.getId());
				}

				Double totalPrice = calculationPrice(saleItemPrices, saleItem
						.getAmount(), true, saleHeader.getShouDaFang());
				Set<SaleItemPrice> saleItemPriceSet = new HashSet<SaleItemPrice>(
						saleItemPrices);
				saleItem.setSaleItemPrices(saleItemPriceSet);
				saleItem.setTotalPrice(totalPrice);
				saleHeader.getSaleItemSet().add(saleItem);
				salerTotal = NumberUtils.add(salerTotal, totalPrice);
			}
		}
		saleHeader.setOrderTotal(salerTotal);
		String fuFuanCond = saleHeader.getFuFuanCond();

		if ("1".equals(fuFuanCond)) {
			fuFuanMoney = salerTotal;
		} else if ("2".equals(fuFuanCond)) {
			fuFuanMoney = 0;
		} else if ("3".equals(fuFuanCond)) {
			fuFuanMoney = NumberUtils.multiply(salerTotal, 0.5);
		} else if ("4".equals(fuFuanCond)) {
			fuFuanMoney = NumberUtils.multiply(salerTotal, 0.3);
		}
		saleHeader.setFuFuanMoney(fuFuanMoney);
		return fuFuanMoney;
	}
	/* (non-Javadoc)
	 * @see com.main.manager.SaleManager#taskCustomComplete()
	 */
	@Override
	@Transactional
	public Message taskCustomComplete(Object nextflow,String uuid,DelegateTask delegateTask,RedisUtils redisUtils,Object assignee) {
		JSONObject obj = new JSONObject();
		try {
		SaleHeader saleHeader = saleHeaderDao.findOne(uuid);
		String orderCodeNew = "";
		if(nextflow!=null && !nextflow.toString().startsWith("flow_rt_")) {
			if (saleHeader != null) {
				saleHeader.setOrderDate(new Date());
				List<SaleItem> findItemsByPid = saleItemDao
						.findItemsByPid(saleHeader.getId());
				String orderCode = saleHeader.getOrderCode();
				Map<String,String> saleForMap=new HashMap<String, String>();
				if (ZStringUtils.isEmpty(orderCode)) {
					String curSerialNumberFullYY = saleHeader.getShouDaFang()
							+ serialNumberManager.curSerialNumberFullYY(saleHeader
									.getShouDaFang()
									+ DateTools.getDateYY(), 4);
					if("OR4".equals(saleHeader.getOrderType())&&ZStringUtils.isNotEmpty(saleHeader.getpOrderCode())) {
						// 免费订单
						List<Map<String, Object>> queryForList = jdbcTemplate
								.queryForList("select t.ORDER_CODE from SALE_HEADER t where t.ORDER_CODE like '%"
										+ saleHeader.getpOrderCode().substring(0,saleHeader.getShouDaFang().length() + 6)
										+ "%' order by t.ORDER_CODE");
						if (queryForList != null) { 
							if (queryForList.size() == 1) {
								orderCodeNew = saleHeader.getpOrderCode()	+ "B01";
							} else {
								orderCodeNew = saleHeader.getpOrderCode().substring(0,saleHeader.getShouDaFang().length() + 6)
										+ "B" + (queryForList.size()<10?"0"+queryForList.size():queryForList.size());
								
							}
						}
						saleHeader.setOrderCode(orderCodeNew);
					}else {
						saleHeader.setOrderCode(curSerialNumberFullYY);
					}
					saleHeaderDao.save(saleHeader);
					
					
					if (findItemsByPid != null && findItemsByPid.size() > 0) {
						for (SaleItem saleItem : findItemsByPid) {
							saleItem.setOrderCodePosex(curSerialNumberFullYY
									+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
							//String isStandard = saleItem.getIsStandard();
							saleItem.setStateAudit("A");//非标状态  起草-订单审汇（设置为A未开始）
							MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
						    if(materialHead==null) {
						    	throw new TypeCastException("数据异常");
						    }
						    saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
						}
						saleItemDao.save(findItemsByPid);
					}
				}else{
					
					//List<SaleItem> findItemsByPid = saleItemDao.findItemsByPid(saleHeader.getId());
					if (findItemsByPid != null && findItemsByPid.size() > 0) {
						for (SaleItem saleItem : findItemsByPid) {
							String isStandard = saleItem.getIsStandard();
							String stateAudit = saleItem.getStateAudit();
							if("0".equals(isStandard)){
								//B已审绘
								//C出错返回
								//D柜体审核完成
								//E已审价
								//QX取消
								if(!("B".equals(stateAudit)||"D".equals(stateAudit)||"E".equals(stateAudit)||"QX".equals(stateAudit))){
									//C状态改为A
									saleItem.setStateAudit("A");//非标状态 起草-订单审汇（设置为A未开始）
									MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
								    if(materialHead==null) {
								    	throw new TypeCastException("数据异常");
								    }
								    saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
								}
							}
							saleItem.setOrderCodePosex(saleHeader.getOrderCode()
									+ ZStringUtils.ZeroPer(saleItem.getPosex(), 4));
							if(!("B".equals(stateAudit)||"D".equals(stateAudit)||"E".equals(stateAudit)||"QX".equals(stateAudit))){
								MaterialHead materialHead = materialHeadDao.findOne(saleItem.getMaterialHeadId());
								if(materialHead==null) {
									throw new TypeCastException("数据异常");
								}
								saleForMap.put(materialHead.getSaleFor(), materialHead.getSpart());
							}
						}
						saleItemDao.save(findItemsByPid);
					}
				}
				
				/**
				 * 生成 物料信息
				 */
				
				//List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
				
				if(saleForMap.size()>0) {
					Set<SaleLogistics> saleLogisticsList=new HashSet<SaleLogistics>();
					for (Entry<String, String> map : saleForMap.entrySet()) {
						String saleHeaderId = saleHeader.getId();
						saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
						String saleFor=map.getKey();
						String spart=map.getValue();
						SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeaderId, saleFor);
						if(saleLogistics == null) {
							saleLogistics = new SaleLogistics();
						}
						saleLogistics.setSaleFor(saleFor);
						CustLogistics custLogistics = custLogisticsDao.findByKunnrAndSpartAndVkorg(saleHeader.getShouDaFang(), spart,"3110");
						saleLogistics.setKunnrS(custLogistics.getKunnrS());
						saleLogistics.setSaleHeader(saleHeader);
						
						if("OR2".equals(saleHeader.getOrderType())) {
							List<SaleItem> sjSaleItems = saleItemDao.findByMatnrIsSj(saleHeader.getId(), "102999996");
							String jiaoQi = "8";
							String zzjqlb = "Z";
							String identify = "";
							if(sjSaleItems.size()>0) {
								jiaoQi = "3";
								identify = "E";
								if(findItemsByPid.size()-sjSaleItems.size()>0) {
									jiaoQi = "8";
								}
							}
							saleLogistics.setDeliveryDay(jiaoQi);
							saleLogistics.setDeliveryIdentify(identify);
							saleLogistics.setZzjqlb(zzjqlb);
						}
						saleLogisticsList.add(saleLogistics);
					}
					saleHeader.setSaleLogisticsSet(saleLogisticsList);
				}
				/*if("OR2".equals(saleHeader.getOrderType())) {
					List<SaleItem> sjSaleItems = saleItemDao.findByMatnrIsSj(saleHeader.getId(), "102999996");
					String jiaoQi = "8";
					String zzjqlb = "Z";
					String identify = "";
					if(sjSaleItems.size()>0) {
						jiaoQi = "3";
						identify = "E";
						if(findItemsByPid.size()-sjSaleItems.size()>0) {
							jiaoQi = "8";
						}
					}
					List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
					if(saleLogisticsList.size()>0) {
						for (SaleLogistics saleLogistics : saleLogisticsList) {
							saleLogistics.setDeliveryDay(jiaoQi);
							saleLogistics.setDeliveryIdentify(identify);
							saleLogistics.setZzjqlb(zzjqlb);
						}
					}
					saleLogisticsDao.save(saleLogisticsList);
				}*/
				String taskId=delegateTask.getId();
				Set<IdentityLink> set=delegateTask.getCandidates();
				String groupId = null;
				for (IdentityLink identityLink : set) {
					groupId=identityLink.getGroupId();
				}
	//		MemoryCacheUtil.endFlow(taskId,groupId,true);
				redisUtils.endTask(assignee, groupId, taskId);
		}
	}
	}catch(Exception e){
        e.printStackTrace();            //打印完整的异常信息
        obj.put("msg", "处理异常，请联系管理员");
		Message msg = new Message(obj);
		logger.info("能力开通接口，开户异常，异常信息："+e);          
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return msg;
    }
		return null;
	}

}
