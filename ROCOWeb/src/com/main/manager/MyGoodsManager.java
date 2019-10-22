package com.main.manager;
import java.util.List;

import com.main.bean.MaterialBean;
import com.main.domain.mm.MaterialBujian;
import com.main.domain.mm.MaterialComplainid;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.mm.MyGoods;
import com.mw.framework.manager.CommonManager;
public interface MyGoodsManager extends CommonManager {
	/**
	 * 删除 
	 * @param tableName 表名
	 * @param ids id数组
	 * @param jdbcTemplate
	 */
	public void deleteByIds(String[] ids,String loadStatus);
	/**
	 * 保存散件(五金&销售道具&材料)
	 * @param pid 
	 */
	public MaterialSanjianHead saveSJ(MaterialBean materialBean, String pid);
	/**
     * 保存补件(客服补购&免费订单)
     */
	public MaterialBujian saveBJ(MaterialBean materialBean,String Pid);
	
	public List<MaterialSanjianItem> getMaterialSanjianItemList(String id);
	
	public void deleteMaterialSanjianItem(String[] ids,String saleItemId);
	public void deleteComplainid(String[] ids,String saleItemId);
	/**
     * 保存标准产品
     */
    public MyGoods saveBZ(MaterialBean materialBean);
    /**
     * ids：SaleItem数组
     * 
     * 删除订单明细，同时清空我的商品表，非标物料，非标文件， 散件，补件 表等数据
     */
    public void deleteDataBySaleItems(String[] ids);
    /**
     * 定时器 删除冗余数据，（商品没有下订单，我的商品界面只做标记删除，因为商品数据是在订单激活的时候才删除的）
     */
    public void deleteData();
	public List<MaterialComplainid> saveKS(MaterialBean materialBean, String Pid);

}
