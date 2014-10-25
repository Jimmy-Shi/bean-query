package cn.jimmyshi.beanquery;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.Mockito;

import cn.jimmyshi.beanquery.example.Address;
import cn.jimmyshi.beanquery.example.Book;

public class BeanPropertyMatcherTest {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldReturnFalseWhenExceptionWhileMatching() {
    //given
    Matcher delegatedMatcher=Mockito.mock(Matcher.class);
    when(delegatedMatcher.matches(any())).thenThrow(NullPointerException.class);
    BeanPropertyMatcher beanPropertyMatcher=new BeanPropertyMatcher("propertyName", delegatedMatcher);
    //when
    boolean result=beanPropertyMatcher.matches(new Book());
    assertFalse(result);
    verify(delegatedMatcher).matches(any());
  }

  @Test
  public void testMatches(){
    //given
    BeanPropertyMatcher<String> postCodeStartedWith000Matcher=new BeanPropertyMatcher<String>("postCode", startsWith("000"));
    Address postCodeStartedWith000=new Address();
    postCodeStartedWith000.setPostCode("000111");

    Address postCodeStartedWith777=new Address();
    postCodeStartedWith777.setPostCode("777666");

    //when, then
    assertTrue(postCodeStartedWith000Matcher.matches(postCodeStartedWith000));
    assertFalse(postCodeStartedWith000Matcher.matches(postCodeStartedWith777));
  }

}
