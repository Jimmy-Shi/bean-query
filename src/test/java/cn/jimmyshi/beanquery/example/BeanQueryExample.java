package cn.jimmyshi.beanquery.example;

import static cn.jimmyshi.beanquery.BeanQuery.*;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.jimmyshi.beanquery.DataLoader;
import cn.jimmyshi.beanquery.selectors.DefaultSelector;

public class BeanQueryExample {
  private DataLoader dataLoader = new DataLoader();
  private List<Book> mainData;

  @Before
  public void setup() {
    mainData = dataLoader.loadMainSourcetData();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void shouldReturnAllFields() {
    List<Map<String, Object>> result = select(allOf(Book.class)).from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnAllFields.json", result);
    assertThat(result, hasSize(3));
    assertThat(result.get(0).keySet(), hasSize(5));
    assertThat(result.get(1).keySet(), hasSize(5));
    assertThat(result.get(2).keySet(), hasSize(5));
    assertThat((String) result.get(0).get("name"), allOf(notNullValue(), isA(String.class)));
    assertThat((Double) result.get(0).get("price"), allOf(notNullValue(), isA(Double.class)));
    assertThat((Author) result.get(0).get("mainAuthor"), allOf(notNullValue(), isA(Author.class)));
    assertThat((List<Author>) result.get(0).get("authorList"),
        allOf(notNullValue(), isA((Class<List<Author>>) (Class) List.class)));
    assertThat((Map<String, Author>) result.get(0).get("authorMap"), allOf(notNullValue(), isA(Map.class)));
  }

  @Test
  public void shouldBooksShortedByNameDesc(){
    List<Book> sortedBooks = select(Book.class).from(mainData).orderBy("name").desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldBooksShortedByName.json", sortedBooks);
  }

  @Test
  public void shouldExecuteFromMethodsWorking(){
    List<Book> sortedBooks = select(Book.class).orderBy("name").desc().executeFrom(mainData);
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldBooksShortedByName.json", sortedBooks);
  }

  @Test
  public void shouldExcecuteFromBeanMethodWorksWithWhereCondition(){
    Book book=new Book();
    book.setName("abc123");
    Map<String,Object> result=select("name,price").where(value("name",startsWith("abc"))).executeFrom(book);
    assertThat((String)result.get("name"),is("abc123"));
  }

  @Test
  public void shouldExecuteFromBeanMethodWorking(){
    Map<String,Object> executeFromResult=select("name,price").where(notNullValue()).executeFrom(new Object());
    assertThat(executeFromResult.keySet(),hasSize(2));
    assertThat(executeFromResult.keySet(),containsInAnyOrder("name","price"));
  }

  @Test
  public void shouldFromBeanMethodWorking(){
    List<Map<String,Object>> executeFromResult=select("name,price").where(notNullValue()).from(new Object()).execute();
    assertThat(executeFromResult, hasSize(1));
    assertThat(executeFromResult.get(0).keySet(),containsInAnyOrder("name","price"));
  }

  @Test
  public void shouldExecuteFromBeanMethodGetNullWhenNotFilteredResult(){
    Map<String,Object> executeFromResult=select("name,price").where(startsWith("abc")).executeFrom(new Object());
    assertThat(executeFromResult, nullValue());
  }

  @Test
  public void shouldOnlyBook1InResult(){
    List<Book> result=select(Book.class).from(mainData).where(value("name", is("Book1"))).execute();
    assertThat(result,hasSize(1));
    assertThat(result.get(0).getName(),is("Book1"));
  }

  @Test
  public void shouldReturnFieldsOnly() {
    List<Map<String, Object>> result = select("name,price,price as p, mainAuthor").from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnFieldsOnly.json", result);
  }

  @Test
  public void shouldWorkWithMixedSelectors(){
    List<Map<String, Object>> result = select(allOf(Book.class).except("authorList","authorMap","mainAuthor"),property("price").as("p"),property("mainAuthor")).from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnFieldsOnly.json", result);
  }

  @Test
  public void shouldReturnEmptyListWhenQueryFromEmptyList(){
    List<Map<String,Object>> result=select("abc").from(Collections.emptyList()).execute();
    assertThat(result, empty());
  }
  @Test
  public void shouldReturnFieldsOnlyWithAllOfAndExcept() {
    List<Map<String, Object>> result = select(allOf(Book.class).except("authorList","authorMap")).from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnFieldsOnlyWithAllOfAndExcept.json", result);
  }

  @Test
  public void shouldReturnFieldsOnlyWithMultiplePropertyStrings() {
    List<Map<String, Object>> result = select("name","price","price as p", "mainAuthor").from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnFieldsOnly.json", result);
  }

  @Test
  public void shouldReturnFieldsOnlyWithPropertys() {
    List<Map<String, Object>> result = select(property("name"), property("price"), property("price").as("p"),
        property("mainAuthor")).from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldReturnFieldsOnly.json", result);
  }

  @Test
  public void shouldNotChangeOrder() {
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldNotChangeOrder.json", result);
  }

  @Test
  public void shouldInPriceAscOrder() {
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy("price").asc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldInPriceAscOrder.json", result);

  }

  @Test
  public void shouldIncludeNullValueWhenSortingDesc(){
    List<Map<String,Object>> result=select("name").from(dataLoader.loadSourceData("forOrdersWithNullValue.json")).orderBy("name").desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldIncludeNullValueWhenSortingDesc.json", result);
  }

  @Test
  public void shouldIncludeNullValueWhenSortingAsc(){
    List<Map<String,Object>> result=select("name").from(dataLoader.loadSourceData("forOrdersWithNullValue.json")).orderBy("name").asc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldIncludeNullValueWhenSortingAsc.json", result);
  }

  @Test
  public void testSortingWithNotExistProperty(){
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy("notExistingProperty").execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldNotChangeOrder.json", result);
  }

  @Test
  public void shouldInPriceDescOrder() {
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy("price").desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldInPriceDescOrder.json", result);
  }

  @Test
  public void testOrderByBeanComparator(){
    Comparator<Book> priceBeanComparator = new Comparator<Book>() {
      @Override
      public int compare(Book o1, Book o2) {
        return Double.compare(o1.getPrice(), o2.getPrice());
      }
    };

    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy(priceBeanComparator).desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldInPriceDescOrder.json", result);
  }

  @Test
  public void testOrderByProvidedPropertyComparator(){
    Comparator<Double> doubleComparator=new Comparator<Double>() {

      @Override
      public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
      }
    };

    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy("price",doubleComparator).desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldInPriceDescOrder.json", result);
  }

