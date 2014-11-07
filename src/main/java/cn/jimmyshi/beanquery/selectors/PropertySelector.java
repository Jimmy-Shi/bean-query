package cn.jimmyshi.beanquery.selectors;

import java.util.Map;

import org.apache.commons.collections4.map.SingletonMap;

import cn.jimmyshi.beanquery.DefaultNullValuePropertyValueGetter;

public class PropertySelector extends KeyValueMapSelector {
  private String property;
  private String alias;

  String getProperty() {
    return property;
  }

  /**
   * @param property name of the property name used to fetch value from the bean.
   * @param alias the key in the result map
   */
  public PropertySelector(String property, String alias) {
    this.property = property;
    this.alias = alias;
  }

  public PropertySelector(String property) {
    this.property = property;
    this.alias = property;
  }

  /**
   * Use the alias as the key in the result map.
   */
  public PropertySelector as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  protected Map<String, Object> doSelect(Object item) {
    Object value = DefaultNullValuePropertyValueGetter.getProperty(item, property);
    return new SingletonMap<String,Object>(alias, value);
  }

}
