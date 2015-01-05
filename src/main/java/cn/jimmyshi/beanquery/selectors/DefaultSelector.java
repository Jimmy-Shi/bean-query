package cn.jimmyshi.beanquery.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jimmyshi.beanquery.Selector;

/**
 * Selector that implemented the select(List&lt;?&gt; from) methods.
 */
public abstract class DefaultSelector<T> implements Selector<T> {
  private transient Logger logger = LoggerFactory.getLogger(DefaultSelector.class);

  @Override
  public List<T> select(List<?> from) {
    if (CollectionUtils.isEmpty(from)) {
      logger.info("The from collection is empty, returning empty List");
      return Collections.emptyList();
    }

    return doSelect(from);
  }

  protected List<T> doSelect(List<?> notEmptyFrom) {
    List<T> result = new ArrayList<T>(notEmptyFrom.size());
    for (Object item : notEmptyFrom) {
      result.add(select(item));
    }
    return result;
  }

}
