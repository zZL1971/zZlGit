/**
 *
 */
package com.mw.framework.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.mw.framework.domain.SysXmlControlText;
import com.mw.framework.support.dao.GenericRepository;

/**
 * @Project SysXmlControlText
 * @fileName com.mw.framework.dao.SysXmlControlTextDao.java
 * @Version 1.0.0
 * @author Chaly
 * @time 2018-09-28
 *
 */
@Component
public interface SysXmlControlTextDao  extends GenericRepository<SysXmlControlText, String>{
	@Query(value="SELECT MAX(S.textCode) FROM SysXmlControlText AS S")
	public Integer findByTextCode();
}
