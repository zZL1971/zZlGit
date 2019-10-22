package com.main.manager.impl;
import java.sql.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jcifs.smb.SmbFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.main.bean.MaterialBean;
import com.main.controller.MyGoodsController;
import com.main.dao.CustHeaderDao;
import com.main.dao.CustLogisticsDao;
import com.main.dao.MaterialBujianDao;
import com.main.dao.MaterialComplainidDao;
import com.main.dao.MaterialHeadDao;
import com.main.dao.MaterialSanjianHeadDao;
import com.main.dao.MaterialSanjianItemDao;
import com.main.dao.MyGoodsDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemFjDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustLogistics;
import com.main.domain.mm.MaterialBujian;
import com.main.domain.mm.MaterialComplainid;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.mm.MyGoods;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.domain.sale.SaleLogistics;
import com.main.manager.MaterialManager;
import com.main.manager.MyGoodsManager;
import com.main.util.MyFileUtil;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.impl.CommonManagerImpl;
import com.mw.framework.utils.StringUtil;

@Service("myGoodsManager")
@Transactional
public class MyGoodsManagerImpl extends CommonManagerImpl implements MyGoodsManager {
    private static final Logger logger = LoggerFactory.getLogger(MyGoodsManagerImpl.class);
    @Autowired
    private MyGoodsDao myGoodsDao;
    @Autowired
    private SaleItemFjDao saleItemFjDao;
    @Autowired
    private MaterialSanjianItemDao materialSanjianItemDao;
    @Autowired
    private MaterialSanjianHeadDao materialSanjianHeadDao;
    @Autowired
    private MaterialBujianDao materialBujianDao;
    @Autowired
    private MaterialComplainidDao materialComplainidDao;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private MaterialManager materialManager;
    @Autowired
    private MaterialHeadDao materialHeadDao;
    @Autowired
    private SaleItemDao saleItemDao;
    @Autowired
    private CustHeaderDao custHeaderDao;
    @Autowired
    private SaleHeaderDao saleHeaderDao;
    @Autowired
    private SaleLogisticsDao saleLogisticsDao;
    @Autowired
    private CustLogisticsDao custLogisticsDao;
	@Override
	public void deleteByIds(String[] ids,String loadStatus) {
	    //我的商品 删除标记删除
        if("2".equals(loadStatus)){
            jdbcTemplate.execute("update MY_GOODS set STATUS='X' where id in("
                    + StringUtil.arrayToString(ids) + ")");
        }else if("3".equals(loadStatus)){
         //订单激活后物理删除
            jdbcTemplate.execute("delete MY_GOODS  where id in("
                    + StringUtil.arrayToString(ids) + ")");
        } 
	}	
    @Override
    public MaterialSanjianHead saveSJ(MaterialBean materialBean,String Pid) {
    	
        MaterialSanjianHead headTemp = materialBean.getMaterialSanjianHead();
        
        List<MaterialSanjianItem> materialSanjiansTemp = materialBean.getMaterialSanjians();
        
        Set<MaterialSanjianItem> materialSanjianItemSet = new HashSet<MaterialSanjianItem>();
        int i = 1;
        for (Iterator iterator = materialSanjiansTemp.iterator(); iterator.hasNext();) {
            MaterialSanjianItem materialSanjianItem = (MaterialSanjianItem) iterator.next();
            materialSanjianItem.setMaterialSanjianHead(headTemp);
            materialSanjianItem.setMaterialHeadId(headTemp.getMaterialHeadId());
            materialSanjianItem.setChanxian("H");
            materialSanjianItem.setOrderby(i++);
            
            materialSanjianItemSet.add(materialSanjianItem);
        }
        headTemp.setMaterialSanjianItemSet(materialSanjianItemSet);
        
          MaterialSanjianHead head = null; 
        if(StringUtils.isEmpty(headTemp.getId())){
        	String vtweg = null;
        	String vkorg = null;
			if("OR3".equals(materialBean.getbgOrderType())||"OR4".equals(materialBean.getbgOrderType())||"buDan".equals(materialBean.getbgOrderType())) {
				//客户
				CustHeader bgCustHeader = custHeaderDao.finByKunnr(materialBean.getKunnr());
				//销售组织
				vkorg = bgCustHeader.getVkorg();
				//分销渠道
				vtweg = bgCustHeader.getVtweg();
				
			}else {
				SysUser sysUser = materialBean.getSysUser();
				//客户
				CustHeader custHeader = sysUser.getCustHeader();
				//销售组织
				vkorg = custHeader.getVkorg();
				//分销渠道
				vtweg = custHeader.getVtweg();
			}
/*        	if(saleHeader!=null) {
        		if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())) {
        		}else {
        		}
        	}*/
            
            String matnr = headTemp.getMatnr();
            
            List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select h.id from material_head h where  h.matnr=? and h.vkorg = ? and h.vtweg = ? ",matnr,vkorg,vtweg);
            if(queryForList==null || queryForList.size()==0){
                return head;
            }
            Map<String, Object> map = queryForList.get(0);
            headTemp.setMaterialHeadId((String)map.get("ID"));
            
            head = materialSanjianHeadDao.save(headTemp);
            
            MyGoods myGoods = new MyGoods();
            
            if("102999995".equals(matnr)||"102999996".equals(matnr)||"102999997".equals(matnr)
            		||"102999994".equals(matnr)){
                myGoods.setOrtype("OR2");
            }else if("102999998".equals(matnr)) {
            	myGoods.setOrtype("OR3");
            }
            if("102999998".equals(matnr)) {
            	 myGoods.setType("FYH");
            }else {
            	 myGoods.setType("SJ");
            }
            myGoods.setMaterialHeadId((String)map.get("ID"));
            myGoods.setSanjianHeadId(head.getId());
            myGoodsDao.save(myGoods);
            if("102999998".equals(matnr)) {
            	//保存费用化行号
            	SaleItem fyhsaleItem = new SaleItem();
            	SaleHeader saleHeader = saleHeaderDao.findOne(Pid);
            	if(saleHeader!=null) {
            		fyhsaleItem.setMyGoodsId(myGoods.getId());
            		fyhsaleItem.setSaleHeader(saleHeader);
            		fyhsaleItem.setMaterialHeadId(head.getMaterialHeadId());
            		fyhsaleItem.setIsStandard("1");
            		fyhsaleItem.setOrtype(saleHeader.getOrderType());
            		fyhsaleItem.setPosex("10");
            		fyhsaleItem.setMaktx(head.getMiaoshu());
            		fyhsaleItem.setMatnr(head.getMatnr());
            		fyhsaleItem.setMtart("Z007");
            		fyhsaleItem.setRowStatus("1");
            		fyhsaleItem.setAmount(1);
            		fyhsaleItem.setSanjianHeadId(head.getId());
            		saleItemDao.save(fyhsaleItem);
            		
            		//添加费用化物流信息
            		Set<SaleLogistics> saleLogisticsList=new HashSet<SaleLogistics>();
        			String saleHeaderId = saleHeader.getId();
        			saleHeaderId = (saleHeaderId==null||saleHeaderId=="")?"":saleHeaderId;
        			String saleFor="0";
        			String spart="01";
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
        		
            		saleHeader.setSaleLogisticsSet(saleLogisticsList);
            		saleLogisticsDao.save(saleLogisticsList);
            	}else {
            		//报错信息
            	}
            }
         
        }else{
            head = materialSanjianHeadDao.save(headTemp);
        }
        //更改价格
        if("3".equals(headTemp.getLoadStatus())){
            SaleItem saleItem = materialBean.getSaleItem();
            if(!StringUtils.isEmpty(saleItem.getId())){
                //保存销售价格信息
                materialManager.updateSaleItem(materialBean);
            }
        }
        
        return head;
    }

