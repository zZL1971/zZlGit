/**
 *
 */
package com.mw.framework.support.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.itextpdf.text.log.SysoCounter;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.OrderBy;
import com.mw.framework.bean.Range;
import com.mw.framework.bean.impl.AssignedEntity;
import com.mw.framework.bean.impl.BaseEntity;
import com.mw.framework.bean.impl.UUID32Entity;
import com.mw.framework.bean.impl.UUIDEntity;
import com.mw.framework.domain.SysSerialNumber;
import com.mw.framework.support.dao.GenericRepository;
import com.mw.framework.util.SerialNumberUtils;
import com.mw.framework.util.StringHelper;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.support.dao.MyRepositoryImpl.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2014-4-15
 * 
 */
@NoRepositoryBean
public class GenericRepositoryImpl<T, ID extends Serializable> extends
		SimpleJpaRepository<T, ID> implements GenericRepository<T, ID> {

	private EntityManager entityManager;
	private JpaEntityInformation<T,?> entityInformation;

	/**
	 * 构造函数
	 * 
	 * @param domainClass
	 * @param em
	 */
	public GenericRepositoryImpl(
			final JpaEntityInformation<T, ?> entityInformation,
			EntityManager entityManager) {

		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		this.entityInformation =  entityInformation;
	}

	/**
	 * 构造函数
	 * 
	 * @param domainClass
	 * @param em
	 */
	public GenericRepositoryImpl(Class<T> domainClass, EntityManager em) {
		this(JpaEntityInformationSupport.getMetadata(domainClass, em), em);
	}
	
	public static void main(String[] args) {
		String str = "2014-08-01,2014-08-01";
		String[] split = str.split(",");
		for (String string : split) {
			System.out.println(string);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TypedQuery<T> queryCriteriaByRange(Map<String, String[]> parameterMap,OrderBy...orders){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityInformation.getJavaType());
        Root<T> rangeRoot = criteriaQuery.from(entityInformation.getJavaType());
        Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
        
        List<Predicate> restrictions = new ArrayList<Predicate>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();
			boolean result = Pattern.matches("^(I|E)(C|D|N)("+Range.Option.getValues()+")[A-Za-z0-9._]+$",key);
			if(result){
				String sign=key.substring(0,1),type=key.substring(1,2),option=key.substring(2, 4),field=key.substring(4);
				String[] entryValues = entry.getValue();
				
				Object value=null;
				Object high = null;
				Object[] values = null;
				boolean single = true;
				if(entryValues!=null){
					values = new Object[entryValues.length];
					for (int i=0;i<entryValues.length;i++) {
						switch (Range.Type.valueOf(type)) {
						case C:
							values[i] = entryValues[i];
							break;
						case D:
							try {
								String[] split = entryValues[i].split(",");
								if(split[0].length()==10){
									values[i] = sdf.parse(split[0]+" 00:00:00");
								}else if(split[0].length()==19){
									values[i] = sdf2.parse(split[0]);
								}
								
								if(split.length==2){
									if(split[1].length()==10){
										high = sdf.parse(split[1]+" 23:59:59");
									}else if(split[1].length()==19){
										high = sdf2.parse(split[1]);
									}
								}
								System.out.println("时间格式化后:"+values[i]);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							break;
						case N:
							values[i] = entryValues[i];
							break;
						default:
							break;
						}
					}
					single = values.length==1?true:false;
					value = values[0];
				}
				
				Path<String> path = null;
				
				if(field.contains("__")){
					String[] names = field.split("__");
					path = rangeRoot.get(names[0]);
					for (int i = 1; i < names.length; i++) {
						path = path.get(names[i]);
					}
				}else if(field.contains("_MW_WM_")){
					String[] names = field.split("_MW_WM_");
					path = rangeRoot.get(names[0]);
					/*for (int i = 1; i < names.length; i++) {
						path = path.get(names[i]);
					}*/
				}else{
					path = rangeRoot.get(field);
				}
				
				Predicate condition = null;
	        	switch (Range.Option.valueOf(option)) {
				case EQ:
					if(single){
						switch (Range.Sign.valueOf(sign)) {
						case I:
							condition = criteriaBuilder.equal(path, value);
							break;
						case E:
							condition = criteriaBuilder.notEqual(path, value);
							break;
						default:
							break;
						}
						
					}else{
						switch (Range.Sign.valueOf(sign)) {
							case I:
								StringBuffer sb = new StringBuffer();
								for(int i=0;i<values.length;i++){
									sb.append("'"+values[i]+"'");
									if(i<values.length-1){
										sb.append(",");
									}
								}
								condition = criteriaBuilder.in(path).value(sb.toString());
								break;
							case E:
								Predicate[] conditions =new Predicate[values.length];
								for (int i =0;i<values.length;i++) {
									conditions[i] = criteriaBuilder.notEqual(path, StringHelper.like(String.valueOf(values[0])));
								}
								condition = criteriaBuilder.and(conditions);
								break;
							default:
								break;
						}
					}
					break;
				case GE:
					condition = criteriaBuilder.greaterThanOrEqualTo(path, (Comparable) value);
					break;
				case LE:
					condition = criteriaBuilder.lessThanOrEqualTo(path, (Comparable) value);
					break;
				case GT:
					condition = criteriaBuilder.greaterThan(path,(Comparable)value);
					break;
				case LT:
					condition = criteriaBuilder.lessThan(path, (Comparable) value);
					break;
				case NE:
					condition = criteriaBuilder.notEqual(path,value);
					break;
				case BT:
					String[] splitValue = value.toString().split(",");
					switch (Range.Type.valueOf(type)) {
					case C:
						condition = criteriaBuilder.between(path, splitValue[0], splitValue[1]);
					case D:
						condition = criteriaBuilder.between(path, (Comparable)value, (Comparable)high);
						break;
					case N:
						condition = criteriaBuilder.between(path, splitValue[0], splitValue[1]);
						break;
					default:
						break;
					}
					
					break;
				case CP:
					if(single){
						switch (Range.Sign.valueOf(sign)) {
						case I:
							condition = criteriaBuilder.like(path,StringHelper.like(String.valueOf(value)));
							break;
						case E:
							condition = criteriaBuilder.notLike(path,StringHelper.like(String.valueOf(value)));
							break;
						default:
							break;
						}
					}else{
						Predicate[] conditions =new Predicate[values.length];
						switch (Range.Sign.valueOf(sign)) {
						case I:
							for (int i =0;i<values.length;i++) {
								conditions[i] = criteriaBuilder.like(path, StringHelper.like(String.valueOf(values[i])));
							}
							break;
						case E:
							for (int i =0;i<values.length;i++) {
								conditions[i] = criteriaBuilder.notLike(path, StringHelper.like(String.valueOf(values[i])));
							}
							break;
						default:
							break;
						}
						condition = criteriaBuilder.or(conditions);
					}
					break;
				case OP:
					if(single){
						String[] fields = field.split("_MW_WM_");
						Predicate[] conditions =new Predicate[fields.length];
						switch (Range.Sign.valueOf(sign)) {
						case I:
							conditions[0] =criteriaBuilder.like(path, StringHelper.like(String.valueOf(value)));
							for (int i = 1; i < fields.length; i++) {
								Path<String> path2 = rangeRoot.get(fields[i]);
								conditions[i] =criteriaBuilder.like(path2, StringHelper.like(String.valueOf(value)));
							}
							
							condition = criteriaBuilder.or(conditions);
							break;
						case E:
							System.out.println("未添加排除选项的处理");
							break;
						default:
							break;
						}
						
					}
					break;
				case IS:
					break;
				default:
					break;
				}
	        	restrictions.add(condition);
			}
		}
        if(restrictions.size()>0) {
        	Predicate[] predicates = (Predicate[]) restrictions.toArray(new Predicate[restrictions.size()]);
        	criteriaQuery.where(predicates);
        }
        
        if(orders!=null && orders.length>0){
        	List<javax.persistence.criteria.Order> ods = new ArrayList<javax.persistence.criteria.Order>();
        	for (OrderBy order : orders) {
        		if(order.getDirection().toUpperCase().equals("DESC")){
        			ods.add(criteriaBuilder.desc(rangeRoot.get(order.getProperty())));
        		}else{
        			ods.add(criteriaBuilder.asc(rangeRoot.get(order.getProperty())));
        		}
			}
        	criteriaQuery.orderBy(ods);
        }
        TypedQuery<T> createQuery = entityManager.createQuery(criteriaQuery);
        return createQuery;
	}
	
	public Page<T> queryByRange(Map<String, String[]> parameterMap,Integer page,Integer limit,OrderBy...orders){
		TypedQuery<T> createQuery = queryCriteriaByRange(parameterMap, orders);
		long size = createQuery.getResultList().size();
        if(page>0)
        	createQuery.setFirstResult(((page-1) * limit));
        createQuery.setMaxResults(limit);
		return new PageImpl<T>(createQuery.getResultList(),new PageRequest(page, limit),size);
	}
	
	public void setDomainDefaultVal(Object obj){
		//long startTime = System.currentTimeMillis();
		Class<?> classType = obj.getClass();
		
		//手动获取request对象
		ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

		HttpServletRequest request=attributes==null?null:attributes.getRequest();
		String loginNo = null;
		if(request!=null){
			Object attribute = request.getSession().getAttribute(Constants.CURR_USER_ID);
			if(!StringUtils.isEmpty(attribute))loginNo = (String) attribute;
		}
		
		if(obj instanceof BaseEntity){
			BaseEntity uuidEntity = (BaseEntity) obj;
			if (StringUtils.isEmpty(uuidEntity.getId())) {
				uuidEntity.setCreateTime(new Date());
				uuidEntity.setCreateUser(loginNo);
				uuidEntity.setUpdateTime(new Date());
				uuidEntity.setUpdateUser(loginNo);
			}else{
				uuidEntity.setUpdateTime(new Date());
				uuidEntity.setUpdateUser(loginNo);
			}
			if(uuidEntity.getId()!=null){
				T one = findOne((ID) uuidEntity.getId());
				if(one==null){
					uuidEntity.setCreateTime(new Date());
					uuidEntity.setCreateUser(loginNo);
				}
			}
		}
		
		Field[] fields = classType.getDeclaredFields();
		for (Field field : fields) {
			
			String fieldName = field.getName();
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getMethodName = "get" + firstLetter + fieldName.substring(1);
			
			Method findGetMethod = org.springframework.util.ReflectionUtils.findMethod(classType, getMethodName);
			if(findGetMethod!=null){
				Object invokeMethod = org.springframework.util.ReflectionUtils.invokeMethod(findGetMethod,obj);
				if(invokeMethod!=null && field.getType().getName().equals(java.util.Set.class.getName())){
					for (Iterator<?> iterator = ((Set<?>)invokeMethod).iterator(); iterator.hasNext();) {
						Object object = iterator.next();
						setDomainDefaultVal(object);
					}
				}
			}
		}
		
		//long endTime = System.currentTimeMillis();
		
		//System.out.println((endTime-startTime));
	}

	@Override
	public <S extends T> S save(S entity) {
		this.setDomainDefaultVal(entity);
		return super.save(entity);
	}
	
	private void setSerialNumber(T entity,String...strings){
		try {
			if(entity instanceof UUIDEntity || entity instanceof UUID32Entity || entity instanceof AssignedEntity){
				String uuid = null;
				if(entity instanceof UUIDEntity){
					UUIDEntity uuidEntity = (UUIDEntity) entity;
					uuid = uuidEntity.getId();
				}else if(entity instanceof UUID32Entity){
					UUID32Entity uuid32Entity = (UUID32Entity) entity;
					uuid = uuid32Entity.getId();
				}else if(entity instanceof AssignedEntity){
					AssignedEntity assignedEntity = (AssignedEntity) entity;
					uuid = assignedEntity.getId();
				}
				
				if (StringUtils.isEmpty(uuid)) {
					for (String fieldName : strings) {
						String firstLetter = fieldName.substring(0, 1).toUpperCase();
						String setMethodName = "set" + firstLetter + fieldName.substring(1);
						//获取set方法
						Method findMethod = BeanUtils.findMethod(entity.getClass(), setMethodName,String.class);
				
						Query query = entityManager.createQuery("from SysSerialNumber where domain='"+entity.getClass().getName()+"' and field='"+fieldName+"'");
						
						SysSerialNumber serialNumber = null;
						
						@SuppressWarnings("unchecked")
						List<SysSerialNumber> resultList = query.getResultList();
						
						if(resultList.size()>0){
							serialNumber = resultList.get(0);
						}
						 
						if(serialNumber==null){
							//生成默认流水号，并保存
							serialNumber = new SysSerialNumber(entity.getClass().getName(), fieldName, 5, 0, null, "");
						}
						String[] sn = SerialNumberUtils.sn(serialNumber.getLen(), serialNumber.getLastnum(), serialNumber.getStem(), String.valueOf(serialNumber.getPrefix()),serialNumber.getLimited());
						findMethod.invoke(entity, sn[0]);//设置流水号
						serialNumber.setLastnum(Integer.valueOf(sn[3]));
						serialNumber.setStem(sn[1]);
						serialNumber.setLen(Integer.valueOf(sn[2]));
						UUIDEntity uuidEntity2 = (UUIDEntity) serialNumber;
						if (StringUtils.isEmpty(uuidEntity2.getId())) {
							entityManager.persist(serialNumber);
						}else{
							entityManager.merge(serialNumber);
						}
						
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public <S extends T> S save(S entity,String...strings) {
		this.setSerialNumber(entity, strings);
		this.setDomainDefaultVal(entity);
		S save = super.save(entity);
		return save;
	}
	
	@Override
	public <S extends T> S saveAndFlush(S entity,String...strings) {
		this.setSerialNumber(entity, strings);
		this.setDomainDefaultVal(entity);
		S save = super.saveAndFlush(entity);
		return save;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> queryByNativeQuery(String sql,Class<?> clazz) {
		return entityManager.createNativeQuery(sql,clazz).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> queryByNativeQuery(String sql) {
		return entityManager.createNativeQuery(sql,super.getDomainClass()).getResultList();
	}

	@Override
	public List<T> createQuery(String hql) {
		return entityManager.createQuery(hql,super.getDomainClass()).getResultList();
	}

	@Override
	public List<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		TypedQuery<T> createQuery = entityManager.createQuery(criteriaQuery);
		return createQuery.getResultList();
	}
 
	@Override
	public Page<T> queryByRange(Object object,int page,int limit,OrderBy...orders) {
		Map<String, String[]> parameterMap = com.mw.framework.utils.BeanUtils.getValues(object);
		return queryByRange(parameterMap, page, limit, orders);
	}

	 
	@Override
	public Page<T> queryByRange(Map<String, String[]> parameterMap, int page,int limit, OrderBy... orders) {
		return queryByRange(parameterMap, Integer.valueOf(page), Integer.valueOf(limit), orders);
	}

 
	@Override
	public List<T> queryByRange(Object object, OrderBy... orders) {
		Map<String, String[]> parameterMap = com.mw.framework.utils.BeanUtils.getValues(object);
		return queryCriteriaByRange(parameterMap, orders).getResultList();
	}

	@Override
	public List<T> queryByRange(Map<String, String[]> parameterMap,
			OrderBy... orders) {
		return queryCriteriaByRange(parameterMap, orders).getResultList();
	}

}