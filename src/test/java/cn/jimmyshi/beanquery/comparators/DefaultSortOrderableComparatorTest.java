package cn.jimmyshi.beanquery.comparators;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public class DefaultSortOrderableComparatorTest {
  DefaultSortOrderableComparator<?> return99Comparator;
  @Before
  public void setup(){
    return99Comparator = new DefaultSortOrderableComparator<Object>() {

      @Override
      protected int ascCompare(Object o1, Object o2) {
        return 99;
      }
    };
  }


  @Test
  public void shouldGetAscCompareResultInDefault() {
    //when
    int result=return99Comparator.compare(null, null);
    //then
    assertEquals(99,result);
  }

  @Test
  public void shouldGetDescresultWhenDescCalled(){
    //given
    Comparator<?> comparator=return99Comparator.desc();
    //when
    int result=comparator.compare(null, null);
    //then
    assertEquals(-99,result);
  }

  @Test
  public void shouldGetAscResultWhenAscCalled(){
    //given
    Comparator<?> comparator=return99Comparator.asc();
    //when
    int result=comparator.compare(null, null);
    //then
    assertEquals(99,result);
  }

}
