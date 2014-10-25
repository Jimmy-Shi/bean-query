package cn.jimmyshi.beanquery;

import java.util.List;
import java.util.Map;

/**
 * Selector response to select some properties from a java bean.
 *
 */
public interface Selector {

  /**
   * Select a list of Maps from the list of java bean.
   */
  List<Map<String, Object>> select(List<?> from);

  /**
   * Select a map from the java bean.
   */
  Map<String, Object> select(Object item);

}
