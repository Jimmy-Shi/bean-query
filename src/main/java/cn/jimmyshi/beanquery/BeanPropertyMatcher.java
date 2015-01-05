package cn.jimmyshi.beanquery;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanPropertyMatcher<T> extends BaseMatcher<T> {
  private transient Logger logger = LoggerFactory.getLogger(BeanPropertyMatcher.class);
  private String property;
  private Matcher<?> matcher;

  public BeanPropertyMatcher(String property, Matcher<?> matcher) {
    this.property = property;
    this.matcher = matcher;
  }


  public boolean matches(Object item) {
    Object propertyValue = DefaultNullValuePropertyValueGetter.getProperty(item, property);
    try {
      return matcher.matches(propertyValue);
    } catch (Exception ex) {
      logger.info("Exception [{}] when matching value [{}] (property [{}] of item [{}]) with matcher [{}]",ex.toString(), propertyValue,
          property, item, matcher);
      return false;
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, false);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(toString());
  }
}
