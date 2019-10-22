/**
 *
 */
package com.mw.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
 
import org.springframework.util.Assert;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.utils.BeanUtils.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-1-9
 *
 */
public class BeanUtils {
	
	public static void getFieldsForSupperClass(Object object,Class<?> clazz,Map<String,String[]> map){
		Class<?> superclass = clazz.getSuperclass();
		
		Field fields[] = clazz.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			
			if(!fieldName.equals("serialVersionUID")){
				Object value = getValue(object, fieldName);
				
				System.out.println(fieldName+"_"+field.getType().getName());
				if(value!=null){
					if(value.getClass().getName().equals("java.util.HashSet")){
						if(!((Set<?>)value).isEmpty()){
							map.put(fieldName, new String[]{String.valueOf(value)});
						}
					}else if(value.getClass().getName().equals("java.util.ArrayList")){
						if(!((List<?>)value).isEmpty()){
							map.put(fieldName, new String[]{String.valueOf(value)});
						}
					}else if(value.getClass().getName().equals("java.util.LinkedHashMap")){
						if(!((Map<?, ?>)value).isEmpty()){
							map.put(fieldName, new String[]{String.valueOf(value)});
						}
					}else{
						String fieldTName = field.getType().getName();
						if(Pattern.matches("^(int|float|double)$", fieldTName))
							fieldName = "INEQ"+fieldName;
						else if(Pattern.matches("^(java.util.Date)$", fieldTName))
							fieldName = "IDEQ"+fieldName;
						else
							fieldName = "ICEQ"+fieldName;
						map.put(fieldName, new String[]{String.valueOf(value)});
					}
					
				}
			}
		}
		
