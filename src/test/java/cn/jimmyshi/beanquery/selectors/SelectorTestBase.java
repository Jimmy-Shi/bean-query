package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.Map;

import cn.jimmyshi.beanquery.Selector;
import cn.jimmyshi.beanquery.example.Book;

/**
 * Provide some methods to verify Selector behavior.
 *
 */
public class SelectorTestBase {
  void assertSelectResultKeySizeIsZero(Selector selector) {
    Map<String, Object> result = selector.select(new Book());
    assertThat(result.keySet(), empty());
  }
  void assertSelectResultKeys(Selector selector,String... keys){
    Map<String, Object> result = selector.select(new Book());
    assertThat(result.keySet(), contains(keys));
  }
}
