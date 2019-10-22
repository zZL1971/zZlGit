package com.main.factory;

public class HoleProudctFactory {
	public static HoleProduct factory =null;
	public  HoleProduct getHoleProduct(){
		
		return new HoleFactoryImpl();
	}
}
