package cn.jimmyshi.beanquery.selectors;

import org.junit.Test;

public class NestedKeyValueMapSelectorTest extends SelectorTestBase{

  @Test
  public void test() {
    //given
    NestedKeyValueMapSelector selector=new NestedKeyValueMapSelector(new StringSelector("a, b.b1,b.b2, b.c.d.e.f,c,d"));

    //then
    assertSelectResultKeys(selector,"a","b","c","d");
  }

}
