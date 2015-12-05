package cn.jimmyshi.beanquery.selectors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A selector that sorted some of the result key-value pairs into a map which has the same prefix ended with ".".
 * For example,
 * <ul>
 * <li>Without this: result of select("a1, a2.b, a2.c") is a list of map with 3 keys: a1, a2.b, a2.c.</li>
 * <li>With this, result of select("a1, a2.b, a2.c") will be a list of map with 2 keys: a1, a2. and the value of a2 is a map with keys: b and c</li>
 * </ul>
 *
 * This class is created to fulfill requirement: https://github.com/Jimmy-Shi/bean-query/issues/9
 *
 */
public class NestedKeyValueMapSelector extends KeyValueMapSelector {
  private KeyValueMapSelector selector;

  public NestedKeyValueMapSelector(KeyValueMapSelector sourceSelector) {
    this.selector=sourceSelector;
  }

  @Override
  protected Map<String, Object> doSelect(Object item) {
    Map<String, Object> originalResult = this.selector.select(item);
    if(MapUtils.isEmpty(originalResult)){
      return originalResult;
    }

    Map<String,Object> result=new LinkedHashMap<String, Object>();
    for (Entry<String, Object> originalItem : originalResult.entrySet()) {
      String key = originalItem.getKey();
      Object value = originalItem.getValue();

      if(StringUtils.isBlank(key) || (!key.contains("."))){
        result.put(key, value);
        continue;
      }
      addNestedKeyValue(key, value, result);
    }

    return result;
  }

  private void addNestedKeyValue(String key,Object value, Map<String,Object> container){
    if(!key.contains(".")){
      container.put(key, value);
      return;
    }

    String firstKey=StringUtils.substringBefore(key, ".");
    Map<String,Object> subContainer=new LinkedHashMap<String, Object>();
    if(container.containsKey(firstKey)){
      Object originalValue = container.get(firstKey);
      if(subContainer.getClass().isAssignableFrom(originalValue.getClass())){
        subContainer=(Map<String, Object>) originalValue;
      }else{
        subContainer.put("", originalValue);
        container.put(firstKey, subContainer);
      }
    }else{
      container.put(firstKey, subContainer);
    }
    addNestedKeyValue(StringUtils.substringAfter(key, "."), value, subContainer);
  }

}
