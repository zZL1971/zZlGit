/*
 * herich.money.util.FieldNameFunction.java
 * created date: 2005-8-24
 * 数据库表字段和实体bean属性的相关方法
 */
package com.mw.framework.utils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author james
 */
public class FieldFunction {
	/**
	 * 数据库表字段名称转化为实体bean字段名称。 采用匈牙利命名法：首单词小写，其他单词首字母大写
	 * 
	 * @param field
	 * @return String
	 */
	public static String dbField2BeanField(String field) {
		String bf = beanAndDbFileMap.get(field);
		if (bf != null) {
			return bf;
		}
		bf = "";
		if (!field.contains("_")) {
			bf = field.toLowerCase();
			beanAndDbFileMap.put(field, bf);
			return bf;
		}

		field = field.toLowerCase();
		String[] split = field.split("_");

		// 首单词小写
		bf += split[0];
		for (int i = 1; i < split.length; i++) {
			// 首字母大写，其余小写
			bf += bigStart(split[i]);
		}
		beanAndDbFileMap.put(field, bf);
		return bf;
	}

	/**
	* 将首字母小写
	* 
	* @param src
	*            String
	* @return String
	*/
	public static String lowStart(String src) {
		StringBuffer strResult = new StringBuffer();
		// 首字母大写
		strResult.append(src.substring(0, 1).toLowerCase());
		strResult.append(src.substring(1));
		return strResult.toString();
	}

	/**
	 * 将tableName 转成类名，这里的表明一定要是模块名+"_"+表名。给出的类名是除了模块名的,
	 * 如果没有"_",直接将整个表名按匈牙利命名法命名
	 * 
	 * @param TableName
	 * @return
	 */
	public static String dbTable2ClassName(String tableName) {
		// String noFunTableName = tableName.substring(tableName.indexOf("_") +
		// 1,
		// tableName.length());
		return FieldFunction.bigStart(dbField2BeanField(tableName));
	}

	/**
	 * 数据库字段跟bean字段的映射或bean字段跟数据库字段的映射
	 */
	public static Map<String, String> beanAndDbFileMap = new HashMap();

	/**
	 * 实体bean字段名称转化为数据库表字段名称。 采用驼峰命名法：单词全部大写，单词与单词之间用‘_‘连接
	 * 
	 * @param field
	 * @return String
	 */
	public static String beanField2DbField(String field) {
		String df = beanAndDbFileMap.get(field);
		if (df != null) {
			return df;
		}
		if (field.toLowerCase().equals(field)) {
			df = field.toUpperCase();
			beanAndDbFileMap.put(field, df);
			return df;
		}
		df = "";
		char[] charArr = field.toCharArray();
		for (int i = 0; i < charArr.length; i++) {
			if (charArr[i] >= 'A' && 'Z' >= charArr[i]) {
				df += "_" + charArr[i];
			} else {
				df += charArr[i];
			}
		}
		charArr = null;
		df = df.toUpperCase();
		beanAndDbFileMap.put(field, df);
		return df;
	}

	/**
	 * 数据库表字段名称转化为实体bean字段名称。 采用匈牙利命名法：首单词小写，其他单词首字母大写
	 * 
	 * @param field
	 * @return String
	 */
	public static String dbField2BeanFieldo(String field) {
		StringBuffer sb = new StringBuffer();

		field = field.toLowerCase();
		String[] split = field.split("_");

		// 首单词小写
		sb.append(split[0]);
		for (int i = 1; i < split.length; i++) {
			// 首字母大写，其余小写
			sb.append(bigStart(split[i]));
		}

		return sb.toString();
	}

	public static String beanField2DbFieldo(String field) {
		if (field == null)
			return field;
		StringBuffer sb = new StringBuffer();
		char[] charArr = field.toCharArray();
		for (int i = 0; i < charArr.length; i++) {
			if (charArr[i] >= 'A' && 'Z' >= charArr[i]) {
				sb.append("_").append(charArr[i]);
			} else {
				sb.append(charArr[i]);
			}
		}
		charArr = null;
		return sb.toString().toUpperCase();
	}

