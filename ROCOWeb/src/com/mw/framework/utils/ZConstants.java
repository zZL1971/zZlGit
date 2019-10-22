package com.mw.framework.utils;

public class ZConstants {
	public static final int MAX_INT=2147483647;
	
	public static final String DB_TYPE ="sqlserver";

	//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

	public static final String AUTHOR = "JAMESLUO";
	/**
	 * 系统名称
	 */
	public static final String APP_NAME = "xpro";
	/**
	 * Action初始化
	 */
	public static final String ACTION_INIT = "action.init";

	/**
	 * 系统实体包
	 */
	public static final String APP_ENTITY_PACKAGE = "com.openktv.entity";
	/**
	 * 申请类别前缀
	 */
	public static final String APPLY_PRE = "apply";
	//BBBBBBBBBBBBBBBBBBB

	/**
	 * 用数字代表的true
	 */
	public static final String BOOLEAN_TRUE_NUM = "1";
	/**
	 * 用数字代表的false
	 */
	public static final String BOOLEAN_FALSE_NUM = "0";//代办false

	//CCCCCCCCCCCCCCCCCCCCCCCC
	/**
	 * 配置文件路径
	 */
	public static final String CONFIG_PATH = "/WEB-INF/config/config.xml";
	/**
	 * 公司名称
	 */
	public static final String COMPANY_ZH = "深圳爱思普咨询公司";
	/**
	 * 公司英文名称
	 */
	public static final String COMPANY_EN = "SPRO CONSULTING LTD.";
	/**
	 * 公司地址
	 */
	public static final String COMPANY_ADDRESS = "深圳卓越时代广场";
	/**
	 *公司邮箱
	 */
	public static final String COMPANY_EMAIL = "JAMESLUO@CONSULTING.COM.CN";

	//DDDDDDDDDDDDDDDDDDDDDDDD

	/**
	 * 数据库方言
	 */
	public static final String DATABASE_DIALECT = "database.dialect";

	/**
	 * 数据库连接字符串
	 */
	public static final String DATABASE_URL = "database.url";

	/**
	 * JDBC驱动类
	 */
	public static final String DATABASE_DRIVER = "database.driver";

	/**
	 * 系统管理员插入语句
	 */
	public static final String DATABASE_ADMIN = "database.admin";

	/**
	 * 系统管理员扩展信息语句
	 */
	public static final String DATABASE_UFIELD = "database.ufield";

	/**
	 * 实体删除标记
	 */
	public static final String DELETE_FLAG_Y = "y";
	public static final String DELETE_FLAG_N = "n";

	/**
	 * 是不是默认公关标记
	 */
	public static final String DEFAULT_FLAG_Y = "y";
	public static final String DEFAULT_FLAG_N = "n";

	//EEEEEEEEEEEEEEEEEEEEE
	/**
	 * 页面执行时间KEY
	 */
	public static final String EXECUTION_TIME = "pageTime";
	/**
	 * 系统字符编码
	 */
	public static final String ENCODING = "utf-8";
	/**
	 * 错误跳转返回值，针对于全局跳转
	 */
	public static final String ERROR_MESSAGE = "errorMessage";//action 失败信息提示跳转返回值

	/**
	 * 标记
	 */
	public static final String FLAG_TRUE = "t";
	public static final String FLAG_FALSE = "f";

	/**
	 * 邀请好友方式:邮件，超连接
	 */
	public static final String INVITE_TYPE_BY_EMAIL = "0";
	public static final String INVITE_TYPE_BY_HTTP_LINK = "1";
	public static final String INVITE_TYPE_BY_EAMIL_OR_HTTP_LINK = "2";

	//IIIIIIIIIIIIIIIIIIIII
	/**
	 * 系统初始运行时间
	 */
	public static final String INIT_YEAR = "20090801";

	//LLLLLLLLLLLLLLLLLLLLLLLLLLLLL
	/**
	 * 登陆用户id session键
	 */

	public static final String LOGIN_USER_ID = "SPRING_SECURITY_LAST_USERNAME";//登录用户ID
	/**
	 * 会话中的用户信息键
	 */
	public static final String LOGIN_USER = "loginUserForSession";//登录用户会话取值用
	public static final String LOGIN_USER_NAME = "loginUserForSessionid";//登录用户id会话取值
	/**
	 * 简体中文
	 */
	public static final String LAN_ZH_CN = "zh_CN";
	/**
	 * 英文
	 */
	public static final String LAN_EN_US = "en_US";
	/**
	 * 繁体
	 */
	public static final String LAN_ZH_TW = "zh_TW";
	//MMMMMMMMMMMMMMMMMMMMMMMMMMMMM
	/**
	 * 我的附件配置文件路径
	 */
	public static final String MYATTACHTYPE_CONFIG_PATH = "/WEB-INF/config/myattachment.xml";

