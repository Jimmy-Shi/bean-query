package cn.jimmyshi.beanquery.selectors;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class KeyValueMapSelector extends DefaultSelector<Map<String,Object>> {

  @Override
  public Map<String, Object> select(Object item) {
    return doSelect(item);
  }

  protected abstract Map<String, Object> doSelect(Object item) ;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE,false);
  }

}