	public static void main(String[] args) {

		String fileName = "abcDDSFdDFD_sdf";
		String fileName1 = "sproDd_fsdaf_d";
		String string2 = FieldFunction.beanField2DbFieldo(fileName);
		String string3 = FieldFunction.dbField2BeanField(fileName1);
		String string4 = FieldFunction.dbField2BeanFieldo(fileName1);
		//		String string = FieldFunction.dbField2BeanField(fileName);
		//		System.out.println( string.equals(string2)+string+"  : "+string2);
		System.out.println(string3.equals(string4) + "  " + string3 + "  : " + string4);
	}

	/**
	 * 取得设置属性值的方法
	 * 
	 * @param property
	 *            String
	 * @return String
	 */
	public static String getSetMethodName(String property) {
		StringBuffer sb = new StringBuffer(property.length() + 3);
		sb.append("set");
		sb.append(bigStart(property));
		return sb.toString();
	}

	public static List getDeclaredFields(Class beanClass) {
		if ("java.lang.Object".equals(beanClass.getName())) {
			return null;
		}
		List list = new ArrayList();
		Field[] colFields = beanClass.getDeclaredFields();
		for (int i = 0; i < colFields.length; i++) {
			list.add(colFields[i]);
		}
		List declaredFields = getDeclaredFields(beanClass.getSuperclass());
		if (declaredFields != null && declaredFields.size() > 0) {
			list.addAll(declaredFields);
		}
		return list;
	}

	/**
	 * 取得字段，查找所有超类，直到Object
	 */
	public static Field getDeclaredField(Class beanClass, String fname) {
		if ("java.lang.Object".equals(beanClass.getName())) {
			//			System.out.println("no column colName,使用自定义或别名字段赋值:" + fname + " in " + beanClass);
			return null;
		}
		Field[] colFields = beanClass.getDeclaredFields();
		for (int i = 0; i < colFields.length; i++) {
			if (colFields[i].getName().equals(fname)) {
				return colFields[i];
			}
		}
		return getDeclaredField(beanClass.getSuperclass(), fname);
	}

