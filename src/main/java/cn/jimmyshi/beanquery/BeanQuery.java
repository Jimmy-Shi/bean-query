package cn.jimmyshi.beanquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jimmyshi.beanquery.comparators.ComparableObjectComparator;
import cn.jimmyshi.beanquery.comparators.DelegatedSortOrderableComparator;
import cn.jimmyshi.beanquery.comparators.PropertyComparator;
import cn.jimmyshi.beanquery.comparators.SortOrderableComparator;
import cn.jimmyshi.beanquery.selectors.BeanSelector;
import cn.jimmyshi.beanquery.selectors.ClassSelector;
import cn.jimmyshi.beanquery.selectors.CompositeSelector;
import cn.jimmyshi.beanquery.selectors.KeyValueMapSelector;
import cn.jimmyshi.beanquery.selectors.NestedKeyValueMapSelector;
import cn.jimmyshi.beanquery.selectors.PropertySelector;
import cn.jimmyshi.beanquery.selectors.StringSelector;

/**
 * Entry of the BeanQuery. Typical usage is as below.
 * <pre>
 * import static cn.jimmyshi.beanquery.BeanQuery.*;
 * List&lt;Map&lt;String,Object&gt;&gt; result=select("name,price,mainAuthor.name as authorName")
 *                                 .from(collectionOfBooks)
 *                                 .where(
 *                                    anyOf(
 *                                      value("name", startsWith("Book1")),
 *                                      value("name", is("Book2"))
 *                                      ),
 *                                    allOf(
 *                                      value("price", greaterThan(53d)),
 *                                      value("price",lessThan(65d))
 *                                      )
 *                                 )
 *                                 .orderBy("name").desc()
 *                                 .execute();
 * </pre>
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class BeanQuery<T> extends BeanQueryCustomizedMatchers {
  private transient Logger logger = LoggerFactory.getLogger(BeanQuery.class);
  private final Selector<T> selector;
  private Collection from;
  private Predicate predicate = TruePredicate.truePredicate();
  private SortOrderableComparator comparator;
  private boolean descSorting = false;

  private BeanQuery(Selector<T> selector) {
    this.selector = selector;
  }

  /**
   * Convert the select into a NestedKeyValueMapSelector.<br>
   * Note<br>:
   * This method is only available for BeanQueries with a KeyValuMapSelector. Calling this method on a BeanQeury with other types of selector will cause a IllegalStateExcewption.
   *
   */
  public BeanQuery<Map<String,Object>> nested(){
    if(!(this.selector instanceof KeyValueMapSelector)){
      throw new IllegalStateException("This is only for BeanQueries which has a KeyValueMapSelector selector. The current selector is a "+this.selector);
    }
    BeanQuery<Map<String,Object>> result= new BeanQuery<Map<String,Object>>(new NestedKeyValueMapSelector((KeyValueMapSelector)this.selector));
    result.from=this.from;
    result.predicate=this.predicate;
    result.comparator=this.comparator;
    result.descSorting=this.descSorting;
    return result;
  }

  /**
   * Specify where to query from.
   *
   * @param from
   */
  public BeanQuery<T> from(Collection<?> from) {
    this.from = from;
    return this;
  }

  /**
   * Same as <code>from(Collections.singleton(bean))</code>
   */
  public BeanQuery<T> from(Object bean){
    this.from=Collections.singleton(bean);
    return this;
  }

  /**
   * Support the Hamcrest Matcher as the query condition. Only items match this
   * matcher will be chosen.
   *
   * @param matcher
   */
  public BeanQuery<T> where(Matcher matcher) {
    this.predicate = new MatcherPredicate(matcher);
    return this;
  }

  /**
   * Support multiple Hamcrest Matchers as the query condition. Only items match
   * all the matchers will be chosen.
   */
  public BeanQuery<T> where(Matcher... matchers) {
    this.predicate = new MatcherPredicate(allOf(matchers));
    return this;
  }

  /**
   * Specify the property of the Beans to be compared when ordering the result.
   * When comparing, the property value of the bean must be instance of
   * {@link Comparable}, Otherwise it will be treated as a null value. The null
   * value is sorted at the top in ASC sorting and at the bottom in DESC
   * sorting. The default Order is ASC order.
   *
   * @param orderByProperty
   */
  public BeanQuery<T> orderBy(String orderByProperty) {
    this.comparator = new DelegatedSortOrderableComparator(new PropertyComparator(orderByProperty,
        new ComparableObjectComparator()));
    return this;
  }

  /**
   * Specify the property of the beans to be compared by the
   * propertyValueComparator when sorting the result. If there is not a
   * accessible public read method of the property, the value of the property
   * passed to the propertyValueComparator will be null.
   */
  public BeanQuery<T> orderBy(String orderByProperty, Comparator propertyValueComparator) {
    this.comparator = new DelegatedSortOrderableComparator(new PropertyComparator(orderByProperty,
        propertyValueComparator));
    return this;
  }

  /**
   * Specify the comparator used to compare the bean when sorting the result.
   */
  public BeanQuery<T> orderBy(Comparator beanComparator) {
    this.comparator = new DelegatedSortOrderableComparator(beanComparator);
    return this;
  }

  /**
   * Using an array of Comparators, applied in sequence until one returns not equal or the array is exhausted.
   */
  public BeanQuery<T> orderBy(Comparator... beanComparator) {
    this.comparator = new DelegatedSortOrderableComparator(ComparatorUtils.chainedComparator(beanComparator));
    return this;
  }

  /**
   * Sort the result in DESC order. The default ordering is ASC order. If the
   * {@link #orderBy(String)} is not specified, calling this method does not
   * affect anything.
   */
  public BeanQuery<T> desc() {
    this.descSorting = true;
    return this;
  }

  /**
   * Sort the result in ASC order. The default ordering is ASC order. If the
   * {@link #orderBy(String)} is not specified, calling this method does not
   * affect anything.
   */
  public BeanQuery<T> asc() {
    this.descSorting = false;
    return this;
  }

  /**
   * Create a Comparator base on a property.
   */
  public static SortOrderableComparator<?> orderByProperty(String propertyName){
    return new DelegatedSortOrderableComparator(new PropertyComparator(propertyName,
        new ComparableObjectComparator()));
  }

  /**
   * A convenient method of from(from).execute();
   */
  public List<T> executeFrom(Collection<?> from){
    return from(from).execute();
  }

  /**
   * Execute from a bean to check does it match the filtering condition and
   * convert it.
   */
  public T executeFrom(Object bean) {
    List<T> executeFromCollectionResult = executeFrom(Collections.singleton(bean));
    if (CollectionUtils.isEmpty(executeFromCollectionResult)) {
      return null;
    } else {
      return executeFromCollectionResult.get(0);
    }
  }

  /**
   * Execute this Query. If query from a null or empty collection, an empty list
   * will be returned.
   *
   * @return
   */
  public List<T> execute() {
    if (CollectionUtils.isEmpty(from)) {
      logger.info("Querying from an empty collection, returning empty list.");
      return Collections.emptyList();
    }

    List copied = new ArrayList(this.from);

    logger.info("Start apply predicate [{}] to collection with [{}] items.", predicate, copied.size());
    CollectionUtils.filter(copied, this.predicate);
    logger.info("Done filtering collection, filtered result size is [{}]", copied.size());

    if (null != this.comparator && copied.size()>1) {
      Comparator actualComparator = this.descSorting ? comparator.desc() : comparator.asc();
      logger.info("Start to sort the filtered collection with comparator [{}]", actualComparator);
      Collections.sort(copied, actualComparator);
      logger.info("Done sorting the filtered collection.");
    }

    logger.info("Start to slect from filtered collection with selector [{}].", selector);
    List<T> select = this.selector.select(copied);
    logger.info("Done select from filtered collection.");
    return select;

  }

  /**
   * Create a BeanQuery instance with multiple Selectors.
   *
   * @param selectors
   */
  public static BeanQuery<Map<String, Object>> select(KeyValueMapSelector... selectors) {
    return new BeanQuery<Map<String, Object>>(new CompositeSelector(selectors));
  }

  /**
   * Create a BeanQuery instance with select String, the select String is in
   * format "propertyName[ as alias][,propertyName[ as alias]]". For example:<br>
   * <code>BeanQuery beanQuery=select("name, price as p, address.officeAddress as address");</code>
   * When executing the BeanQuery instance created in above code will
   * return a list of map with 3 keys:[name,p,address].
   */
  public static BeanQuery<Map<String, Object>> select(String selectString) {
    return new BeanQuery<Map<String, Object>>(new StringSelector(selectString));
  }

  /**
   * Create a BeanQuery instance with some propertyString, a property string is
   * in format "propertyName[ as alias]". For example:<br>
   * <code>BeanQuery beanQuery=select("name","price as p", "address.officeAddress as address");</code>
   * <br>
   * When executing the BeanQuery instance created in above code will return a
   * list of map with 3 keys:[name,p,address].
   */
  public static BeanQuery<Map<String, Object>> select(String... propertyStrings) {
    return new BeanQuery<Map<String, Object>>(new StringSelector(propertyStrings));
  }

  /**
   * Create a BeanQuery instance without the function of convert result into Map
   * function. If you just want to filter bean collection, sort bean collection
   * and want to get the execute result as a list of beans, you should use this
   * method to create a BeanQuery instance.
   * @deprecated use {@link #select(Class)} method instead
   */
  public static <T> BeanQuery<T> selectBean(Class<T> beanClass) {
    return new BeanQuery<T>(new BeanSelector<T>(beanClass));
  }

  /**
   * Create a BeanQuery instance without the function of convert result into Map
   * function. If you just want to filter bean collection, sort bean collection
   * and want to get the execute result as a list of beans, you should use this
   * method to create a BeanQuery instance.
   */
  public static <T> BeanQuery<T> select(Class<T> beanClass) {
    return new BeanQuery<T>(new BeanSelector<T>(beanClass));
  }

  /**
   * Allow client to create a BeanQuery instance with a customized selector.
   */
  public static <T> BeanQuery<T> select(Selector<T> selector){
    return new BeanQuery(selector);
  }

  /**
   * Create a Selector that will use all the public readable
   * propertyNames(except the class property) as the keys. When saying
   * "public readable", means a property has a public read method, for example:
   * <ul>
   * <li><code>public String getName()</code> for property name.</li>
   * <li><code>public boolean isActive()</code> for property active</li>
   * </ul>
   * Usage sample:<br>
   * <code>
   * BeanQuery beanQuery=select(allOf(Book.class).except("authorList","authorMap"));
   * </code>
   */
  public static ClassSelector allOf(Class clazz) {
    return new ClassSelector(clazz);
  }

  /**
   * Create a PropertySelector with the property name. Code sample below:<br>
   * <code>
   * BeanQuery beanQuery=select(property("name"), property("price"), property("price").as("p"));
   * </code>
   */
  public static PropertySelector property(String property) {
    return new PropertySelector(property);
  }

  /**
   * Create a matcher to apply on the property of the from items.Code sample
   * below:<br>
   * <code>
   * BeanQuery beanQuery=select(allOf(Book.class)).from(bookList).where(value("name",startsWith("Book1")));
   * </code>
   */
  public static BeanPropertyMatcher value(String property, Matcher<?> matcher) {
    return new BeanPropertyMatcher(property, matcher);
  }

}
