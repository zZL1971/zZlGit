package com.mw.framework.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.utils.ZStringUtils;

@Entity
@Table(name = "SYS_MES_SEND")
public class SysMesSend extends UUIDEntity implements Serializable {
	// 默认构造方法
	public SysMesSend() {

	}

	/**
	 * 发送人
	 */
	private String sendUser;
	/**
	 * 接受人
	 */
	private String receiveUser;
	/**
	 * 抄送人
	 */
	private String csUser;
	/**
	 * 是否已经发送 x发送 空未发送
	 */
	private String sendFlag;
	/**
	 * 消息类型暂时只有1邮件 2短信，
	 */
	private String msgType;
	/**
	 * 消息头
	 */
	private String msgTitle;
	/**
	 * 消息体
	 */
	private String msgBody;
	/**
	 * 发送时间
	 */
	private Date sendTime;
	private String receiveUserEmail;
	private String sendUserEmail;
	private String sendUserEmailPsw;

	/**
	 * 是否已读的，0时，表示未读，但设置为1时，表示已读
	 */
	private String hasRead;

	/**
	 * 发送人
	 */
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	/**
	 * 发送人
	 */
	@Column(name = "SEND_USER")
	public String getSendUser() {

		if (sendUser != null && sendUser.endsWith(","))
			return sendUser.substring(0, sendUser.length() - 1);
		return sendUser;
	}

	/**
	 * 接受人
	 */
	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}

	/**
	 * 接受人
	 */
	@Column(name = "RECEIVE_USER", length = 4000)
	public String getReceiveUser() {
		return receiveUser;
	}

	/**
	 * 抄送人
	 */
	public void setCsUser(String csUser) {
		this.csUser = csUser;
	}

	/**
	 * 抄送人
	 */
	@Column(name = "CS_USER", length = 4000)
	public String getCsUser() {
		return csUser;
	}

	/**
	 * 是否已经发送 x发送 空未发送
	 */
	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}

	/**
	 * 是否已经发送 x发送 空未发送
	 */
	@Column(name = "SEND_FLAG", length = 2)
	public String getSendFlag() {
		return sendFlag;
	}

	/**
	 * 消息类型暂时只有1邮件 2短信，
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	/**
	 * 消息类型暂时只有1邮件 2短信，
	 */
	@Column(name = "MSG_TYPE", length = 2)
	public String getMsgType() {
		return msgType;
	}

	/**
	 * 消息头
	 */
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	/**
	 * 消息头
	 */
	@Column(name = "MSG_TITLE", length = 1000)
	public String getMsgTitle() {
		return msgTitle;
	}

	/**
	 * 消息体
	 */
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}

	/**
	 * 消息体
	 */
	@Column(name = "MSG_BODY", length = 4000)
	public String getMsgBody() {
		return msgBody;
	}

	public void appendReceiveUser(String receiveUser) {
		this.receiveUser = this.receiveUser + receiveUser + ";";
	}

	@Column(name = "SEND_TIME")
	public Date getSendTime() {
		if (ZStringUtils.isEmpty(sendTime))
			return this.createTime;
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Column(name = "RECEIVE_USER_EMAIL", length = 4000)
	public String getReceiveUserEmail() {
		return receiveUserEmail;
	}

	public void setReceiveUserEmail(String receiveUserEmail) {
		this.receiveUserEmail = receiveUserEmail;
	}

	@Column(name = "SEND_USER_EMAIL", length = 100)
	public String getSendUserEmail() {
		return sendUserEmail;
	}

	public void setSendUserEmail(String sendUserEmail) {
		this.sendUserEmail = sendUserEmail;
	}

	@Column(name = "SEND_USER_EMAIL_PSW", length = 100)
	public String getSendUserEmailPsw() {
		return sendUserEmailPsw;
	}

	public void setSendUserEmailPsw(String sendUserEmailPsw) {
		this.sendUserEmailPsw = sendUserEmailPsw;
	}

	@Column(name = "HAS_READED", length = 1)
	public String getHasRead() {
		return hasRead;
	}

	public void setHasRead(String hasRead) {
		this.hasRead = hasRead;
	}

}