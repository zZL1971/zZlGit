package com.main.manager.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.main.bean.MaterialBean;
import com.main.dao.CustHeaderDao;
import com.main.dao.MaterialFileDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.MaterialItemDao;
import com.main.dao.MaterialPriceConditionDao;
import com.main.dao.MaterialPriceDao;
import com.main.dao.MaterialPropertyDao;
import com.main.dao.MaterialPropertyItemDao;
import com.main.dao.MyGoodsDao;
import com.main.dao.PriceConditionDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemFjDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialItem;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.MaterialPriceCondition;
import com.main.domain.mm.MaterialProperty;
import com.main.domain.mm.MaterialPropertyItem;
import com.main.domain.mm.MyGoods;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.domain.sale.SaleItemPrice;
import com.main.manager.MaterialManager;
import com.main.manager.SaleManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysActCTMappingDao;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.domain.SysUser;
import com.mw.framework.exception.TypeCastException;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.manager.SysActCtOrdErrManager;
import com.mw.framework.manager.impl.CommonManagerImpl;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.FieldFunction;
import com.mw.framework.utils.NumberUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

@Service("materialManager")
@Transactional
public class MaterialManagerImpl extends CommonManagerImpl implements
        MaterialManager {
    
    @Autowired
    private MaterialHeadDao materialHeadDao;
    @Autowired
    private MaterialFileDao materialFileDao;
    @Autowired
    private MaterialItemDao materialItemDao;
    @Autowired
    private PriceConditionDao priceConditionDao;
    @Autowired
    private MaterialPriceConditionDao materialPriceConditionDao;
    @Autowired
    private MaterialPropertyDao materialPropertyDao;
    @Autowired
    private MaterialPropertyItemDao materialPropertyItemDao;
    @Autowired
    private MyGoodsDao myGoodsDao;
    @Autowired
    private SaleItemDao saleItemDao;
    @Autowired
    private SaleItemFjDao saleItemFjDao;
    @Autowired
    private CustHeaderDao custHeaderDao;
    @Autowired
    private SaleManager saleManager;
    @Autowired
    private SerialNumberManager serialNumberManager;
    
    @Autowired
    CommonManager commonManager;
    
    @Autowired
    SysTrieTreeDao sysTrieTreeDao;
    
    @Autowired
    SysDataDictDao sysDataDictDao;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    MaterialPriceDao materialPriceDao;
    
    @Autowired
    private SysActCtOrdErrManager sysActCtOrdErrManager;
    
    @Autowired
    private SysActCTMappingDao sysActCTMappingDao;
    
    @Autowired
    private TaskService taskService;
    
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MaterialFile> queryMaterialFile(String pid,String fileType){
        return materialFileDao.queryMaterialFile(pid, fileType);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MaterialItem> queryMaterialItem(String pid) {
        return materialItemDao.queryMaterialItem(pid);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<PriceCondition> queryPriceCondition() {
        return priceConditionDao.queryPriceCondition();
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MaterialPriceCondition> queryMaterialPriceCondition(String pid) {
//      return materialPriceConditionDao.queryMaterialPriceCondition(pid);
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MaterialProperty> queryMaterialProperty(String pid) {
        return materialPropertyDao.queryMaterialProperty(pid);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MaterialPropertyItem> queryMaterialPropertyItem(String pid) {
        return  materialPropertyItemDao.queryMaterialPropertyItem(pid);
    }

    @Override
    public MaterialHead saveMaterialPropertyItem(MaterialBean materialBean) {
        MaterialHead materialHeadTemp = materialBean.getMaterialHead();
        MaterialHead materialHead = materialHeadDao.findOne(materialHeadTemp.getId());
        if(materialHead!=null){
            materialHead.setPropertyDesc(materialHeadTemp.getPropertyDesc());
            Set<MaterialPropertyItem> propertyItems = materialBean.getMaterialPropertyItems();
            
            Set<MaterialPropertyItem> set = new HashSet<MaterialPropertyItem>();
            
            for (MaterialPropertyItem item : propertyItems) {
                item.setMaterialHead(materialHead);
                set.add(item);
            }
            materialHead.setMaterialPropertyItemSet(set);
            
            jdbcTemplate.execute(" delete from MATERIAL_PROPERTY_ITEM t where t.pid='"+materialHead.getId()+"' ");
            MaterialHead head = materialHeadDao.save(materialHead);
            
            return head;
        }else{
            return new MaterialHead();
        }
    }

    @Override
    public MaterialHead saveBase(MaterialBean materialBean) {
        String stateAudit = null;
        MaterialHead materialHead = materialBean.getMaterialHead();
        if("1".equals(materialHead.getLoadStatus())){
            Double price = materialHead.getPrice();//炸弹价格
            String textureOfMaterial = materialHead.getTextureOfMaterial();//材质
            /*附加信息*/
            String zzcomt = materialHead.getZzcomt();//颜色及材质
            String zzcpyt = materialHead.getZzcpyt();//产品用途
            String zzwgfg = materialHead.getZzwgfg();//是否外购物料
            Double zztyar = materialHead.getZztyar();//投影面积
            Double zzzkar = materialHead.getZzzkar();//板件展开面积
            Double zzymfs = materialHead.getZzymfs();//移门方数
            int zzymss = materialHead.getZzymss();//移门扇数
            String zzcpdj = materialHead.getZzcpdj();//产品等级
            Double zzxsfs = materialHead.getZzxsfs();//吸塑方数
            String drawGrade = materialHead.getDrawGrade();//绘图评级
            String drawType = materialHead.getDrawType();//绘图类型
            String imosPath = materialHead.getImosPath();//IMOS服务器
            //物料二次分组
            String matkl2=materialHead.getMatkl2();
            /*附加信息*/
            materialHead = materialHeadDao.findOne(materialHead.getId());
            materialHead.setPrice(price);//炸弹价格
            materialHead.setTextureOfMaterial(textureOfMaterial);//材质
            /*附加信息*/
            materialHead.setZzcomt(zzcomt);//颜色及材质
            materialHead.setZzcpyt(zzcpyt);//产品用途
            materialHead.setZzwgfg(zzwgfg);//是否外购物料
            materialHead.setZztyar(zztyar);//投影面积
            materialHead.setZzzkar(zzzkar);//板件展开面积
            materialHead.setZzymfs(zzymfs);//移门方数
            materialHead.setZzymss(zzymss);//移门扇数
            materialHead.setZzcpdj(zzcpdj);//产品等级
            materialHead.setZzxsfs(zzxsfs);//吸塑方数
            materialHead.setDrawGrade(drawGrade);//绘图评级
            materialHead.setDrawType(drawType);//绘图类型
            materialHead.setImosPath(imosPath);//IMOS服务器
            materialHead.setMatkl2(matkl2);
            /*附加信息*/
        }else if("4".equals(materialHead.getLoadStatus())){
            String flowStatus = materialHead.getFlowStatus();
            if("gp_drawing_2020".equals(flowStatus)){
                //子流程 2020绘图保存--选择IMOS服务器
                String imosPath = materialHead.getImosPath();
                materialHead = materialHeadDao.findOne(materialHead.getId());
                materialHead.setImosPath(imosPath);
                return  materialHead;
            }
              
        }else{
            stateAudit = materialHead.getStateAudit();
        }
        //生成编号
        if(StringUtils.isEmpty(materialHead.getId())
                &&"0".equals(materialHead.getIsStandard())
                &&StringUtils.isEmpty(materialHead.getSerialNumber())){
                String curSerialNumber = serialNumberManager.curSerialNumberFullYY("MM", 8);
                materialHead.setSerialNumber(curSerialNumber);
                
                SysUser sysUser = materialBean.getSysUser();
                if("OR3".equals(materialBean.getbgOrderType())||"OR4".equals(materialBean.getbgOrderType())) {
                	CustHeader bgCustHeader = custHeaderDao.finByKunnr(materialBean.getKunnr());
                	//销售组织
                	String vkorg = bgCustHeader.getVkorg();
                	materialHead.setVkorg(vkorg);
                	//分销渠道
                	String vtweg = bgCustHeader.getVtweg();
                	materialHead.setVtweg(vtweg);
                }else {
                	//客户
                	CustHeader custHeader = sysUser.getCustHeader();
                	//销售组织
                	String vkorg = custHeader.getVkorg();
                	materialHead.setVkorg(vkorg);
                	//分销渠道
                	String vtweg = custHeader.getVtweg();
                	materialHead.setVtweg(vtweg);
                }
                
        }
        if(materialHead.getMatkl()!=null&&!"".equals(materialHead.getMatkl())) {
        	if("0".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())) {
        		materialHead.setMatnr("102999999");
        	}else if("1".equals(materialHead.getSaleFor())&&"1999".equals(materialHead.getMatkl())) {
        		materialHead.setMatnr("102999993");
        	} else {
        		SysDataDict sysDataDict = sysDataDictDao.findByKeyVal(materialHead.getMatkl());
        		materialHead.setMatnr(sysDataDict.getTypeDesc());
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
        MaterialHead head =null;
        if(materialHead !=null){
        	if(materialHead.getId()!=null&&!"".equals(materialHead.getId())) {
        		MaterialHead newMaterialHead = materialHeadDao.findOne(materialHead.getId());
        		if(newMaterialHead==null){
        			head = materialHeadDao.save(materialHead);
        		}else{
        			FieldFunction.copyValue(newMaterialHead, materialHead);
        			head = materialHeadDao.save(newMaterialHead);
        		}
        	}else {
        		head = materialHeadDao.save(materialHead);
        	}
        }
//        MaterialHead head = materialHeadDao.save(materialHead);
        
        //同时保存我的物品表
        if("2".equals(materialHead.getLoadStatus())){
            SaleItemFj saleItemFj = null;
            if(StringUtils.isEmpty(materialHead.getId())){
            	 SaleItemFj saleItemFjNew = new SaleItemFj();
            	//非标新增不添加我的商品
            	if(materialBean.getMyGoods()!=null){
	            	if(materialBean.getMyGoods().getId()==null){
	            		 MyGoods myGoods = new MyGoods();
	                     myGoods.setMaterialHeadId(head.getId());
	                     myGoods.setOrtype("OR1");
	                     MyGoods myGoods2 = myGoodsDao.save(myGoods);
	                     saleItemFjNew.setMyGoodsId(myGoods2.getId());
	            	}else{
	            		//新下单界面取消我的商品，商品id等于产品表id
	            		saleItemFjNew.setMaterialHeadId(head.getId());
	            		saleItemFjNew.setMyGoodsId(head.getId());
	            	}
            	}else{
            		 MyGoods myGoods = new MyGoods();
                     myGoods.setMaterialHeadId(head.getId());
                     myGoods.setOrtype("OR1");
                     MyGoods myGoods2 = myGoodsDao.save(myGoods);
                     saleItemFjNew.setMyGoodsId(myGoods2.getId());
            	}
                
                SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
                //新增
                saleItemFjTemp.setMaterialHeadId(head.getId());
                saleItemFjNew.setZzazdr(saleItemFjTemp.getZzazdr());
                saleItemFj = saleItemFjDao.save(saleItemFjNew);
            }else{
                //更新
                SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
                String zzazdr=saleItemFjTemp.getZzazdr();
                String productSpace="99";
                boolean isProductSpace = true;
                SysTrieTree tex = sysTrieTreeDao.findByKeyVal("ZZAZDR");
    			List<SysDataDict> sysData = sysDataDictDao.findByTrieTreeId(tex.getId());
    			for (SysDataDict sysDataDict : sysData) {
    				if(zzazdr.equals(sysDataDict.getDescZhCn())){
    					productSpace = sysDataDict.getKeyVal();
    					isProductSpace=false;
    					break;
    				}
    			}
    			if(isProductSpace){
    				productSpace="99";
    			}
                saleItemFjTemp.setMaterialHeadId(head.getId());
                saleItemFjTemp.setProductSpace(productSpace);
                saleItemFj = saleItemFjDao.save(saleItemFjTemp);
            }
            head.setMyGoodsId(saleItemFj.getMyGoodsId());
            
        }else if("3".equals(materialHead.getLoadStatus())){
            SaleItem saleItem = materialBean.getSaleItem();
            String isCnc=materialBean.getSaleItem().getIsCnc();
            if(!StringUtils.isEmpty(saleItem.getId())){
                SaleItem obj = saleItemDao.findOne(saleItem.getId());
                //更新销售行项目状态
                if(!StringUtils.isEmpty(stateAudit)){
                    obj.setStateAudit(stateAudit);
                }
                //更改物料描述
                obj.setMaktx(head.getMaktx());
                //保存销售价格信息
                List<SaleItemPrice> saleItemPrices = materialBean.getSaleItemPrices();
                if(!saleItemPrices.isEmpty()){
                    this.updateSaleItem(materialBean);
                }
                //保存产线价格数据
                memoryMaterialPrice(materialBean);
                //保存是否需要CNC文件设置
                obj.setIsCnc(isCnc);
            }
            
            //更新
            SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
            if(saleItemFjTemp!=null && !StringUtils.isEmpty(saleItemFjTemp.getId())){
                SaleItemFj saleItemFj = saleItemFjDao.save(saleItemFjTemp);
                head.setMyGoodsId(saleItemFj.getMyGoodsId());
            }
            String logerType="SALE";
            SaleItem saveActSale = saleItemDao.findOne(saleItem.getId());
            SysActCTMapping sysActCTMapping = sysActCTMappingDao.findOne(saveActSale.getSaleHeader().getId());
            if(sysActCTMapping==null) {
            	throw new TypeCastException("警告：当前订单无流程记录，请尝试退回起草");
            }
            Task task = taskService.createTaskQuery().processInstanceId(sysActCTMapping.getProcinstid()).singleResult();
            sysActCtOrdErrManager.saveSysActCtOrdErrLoger(task, saleItem.getId(), materialBean.getErrType(),materialBean.getTackit(), materialBean.getErrRea(), materialBean.getErrDesc(), "订单审绘", saveActSale.getOrderCodePosex(), "usertask_drawing", logerType);
        }else if("4".equals(materialHead.getLoadStatus())){
            SaleItem saleItem = materialBean.getSaleItem();
            SaleItem obj = null;
            if(!StringUtils.isEmpty(saleItem.getId())){
                obj = saleItemDao.findOne(saleItem.getId());
                //更改物料描述
                obj.setMaktx(head.getMaktx());
            }
            String flowStatus = materialHead.getFlowStatus();
            if("gp_store".equals(flowStatus)){//子流程-客户起草
                //更新
                SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
                if(saleItemFjTemp!=null && !StringUtils.isEmpty(saleItemFjTemp.getId())){
                    SaleItemFj saleItemFj = saleItemFjDao.save(saleItemFjTemp);
                    head.setMyGoodsId(saleItemFj.getMyGoodsId());
                }
                //更改数量
                if(saleItem.getAmount()!=0)
                {
                	obj.setAmount(saleItem.getAmount());
                }
                
            }else if("gp_drawing".equals(flowStatus)){//子流程-重新审汇
                stateAudit = materialHead.getStateAudit();
                
                if(!StringUtils.isEmpty(saleItem.getId())){
                    //更新销售行项目状态
                    if(!StringUtils.isEmpty(stateAudit)){
                        obj.setStateAudit(stateAudit);
                    }
                }
                //更改数量
                if(saleItem.getAmount()!=0)
                {
                	obj.setAmount(saleItem.getAmount());
                }
            }
        }else if("5".equals(materialHead.getLoadStatus())){
            SaleItem saleItem = materialBean.getSaleItem();
            SaleItem obj = null;
            if(!StringUtils.isEmpty(saleItem.getId())){
                obj = saleItemDao.findOne(saleItem.getId());
            }
            String flowStatus = materialHead.getFlowStatus();
            if("gp_drawing_imos".equals(flowStatus)){
            	if(saleItem.getIsCnc()!=""){
            		obj.setIsCnc(saleItem.getIsCnc());
            	}
            }
        }
        
        return head;
    }

	private void memoryMaterialPrice(MaterialBean materialBean) {
		List<MaterialPrice> materilaPrices = materialBean.getMaterialPrices();
		if(!materilaPrices.isEmpty()) {
			List<MaterialPrice> metaMaterialPriceList = materialPriceDao.findByPid(materilaPrices.get(0).getPid());
			List<MaterialPrice> newMaterialPriceList=new ArrayList<MaterialPrice>();
			for (MaterialPrice materialPrice1 : metaMaterialPriceList) {
				for (MaterialPrice materialPrice2 : materilaPrices) {
					if(materialPrice2.getId()==null&&"".equals(materialPrice2.getId())) {
						continue;
					}
					if(materialPrice1.getId().equals(materialPrice2.getId())) {
						newMaterialPriceList.add(materialPrice1);
					}
				}
			}
			metaMaterialPriceList.removeAll(newMaterialPriceList);
			materialPriceDao.delete(metaMaterialPriceList);
			for (MaterialPrice materialPrice : materilaPrices) {
				materialPriceDao.save(materialPrice);
			}
		}
	}

    @Override
    public void deleteMaterialPropertyByIds(String[] ids, String pid) {
        StringBuffer sb = new StringBuffer(" delete from MATERIAL_PROPERTY ");
        sb.append("  where ID in (");
        
        for (String id : ids) {
            sb.append("'").append(id).append("',");
        }
        String sql = sb.toString();
        sql = sql.substring(0, sql.length()-1);
        sql = sql + ")";
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute(" update  MATERIAL_PROPERTY t  SET  INFO_DESC = '' where t.pid='"+pid+"' ");
        jdbcTemplate.execute(" delete from MATERIAL_PROPERTY_ITEM t where t.pid='"+pid+"' ");
        jdbcTemplate.execute(" update  material_head t set t.PROPERTY_DESC = '' where t.id='"+pid+"' ");
    }

    @Override
    public void deleteMaterialHeadByIds(String[] ids) {
        StringBuffer sb = new StringBuffer(" UPDATE MATERIAL_HEAD T SET T.STATUS='X' where T.ID in (");
        for (String id : ids) {
            sb.append("'").append(id).append("',");
        }
        String sql = sb.toString();
        sql = sql.substring(0, sql.length()-1);
        sql = sql + ")";
        jdbcTemplate.execute(sql);
    }

    @Override
    public void deleteByIds(String tableName, String[] ids,
            JdbcTemplate jdbcTemplate) {
        StringBuffer sb = new StringBuffer(" UPDATE ");
        sb.append(tableName);
        sb.append(" SET STATUS='X' where ID in (");
        
        for (String id : ids) {
            sb.append("'").append(id).append("',");
        }
        String sql = sb.toString();
        sql = sql.substring(0, sql.length()-1);
        sql = sql + ")";
        jdbcTemplate.execute(sql);
    }

    @Override
    public SaleItem updateSaleItem(MaterialBean materialBean) {
        //保存销售价格信息
        SaleItem saleItem = materialBean.getSaleItem();
        List<SaleItemPrice> saleItemPrices=materialBean.getSaleItemPrices();
        
        SaleItem obj = saleItemDao.findOne(saleItem.getId());
        obj.setAmount(saleItem.getAmount());
        
        
        String shouDaFang= materialBean.getShouDaFang();
        Double totalPrice= saleManager.calculationPrice(saleItemPrices, saleItem.getAmount(),true,shouDaFang);
        obj.setTotalPrice(totalPrice);
        Set<SaleItemPrice> saleItemPriceSet = new HashSet<SaleItemPrice>();
        for (SaleItemPrice saleItemPrice : saleItemPrices) {
            saleItemPrice.setSaleItem(obj);
            saleItemPriceSet.add(saleItemPrice);
        }
        obj.setSaleItemPrices(saleItemPriceSet);
        SaleItem save = saleItemDao.save(obj);
        SaleHeader saleHeader = save.getSaleHeader();
        Set<SaleItem> saleItemSet = saleHeader.getSaleItemSet();
        double orderTotal = 0;
        for (Iterator iterator = saleItemSet.iterator(); iterator.hasNext();) {
            SaleItem saleItem2 = (SaleItem) iterator.next();
            if(!"QX".equals(saleItem.getStateAudit()))
			{
            	orderTotal = NumberUtils.add(orderTotal, saleItem2.getTotalPrice().doubleValue());
			}
        }
        saleHeader.setOrderTotal(orderTotal);
        String fuFuanCond = saleHeader.getFuFuanCond();
        double fuFuanMoney = 0;
        if ("1".equals(fuFuanCond)) {
            fuFuanMoney = orderTotal;
        } else if ("2".equals(fuFuanCond)) {
            fuFuanMoney = 0;
        } else if ("3".equals(fuFuanCond)) {
            fuFuanMoney = NumberUtils.multiply(orderTotal, 0.5);
        } else if ("4".equals(fuFuanCond)) {
            fuFuanMoney = NumberUtils.multiply(orderTotal, 0.3);
        }
        saleHeader.setFuFuanMoney(fuFuanMoney);
        return save;
    }

    @Override
    public void syncMatnr() throws Exception {
        
        JCoDestination connect = SAPConnect.getConnect();
        JCoFunction function = connect.getRepository().getFunction(
                "ZRFC_SD_JG01");
        JCoParameterList importParameterList = function.getImportParameterList();
//        importParameterList.setValue("P_KSCHL", "PR01");
//        importParameterList.setValue("P_VKORG", "3100");
//        importParameterList.setValue("P_VTWEG", "02");
        String date = DateTools.getDateYYYYMMDD();
        importParameterList.setValue("P_DATES", date);
        importParameterList.setValue("P_KSTBM", 1);
        
        function.execute(connect);
        
        JCoTable table = function.getTableParameterList().getTable("IT_TAB");
        JCoTable table2 = function.getTableParameterList().getTable("IT_TAB1");
        
        Map<String, Map<String,SysTrieTree>> mapMatnr =new HashMap<String, Map<String,SysTrieTree>>();
//        SysTrieTree parent=commonManager.getOne("MATERIAL_PROPERTY", SysTrieTree.class);
        SysTrieTree parent = sysTrieTreeDao.findByKeyVal("MATERIAL_PROPERTY");
        List<SysTrieTree> findByTrieTreeId = sysTrieTreeDao.findByParentId(parent.getId());
        for (SysTrieTree sysTrieTree : findByTrieTreeId) {
			commonManager.delete(sysTrieTree);
		}
        for (int j = 0; j < table2.getNumRows(); j++) {
            table2.setRow(j);
            Map<String, SysTrieTree> mapTree =mapMatnr.get(table2.getValue("MATNR"));
            if(mapTree==null)
            {
                mapTree=new HashMap<String, SysTrieTree>();
            }
            
            
            SysTrieTree sysTrieTree=mapTree.get(table2.getValue("ATNAM"));
            SysDataDict  sysDataDict;
            Set<SysDataDict> dataDicts;
            if(sysTrieTree==null)
            {
                sysTrieTree = sysTrieTreeDao.findByKeyVal(table2.getValue("ATNAM").toString());
                if(sysTrieTree==null)
                {
                    sysTrieTree=new SysTrieTree();
                    dataDicts=new HashSet<SysDataDict>();
                }
                sysTrieTree.setParent(parent);
                sysTrieTree.setKeyVal(table2.getString("ATNAM"));
                sysTrieTree.setDescZhCn(table2.getString("ATBEZ"));
                
                
            }
            sysDataDict=sysDataDictDao.findByTrieTreeKeyValAndKeyVal(table2.getString("ATNAM"),table2.getString("ATWRT"));
            if(sysDataDict==null)
            {
                sysDataDict =new SysDataDict();
                sysDataDict.setKeyVal(table2.getString("ATWRT"));
            }
            sysDataDict.setDescZhCn(table2.getString("ATWTB"));
            sysDataDict.setTrieTree(sysTrieTree);
            
            dataDicts=sysTrieTree.getDataDicts();
            dataDicts.add(sysDataDict);
            sysTrieTree.setDataDicts(dataDicts);
            
            sysTrieTree.setParent(parent);
            mapTree.put(table2.getValue("ATNAM").toString(), sysTrieTree);
            
            mapMatnr.put(table2.getValue("MATNR").toString(), mapTree);
        }
        
        Iterator iteratorMatnr = mapMatnr.entrySet().iterator();  
        while(iteratorMatnr.hasNext()){  
            Entry  entryMatnr=(Entry)iteratorMatnr.next();  
            Map<String, SysTrieTree> mapTree=(Map<String, SysTrieTree>) entryMatnr.getValue();
            Iterator iteratorTree =mapTree.entrySet().iterator();
            while (iteratorTree.hasNext()) {
                Entry  entryTree=(Entry)iteratorTree.next();  
                SysTrieTree sysTrieTree=(SysTrieTree) entryTree.getValue();
                commonManager.save(sysTrieTree);
            } 
        }

        
        List<MaterialHead> materialHeadList = new ArrayList<MaterialHead>();
        
        if (table.getNumRows() > 0) {
            table.firstRow();
            for (int i = 0; i < table.getNumRows(); i++, table.nextRow()) {
                MaterialHead materialHead = new MaterialHead();
                materialHead.setIsStandard("1");
                for (JCoField jCoField : table) {
                    
                    Object value = table.getValue(jCoField.getName());
                    try {
                        if(value instanceof BigDecimal){
                            BigDecimal bigDecimal = jCoField.getBigDecimal();
                            double doubleValue = bigDecimal.doubleValue();
                            BeanUtils.setValue(materialHead, FieldFunction
                                    .dbField2BeanField(jCoField.getName()), doubleValue);
                        }else{
                            BeanUtils.setValue(materialHead, FieldFunction
                                    .dbField2BeanField(jCoField.getName()), value);
                        }
                        
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    if("spart".equals(FieldFunction.dbField2BeanField(jCoField.getName()))) {
                    	if(value!=null&&!"".equals(value)) {
                    		SysTrieTree saleForSysTrieTree = sysTrieTreeDao.findByKeyVal("SALE_FOR");
                    		if(saleForSysTrieTree!=null) {
                    			List<SysDataDict> saleForSysDataDictList = sysDataDictDao.findByTrieTreeId(saleForSysTrieTree.getId());
                    			for (SysDataDict sysDataDict : saleForSysDataDictList) {
                    				if(String.valueOf(value).equals(sysDataDict.getTypeKey())) {
                    					materialHead.setSaleFor(sysDataDict.getKeyVal());
                    					break;
                    				}
                    			}
                    		}
                    	}
                    }
                }
                if("X".equals(materialHead.getKzkfg()))
                {
                    Set<MaterialProperty> materialProperties = new HashSet<MaterialProperty>();
                    
                    Map<String,SysTrieTree> mapTree = mapMatnr.get(materialHead.getMatnr());
                    if(mapTree!=null)
                    {
                        Iterator iteratorTree =mapTree.entrySet().iterator();
                        int orderby=0;
                        while (iteratorTree.hasNext()) {
                            Entry  entryTree=(Entry) iteratorTree.next();
                            SysTrieTree sysTrieTree= (SysTrieTree) entryTree.getValue();
                            MaterialProperty materialProperty=new MaterialProperty();
                            materialProperty.setMaterialHead(materialHead);
                            materialProperty.setPropertyDesc(sysTrieTree.getDescZhCn());
                            materialProperty.setPropertyCode(sysTrieTree.getKeyVal());
                            materialProperty.setOrderby(orderby++);
                            materialProperties.add(materialProperty);
                        }
                        materialHead.setMaterialPropertySet(materialProperties);
                    }
                    
                
                }
                materialHeadList.add(materialHead);
            }
        }
        
        List<MaterialHead> allLists = materialHeadDao.createQuery("select t from MaterialHead t where t.isStandard='1' ");
        List<MaterialHead> updateAllLists = new ArrayList<MaterialHead>();
        
        Set<String> fieldNameSet = new HashSet<String>();
        fieldNameSet.add("id");
        fieldNameSet.add("createUser");
        fieldNameSet.add("createTime");
        fieldNameSet.add("updateUser");
        fieldNameSet.add("updateTime");
        fieldNameSet.add("rowStatus");
        fieldNameSet.add("propertyDesc");
        //材质
        fieldNameSet.add("textureOfMaterial");
        //是否外购
        fieldNameSet.add("zzwgfg");
        //投影面积
        fieldNameSet.add("zztyar");
        //展开面积
        fieldNameSet.add("zzzkar");
        //移门方数
        fieldNameSet.add("zzymfs");
        //移门扇数
        fieldNameSet.add("zzymss");
        //产品等级
        fieldNameSet.add("zzcpdj");
        //吸塑方数
        fieldNameSet.add("zzxsfs");
        //产品用途
        fieldNameSet.add("zzcpyt");
        for (Iterator iterator = allLists.iterator(); iterator.hasNext();) {
            MaterialHead updateMaterialHead = (MaterialHead) iterator.next();
            
            for (Iterator iterator2 = materialHeadList.iterator(); iterator2.hasNext();) {
                MaterialHead newMaterialHead = (MaterialHead) iterator2.next();
                if(updateMaterialHead.getMatnr().equals(newMaterialHead.getMatnr())&&
                        updateMaterialHead.getVkorg().equals(newMaterialHead.getVkorg())
                        &&updateMaterialHead.getVtweg().equals(newMaterialHead.getVtweg())){
                	//物料数据同步的时候，如果处理状态为A的排除更新 --add by Mark on 2017-09-1
                    if("A".equals(newMaterialHead.getKbstat())){
                    	iterator2.remove();
                    	continue;
                    }
                    FieldFunction.copyValue(updateMaterialHead, newMaterialHead,fieldNameSet);
                    jdbcTemplate.execute(" delete from MATERIAL_PROPERTY t where t.pid='"+updateMaterialHead.getId()+"' ");
                    Set<MaterialProperty> properties=newMaterialHead.getMaterialPropertySet();
                    for (MaterialProperty materialProperty : properties) {
                        materialProperty.setMaterialHead(updateMaterialHead);
                    }
                    updateAllLists.add(updateMaterialHead);
                    iterator2.remove();
                }
            }
        }
        
        if (updateAllLists != null && updateAllLists.size() > 0) {
            materialHeadDao.save(updateAllLists);// 更新
        }
        
        if (materialHeadList != null && materialHeadList.size() > 0) {
            materialHeadDao.save(materialHeadList);// 新增
        }
    }
    public void getZRFC_VC_CLASS(String matnr) throws Exception {
        
        JCoDestination connect = SAPConnect.getConnect();
        JCoFunction function = connect.getRepository().getFunction("ZRFC_VC_CLASS");
        JCoTable table = function.getTableParameterList().getTable("S_MATNR");
        table.appendRow();
        table.setValue("SIGN","I");
        table.setValue("OPTION","EQ");
        table.setValue("LOW", matnr);
        function.execute(connect);
        
        JCoTable itTab = function.getTableParameterList().getTable("IT_TAB");
//        for (JCoField jCoField : itTab) {
//            System.out.println(jCoField.getName()+":"+jCoField.getValue().toString());
//        }
    }
    
    public MaterialHead getMaterialItem(MaterialHead materialHead,MaterialBean materialBean){
        Set<MaterialItem> materialItems = materialBean.getMaterialItems();
        if(materialItems!=null){
            Set<MaterialItem> materialItem2 = new HashSet<MaterialItem>();
            for (MaterialItem obj : materialItems) {
                obj.setMaterialHead(materialHead);
                materialItem2.add(obj);
            }
            materialHead.setMaterialItemSet(materialItem2);
        }else{
        }
        /*List<MaterialItem> queryByNativeQuery = materialItemDao.queryByNativeQuery("select * from MATERIAL_ITEM where pid is null ", MaterialItem.class);
        Set<MaterialItem> materialItem2 = new HashSet<MaterialItem>();
        for (MaterialItem obj : queryByNativeQuery) {
            obj.setMaterialHead(materialHead);
            materialItem2.add(obj);
            materialHead.setMaterialItemSet(materialItem2);
        }*/
        return materialHead;
    }
    @Override
	/**
	 * 生成非标序号，同事保存
	 * @param materialBean
	 * @return
	 */
	public MaterialHead saveBase2020(MaterialBean materialBean,String user) {
		SerialNumberManager serialNumberManager = SpringContextHolder.getBean("serialNumberManager");
		MaterialHeadDao materialHeadDao = SpringContextHolder.getBean("materialHeadDao");
		MyGoodsDao myGoodsDao = SpringContextHolder.getBean("myGoodsDao");
		SaleItemFjDao saleItemFjDao = SpringContextHolder.getBean("saleItemFjDao");
		String stateAudit = null;
		MaterialHead materialHead = materialBean.getMaterialHead();
		stateAudit = materialHead.getStateAudit();
		//生成编号
		if(StringUtils.isEmpty(materialHead.getId())
				&&"0".equals(materialHead.getIsStandard())
				&&StringUtils.isEmpty(materialHead.getSerialNumber())){
			
			String curSerialNumber = serialNumberManager.curSerialNumberFullYY("MM", 8);
			//String curSerialNumber="1800129878";
			materialHead.setSerialNumber(curSerialNumber);

			SysUser sysUser = materialBean.getSysUser();
			//客户
			CustHeader custHeader = sysUser.getCustHeader();
			//销售组织
			String vkorg = custHeader.getVkorg();
			materialHead.setVkorg(vkorg);
			//分销渠道
			String vtweg = custHeader.getVtweg();
			materialHead.setVtweg(vtweg);
		}
		MaterialHead head = materialHeadDao.save(materialHead);
		head.setCreateUser(user);
		//同时保存我的物品表
		SaleItemFj saleItemFj = null;
		if(StringUtils.isEmpty(materialHead.getId())){
			SaleItemFj saleItemFjNew = new SaleItemFj();
			//非标新增不添加我的商品
			if(materialBean.getMyGoods()!=null){
				if(materialBean.getMyGoods().getId()==null){
					MyGoods myGoods = new MyGoods();
					myGoods.setMaterialHeadId(head.getId());
					myGoods.setOrtype("OR1");
					MyGoods myGoods2 = myGoodsDao.save(myGoods);
					saleItemFjNew.setMyGoodsId(myGoods2.getId());
				}else{
					//新下单界面取消我的商品，商品id等于产品表id
					saleItemFjNew.setMaterialHeadId(head.getId());
					saleItemFjNew.setMyGoodsId(head.getId());
				}
			}else{
				MyGoods myGoods = new MyGoods();
				myGoods.setMaterialHeadId(head.getId());
				myGoods.setOrtype("OR1");
				MyGoods myGoods2 = myGoodsDao.save(myGoods);
				saleItemFjNew.setMyGoodsId(myGoods2.getId());
			}

			SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
			//新增
			saleItemFjTemp.setMaterialHeadId(head.getId());
			saleItemFjNew.setZzazdr(saleItemFjTemp.getZzazdr());
			saleItemFjNew.setProductSpace(saleItemFjTemp.getProductSpace());
			saleItemFj = saleItemFjDao.save(saleItemFjNew);
		}else{
			//更新
			SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
			saleItemFjTemp.setMaterialHeadId(head.getId());
			saleItemFj = saleItemFjDao.save(saleItemFjTemp);
		}
		head.setMyGoodsId(saleItemFj.getMyGoodsId());
		materialHead.setId(head.getId().toString());
		return head;
	}
}
