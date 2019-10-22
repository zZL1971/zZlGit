package com.mw.framework.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 系统加密解密方法。
 * 
 * @author james
 * 
 */
public class PwdUtil {
	private PwdUtil() {
	}

	private static final String CODE = "abcd_1234";

	public static String encrypt(String pwd) {
		StringBuffer sb = new StringBuffer();
		if (ZStringUtils.isEmpty(pwd)) {
			return pwd;
		}
		char[] cs = pwd.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			String hex = Integer.toHexString(cs[i] ^ getChar(i));
			hex = ZStringUtils.LPad(hex, 2, '0');
			sb.append(hex);
		}
		return sb.toString();
	}

	public static String dencrypt(String pwd) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isEmpty(pwd)) {
			return pwd;
		}
		char[] cs = pwd.toCharArray();
		try {
			for (int i = 0; i < cs.length; i++) {
				sb.append((char) Integer.parseInt(cs[i++] + "" + cs[i], 16));
			}
		} catch (NumberFormatException e) {
			System.out.println("密码异常");
			return "";
		}

		cs = sb.toString().toCharArray();
		sb = new StringBuffer();
		for (int i = 0; i < cs.length; i++) {
			sb.append((char) (cs[i] ^ getChar(i)));
		}
		return sb.toString();
	}

	public static char getChar(int index) {
		return CODE.charAt(index % CODE.length());
	}

	public static void main(String[] args) {
		//String string = PwdUtil.encrypt("oMrP70PHjKdSRu7auP81Jvls_cQ0oMrP70PHjKdSRu7auP81Jvls_cQ0");
		String string = PwdUtil.dencrypt("00");
		System.out.println(string);

		// SmtpManager smtpManager = SpringContextUtil.getBean("smtpManager");
		// List<String> annexList = new ArrayList<String>();
		// String SourceMail = smtpManager.getSourceMail();
		// String Smtp = smtpManager.getSmtp();
		// String Username = smtpManager.getUsername();
		// String Password = smtpManager.getPassword();
		// MessageBean mb = smtpManager.send("66816345@qq.com", "test",
		// "testluran",SourceMail ,Smtp ,Username ,Password, annexList);
		// if (mb.isFalse()) {
		// System.out.println("====发送邮件错误（店铺信息）====>" + mb.getMsg());
		// }
	}

}
