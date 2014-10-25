package cn.jimmyshi.beanquery;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.jimmyshi.beanquery.example.Book;

public class DefaultNullValuePropertyValueGetterTest {

  @Test
  public void testFromNullBean() {
    Object result = DefaultNullValuePropertyValueGetter.getProperty(null, "abc");
    assertNull(result);
  }

  @Test
  public void testNullPropertyName() {
    Object result = DefaultNullValuePropertyValueGetter.getProperty(new Book(), null);
    assertNull(result);
  }

  @Test
  public void testEmptyPropertyName() {
    Object result = DefaultNullValuePropertyValueGetter.getProperty(new Book(), "");
    assertNull(result);
  }

  @Test
  public void testBlankPropertyName() {
    Object result = DefaultNullValuePropertyValueGetter.getProperty(new Book(), " ");
    assertNull(result);
  }

  @Test
  public void testGetProperty() {
    // given
    Book book = new Book();
    book.setName("bookName");
    // when
    Object result = DefaultNullValuePropertyValueGetter.getProperty(book, "name");
    // then
    assertEquals("bookName", result);
  }

  @Test
  public void testGetNotExistingProperty() {
    Object result = DefaultNullValuePropertyValueGetter.getProperty(new Book(), "abc");
    assertNull(result);
  }

}
