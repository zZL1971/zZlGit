package com.mw.framework.dao;

import org.springframework.stereotype.Component;

import com.mw.framework.domain.SysMesSend;
import com.mw.framework.support.dao.GenericRepository;

@Component
public interface SysMesSendDao extends GenericRepository<SysMesSend, String> {

}
