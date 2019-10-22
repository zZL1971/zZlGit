/**
 *
 */
package com.mw.framework.bean;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.bean.Constants.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-3-12
 *
 */
public class Constants {
	
	public static final String CURR_USER_NAME = "CURR_USER_NAME";
	public static final String CURR_USER_KUNNR = "CURR_USER_KUNNR";
	public static final String CURR_USER = "CURR_USER";
	public static final String CURR_WEBSOCKET_USERS = "CURR_WEBSOCKET_USERS";
	public static final String CURR_USER_ID = "CURR_USER_ID";
	public static final String CURR_USER_LOGIN_NO = "CURR_USER_LOGIN_NO";
	public static final String CURR_USER_OGN_ID = "CURR_USER_OGN_ID";
	public static final String CURR_USER_OGN_NAME = "CURR_USER_OGN_NAME";
	public static final String CURR_USER_ROLES = "CURR_USER_ROLES";
	public static final String CURR_ROLE_GROUPS = "CURR_ROLE_GROUPS";
	public static final String CURR_ROLE_MENUS = "CURR_ROLE_MENUS";
	public static final String CURR_ROLE_MENUS_MAP = "CURR_ROLE_MENUS_MAP";
	//用户SAP编码
	public static final String CURR_SAP_CODE="CURR_SAP_CODE";
	//下单橱柜权限
	public static final String CURR_USER_KUNNR_CUP="CURR_USER_KUNNR_CUP";
	//下单木门权限
	public static final String CURR_USER_KUNNR_TIMBER="CURR_USER_KUNNR_TIMBER";
	
	/*当前用户外网IP*/
	public static final String CURR_USER_IA = "CURR_USER_IA";
	/*当前用户内网IP*/
	public static final String CURR_USER_NA = "CURR_USER_NA";
	
	public static final String LOGIN_USER_LIST = "LOGIN_USER_LIST";
	
	/** 验证码*/
	public static final String VALID_CODE = "VALID_CODE";
	public static final String[] IGNORE_PROPERTY_BASEENTITY={"rowStatus","createUser","createTime","updateUser","updateTime"};
	
	public static final String language="language";//zh_CN
	
	
	public static final String CACHE_LIST_TRIE_DICT="TRIE_DICT_LIST";
	public static final String CACHE_TASK_LIST="sys:task_list:";

	/* 字典缓存空间*/
	public static final String TRIE_NAMESPACE="sys:dicts:";
	
	/** 模板缓存空间*/
	public static final String TM_GRID_PARSE="sys:tm:grid:parse:";
	public static final String TM_GRID_COLUMN="sys:tm:grid:column:";
	public static final String TM_GRID_MODEL="sys:tm:grid:model:";
	public static final String TM_GRID_TREEMODEL="sys:tm:grid:treemodel:";
	public static final String TM_GRID_SEARCHFORM="sys:tm:grid:searchform:";
	public static final String TM_GRID_CONFIG="sys:tm:grid:config:";
	public static final String TM_GRID_SQL="sys:tm:grid:sql:";
	public static final String TM_GRID_TREESQL="sys:tm:grid:treesql:";
	public static final String TM_GRID_FORM = "sys:tm:grid:form:";
	public static final String TM_GRID_VALIDATION = "sys:tm:grid:validation:";
	public static final String TM_GRID_GANTMODULE = "sys:tm:grid:gantmodule:";
	
	public static String FILE_DIR = "/";
	
	public enum CalculateType{
		GTR,LT,LE,GE,EQ
	}
	public enum AND_OR{
		AND,OR
	}
	public enum YES_NO{
		NO,YES
	}
	
	/* 语言*/
	public enum Language {
		zh_CN,zh_TW,en_US;
	}
	
	/**
	 * redisDBfenpei
	 * @author ITWM
	 *
	 */
	public enum RedisMsgType{
		XML_WORK,
		TASK_WORK
	}
	/**
	 * Extjs Field支持类型：string/int/float/boolean/date
	 * @Project ROCOWeb
	 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
	 * @fileName com.mw.framework.bean.Constants.java
	 * @Version 1.0.0
	 * @author Allan Ai
	 * @time 2015-3-19
	 *
	 */
	public enum OracleColType{
		VARCHAR2("string"),NVARCHAR2("string"),NUMBER("int"),FLOAT("float"),DATE("date"),TIMESTAMP("date"),BINARY_FLOAT("float");
		private String name;
		private OracleColType(String name) {
            this.name = name;
        }
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
}
