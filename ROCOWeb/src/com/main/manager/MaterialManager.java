package com.main.manager;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.main.bean.MaterialBean;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialItem;
import com.main.domain.mm.MaterialPriceCondition;
import com.main.domain.mm.MaterialProperty;
import com.main.domain.mm.MaterialPropertyItem;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleItem;
import com.mw.framework.bean.Message;
import com.mw.framework.manager.CommonManager;
public interface MaterialManager extends CommonManager {
	/**
	 * 根据类型MaterialHead.id,MaterialItem.fileType 查询MaterialFile
	 * @param pid -->MmMaterialHead.id
	 * @param fileType -->MmMaterialItem.fileType
	 * @return
	 */
	public List<MaterialFile> queryMaterialFile(String pid,String fileType);
	/**
	 * 根据类型MmMaterialHead.id 查询MmMaterialItem
	 * @param pid -->MmMaterialHead.id
	 * @return
	 */
	public List<MaterialItem> queryMaterialItem(String pid);
	/**
	 * 查询默认定价条件
	 * @return
	 */
	public List<PriceCondition> queryPriceCondition();
	
	/**
	 * 根据类型MmMaterialHead.id 查询对应定价条件
	 * @return
	 */
	public List<MaterialPriceCondition> queryMaterialPriceCondition(String pid);
	/**
	 * 根据类型MmMaterialHead.id 查询属性
	 * @return
	 */
	public List<MaterialProperty> queryMaterialProperty(String pid);
	/**
	 * 根据类型MmMaterialHead.id 查询属性明细
	 * @return
	 */
	public List<MaterialPropertyItem> queryMaterialPropertyItem(String pid);
	/**
	 * 保存属性价格
	 */
	public MaterialHead saveMaterialPropertyItem(MaterialBean materialBean);
	/**
	 * 保存物料
	 */
	public MaterialHead saveBase(MaterialBean materialBean);
	/**
	 * 保存物料
	 */
	public MaterialHead saveBase2020(MaterialBean materialBean,String user);
	/**
	 * 删除属性
	 */
	public void deleteMaterialPropertyByIds(String[] ids,String pid);
	/**
	 * 物料标记删除
	 * @param ids
	 * @param jdbcTemplate
	 */
	public void deleteMaterialHeadByIds(String[] ids);
	/**
	 * 删除 (标记删除)
	 * @param tableName 表名
	 * @param ids id数组
	 * @param jdbcTemplate
	 */
	public void deleteByIds(String tableName,String[] ids,
			JdbcTemplate jdbcTemplate);
	/**
	 *更新订单SaleItem 改数量和价格
	 *更新订单SaleItemPrice  定价过程 
	 */
	public SaleItem updateSaleItem(MaterialBean materialBean);
	/**
	 * 同步物料
	 */
	public void syncMatnr() throws Exception;
	
	/**
	 * 同步物料VC属性
	 */
	public void getZRFC_VC_CLASS(String matnr) throws Exception;
	
}
