package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class BeanSelectorTest {
  @Test
  public void shouldGetNullWhenSelectNull(){
    //given
    BeanSelector<String> stringBeanSelector=new BeanSelector<String>(String.class);
    //when
    String result=stringBeanSelector.select((Object)null);
    //then
    assertNull(result);
  }

  @Test
  public void shouldGetNullWhenIsNotInstance(){
    //given
    BeanSelector<String> stringBeanSelector=new BeanSelector<String>(String.class);
    //when
    String result=stringBeanSelector.select(new Integer(2));
    //then
    assertNull(result);
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void shouldGetItselfWhenSelectCastable(){
    //given
    BeanSelector<Set> setBeanSelector=new BeanSelector<Set>(Set.class);
    Set item=new HashSet();
    //when
    Set result=setBeanSelector.select(item);
    //then
    assertSame(item, result);
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void shouldGetEmptyListWhenSelectFromNullList(){
    BeanSelector<String> stringBeanSelector=new BeanSelector<String>(String.class);
    List<String> result=stringBeanSelector.select((List)null);
    assertThat(result, empty());
  }

  @Test
  public void shouldGetEmptyListWhenSelectFromEmptyList(){
    BeanSelector<String> stringBeanSelector=new BeanSelector<String>(String.class);
    List<String> result=stringBeanSelector.select(new ArrayList<String>());
    assertThat(result, empty());
  }

  @Test
  public void shouldGetBeanList(){
    BeanSelector<String> stringBeanSelector=new BeanSelector<String>(String.class);
    List<String> result=stringBeanSelector.select(Arrays.asList("ABC","EDF"));
    assertThat(result, contains("ABC","EDF"));

  }
}
