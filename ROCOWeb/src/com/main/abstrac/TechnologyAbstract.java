package com.main.abstrac;

import java.util.Map;

import com.main.factory.ProductFactory;

public abstract class TechnologyAbstract {
	/**
	 * 
	* @Title: calculateProcessRoute 
	* @Description: TODO(计算工艺路线) 
	* @param @param 
	* @param @return    设定文件 
	* @return String    返回一个JSONArray
	* @throws
	 */
	public abstract Map<String, String> calculateProcessRoute(ProductFactory pf);
}
