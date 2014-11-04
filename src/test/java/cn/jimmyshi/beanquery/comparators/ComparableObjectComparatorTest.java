package cn.jimmyshi.beanquery.comparators;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class ComparableObjectComparatorTest {
  ComparableObjectComparator comparator=new ComparableObjectComparator();

  @Test
  public void shouldGet0IfCompareing2nulls() {
    assertEquals(0,comparator.compare(null, null));
  }

  @Test
  public void shouldGet1IfArg1NotNullAndArg2Null(){
    assertEquals(1,comparator.compare(mock(Comparable.class), null));
  }

  @Test
  public void shouldGet1IfArgComprableAndArg2NotComparable(){
    assertEquals(1,comparator.compare(mock(Comparable.class), new Object()));
  }

  @Test
  public void shouldGetNegotiveIfArg1NullAndArg2NotNull(){
    assertEquals(-1,comparator.compare(null, mock(Comparable.class)));
  }

  @Test
  public void shouldGetNegotiveIfArg1NotComprableAndArg2Comparable(){
    assertEquals(-1,comparator.compare(new Object(), mock(Comparable.class)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldGet0WhenExceptionWhileComparing(){
    //given
    Comparable o1=mock(Comparable.class);
    when(o1.compareTo(any())).thenThrow(IllegalAccessException.class);
    //when
    int result=comparator.compare(o1, mock(Comparable.class));
    //then
    assertEquals(0,result);
    verify(o1,only()).compareTo(any());

  }

  @Test
  public void testCompare(){
    assertEquals(0,comparator.compare("ABC", "ABC"));
  }

}
