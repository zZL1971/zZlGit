/**
 *
 */
package com.main.dao;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialSanjianItem;
import com.mw.framework.support.dao.GenericRepository;


public interface MaterialSanjianItemDao extends GenericRepository<MaterialSanjianItem, String>{
    @Query("select t from MaterialSanjianItem t left join t.materialSanjianHead h where h.id = ? order by t.orderby ")
    public List<MaterialSanjianItem> getMaterialSanjianItemList(String id);
}
