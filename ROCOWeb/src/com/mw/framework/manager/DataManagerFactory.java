package com.mw.framework.manager;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.model.PlateInfo;

public interface DataManagerFactory {

	public void calculateProcessRoute(List<PlateInfo> infos,JdbcTemplate jdbcTemplate);
}
