package com.mw.framework.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.main.dao.CustHeaderDao;
import com.main.domain.cust.CustHeader;
import com.mw.framework.bean.impl.AssignedEntity;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.util.JsonBooleanForIntegerSerializer;
import com.mw.framework.util.annotation.FieldMeta;

@Entity
@Table(name = "SYS_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "login_no" }) })
@JsonIgnoreProperties(ignoreUnknown = true, value = "password")
public class SysUser extends AssignedEntity implements Serializable {

	private static final long serialVersionUID = 1836109006108297063L;

	@FieldMeta(name = "登陆账号")
	private String loginNo;

	@FieldMeta(name = "用户名")
	private String userName;

	@FieldMeta(name = "密码")
	private String password;

	@FieldMeta(name = "用户类型", description = "{1:'企业用户';2:'供应商'}")
	private String userType;

	@FieldMeta(name = "用户组", description = "{1:'门店销售';2:'总部审单'}")
	private String userGroup;

	@FieldMeta(description = "当前用户所有组织架构集合")
	private Set<SysRole> roles = new HashSet<SysRole>();

	@FieldMeta(description = "当前用户所有用户组集合")
	private Set<SysGroup> groups = new HashSet<SysGroup>();
	
	@FieldMeta(description="当前微信用户所有组织架构")
	private Set<WeChat> wechatroles = new HashSet<WeChat>();

	@FieldMeta(name = "金额字段是否有查看权限")
	private boolean money = false;

	@FieldMeta(name = "性别")
	private String sex;

	@FieldMeta(name = "用户编号")
	private String userNo;

	@FieldMeta(name = "邮箱")
	private String email;

	@FieldMeta(name = "SAP内部客户编码")
	private String kunnr;
	
	@FieldMeta(name = "账号状态:{1:正常,0:冻结}")
	private int status;
	
	@FieldMeta(name = "电话号码")
	private String tel;
	
	@FieldMeta(name = "电话号码")
	private String qqNumber;

	private CustHeader custHeader;
	
	@FieldMeta(name = "技能等级")
	private String skillLevel;
	
	@FieldMeta(name = "订单审绘组别")
	private String drawingGroup;
	
	@FieldMeta(name="接口权限")
	private String interJuri;
	
	@FieldMeta(name="2020加密狗")
	private String softdogId;
	
	public String getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(String skillLevel) {
		this.skillLevel = skillLevel;
	}

	public String getDrawingGroup() {
		return drawingGroup;
	}

	public void setDrawingGroup(String drawingGroup) {
		this.drawingGroup = drawingGroup;
	}

	public String getCabinetTarget() {
		return cabinetTarget;
	}

	public void setCabinetTarget(String cabinetTarget) {
		this.cabinetTarget = cabinetTarget;
	}

	public String getQualityTarget() {
		return qualityTarget;
	}

	public void setQualityTarget(String qualityTarget) {
		this.qualityTarget = qualityTarget;
	}

	public String getTaskArea() {
		return taskArea;
	}

	public void setTaskArea(String taskArea) {
		this.taskArea = taskArea;
	}

	@FieldMeta(name = "柜体目标")
	private String cabinetTarget;
	
	@FieldMeta(name = "质量目标")
	private String qualityTarget;
	
	@FieldMeta(name="任务分区")
	private String taskArea;
	
	private SysInnerUser sysInnerUser;

	public SysUser() {
		super();
	}

	public SysUser(String loginNo, String userName, String password,
			String sex, String userNo,Set<WeChat> wechatroles) {
		super();
		this.loginNo = loginNo;
		this.userName = userName;
		this.password = password;
		this.sex = sex;
		this.userNo = userNo;
		this.wechatroles=wechatroles;
	}

	@Column(name = "TEL", length = 30)
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Column(name = "QQ_NUMBER", length = 30)
	public String getQqNumber() {
		return qqNumber;
	}

	public void setQqNumber(String qqNumber) {
		this.qqNumber = qqNumber;
	}

	@Column(name = "LOGIN_NO", length = 30)
	public String getLoginNo() {
		return loginNo;
	}

	public void setLoginNo(String loginNo) {
		this.loginNo = loginNo;
	}

	@Column(name = "USER_NAME", length = 30)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "USER_TYPE", length = 30)
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Column(name = "SEX")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "USER_NO", length = 30)
	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	@Column(name = "PASSWORD", length = 30)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "STATUS")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	public Set<SysRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<SysRole> roles) {
		this.roles = roles;
	}
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "WECHAT_ROLE", joinColumns = { @JoinColumn(name = "wechat_user_id") }, inverseJoinColumns = { @JoinColumn(name = "wechat_role_id") })
	public Set<WeChat> getWechatRoles(){
		return wechatroles;
		
	}
	
	public void setWeChatRoles(Set<WeChat> wechatroles){
		this.wechatroles=wechatroles;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "MONEY")
	@Type(type = "java.lang.Boolean")
	@JsonSerialize(using = JsonBooleanForIntegerSerializer.class)
	public boolean isMoney() {
		return money;
	}

	public void setMoney(boolean money) {
		this.money = money;
	}

	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_group", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
	// @ManyToMany(targetEntity = SysRole.class, cascade = {
	// CascadeType.MERGE,CascadeType.PERSIST })
	// @JoinTable(name = "sys_user_group", joinColumns = { @JoinColumn(name =
	// "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
	public Set<SysGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<SysGroup> groups) {
		this.groups = groups;
	}

	public String getKunnr() {
		return kunnr;
	}

	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	/**
	 * 取当前用户的客户信息（售达方信息）
	 * 
	 * @return
	 */
	@Transient
	public CustHeader getCustHeader() {
		if (this.kunnr == null || this.kunnr.equals("")) {
			return null;
		} else {
			CustHeaderDao custHeaderDao = SpringContextHolder
					.getBean("custHeaderDao");
			List<CustHeader> findByCode = custHeaderDao.findByCode(this.kunnr);
			if (findByCode == null || findByCode.size() == 0) {
				return null;
			} else {
				return findByCode.get(0);
			}
		}
	}

	public void setCustHeader(CustHeader custHeader) {
		this.custHeader = custHeader;
	}

	@Override
	public String toString() {
		return "SysUser [loginNo=" + loginNo + ", userName=" + userName
				+ ", id=" + id + ", password=" + password + ", userType="
				+ userType + ", userGroup=" + userGroup + ", groups=" + groups
				+ ", sex=" + sex + ", userNo=" + userNo + ", email=" + email
				+ ", kunnr=" + kunnr + "]";
	}
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sysUser")
	public SysInnerUser getSysInnerUser() {
		return sysInnerUser;
	}

	public void setSysInnerUser(SysInnerUser sysInnerUser) {
		this.sysInnerUser = sysInnerUser;
	}

	public void setWechatRoles(Set<WeChat> wechatroles) {

				this.wechatroles=wechatroles;
	}
	public void setInterJuri(String interJuri) {
		this.interJuri = interJuri;
	}
	@Column(name = "INTER_JURI", length = 5)
	public String getInterJuri() {
		return interJuri;
	}

	public void setSoftdogId(String softdogId) {
		this.softdogId = softdogId;
	}
	@Column(name = "SOFTDOG_ID", length = 30)
	public String getSoftdogId() {
		return softdogId;
	}
}
