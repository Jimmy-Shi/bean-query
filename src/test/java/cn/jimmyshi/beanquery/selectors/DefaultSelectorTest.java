package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.jimmyshi.beanquery.Selector;

public class DefaultSelectorTest {

  @Test
  public void testSelectoFromNullList() {
    //given
    Selector selector=new DefaultSelector() {
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
    Selector selector=new DefaultSelector() {
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
