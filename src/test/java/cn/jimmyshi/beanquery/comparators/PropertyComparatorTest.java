package cn.jimmyshi.beanquery.comparators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Comparator;

import org.junit.Test;
import org.mockito.Mockito;

import cn.jimmyshi.beanquery.comparators.PropertyComparator;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PropertyComparatorTest {

  public static class TestComparingClass {
    private String name = null;
    private Object notComparable;
    private int intValue = 0;
    private Comparable comparable;

    public TestComparingClass(Comparable comparable) {
      super();
      this.comparable = comparable;
    }

    public Comparable getComparable() {
      return comparable;
    }

    public TestComparingClass(int intValue) {
      super();
      this.intValue = intValue;
    }

    public int getIntValue() {
      return intValue;
    }

    public TestComparingClass(Object notComparable) {
      super();
      this.notComparable = notComparable;
    }

    public Object getNotComparable() {
      return notComparable;
    }

    public String getName() {
      return name;
    }

    public TestComparingClass(String name) {
      super();
      this.name = name;
    }

    public TestComparingClass() {
    }

  }

  @Test
  public void shouldGet0WhenBothObjectIsNull() {
    assertEquals(0, new PropertyComparator("abc").compare(null, null));
  }

  @Test
  public void shouldGet0WhenBothPropertyIsNull() {
    assertEquals(0, new PropertyComparator("name").compare(new TestComparingClass(), new TestComparingClass()));
  }

  @Test
  public void shouldGetNegativeResultWhenOnlyProperty1IsNull() {
    assertEquals(-1,
        new PropertyComparator("name").compare(new TestComparingClass(), new TestComparingClass("notNull")));

  }

  @Test
  public void shouldGetPostiveResultWhenOnlyProperty2IsNull() {
    assertEquals(1, new PropertyComparator("name").compare(new TestComparingClass("notNull"), new TestComparingClass()));

  }

  @Test
  public void shouldGetNullPropertyWhenIsNotComparable() {
    assertEquals(0, new PropertyComparator("notComparable").compare(new TestComparingClass(new Object()),
        new TestComparingClass(new Object())));
  }

  @Test
  public void shouldWorkForPrimitiveType() {
    Comparator p = new PropertyComparator("intValue");
    assertEquals(0, p.compare(new TestComparingClass(1), new TestComparingClass(1)));
    assertEquals(1, p.compare(new TestComparingClass(2), new TestComparingClass(1)));
    assertEquals(-1, p.compare(new TestComparingClass(2), new TestComparingClass(3)));
  }

  @Test
  public void shouldGet0WhenGetExceptionComparing() {
    TestComparingClass obj1 = new TestComparingClass(new Comparable() {

      @Override
      public int compareTo(Object o) {
        throw new IllegalStateException("Exception!!!!!!!!!!!!");
      }
    });
    TestComparingClass obj2 = new TestComparingClass(Mockito.mock(Comparable.class));

    Comparator p = new PropertyComparator("comparable");
    assertEquals(0, p.compare(obj1, obj2));

  }

  @Test
  public void shouldCallCompareToWhenComparable() {
    // given
    Comparable comparable = Mockito.mock(Comparable.class);
    when(comparable.compareTo(any())).thenReturn(1).thenReturn(-1).thenReturn(0);
    TestComparingClass obj1 = new TestComparingClass(comparable);
    TestComparingClass obj2 = new TestComparingClass(Mockito.mock(Comparable.class));
    Comparator p = new PropertyComparator("comparable");

    // when & then
    assertEquals(1, p.compare(obj1, obj2));
    assertEquals(-1, p.compare(obj1, obj2));
    assertEquals(0, p.compare(obj1, obj2));

  }

}
