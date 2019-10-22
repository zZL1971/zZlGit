package com.mw.framework.utils;


public class StringUtil extends ZStringUtils {

	public static String arrayToString(String[] ids)
	{
		StringBuffer stringBuffer=new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			stringBuffer.append("'").append(ids[i]).append("'");
			if(i<ids.length-1)
			{
				stringBuffer.append(",");
			}
		}
		return stringBuffer.toString();
	}

}
