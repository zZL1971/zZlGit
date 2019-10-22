/**
 *
 */
package com.mw.framework.dao;

import org.springframework.stereotype.Component;

import com.mw.framework.domain.ComponentConfig;
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
public interface ComponentConfigDao  extends GenericRepository<ComponentConfig, String>{
	
}
