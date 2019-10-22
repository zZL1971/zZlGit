/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.sale.SaleItem;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleItemDao extends GenericRepository<SaleItem, String> {

	@Query("from SaleItem where pid = ?1 order by to_number(posex) ")
	public List<SaleItem> findItemsByPid(String pid);

	@Query("from SaleItem where orderCodePosex=?1")
	public List<SaleItem> findByOrderCodePosex(String orderCodePosex);
	
	@Query("select si from SaleHeader sh,SaleItem si where sh.id=si.saleHeader.id and sh.orderCode = ?1 and si.sapCode is null and nvl(si.stateAudit,'E') !='QX'")
	public List<SaleItem> findByItems(String pid);
	
	@Query("select si from SaleHeader sh,SaleItem si where sh.id=si.saleHeader.id and sh.orderCode=?1 and si.sapCode is not null and si.stateAudit=?2")
	public List<SaleItem> findByItemsToSapCodeIsNotNullAndStateAudit(String pid,String stateAudit);
	
	@Query("from SaleItem si where si.saleHeader.id = ?1 and si.sapCode is not null group by si.sapCode")
	public List<SaleItem> findBySapCodeBeGroupBy(String pid);
	
	@Query("from SaleItem si where si.saleHeader.id = ?1 and si.matnr=?2")
	public List<SaleItem> findByMatnrIsSj(String pid,String matnr);
	
	@Query("select si from SaleHeader sh,SaleItem si where sh.id=si.saleHeader.id and sh.orderCode=?1 and si.stateAudit=?2")
	public List<SaleItem> findByItemsAndStateAudit(String pid,String stateAudit);
}
