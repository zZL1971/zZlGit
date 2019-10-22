package com.mw.framework.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mw.framework.domain.WeChat;
import com.mw.framework.support.dao.GenericRepository;

@Component
public interface WeChatDao  extends GenericRepository<WeChat, String>{
	
	public Page<WeChat> findById(String id,Pageable pageRequest);
	
	public List<WeChat> findByParent(WeChat parent,Sort sort);
}