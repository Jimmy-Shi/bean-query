package cn.jimmyshi.beanquery.selectors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertySelectorTest extends SelectorTestBase{

  @Test
  public void testUpdateAlias() {
    //given
    PropertySelector selector=new PropertySelector("propertyName","firstAlias");
    assertSelectResultKeys(selector, "firstAlias");
    //when
    selector.as("laterAlias");
    //then
    assertSelectResultKeys(selector, "laterAlias");
  }

  @Test
  public void testConstructWithoutAlias(){
    PropertySelector selector=new PropertySelector("keyName");
    assertEquals("keyName",selector.getProperty());
    assertSelectResultKeys(selector, "keyName");

  }

}
