package com.mw.framework.manager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SerialNumberDao;
import com.mw.framework.domain.SerialNumber;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ZConstants;
import com.mw.framework.utils.ZStringUtils;


@Service("serialNumberManager")
@Transactional
public class SerialNumberManagerImpl extends CommonManagerImpl implements SerialNumberManager {
	private static int jz= 36;
	@Autowired
	private SerialNumberDao serialNumberDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumber(java.lang.String)
	 */
	public String curSerialNumber(String category) {
		return curSerialNumber(category, 1, ZConstants.MAX_INT, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumberFull(java.lang.String,
	 *      int)
	 */
	public String curSerialNumberFull(String category, int length) {
		return curSerialNumber(category, 1, ZConstants.MAX_INT, length);
	}
	
	public String curSerialNumberFullYY(String category, int length) {
		String dateYY = DateTools.getDateYY();
		return dateYY + curSerialNumber(category, 1, ZConstants.MAX_INT, length);
	}
	public String curSerialNumberFullYY(String category, int length, int bg) {
		String dateYY = DateTools.getDateYY();
		return dateYY + curSerialNumber(category, 1, ZConstants.MAX_INT, length,1);
	}
	public String curSerialNumberFullYYMM(String category, int length) {
		String dateYYMM = DateTools.getDateYYMM();
		return dateYYMM + curSerialNumber(category, 1, ZConstants.MAX_INT, length);
	}

	public String curSerialNumberFullYYMMDD(String category, int length) {
		String dateYYMMDD = DateTools.getDateYYMMDD();
		return dateYYMMDD + curSerialNumber(category, 1, ZConstants.MAX_INT, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumber(java.lang.String,
	 *      int)
	 */
	public String curSerialNumber(String category, int maxMum) {
		return curSerialNumber(category, 1, maxMum, 0);
	}

	public static void main(String[] args) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumber(java.lang.String,
	 *      int, int, int)
	 */
	public String curSerialNumber(String category, int beginNum, int maxMum, int length) {
		return curSerialNumberJz(category, beginNum, maxMum, length, 10);
	}
	
	public String curSerialNumber(String category, int beginNum, int maxMum, int length,int bg) {
		return curSerialNumberbg(category, beginNum, maxMum, length, 10);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumber(java.lang.String,
	 *      int, int, int)
	 */
	public String curSerialNumberZm(String category, int beginNum, int maxMum, int length) {
		return curSerialNumberJz(category, beginNum, maxMum, length, jz);
	}
	 
	 
	private static synchronized String curSerialNumberbg(String category, int beginNum, int maxMum, int length,int bg) {
		int lastBgNumber;
//		String hql = "select  s from SerialNumber s where s.category = '" + category + "'";
		SerialNumberDao serialNumberDao = SpringContextHolder.getBean("serialNumberDao");
		List list = serialNumberDao.findByCategory(category);
		SerialNumber serialNumber = null;
		if (list != null && list.size() > 0) {
			serialNumber = (SerialNumber) list.get(0);
		}
		if (serialNumber == null) {
			SerialNumber sm = new SerialNumber();
			sm.setCategory(category);
			sm.setLastBgNumber(beginNum);
			sm.setInitialNumber(beginNum);
			sm.setMaxNumber(maxMum);
			serialNumberDao.save(sm);
			lastBgNumber = beginNum;
		} else {
			if(serialNumber.getLastBgNumber()==null) {
				serialNumber.setLastBgNumber(0);
			}
			if (serialNumber.getLastNumber().intValue() == serialNumber.getMaxNumber().intValue()) {
				/*
				 * //需邮件通知到管理员 SimpleMailMessage msg = new SimpleMailMessage();
				 * msg.setFrom("jamesluo@sproconsulting.com.cn");
				 * msg.setTo("luoliuqiang@sina.com"); msg.setSubject("序列号已满通知");
				 * msg.setText(category + "为前缀的流水号已经满了。到了最大的号码:" +
				 * serialNumber.getLastNumber());
				 * simpleMailService.sendSerialNumberInfo(msg);
				 */
				return null;
			}
			lastBgNumber = serialNumber.getLastBgNumber() + 1;
			serialNumber.setLastBgNumber(lastBgNumber);
			serialNumberDao.save(serialNumber);

		}
		serialNumberDao.flush();
		if (length == 0) {
			return Integer.toString(lastBgNumber, bg).toUpperCase();
		} else {
			return ZStringUtils.ZeroPer(Integer.toString(lastBgNumber, bg).toUpperCase(), length);

		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spro.core.service.impl.SerialNumberManager#curSerialNumber(java.lang.String,
	 *      int, int, int)
	 */

	private static synchronized String curSerialNumberJz(String category, int beginNum, int maxMum, int length, int jz) {
		int lastNumber;
//		String hql = "select  s from SerialNumber s where s.category = '" + category + "'";
		SerialNumberDao serialNumberDao = SpringContextHolder.getBean("serialNumberDao");
		List list = serialNumberDao.findByCategory(category);
		SerialNumber serialNumber = null;
		if (list != null && list.size() > 0) {
			serialNumber = (SerialNumber) list.get(0);
		}
		if (serialNumber == null) {
			SerialNumber sm = new SerialNumber();
			sm.setCategory(category);
			sm.setLastNumber(beginNum);
			sm.setInitialNumber(beginNum);
			sm.setMaxNumber(maxMum);
			serialNumberDao.save(sm);
			lastNumber = beginNum;
		} else {
			if(serialNumber.getLastNumber()==null) {
				serialNumber.setLastNumber(0);
			}
			if (serialNumber.getLastNumber().intValue() == serialNumber.getMaxNumber().intValue()) {
				/*
				 * //需邮件通知到管理员 SimpleMailMessage msg = new SimpleMailMessage();
				 * msg.setFrom("jamesluo@sproconsulting.com.cn");
				 * msg.setTo("luoliuqiang@sina.com"); msg.setSubject("序列号已满通知");
				 * msg.setText(category + "为前缀的流水号已经满了。到了最大的号码:" +
				 * serialNumber.getLastNumber());
				 * simpleMailService.sendSerialNumberInfo(msg);
				 */
				return null;
			}
			
			lastNumber = serialNumber.getLastNumber() + 1;
			serialNumber.setLastNumber(lastNumber);
			serialNumberDao.save(serialNumber);

		}
		serialNumberDao.flush();
		if (length == 0) {
			return Integer.toString(lastNumber, jz).toUpperCase();
		} else {
			return ZStringUtils.ZeroPer(Integer.toString(lastNumber, jz).toUpperCase(), length);

		}

	}

	public static String sequence(String s, int len, String s1, int lastnum) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int i = 0; i < 34; i++) {

			if (i > 9) {
				map.put(i, String.valueOf((char) (65 + i - 10)));
			} else {
				map.put(i, i + "");
			}
		}

		if (String.valueOf(lastnum).length() > len) {
			int l = 10;
			for (int i1 = 1; i1 < len - 1; i1++)
				l *= 10;

			int j1 = lastnum - l;
			char c = (char) ((65 + j1));
			String a = String.valueOf(c);
			return a;
		} else {
			return String.valueOf(lastnum);
		}
	}
}
