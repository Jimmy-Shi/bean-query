package cn.jimmyshi.beanquery.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class DefaultKeyValueMapSelector implements KeyValueMapSelector {

  @Override
  public List<Map<String, Object>> select(List<?> from) {
    if(CollectionUtils.isEmpty(from)){
      return Collections.emptyList();
    }
    return doSelect(from);
  }

  protected List<Map<String, Object>> doSelect(List<?> notEmptyFrom){
    List<Map<String,Object>> result=new ArrayList<Map<String,Object>>(notEmptyFrom.size());
    for (Object item : notEmptyFrom) {
      result.add(select(item));
    }
    return result;
  }

  @Override
  public Map<String, Object> select(Object item) {
    return doSelect(item);
  }

  protected abstract Map<String, Object> doSelect(Object item) ;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
