package cn.jimmyshi.beanquery;

import java.util.List;

/**
 * Selector response to select some properties from a java bean.
 *
 */
public interface Selector<T> {

  /**
   * Select a list from the list of java bean.
   */
  List<T> select(List<?> from);

  /**
   * select from the java bean.
   */
  T select(Object item);

}
