package cn.jimmyshi.beanquery.comparators;

import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DelegatedSortOrderableComparator<T> extends DefaultSortOrderableComparator<T> {
  private Comparator<T> delegated;

  public DelegatedSortOrderableComparator(Comparator<T> delegated) {
    this.delegated = delegated;
  }

  @Override
  protected int ascCompare(T o1, T o2) {
    return delegated.compare(o1, o2);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE,false);
  }

}