	/**
	 *公司邮箱
	 */
	public static final String ORDER_STATUS_NOT_ASSIGN = "0";//未分配房间
	public static final String ORDER_STATUS_HAD_ASSIGN = "1";//已分配房间
	public static final String ORDER_STATUS_OVERDUE = "2";//过期不来
	public static final String ORDER_STATUS_END = "3";//正常结束 
	public static final String ORDER_STATUS_DISANNUL = "4";//作废,取消(删除)

	//PPPPPPPPPPPPPPPPPPPPPPPPPPP
	/**
	 * 分页数
	 */
	public static final int PAGE_NO = 4;// 分页没有分页数

	/**
	 * 分页数
	 */
	public static final int PROJECT_NAME = 4;// 分页没有分页数
	/**
	 * 系统版权
	 */
	public static final String POWER_BY = "POWER BY";
	//RRRRRRRRRRRRRRRRRRRRRRRRRRRR

	/**
	 * 已读标志
	 */
	public static final String READ_FLAG_Y = "y";
	public static final String READ_FLAG_N = "n";

	/**
	 * DBA角色编码
	 */
	public static final String ROLE_DBA = "DBA";
	/**
	 * 管理员角色编码
	 */
	public static final String ROLE_ADMIN = "admin";
	/**
	 * 用户角色编码
	 */
	public static final String ROLE_REGIST_USER = "regist_user";
	/**
	 * 商户用户角色编码
	 */
	public static final String ROLE_SHOP_USER = "shopUser";
	/**
	 * 公关经理角色编码
	 */
	public static final String ROLE_PUBLICK_RELATION_MANAGER = "PubRelManger";
	/**
	 * ktv后台管理员
	 */
	public static final String ROLE_KTV_ADMIN = "ktv_admin";
	/**
	 * 随机数取值范围
	 */

	public static final int RANDOM_RANGE = 1000;// 数据数随机码
	/**
	 * 显示请求信息KEY
	 */
	public static final String REQUEST_CONFIG = "reqcfg";
	/**
	 * 行状态标记为有效
	 */
	public static final String ROW_STATUS_EFFICACY = "1";
	/**
	 * 行状态标记为无效
	 */
	public static final String ROW_STATUS_INEFFICACY = "0";
	/**
	 * 行状态标记为其他原因
	 */
	public static final String ROW_STATUS_OTHER = "2";
	/**
	 * 推广链接的属性名称
	 */
	public static final String RECOMMEND_UUID = "uuid";
	/**
	 * 确认注册的属性名称
	 */
	public static final String AFFIRM_UUID = "affirm";

	//SSSSSSSSSSSSSSSSSSSSSSSSSS
	/**
	 * action 成功信息提示跳转返回值。针对于全局跳转
	 */

	public static final String SUCCESS_MESSAGE = "successMessage";//action 成功信息提示跳转返回值
	/**
	 * 负责发送新用户注册欢迎信件的用户名称, 该名称同时不允许用户注册
	 */
	public static final String SYSTEM_USERNAME = "系统";
	/**
	 * 字符串分割符 为了避免分割符更原有字符串相同，在长度允许情况下尽量复杂店
	 */

	public static final String SPLIT_STR = "_Split-Str_";//
	/**
	 * 负责发送新用户注册欢迎信件的用户名称, 该名称同时不允许用户注册
	 */
	public static final double SERIAL_NUMBER_LEAVER_WARN = 0.9;

	//TTTTTTTTTTTTTTTTTTTTT
	public static final String TYPE_FRIEND = "f";//普通用户的朋友
	public static final String TYPE_CUSTOMER = "c";//客户经理的客户

	//WWWWWWWWWWWWWWWWWWW
	/**
	 * 程序全局配置键
	 */
	public static final String WEB_CONFIG = "config";

	/**
	 * 程序主页键
	 */
	public static final String WEB_MAIN = "main";

	/**
	 * 程序安装向导键
	 */
	public static final String WEB_INSTALL = "install";

	/**
	 * 显示系统提示信息
	 */
	public static final String WEB_MESSAGE = "message";

	/**
	 * 登录页面键
	 */
	public static final String WEB_LOGIN = "login";

	/**
	 * 分页数据键
	 */
	public static final String WEB_PAGE = "page";
	/**
	 * 匿名用户
	 */
	public static final String ROLE_ANONYMOUS = "roleAnonymous";
	/**
	 * 系统操作类型
	 */
	public static final String SYS_OPERTATE_TYPE = "SYS_OPERTATE_TYPE";

}
