package cn.jimmyshi.beanquery;

import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class PropertyComparator implements Comparator {
  private final static Logger logger = LoggerFactory.getLogger(PropertyComparator.class);
  private String propertyName;
  private boolean desc = false;

  public PropertyComparator(String propertyName) {
    this.propertyName = propertyName;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(Object o1, Object o2) {
    logger.debug("comparing [{}] and [{}] with comparator [{}]", o1, o2, this);
    Comparable property1 = asNullIfNotComparable(DefaultNullValuePropertyValueGetter.getProperty(o1, propertyName));
    Comparable property2 = asNullIfNotComparable(DefaultNullValuePropertyValueGetter.getProperty(o2, propertyName));
    int ascResult = 0;
    if (property1 == null && property2 != null) {
      logger.debug("property1 is null & property2 [{}] is not null, asc result is -1", property2);
      ascResult = -1;
    } else if (property1 != null && property2 == null) {
      logger.debug("property1 [{}] is not nul l& property2 is null, asc result is 1", property1);
      ascResult = 1;
    } else {
      try {
        ascResult = property1.compareTo(property2);
        logger.debug("property1 [{}] compareTo property2 [{}] asc result is [{}]", property1, property2, ascResult);
      } catch (Exception ex) {
        logger.debug("Get exception [{}] when property1 [{}] compareTo property2 [{}], set asc result as 0",
            ex.getMessage(), property1, property2);
        ascResult = 0;
      }
    }
    logger.debug("asc result is [{}]", ascResult);

    int result = desc ? 0 - ascResult : ascResult;
    logger.debug("final result is [{}]", result);
    return result;
  }

  private Comparable asNullIfNotComparable(Object value) {
    if (value == null) {
      return null;
    }
    if (!(value instanceof Comparable)) {
      return null;
    }
    return (Comparable) value;

  }

  public String getPropertyName() {
    return this.propertyName;
  }

  public PropertyComparator desc() {
    this.desc = true;
    return this;
  }

  public PropertyComparator asc() {
    this.desc = false;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
