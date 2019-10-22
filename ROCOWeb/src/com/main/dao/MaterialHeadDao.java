/**
 *
 */
package com.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.main.domain.mm.MaterialHead;
import com.mw.framework.support.dao.GenericRepository;


public interface MaterialHeadDao extends GenericRepository<MaterialHead, String>{
	@Query("from MaterialHead where isStandard='1' and mtart='Z101' and kbstat is null and matnr=?1 ")
	public List<MaterialHead> findStandardByMatnr(String matnr);
	
	@Query("from MaterialHead where isStandard='1' and mtart <>'Z101' and kbstat is null and matnr=?1 and vkorg=?2 and vtweg=?3")
	public MaterialHead findMaterialHeadByMatnr(String matnr,String vkorg,String vtewg);
}