    @Override
    public List<MaterialSanjianItem> getMaterialSanjianItemList(String id) {
        return materialSanjianItemDao.getMaterialSanjianItemList(id);
    }
    @Override
    public List<MaterialComplainid> saveKS(MaterialBean materialBean,String Pid) {
        List<MaterialComplainid> MaterialBujianList = materialBean.getMaterialComplainid();
        Date complainidTime = materialBean.getBjTime();
        materialBean.getMaterialHead();
        for (MaterialComplainid materialComplainid : MaterialBujianList) {
        	materialComplainid.setPid(Pid);
        	materialComplainid.setComplaintTime(complainidTime);
        	materialComplainidDao.save(materialComplainid);
		}
		return MaterialBujianList;
    	
    }
    @Override
    public MaterialBujian saveBJ(MaterialBean materialBean,String Pid) {
    	MaterialBujian head=new MaterialBujian();
    	/*
    	CommonManager commonManager=SpringContextHolder.getBean("commonManager");
    	 MaterialBujian headTemp = materialBean.getMaterialBujian();
        if(StringUtils.isEmpty(headTemp.getId())){
            
            String matnr = headTemp.getMatnr();
            
            head = materialBujianDao.save(headTemp);
            
            MyGoods myGoods = new MyGoods();
            
            if("OR3".equals(headTemp.getType())){
                myGoods.setOrtype("OR3");  
            }else if("OR4".equals(headTemp.getType())){
                myGoods.setOrtype("OR4");  
            }
            
            myGoods.setType("BJ");
            myGoods.setBujianId(head.getId());
            myGoodsDao.save(myGoods);
        }else{
            head = materialBujianDao.save(headTemp);
        }
        */
        return head;
    }

