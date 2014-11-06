package cn.jimmyshi.beanquery.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jimmyshi.beanquery.Selector;

public class BeanSelector<T> implements Selector<T> {
  private final static Logger logger = LoggerFactory.getLogger(BeanSelector.class);
  private Class<T> itemClass;

  public BeanSelector(Class<T> itemClass) {
    this.itemClass = itemClass;
  }

  @Override
  public List<T> select(List<?> from) {
    if (CollectionUtils.isEmpty(from)) {
      logger.info("The from collection is empty, returning empty List");
      return Collections.emptyList();
    } else {
      List<T> result = new ArrayList<T>(from.size());
      for (Object item : from) {
        result.add(select(item));
      }
      return result;
    }
  }

  @Override
  public T select(Object item) {
    if (item == null) {
      return null;
    }

    if (itemClass.isInstance(item)) {
      return itemClass.cast(item);
    }
    return null;
  }

}
