package cn.jimmyshi.beanquery;

import org.apache.commons.collections4.Predicate;
import org.hamcrest.Matcher;

public class MatcherPredicate<T> implements Predicate<T> {
  private final Matcher<?> matcher;

  public MatcherPredicate(Matcher<?> matcher) {
    this.matcher=matcher;
  }

  @Override
  public boolean evaluate(Object object) {
    return matcher.matches(object);
  }

  @Override
  public String toString() {
    return String.format("Predicate for Hamcrest Matcher [%s]", matcher);
  }

}
