/**
 *
 */
package com.main.dao;
import java.util.List;
import com.main.domain.sale.SaleItemFj;
import org.springframework.data.jpa.repository.Query;
import com.mw.framework.support.dao.GenericRepository;

public interface SaleItemFjDao extends GenericRepository<SaleItemFj, String> {
    @Query("select t from SaleItemFj t where t.myGoodsId = ?1 ")
    public List<SaleItemFj> querySaleItemFj(String myGoodsId);
    
    @Query("from SaleItemFj where materialHeadId = ?1")
	public SaleItemFj findByMaterialHeadId(String materialHeadId);
}