    @Override
    public void deleteMaterialSanjianItem(String[] ids, String saleItemId) {
        for (String id : ids) {
            materialSanjianItemDao.delete(id);
        }
        //清空saleItem，saleItemPrice数据
        if(!StringUtils.isEmpty(saleItemId)){
            SaleItem saleItem = saleItemDao.findOne(saleItemId);
            if(saleItem!=null){
                saleItem.setTotalPrice(0.0);
                jdbcTemplate.execute("delete from sale_item_price  where  sale_itemid='"+saleItemId+"' ");
            }
        }
    }

    @Override
    public MyGoods saveBZ(MaterialBean materialBean) {
        MyGoods myGoodsTemp = materialBean.getMyGoods();
        
        myGoodsTemp.setOrtype("OR1");
        MyGoods myGoods2 = myGoodsDao.save(myGoodsTemp);
        
        SaleItemFj saleItemFjTemp = materialBean.getSaleItemFj();
        //新增
        SaleItemFj saleItemFjNew = new SaleItemFj();
        saleItemFjNew.setMyGoodsId(myGoods2.getId());
        saleItemFjNew.setZzazdr(saleItemFjTemp.getZzazdr());
        
        saleItemFjDao.save(saleItemFjNew);
        
        return myGoods2;
    }

    @Override
    public void deleteDataBySaleItems(String[] ids) {
        for (String id : ids) {
            SaleItem saleItem = saleItemDao.findOne(id);
            SaleHeader saleHeader = saleItem.getSaleHeader();
            String MaterialHeadId  = saleItem.getMaterialHeadId();
            MaterialHead material = materialHeadDao.getOne(MaterialHeadId);
            String saleFor = material.getSaleFor();
            List<SaleLogistics> saleLogistics = saleLogisticsDao.findBySaleHeaderId(saleHeader.getId());
            for (SaleLogistics saleLogistic : saleLogistics) {
            	if(saleLogistic.getSaleFor().equals(saleFor)) {
            		saleLogisticsDao.delete(saleLogistic);
            	}
			}
         /*   if(saleLogistics.size()>0) {
            	saleLogisticsDao.delete(saleLogistics);
            }*/
            if(saleItem==null){
                continue;
            }
            //订单类型
            String ortype = saleItem.getOrtype();
            
            String myGoodsId = saleItem.getMyGoodsId();
            //避免报错
            if(myGoodsId==null){
            	continue;
            }
            
            MyGoods myGoods = myGoodsDao.findOne(myGoodsId);
            //myGoods==null 订单流程在起草状态，要清空其他表数据
            //！=null 订单流程还没激活流程，可以重新加入订单。订单流程激活时会删除商品表
            if(myGoods==null){
                //非标产品删除
                if("OR1".equals(ortype)){
                    String materialHeadId = saleItem.getMaterialHeadId();
                    MaterialHead materialHead = materialHeadDao.findOne(materialHeadId);
                    
                    if(materialHead!=null){
                        if("0".equals(materialHead.getIsStandard()) 
                         && !StringUtils.isEmpty(materialHead.getSerialNumber())
                         &&  StringUtils.isEmpty(materialHead.getMatnr())){
                            //获取文件列表，删除
                            Set<MaterialFile> materialFileSet = materialHead.getMaterialFileSet();
                            if(materialFileSet!=null){
                                for (Iterator iterator = materialFileSet.iterator(); iterator.hasNext();) {
                                    MaterialFile materialFile = (MaterialFile) iterator.next();
                                    
                                    String uploadFilePath = materialFile.getUploadFilePath();
                                    String uploadFileName = materialFile.getUploadFileName();
                                    try {
                                        SmbFile file  = new SmbFile(uploadFilePath+MyFileUtil.FILE_DIR+uploadFileName);
                                        if (file!=null && file.exists()){
                                            file.delete();  
                                        } 
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        logger.info("删除文件异常！");
                                    }
                                } 
                            }
                            //清空主表
                            materialHeadDao.delete(materialHead);
                        }
                        //删除附加表
                        List<SaleItemFj> saleItemFjLists = saleItemFjDao.querySaleItemFj(myGoodsId);
                        saleItemFjDao.delete(saleItemFjLists);
                    }
                //散件删除
                }else if("OR2".equals(ortype)||"OR3".equals(ortype)||"OR4".equals(ortype)){
                    String sanjianHeadId = saleItem.getSanjianHeadId();
                    MaterialSanjianHead materialSanjianHead = materialSanjianHeadDao.findOne(sanjianHeadId);
                    
                    if(materialSanjianHead!=null){
                        materialSanjianHeadDao.delete(materialSanjianHead);
                    }
                //补件删除   
                }else if("OR3".equals(ortype)||"OR4".equals(ortype)){
                    String bujianId = saleItem.getBujianId();
                    MaterialBujian materialBujian = materialBujianDao.findOne(bujianId);
                    
                    if(materialBujian!=null){
                        materialBujianDao.delete(materialBujian);
                    }
                }
                
            }
            
            if("OR3".equals(saleHeader.getOrderType())||"OR4".equals(saleHeader.getOrderType())) {
            	String materialHeadId = saleItem.getMaterialHeadId();
                MaterialHead materialHead = materialHeadDao.findOne(materialHeadId);
                if(materialHead!=null){
                    if("0".equals(materialHead.getIsStandard()) 
                     && !StringUtils.isEmpty(materialHead.getSerialNumber())
                     &&  StringUtils.isEmpty(materialHead.getMatnr())){
                        //获取文件列表，删除
                        Set<MaterialFile> materialFileSet = materialHead.getMaterialFileSet();
                        if(materialFileSet!=null){
                            for (Iterator iterator = materialFileSet.iterator(); iterator.hasNext();) {
                                MaterialFile materialFile = (MaterialFile) iterator.next();
                                
                                String uploadFilePath = materialFile.getUploadFilePath();
                                String uploadFileName = materialFile.getUploadFileName();
                                try {
                                    SmbFile file  = new SmbFile(uploadFilePath+MyFileUtil.FILE_DIR+uploadFileName);
                                    if (file!=null && file.exists()){
                                        file.delete();  
                                    } 
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.info("删除文件异常！");
                                }
                            } 
                        }
                        //清空主表
                        materialHeadDao.delete(materialHead);
                    }
                    //删除附加表
                    List<SaleItemFj> saleItemFjLists = saleItemFjDao.querySaleItemFj(myGoodsId);
                    saleItemFjDao.delete(saleItemFjLists);
                }
            }
        }
    }

