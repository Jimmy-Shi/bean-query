package cn.jimmyshi.beanquery.comparators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultSortOrderableComparator<T> implements SortOrderableComparator<T> {
  private transient Logger logger = LoggerFactory.getLogger(DefaultSortOrderableComparator.class);
  private boolean desc = false;

  @Override
  public int compare(T o1, T o2) {
    int ascResult = ascCompare(o1, o2);
    int finalResult = desc ? 0 - ascResult : ascResult;
    logger.debug("desc [{}], ascResult [{}], finalResult [{}]", desc, ascResult, finalResult);
    return finalResult;
  }

  protected abstract int ascCompare(T o1, T o2);

  @Override
  public SortOrderableComparator<T> desc() {
    desc = true;
    return this;
  }

  @Override
  public SortOrderableComparator<T> asc() {
    desc = false;
    return this;
  }
}
