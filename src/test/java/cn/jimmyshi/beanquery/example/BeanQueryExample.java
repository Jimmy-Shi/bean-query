package cn.jimmyshi.beanquery.example;

import static cn.jimmyshi.beanquery.BeanQuery.*;
import static org.junit.Assert.assertThat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import cn.jimmyshi.beanquery.DataLoader;

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
  public void shouldReturnFieldsOnly() {
    List<Map<String, Object>> result = select("name,price,price as p, mainAuthor").from(mainData).execute();
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
  public void shouldInPriceDescOrder() {
    List<Map<String, Object>> result = select("name,price,mainAuthor")
        .from(dataLoader.loadSourceData("forOrders.json")).orderBy("price").desc().execute();
    dataLoader.assertDataToJsonEqualsExpectedFileContent("shouldInPriceDescOrder.json", result);
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
}