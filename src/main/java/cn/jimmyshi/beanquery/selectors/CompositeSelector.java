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

public class CompositeSelector extends KeyValueMapSelector{
  private transient Logger logger = LoggerFactory.getLogger(CompositeSelector.class);
  private static final List<KeyValueMapSelector> SINGLETON_NULL_LIST = Collections.singletonList(null);
  private final List<KeyValueMapSelector> selectors;

  public CompositeSelector(KeyValueMapSelector... selectors) {
    if (ArrayUtils.isEmpty(selectors)) {
      this.selectors = new LinkedList<KeyValueMapSelector>();
    } else {
      this.selectors = new LinkedList<KeyValueMapSelector>(Arrays.asList(selectors));
      removeNullSubSelectors();
    }
  }

  CompositeSelector() {
    this.selectors = new LinkedList<KeyValueMapSelector>();
  }

  CompositeSelector(List<? extends KeyValueMapSelector> selectors) {
    this.selectors = new ArrayList<KeyValueMapSelector>(ListUtils.emptyIfNull(selectors));
    removeNullSubSelectors();
  }

  private void removeNullSubSelectors() {
    this.selectors.removeAll(SINGLETON_NULL_LIST);
  }

  CompositeSelector addSubSelector(KeyValueMapSelector subSelector) {
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
    for (KeyValueMapSelector selector : selectors) {
      result.putAll(selector.select(item));
    }
    return result;
  }
}
