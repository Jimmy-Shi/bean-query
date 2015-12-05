package cn.jimmyshi.beanquery.selectors;

import org.junit.Test;

public class StringSelectorTest extends SelectorTestBase{

  @Test
  public void testStringConstructor(){
    StringSelector selector=new StringSelector("abc");
    assertSelectResultKeys(selector, "abc");
  }

  @Test
  public void testStringConstructorWithAlias(){
    StringSelector selector=new StringSelector("abc as edf");
    assertSelectResultKeys(selector, "edf");
  }

  @Test
  public void testStringContainsAsButItisNotAlias(){
    StringSelector selector=new StringSelector("task,abc");
    assertSelectResultKeys(selector, "task","abc");
  }

  @Test
  public void testStringContaningMultipleAs(){
    StringSelector selector=new StringSelector("task as it as m, abc");
    assertSelectResultKeys(selector, "it as m", "abc");
  }

  @Test
  public void testStringConstructorWithMultipleProperties(){
    StringSelector selector=new StringSelector("abc,, abc as edf,");
    assertSelectResultKeys(selector, "abc","edf");
  }

  @Test
  public void testStringConstructorWithEmptyString(){
    StringSelector selector=new StringSelector(" ");
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testStringConstructorWithNull(){
    StringSelector selector=new StringSelector((String)null);
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testStringArrayConstructor(){
    StringSelector selector=new StringSelector("abc",null," ", "", " abc as edf");
    assertSelectResultKeys(selector, "abc","edf");
  }

  @Test
  public void testStringArraysConstructorWithNull(){
    StringSelector selector=new StringSelector((String[])null);
    assertSelectResultKeySizeIsZero(selector);

  }

}
