package cn.jimmyshi.beanquery.selectors;

import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selector that use the bean properties as the key and the property values as
 * the value. Because every object has a "class" property which is not required
 * in most scenario, it will be excluded from the result. If you really want it,
 * you can use the {@link #add(String...)} method to add it in the result map.
 */
public class ClassSelector extends KeyValueMapSelector {
  private transient Logger logger = LoggerFactory.getLogger(ClassSelector.class);
  private List<PropertySelector> propertySelectors = new LinkedList<PropertySelector>();
  private CompositeSelector compositeSelector = new CompositeSelector();

  /**
   * @param clazz
   *          null will cause the select methods returning an empty Map or a
   *          list of Empty map as the result.
   */
  public ClassSelector(Class<?> clazz) {
    if (null == clazz) {
      logger.warn("Input class is null");
      return;
    }
    PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      boolean propertyReadable = propertyDescriptor.getReadMethod() != null;
      String propertyName = propertyDescriptor.getName();
      // ignore the class property
      if ("class".equals(propertyName)) {
        continue;
      }

      if (propertyReadable) {
        PropertySelector propertySelector = new PropertySelector(propertyDescriptor.getName());
        propertySelectors.add(propertySelector);
        compositeSelector.addSubSelector(propertySelector);
      }
    }
  }

  /**
   * Exclude properties from the result map.
   */
  public ClassSelector except(String... propertyNames) {
    if(ArrayUtils.isEmpty(propertyNames)){
      return this;
    }

    boolean updated = false;
    for (String propertyToRemove : propertyNames) {
      if (removePropertySelector(propertyToRemove)) {
        updated = true;
      }
    }

    if (updated) {
      compositeSelector = new CompositeSelector(this.propertySelectors);
    }
    return this;
  }

  private boolean removePropertySelector(String propertyName) {
    if (StringUtils.isBlank(propertyName)) {
      return false;
    }

    for (PropertySelector selector : propertySelectors) {
      if (selector.getProperty().equals(propertyName)) {
        propertySelectors.remove(selector);
        return true;
      }
    }
    return false;
  }

  /**
   * Include properties in the result map.
   */
  public ClassSelector add(String... propertyNames) {
    if (ArrayUtils.isNotEmpty(propertyNames)) {
      for (String propertyNameToAdd : propertyNames) {
        addPropertySelector(propertyNameToAdd);
      }
    }
    return this;
  }

  private void addPropertySelector(String propertyNameToAdd) {
    if (StringUtils.isBlank(propertyNameToAdd)) {
      return;
    }

    for (PropertySelector propertySelector : propertySelectors) {
      if (propertySelector.getProperty().equals(propertyNameToAdd)) {
        return;
      }
    }

    PropertySelector newPropertySelector = new PropertySelector(propertyNameToAdd);
    propertySelectors.add(newPropertySelector);
    compositeSelector.addSubSelector(newPropertySelector);
  }

  @Override
  protected Map<String, Object> doSelect(Object item) {
    return this.compositeSelector.select(item);
  }

}