	public static Object getFieldValue(Object obj, String fname) {
		Field field = getDeclaredField(obj.getClass(), fname);
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将首字母大写
	 * 
	 * @param src
	 *            String
	 * @return String
	 */
	public static String bigStart(String src) {
		StringBuffer strResult = new StringBuffer();
		// 首字母大写
		strResult.append(src.substring(0, 1).toUpperCase());
		strResult.append(src.substring(1));
		return strResult.toString();
	}

	public static boolean isNormalType(Class cls) {
		if (String.class == cls) {
			return true;
		}
		if (int.class == cls || Integer.class == cls) {
			return true;
		}
		if (Long.class == cls || long.class == cls) {
			return true;
		}
		if (Short.class == cls || short.class == cls) {
			return true;
		}
		if (double.class == cls || Double.class == cls) {
			return true;
		}
		if (float.class == cls || Float.class == cls) {
			return true;
		}
		if (Class.class == cls) {
			return true;
		}
		if (char.class == cls || Character.class == cls) {
			return true;
		} else if (byte.class == cls || Byte.class == cls) {
			return true;
		}
		if (StringBuffer.class == cls) {
			return true;
		}
		if (Boolean.class == cls) {
			return true;
		}
		if (boolean.class == cls) {
			return true;
		}
		if (Date.class == cls) {
			return true;
		}
		if (cls == java.sql.Date.class) {
			return true;
		}
		if (cls == Timestamp.class) {
			return true;
		}
		return false;
	}

	/**
	 * 判定对象是否为一般类型，如果为一般类型返回字符串<br>
	 * 注意调用前检测obj是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public static String isNormalType2Str(Object obj) {
		if (obj == null) {
			//			System.out.println("isNormalType(obj)要判断的对象obj为null，不能使用该方法");
			return null;
		}
		Class cls = obj.getClass();
		if (int.class == cls || Integer.class == cls) {
			return obj.toString();
		} else if (String.class == cls) {
			return obj.toString();
		} else if (double.class == cls || Double.class == cls) {
			return obj.toString();
		} else if (Long.class == cls || long.class == cls) {
			return obj.toString();
		} else if (Short.class == cls || short.class == cls) {
			return obj.toString();
		} else if (float.class == cls || Float.class == cls) {
			return obj.toString();
		} else if (char.class == cls || Character.class == cls) {
			return obj.toString();
		} else if (byte.class == cls || Byte.class == cls) {
			return obj.toString();
		} else if (Class.class == cls) {
			return ((Class) obj).getName();
		} else if (Boolean.class == cls || boolean.class == cls) {
			return obj.toString();
		} else if (StringBuffer.class == cls) {
			return obj.toString();
		} else if (Date.class == cls) {
			return String.valueOf(((Date) obj).getTime());
		} else if (cls == java.sql.Date.class) {
			return String.valueOf(((java.sql.Date) obj).getTime());
		} else if (cls == Timestamp.class) {
			return String.valueOf(((Timestamp) obj).getTime());
		}
		return null;
	}

	/**
	 * 拷贝新对象值给原对象
	 * 
	 * @param old
	 * @param newObj
	 */
	public static void copyValue(Object old, Object newObj) {
		if (old.getClass() != newObj.getClass()) {
			//			System.out.println("类型不一致,不能拷贝新值给原对象!");
		}
		List fields = FieldFunction.getDeclaredFields(newObj.getClass());
		for (Iterator iter = fields.iterator(); iter.hasNext();) {
			Field element = (Field) iter.next();
			try {
				element.setAccessible(true);
				//如果存在旧对象中的有值，但是新对象中为null的，不进行交替  --add by Mark on 2017-09-01
				if(element.get(newObj)==null && element.get(old)!=null){
					continue;
				}
				if (newObj == null) {
					element.set(old, null);
				} else {
					element.set(old, element.get(newObj));
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
				//				System.out.println(element.getName() + e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * 拷贝新对象值给原对象
	 * @param old 原对象
	 * @param newObj 新对象
	 * @param fieldNameSet 被排除的 属性名称
	 */
	public static void copyValue(Object old, Object newObj,Set<String> fieldNameSet) {
		if (old.getClass() != newObj.getClass()) {
			//			System.out.println("类型不一致,不能拷贝新值给原对象!");
		}
		List fields = FieldFunction.getDeclaredFields(newObj.getClass());
		for (Iterator iter = fields.iterator(); iter.hasNext();) {
			Field element = (Field) iter.next();
			String name = element.getName();
			if(fieldNameSet.contains(name)){
				continue;
			}
			try {
				element.setAccessible(true);
				if (newObj == null) {
					element.set(old, null);
				} else {
					element.set(old, element.get(newObj));
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
				//				System.out.println(element.getName() + e.getLocalizedMessage());
			}
		}
	}
	/**
	 * 拷贝新对象值给原对象
	 * @param old 原对象
	 * @param newObj 新对象
	 * @param fieldNameSet 包含的 属性名称
	 */
	public static void copyValueIn(Object old, Object newObj,Set<String> fieldNameSet) {
		if (old.getClass() != newObj.getClass()) {
			//			System.out.println("类型不一致,不能拷贝新值给原对象!");
		}
		List fields = FieldFunction.getDeclaredFields(newObj.getClass());
		for (Iterator iter = fields.iterator(); iter.hasNext();) {
			Field element = (Field) iter.next();
			String name = element.getName();
			if(!fieldNameSet.contains(name)){
				continue;
			}
			try {
				element.setAccessible(true);
				if (newObj == null) {
					element.set(old, null);
				} else {
					element.set(old, element.get(newObj));
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
				//				System.out.println(element.getName() + e.getLocalizedMessage());
			}
		}
	}

	public static boolean isNormalType(String cls) {
		if (String.class.getName().equals(cls)) {
			return true;
		}
		if (int.class.getName().equals(cls) || Integer.class.getName().equals(cls)) {
			return true;
		}
		if (double.class.getName().equals(cls) || Double.class.getName().equals(cls)) {
			return true;
		}
		if (Boolean.class.getName().equals(cls) || boolean.class.getName().equals(cls)) {
			return true;
		}
		if (Long.class.getName().equals(cls) || long.class.getName().equals(cls)) {
			return true;
		}
		if (Short.class.getName().equals(cls) || short.class.getName().equals(cls)) {
			return true;
		}
		if (Class.class.getName().equals(cls)) {
			return true;
		}
		if (StringBuffer.class.getName().equals(cls)) {
			return true;
		}
		if (Date.class.getName().equals(cls)) {
			return true;
		}
		if (cls == java.sql.Date.class.getName()) {
			return true;
		}
		if (cls == Timestamp.class.getName()) {
			return true;
		}
		return true;
	}
}