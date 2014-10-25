package cn.jimmyshi.beanquery.selectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jimmyshi.beanquery.Selector;

public class CompositeSelector extends DefaultSelector implements Selector {
  private static final Logger logger = LoggerFactory.getLogger(CompositeSelector.class);
  private static final List<Selector> SINGLETON_NULL_LIST = Collections.singletonList(null);
  private final List<Selector> selectors;

  public CompositeSelector(Selector... selectors) {
    if (ArrayUtils.isEmpty(selectors)) {
      this.selectors = new LinkedList<Selector>();
    } else {
      this.selectors = new LinkedList<Selector>(Arrays.asList(selectors));
      removeNullSubSelectors();
    }
  }

  CompositeSelector() {
    this.selectors = new LinkedList<Selector>();
  }

  CompositeSelector(List<? extends Selector> selectors) {
    this.selectors = new ArrayList<Selector>(ListUtils.emptyIfNull(selectors));
    removeNullSubSelectors();
  }

  private void removeNullSubSelectors() {
    this.selectors.removeAll(SINGLETON_NULL_LIST);
  }

  CompositeSelector addSubSelector(Selector subSelector) {
    if (null != subSelector) {
      this.selectors.add(subSelector);
    }
    return this;
  }

  @Override
  protected Map<String, Object> doSelect(Object item) {
    if (CollectionUtils.isEmpty(selectors)) {
      logger.debug("Not any subSelectors found, returning emptyMap");
      return Collections.emptyMap();
    }

    Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Selector selector : selectors) {
      result.putAll(selector.select(item));
    }
    return result;
  }
}
