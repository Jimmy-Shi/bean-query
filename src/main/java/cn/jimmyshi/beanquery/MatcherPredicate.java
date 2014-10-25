package cn.jimmyshi.beanquery;

import org.apache.commons.collections4.Predicate;
import org.hamcrest.Matcher;

@SuppressWarnings("rawtypes")
public class MatcherPredicate implements Predicate {
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
