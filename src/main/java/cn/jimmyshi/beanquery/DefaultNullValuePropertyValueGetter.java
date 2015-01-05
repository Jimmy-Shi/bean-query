package cn.jimmyshi.beanquery;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class DefaultNullValuePropertyValueGetter {
  public static Object getProperty(Object from,String propertyName){
    if(null==from || StringUtils.isBlank(propertyName)){
      LoggerFactory.getLogger(DefaultNullValuePropertyValueGetter.class).info("Object is null or the property [{}] is blank, returning null",propertyName);
      return null;
    }

    try {
      return PropertyUtils.getProperty(from, propertyName);
    } catch (Exception e) {
      LoggerFactory.getLogger(DefaultNullValuePropertyValueGetter.class).info("Exception [{}] when fetching property [{}] from object [{}], returning null as the value.",e.toString(),propertyName,from);
      return null;
    }
  }

}