		if(superclass!=null){
			System.out.println("再次递归");
			getFieldsForSupperClass(object,superclass, map);
		}
	}
	
	public static Map<String, String[]> getValues(Object object){
		Map<String, String[]> map = new HashMap<String, String[]>();
		getFieldsForSupperClass(object,object.getClass(), map);
		/*Class<?> classType = object.getClass();
		Class<?> superclass = classType.getSuperclass();
		if(superclass!=null){
			
			Class<?> superclazz = superclass.getSuperclass();
			if(superclazz!=null){
				Field superfields[] = superclazz.getDeclaredFields();
				for (Field field : superfields) {
					String fieldName = field.getName();
					Object value = getValue(object, fieldName);
					
					System.out.println(fieldName+"_"+field.getType().getSimpleName());
					if(value!=null){
						map.put(fieldName, new String[]{String.valueOf(value)});
					}
				}
			}
			
			Field superfields[] = superclass.getDeclaredFields();
			for (Field field : superfields) {
				String fieldName = field.getName();
				Object value = getValue(object, fieldName);
				
				System.out.println(fieldName+"_"+field.getType().getSimpleName());
				if(value!=null){
					map.put(fieldName, new String[]{String.valueOf(value)});
				}
			}
		}
		
		Field fields[] = classType.getDeclaredFields();
		
		for (Field field : fields) {
			String fieldName = field.getName();
			Object value = getValue(object, fieldName);
			
			System.out.println(fieldName+"_"+field.getType().getSimpleName());
			if(value!=null){
				map.put(fieldName, new String[]{String.valueOf(value)});
			}
		}*/
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object obj,String fieldName) {
			try {
				for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
					try {
						Field field = superClass.getDeclaredField(fieldName);
						
						boolean accessible = field.isAccessible();
						field.setAccessible(true);
						Object object = field.get(obj);
						field.setAccessible(accessible);
						return (T)object;
					} catch (NoSuchFieldException e) {
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	public static Map<String,Object> getValues(Object obj,String...fieldNames){
		Map<String,Object> map = new HashMap<String, Object>();
		for (String fieldName : fieldNames) {
			Object value = getValue(obj, fieldName);
			map.put(fieldName, getValue(obj, fieldName));
		}
		return map;
	}
	
	public static Field getDeclaredField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
		Assert.notNull(clazz);
		Assert.hasText(propertyName);
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
	}
	
	public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException {
		Assert.notNull(object);
		Assert.hasText(propertyName);
		return getDeclaredField(object.getClass(), propertyName);
	}
	
	public static void setValue(Object object, String propertyName, Object newValue) throws NoSuchFieldException{
		Field field = getDeclaredField(object, propertyName);
		Class<?> type = field.getType();
		Object o = TypeCaseHelper.convert(newValue, type.getName(), null);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, o);
		} catch (IllegalAccessException e) {
			// logger.info("Error won't happen");
		}
		field.setAccessible(accessible);
	}
	
	public static void copyProperties(Object obj, Object target,boolean bool) {

		try {
			Class<?> classType = obj.getClass();
			// Class<?> classTarget;

			/*
			 * if(target==null){
			 * classTarget=Class.forName(target.getClass().getName()); }
			 */
			// Object objectCopy = target.getClass().getConstructor(new
			// Class[]{}).newInstance(new Object[]{});
			Field fields[] = classType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				
				if(!field.getType().getName().equals("java.util.HashSet")&&
						!field.getType().getName().equals("java.util.ArrayList") &&
						!field.getType().getName().equals("java.util.Set") &&
						!field.getType().getName().equals("java.util.LinkedHashMap") && !fieldName.equals("serialVersionUID") && !bool){
					
					String firstLetter = fieldName.substring(0, 1).toUpperCase();
					// 获得和属性对应的getXXX()方法的名字
					String getMethodName = (field.getType().getName().equals("boolean")||field.getType().getName().equals("java.lang.Boolean")?"is":"get") + firstLetter
							+ fieldName.substring(1);
					// 获得和属性对应的setXXX()方法的名字
					String setMethodName = "set" + firstLetter
							+ fieldName.substring(1);
					// 获得和属性对应的getXXX()方法
					Method getMethod = classType.getMethod(getMethodName,
							new Class[] {});
					// 获得和属性对应的setXXX()方法
					Method setMethod;
					try {
						setMethod = target.getClass().getMethod(setMethodName,
								new Class[] { field.getType() });

						// 调用原对象的getXXX()方法
						Object value = getMethod.invoke(obj, new Object[] {});

						if (value != null && BeanUtils.isNotEmpty(value.toString())) {
							// 调用拷贝对象的setXXX()方法
							//System.out.println(classType.getSimpleName()+"-->"+target.getClass().getSimpleName()+"|"+fieldName+"|"+value);
							setMethod.invoke(target, new Object[] { value });
						}else{
							setMethod.invoke(target, new Object[] { null });
						}
					} catch (NoSuchMethodException e1) {
						// System.err.println(target.getClass().getSimpleName()+"对象中不存在"+setMethodName+"("+field.getType().getSimpleName()+")"+"方法，将跳过该值赋值！");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object obj, Class<?> clazz) {

		try {
			Class<?> classType = obj.getClass();
			Object objectCopy = clazz.getConstructor(new Class[] {})
					.newInstance(new Object[] {});
			Field fields[] = classType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				// 获得和属性对应的getXXX()方法的名字
				String getMethodName = "get" + firstLetter
						+ fieldName.substring(1);
				// 获得和属性对应的setXXX()方法的名字
				String setMethodName = "set" + firstLetter
						+ fieldName.substring(1);
				// 获得和属性对应的getXXX()方法
				Method getMethod = classType.getMethod(getMethodName,
						new Class[] {});
				// 获得和属性对应的setXXX()方法
				Method setMethod = null;
				try {
					setMethod = clazz.getMethod(setMethodName,
							new Class[] { field.getType() });

					// 调用原对象的getXXX()方法
					Object value = getMethod.invoke(obj, new Object[] {});
					System.out.println(objectCopy.getClass().getSimpleName()
							+ "." + setMethodName + "=" + value);
					if (value != null && BeanUtils.isNotEmpty(value.toString())) {
						// 调用拷贝对象的setXXX()方法
						setMethod.invoke(objectCopy, new Object[] { value });
					}

				} catch (Exception e) {
					// e.printStackTrace();
				}

			}
			return (T) objectCopy;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <strong><font color=blue>不为空 version: 1.0(2013-1-14)</font></strong><br/>
	 * <font color=green>object is null return false</font><br/>
	 * <font color=orange>e.g. </font><br/>
	 * 
	 * @author AiBo
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		if (obj != null) {
			switch (Type.valueOf(obj.getClass().getSimpleName())) {
			case String:
				return isNotEmpty(obj.toString());
			case Date:
				return true;
			case Long:
				return true;
			case Double:
				return true;
			case Integer:
				return true;
			case Float:
				return true;
			case BigDecimal:
				return true;
			case Timestamp:
				return true;
			default:
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * <strong><font color=blue>是否为空 version: 1.0(2013-1-14)</font></strong><br/>
	 * <font color=green>e.g. </font><br/>
	 * <font color=orange>e.g. </font><br/>
	 * 
	 * @author AiBo
	 * @param parameter
	 * @return
	 */
	public static boolean isNotEmpty(String parameter) {
		if (parameter != null && !parameter.isEmpty() && !"　".equals(parameter)
				&& parameter.replace("　", "").trim().length() > 0
				&& !"undefined".equals(parameter)) {
			return true;
		}
		return false;
	}
	
	public enum Type {
		String, Date, Long, Double, Integer, Float, BigDecimal,Timestamp;
	}
	
	public static Object getValueForType(Object obj,String valType){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(valType!=null)
		switch (Type.valueOf(valType)) {
		case Integer:
			return Integer.valueOf(String.valueOf(obj));
		case String:
			return String.valueOf(obj);
		case Date:
			try {
				sdf.parse(String.valueOf(obj));
			} catch (ParseException e) {
				//e.printStackTrace();
				System.out.println("object to date error!");
			}
			break;
		case Long:
			
			break;
		case Double:
			
			break;
		case Float:
			
			break;
		case BigDecimal:
			
			break;
		case Timestamp:
			
			break;

		default:
			break;
		}
		return obj;
	}
	
	/**
	 * 将Map转成对象
	 * @param map<DB_NAME,VALUE> key是数据库字段，value是值
	 * @param obj
	 */
	public static void tranMapToObj(Map map, Object obj) {
		Field[] declaredFields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < declaredFields.length; i++) {
			Field colField = declaredFields[i];
			String name = colField.getName();
			String dbName = FieldFunction.beanField2DbField(name);
			Object colValue = map.get(dbName);
			if (colValue == null)
				continue;
			Object obj2 = null;
			if (colField.getType() == Timestamp.class || colField.getType() == java.sql.Date.class
					|| colField.getType() == java.util.Date.class) {
				obj2 = DateTools.strToDate(colValue.toString(), DateTools.defaultFormat);
			} else if (colField.getType() == Double.class || colField.getType() == double.class) {
				obj2 = new Double(colValue.toString());
			} else if (colField.getType() == Integer.class || colField.getType().toString().equals("int")) {
				obj2 = new Integer(colValue.toString());
			} else if (colField.getType() == Long.class || colField.getType().toString().equals("long")) {
				obj2 = new Long(colValue.toString());
			} else {
				obj2 = colValue;
			}
			try {
				BeanUtils.forceSetProperty(obj, name, obj2);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 暴力设置对象变量值,忽略private,protected修饰符的限制.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static void forceSetProperty(Object object, String propertyName, Object newValue)
			throws NoSuchFieldException {
		Assert.notNull(object);
		Assert.hasText(propertyName);

		Field field = getDeclaredField(object, propertyName);
		Class type = field.getType();
		Object o = TypeCaseHelper.convert(newValue, type.getName(), null);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(object, o);
		} catch (IllegalAccessException e) {
			// logger.info("Error won't happen");
		}
		field.setAccessible(accessible);
	}
	
	public static void main(String[] args) {
		
		System.out.println(getValueForType("1",null));
	}
}
