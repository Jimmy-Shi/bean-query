package cn.jimmyshi.beanquery.selectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selector that cast the item to the type used to construct the BeanSelector
 * instance.
 *
 */
public class BeanSelector<T> extends DefaultSelector<T> {
  private transient Logger logger = LoggerFactory.getLogger(BeanSelector.class);
  private Class<T> itemClass;

  public BeanSelector(Class<T> itemClass) {
    this.itemClass = itemClass;
  }

  /**
   * If the item is null, return null. If the item is instance of the
   * constructor parameter itemClass, return the type cast result, otherwise,
   * return null.
   */
  @Override
  public T select(Object item) {
    if (item == null) {
      return null;
    }

    if (itemClass.isInstance(item)) {
      return itemClass.cast(item);
    }
    logger.debug("item [{}] is not assignable to class [{}], returning null", item, itemClass);
    return null;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE,false);
  }

}
