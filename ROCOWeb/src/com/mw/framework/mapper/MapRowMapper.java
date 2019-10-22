/**
 *
 */
package com.mw.framework.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import oracle.sql.TIMESTAMP;

import org.springframework.jdbc.core.RowMapper;

import com.mw.framework.bean.Message;
import com.mw.framework.util.StringHelper;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.mapper.MapRowMapper.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-14
 *
 */
public class MapRowMapper implements RowMapper<Map<String,Object>> {

	private boolean camelCase= false;
	private Map<String,SimpleDateFormat> formats;
	private String format="yyyy-MM-dd HH:mm:ss";
	private SimpleDateFormat sdf;
	
	
	public MapRowMapper() {
		super();
		this.sdf = new SimpleDateFormat(format);
	}
	
	public MapRowMapper(boolean camelCase) {
		super();
		this.camelCase = camelCase;
		this.sdf = new SimpleDateFormat(this.format);
	}

	public MapRowMapper(boolean camelCase,String format) {
		super();
		this.camelCase = camelCase;
		this.format = format;
		this.sdf = new SimpleDateFormat(format);
	}
	
	public MapRowMapper(boolean camelCase, Map<String, SimpleDateFormat> formats) {
		super();
		this.camelCase = camelCase;
		this.formats = formats;
		this.sdf = new SimpleDateFormat(format);
	}

	private String getFormatDate(String key,Date date){
		SimpleDateFormat simpleDateFormat = formats!=null?formats.get(key):null;
		if(simpleDateFormat!= null){
			return simpleDateFormat.format(date);
		}else{
			return sdf.format(date);
		}
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columns = metaData.getColumnCount();
		
		for (int i = 1; i <= columns; i++) {
			String key = metaData.getColumnName(i);
			if(camelCase)
				key = StringHelper.toFieldName(key);
			
			Object object = rs.getObject(i);
			if(object != null){
				//System.out.println(key+"|"+object.getClass().getName());
				if(object.getClass().getName().equals("oracle.sql.TIMESTAMP")){
					TIMESTAMP timestamp = (TIMESTAMP) object;
					Date date = new Date(timestamp.timestampValue().getTime());
					map.put(key,getFormatDate(key,date));
				}else if(object.getClass().getName().equals("java.sql.Timestamp")){
					java.sql.Timestamp timestamp = (java.sql.Timestamp) object;
					Date date = new Date(timestamp.getTime());
					map.put(key,getFormatDate(key,date));
				}else{
					map.put(key, rs.getObject(i));
				}
			}else{
				map.put(key, "");
			}
		}
		return map;
	}

	public boolean isCamelCase() {
		return camelCase;
	}

	public void setCamelCase(boolean camelCase) {
		this.camelCase = camelCase;
	}

	public Map<String, SimpleDateFormat> getFormats() {
		return formats;
	}

	public void setFormats(Map<String, SimpleDateFormat> formats) {
		this.formats = formats;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
