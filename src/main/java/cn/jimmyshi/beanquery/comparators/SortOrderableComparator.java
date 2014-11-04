package cn.jimmyshi.beanquery.comparators;

import java.util.Comparator;

public interface SortOrderableComparator<T> extends Comparator<T> {
  SortOrderableComparator<T> desc();

  SortOrderableComparator<T> asc();
}
