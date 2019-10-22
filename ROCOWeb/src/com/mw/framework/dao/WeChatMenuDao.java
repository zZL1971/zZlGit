package com.mw.framework.dao;

import java.util.List;

import com.mw.framework.domain.WeChatMenu;
import com.mw.framework.support.dao.GenericRepository;

public interface WeChatMenuDao extends GenericRepository<WeChatMenu, String>  {
	public List<WeChatMenu> findByPid(String pid);
}
