package cn.jimmyshi.beanquery.comparators;

import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jimmyshi.beanquery.DefaultNullValuePropertyValueGetter;

@SuppressWarnings("rawtypes")
public class PropertyComparator implements Comparator {
  private transient Logger logger = LoggerFactory.getLogger(PropertyComparator.class);
  private final String propertyName;
  private final Comparator comparator;

  public PropertyComparator(String propertyName, Comparator propertyValueComparator) {
    this.propertyName = propertyName;
    this.comparator = propertyValueComparator;
  }

  public PropertyComparator(String propertyName) {
    this(propertyName, new ComparableObjectComparator());
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(Object o1, Object o2) {
    logger.debug("comparing [{}] and [{}] with comparator [{}].", o1, o2,this);
    int result = 0;
    Object property1 = DefaultNullValuePropertyValueGetter.getProperty(o1, propertyName);
    Object property2 = DefaultNullValuePropertyValueGetter.getProperty(o2, propertyName);
    result=comparator.compare(property1, property2);
    logger.debug("Compare result is [{}]", result);
    return result;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE,false);
  }

}