    @Override
    public void deleteData() {
        List<Map<String, Object>> myGoodsLists = jdbcTemplate.queryForList("select t.* from  MY_GOODS t where t.STATUS='X'");
        for (Map<String, Object> map : myGoodsLists) {
            String myGoodsId = (String)map.get("ID");
            
            List<Map<String, Object>> saleItemLists = jdbcTemplate.queryForList("select i.* from sale_item i where i.my_goods_id ='"+myGoodsId+"' ");
            //myGoodsId 不存在saleItem表中
            if(saleItemLists==null || saleItemLists.size()==0){
                //订单类型
                String ortype = (String)map.get("ORTYPE");
                
                //非标产品删除
                if("OR1".equals(ortype)){
                    String materialHeadId = (String)map.get("MATERIAL_HEAD_ID");
                    MaterialHead materialHead = materialHeadDao.findOne(materialHeadId);
                    
                    if(materialHead!=null){
                        if("0".equals(materialHead.getIsStandard()) 
                           && !StringUtils.isEmpty(materialHead.getSerialNumber())
                           &&  StringUtils.isEmpty(materialHead.getMatnr())){
                            //获取文件列表，删除
                            Set<MaterialFile> materialFileSet = materialHead.getMaterialFileSet();
                            if(materialFileSet!=null){
                                for (Iterator iterator = materialFileSet.iterator(); iterator.hasNext();) {
                                    MaterialFile materialFile = (MaterialFile) iterator.next();
                                    
                                    String uploadFilePath = materialFile.getUploadFilePath();
                                    String uploadFileName = materialFile.getUploadFileName();
                                    try {
                                        SmbFile file  = new SmbFile(uploadFilePath+MyFileUtil.FILE_DIR+uploadFileName);
                                        if (file!=null && file.exists()){
                                            file.delete();  
                                        } 
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        logger.info("删除文件异常！");
                                    }
                                } 
                            }
                            //清空主表
                            materialHeadDao.delete(materialHead);
                        }
                        //删除附加表
                        List<SaleItemFj> saleItemFjLists = saleItemFjDao.querySaleItemFj(myGoodsId);
                        saleItemFjDao.delete(saleItemFjLists);
                    }
                //散件删除
                }else if("OR2".equals(ortype)||"OR3".equals(ortype)||"OR4".equals(ortype)){
                    String sanjianHeadId = (String)map.get("SANJIAN_HEAD_ID");
                    MaterialSanjianHead materialSanjianHead = materialSanjianHeadDao.findOne(sanjianHeadId);
                    
                    if(materialSanjianHead!=null){
                        materialSanjianHeadDao.delete(materialSanjianHead);
                    }
                    
                //补件删除
                }else if("OR3".equals(ortype)||"OR4".equals(ortype)){
                    String bujianId = (String)map.get("BUJIAN_ID");
                    MaterialBujian materialBujian = materialBujianDao.findOne(bujianId);
                    
                    if(materialBujian!=null){
                        materialBujianDao.delete(materialBujian);
                    }
                }
                //商品删除
                myGoodsDao.delete(myGoodsId);
            }
        }
    }
	@Override
	public void deleteComplainid(String[] ids, String saleItemId) {
        for (String id : ids) {
        	materialComplainidDao.delete(id);
        }
        //清空saleItem，saleItemPrice数据
        if(!StringUtils.isEmpty(saleItemId)){
            SaleItem saleItem = saleItemDao.findOne(saleItemId);
            if(saleItem!=null){
                saleItem.setTotalPrice(0.0);
                jdbcTemplate.execute("delete from sale_item_price  where  sale_itemid='"+saleItemId+"' ");
            }
        }
    }
}
