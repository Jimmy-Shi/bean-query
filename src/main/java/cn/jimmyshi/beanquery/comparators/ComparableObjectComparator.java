package cn.jimmyshi.beanquery.comparators;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ComparableObjectComparator implements Comparator {
  private static final Logger logger = LoggerFactory.getLogger(ComparableObjectComparator.class);

  /**
   * <ul>
   * <li>First convert the input objects to comparable instances(comparable1,
   * comparable2). If they are null or not instance of comparable, the convert
   * result will be null.</li>
   * <li>If both both comparable instances are null, return 0.</li>
   * <li>If comparable1 is null and comparable2 is not null, return -1</li>
   * <li>If comparable1 is not null and comparable2 is null, return 1</li>
   * <li>Then call the compareTo method of the first converted
   */
  @SuppressWarnings("unchecked")
  @Override
  public int compare(Object o1, Object o2) {
    logger.debug("Comparing [{}] and [{}]", o1, o2);
    int result = 0;
    Comparable comparable1 = asNullIfNotComparable(o1);
    Comparable comparable2 = asNullIfNotComparable(o2);

    if (comparable1 == null && comparable2 == null) {
      logger.debug("Both items are null, result is 0");
      result=0;
    } else if (comparable1 == null && comparable2 != null) {
      logger.debug("comparable1 is null & comparable2 [{}] is not null, result is -1", comparable2);
      result = -1;
    } else if (comparable1 != null && comparable2 == null) {
      logger.debug("comparable1 [{}] is not null & comparable2 is null, result is 1", comparable1);
      result = 1;
    } else {
      try {
        result = comparable1.compareTo(comparable2);
        logger.debug("comparable1 [{}] compareTo comparable2 [{}] result is [{}]", comparable1, comparable2, result);
      } catch (Exception ex) {
        logger.debug("Get exception [{}] when comparable1 [{}] compareTo comparable2 [{}], set result as 0",
            ex.toString(), comparable1, comparable2);
        result = 0;
      }
    }
    logger.debug("Compared result is [{}]", result);
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

}
