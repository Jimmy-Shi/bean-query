package cn.jimmyshi.beanquery;

import java.util.List;

/**
 * Selector response to select some properties from a java bean.
 *
 */
public interface Selector<T> {

  /**
   * Select a list of Maps from the list of java bean.
   */
  List<T> select(List<?> from);

  /**
   * Select a map from the java bean.
   */
  T select(Object item);

}