  @Test
  public void testChainedOrderByProperties(){
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forChainedOrder.json")).orderBy(orderByProperty("price"),orderByProperty("name").desc()).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testChainedOrderByProperties.json", result);
  }

  @Test
  public void testChainedOrderByPropertiesDesc(){
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forChainedOrder.json")).orderBy(orderByProperty("price"),orderByProperty("name").desc()).desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testChainedOrderByPropertiesDesc.json", result);
  }

  @Test
  public void testSelectNullProperties() {
    List<Map<String, Object>> result = select("name,price,mainAuthor,authorList").from(
        dataLoader.loadSourceData("withoutAuthorMapAndList.json")).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testSelectNullProperties.json", result);
  }

  @Test
  public void testNotExistingProperties() {
    List<Map<String, Object>> result = select("a,b,mainAuthor").from(
        dataLoader.loadSourceData("withoutAuthorMapAndList.json")).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testNotExistingProperties.json", result);
  }

  @Test
  public void testSelectNestedProperties() {
    List<Map<String, Object>> result = select("name,price,mainAuthor.name as authorName").from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testSelectNestedProperties.json", result);
  }

  @Test
  public void testSelectListNestedProperties() {
    List<Map<String, Object>> result = select("name,price,authorList[0].name as firstAuthorName").from(mainData)
        .execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testSelectListNestedProperties.json", result);
  }

  @Test
  public void testSelectMapNestedProperties() {
    List<Map<String, Object>> result = select("name,price,authorMap(Book1-Author-1).address as book1AuthorAddress")
        .from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testSelectMapNestedProperties.json", result);
  }

  @Test
  public void testCustomizedSelector(){
    List<String> bookNames=select(new DefaultSelector<String>() {
      @Override
      public String select(Object item) {
        return ((Book)item).getName();
      }
    }).executeFrom(mainData);

    assertThat(bookNames,containsInAnyOrder("Book1","Book2","Book3"));
  }

  @Test
  public void testUsingMapHamcrestMatchers() {
    List<Map<String, Object>> result = select(allOf(Book.class)).from(mainData)
        .where(value("authorMap", hasKey("Book1-Author-1"))).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testUsingMapHamcrestMatchers.json", result);
  }

  @Test
  public void testUsingCollectionHamcestMatchers() {
    Author firstAuthorOfBook1AuthorList = mainData.get(0).getAuthorList().get(0);
    List<Map<String, Object>> result = select(allOf(Book.class)).from(mainData)
        .where(value("authorList", hasItem(firstAuthorOfBook1AuthorList))).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testUsingCollectionHamcestMatchers.json", result);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsingMultipleWhereConditions() {
    List<Map<String, Object>> result = select("price,name,mainAuthor.name as mainAuthorName")
        .from(dataLoader.loadSourceData("forMultipleWhereConditions.json"))
        .where(
            //for books name is Book2 or starts with Book1
            anyOf(value("name", startsWith("Book1")), value("name", is("Book2"))),
            //for books price between (53,65)
            allOf(value("price", greaterThan(53d)),value("price",lessThan(65d)))
            ).orderBy("name").desc()
            .execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testUsingMultipleWhereConditions.json", result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsingMapMatchersOnNormalProperty() {
    List<Map<String, Object>> result = select("price,name,mainAuthor.name as mainAuthorName")
        .from(dataLoader.loadSourceData("forMultipleWhereConditions.json"))
        .where(
            //for books name is Book2 or starts with Book1
            anyOf(value("name", startsWith("Book1")), value("name", is("Book2")),value("name",hasEntry("abc", "edf"))),
            //for books price between (53,65)
            allOf(value("price", greaterThan(53d)),value("price",lessThan(65d)))
            ).orderBy("name").desc()
            .execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testUsingMultipleWhereConditions.json", result);
  }

  @Test
  public void testNestedResultFeature() {
    List<Map<String, Object>> result = select(
        "name,price,authorMap(Book1-Author-1).address.address as author.address, authorMap(Book1-Author-1).address.postCode as author.postCode")
            .nested().from(mainData).execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("testNestedResultFeature.json", result);

  }
}
