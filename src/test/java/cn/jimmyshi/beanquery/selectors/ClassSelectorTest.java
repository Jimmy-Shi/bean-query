package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import cn.jimmyshi.beanquery.example.Address;
import cn.jimmyshi.beanquery.selectors.ClassSelector;

public class ClassSelectorTest {
  String[] defaultPropertyNamesOfAddress = new String[] { "address", "postCode" };

  @Test
  public void testConstructWithNull() {
    // given
    ClassSelector selector = new ClassSelector(null);
    // when
    Map<String, Object> result = selector.select(new Address());
    // then
    assertThat(result.keySet().size(), is(0));
  }

  @Test
  public void testConstructWithClassWithoutReadableProperty() {
    // given
    ClassSelector selector = new ClassSelector(ClassWithNotReadableProperty.class);
    // when
    Map<String, Object> result = selector.select(new ClassWithNotReadableProperty());
    // then
    assertThat(result.keySet(), contains("name"));
  }

  public static class ClassWithNotReadableProperty {
    private String name;
    private String notReadable;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setNotReadable(String notReadable) {
      this.notReadable = notReadable;
    }
  }

  @Test
  public void testExceptEmptyArray() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.except();
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testExceptNull() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.except(null);
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testExceptNullAndBlanks() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.except(null, "", "  ");
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testExcept() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.except("address", "officeAddress");
    // then
    assertThat(selector.select(new Address()).keySet(),
        containsInAnyOrder(ArrayUtils.removeElements(defaultPropertyNamesOfAddress, "address","officeAddress")));

  }

  @Test
  public void testAddEmptyArray() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.add();
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testAddNull() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.add(null);
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testAddNullAndBlanks() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.add(null, "", "  ");
    // then
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
  }

  @Test
  public void testAdd() {
    // given
    ClassSelector selector = new ClassSelector(Address.class);
    assertThat(selector.select(new Address()).keySet(), containsInAnyOrder(defaultPropertyNamesOfAddress));
    // when
    selector.add("address", "officeAddress");
    // then
    assertThat(selector.select(new Address()).keySet(),
        containsInAnyOrder(ArrayUtils.add(defaultPropertyNamesOfAddress, "officeAddress")));

  }

}
