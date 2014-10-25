package cn.jimmyshi.beanquery;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultNullValuePropertyValueGetter {
  private final static Logger logger=LoggerFactory.getLogger(DefaultNullValuePropertyValueGetter.class);
  public static Object getProperty(Object from,String propertyName){
    if(null==from || StringUtils.isBlank(propertyName)){
      logger.info("Object is null or the property [{}] is blank, returning null",propertyName);
      return null;
    }

    try {
      return PropertyUtils.getProperty(from, propertyName);
    } catch (Exception e) {
      logger.info("Exception [{}] when fetching property [{}] from object [{}], returning null as the value.",e.toString(),propertyName,from);
      return null;
    }
  }

}
