package com.main.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MakeOrderNumUtil {
	public static  String makeOrderNum(){  

		 String numStr = "" ;       

		 String trandStr = String.valueOf((Math.random() * 9 + 1) * 1000000);     

		 String dataStr = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date());       

		 numStr = trandStr.toString().substring(0, 4);      

		 numStr = numStr + dataStr ;       

		 return numStr ;   

		}

	  public static String getUUID32(){

		  return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() ;

	  }

	  public static String makeOrderNum(Boolean flg){
		  if(flg){
			  return String.valueOf(new Date().getTime());
		  }
		  return null;
	  }
}
