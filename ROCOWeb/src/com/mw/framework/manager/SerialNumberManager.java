package com.mw.framework.manager;



public interface SerialNumberManager {

	/**
	 * 根据前缀去取流水号 有事务要求,默认最大数为999999999
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumber(String category);

	/**
	 * 根据前缀去取流水号,有前导0 有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumberFull(String category, int length);
	
	/**
	 * 根据前缀去取流水号,有前导并且有年有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumberFullYY(String category, int length);
	
	public abstract String curSerialNumberFullYY(String category, int length,int bg);

	/**
	 * 根据前缀去取流水号,有前导并且有年月 有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumberFullYYMM(String category, int length);

	/**
	 * 根据前缀去取流水号,有前导并且有年月日 有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumberFullYYMMDD(String category, int length);

	/**
	 * 根据前缀去取流水号 有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumber(String category, int maxMum);

	/**
	 * 根据前缀去取流水号 有事务要求
	 * @author luoliuqiang
	 */
	public abstract String curSerialNumber(String category, int beginNum, int maxMum, int length);
	
	/**
	 * 根据前缀去取流水号 有事务要求根据前缀去取流水号 有事务要求(补购订单流水号)
	 *  @author H
	 */
	public abstract String curSerialNumber(String category, int beginNum, int maxMum, int length,int bd);

	/**
	 * 根据前缀去取流水号 含有字母
	 * @author luoliuqiang
	 */
	public String curSerialNumberZm(String category, int beginNum, int maxMum, int length);

}