package com.mw.framework.dao;

import com.mw.framework.domain.SysActTaskLog;
import com.mw.framework.support.dao.GenericRepository;
import org.springframework.stereotype.Component;

@Component
public interface SysActTaskLogDao extends GenericRepository<SysActTaskLog,String> {

}
