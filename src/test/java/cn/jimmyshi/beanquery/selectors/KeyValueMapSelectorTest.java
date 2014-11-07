package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class KeyValueMapSelectorTest {

  @Test
  public void testSelectoFromNullList() {
    //given
    KeyValueMapSelector selector=new KeyValueMapSelector() {
      @Override
      protected Map<String, Object> doSelect(Object item) {
        return Collections.emptyMap();
      }
    };
    //when
    List<Map<String, Object>> result = selector.select((List<Object>)null);
    //then
    assertThat(result, empty());
  }

  @Test
  public void testSelectFromEmptyList(){
  //given
    KeyValueMapSelector selector=new KeyValueMapSelector() {
      @Override
      protected Map<String, Object> doSelect(Object item) {
        return Collections.emptyMap();
      }
    };
    //when
    List<Map<String, Object>> result = selector.select(Collections.emptyList());
    //then
    assertThat(result, empty());
  }

}
