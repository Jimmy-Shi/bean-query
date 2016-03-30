User Guide
===

# Index
1. [Usage](#usage)  
1.1. [Using Bean Query](#using-bean-query)  
1.2. [Sorting](#sorting)  
1.3. [Filtering](#filtering)  
1.4. [Converting](#converting)   
2. [Concepts](#concepts)  
2.1. [Hamcrest matchers](#hamcrest-matchers)  
2.2. [BeanUtils](#beanutils)  

# Usage
Bean Query reuses [Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils/), [Apache Commons Collections](http://commons.apache.org/proper/commons-collections/), [Java Hamcrest](http://hamcrest.org/JavaHamcrest/) to make [sorting](#sorting), [filtering](#filtering) and [converting](#converting) a (collection of) java bean(s) easily.

* The first section [Using Bean Query](#using-bean-query) guide you through all important methods. 
* Sections [sorting](#sorting), [filtering](#filtering) and [converting](#converting) classify usage according to different scenario.
* If you prefer test case based illustration, you can read test case suit [BeanQueryExample.java](.././src/test/java/cn/jimmyshi/beanquery/example/BeanQueryExample.java).

## Using Bean Query
In order to use BeanQuey, you need to know how to enable the BeanQuery project, how to create the BeanQuery instance, how to setup where condition, how to setup sorting `Comparator`, and how to execute the BeanQuery.
This section lead you to go through all important methods of this BeanQuery lib. 

* 1. Added BeanQuery to your project dependencies.

```xml
<dependency>
  <groupId>cn.jimmyshi</groupId>
  <artifactId>bean-query</artifactId>
  <version>${Bean-Query-Version}</version>
</dependency>
```

* 2. Static import BeanQuery

```java
import static cn.jimmyshi.beanquery.BeanQuery.*;
```

* 3. Create a [`BeanQuery<T>`](.././src/main/java/cn/jimmyshi/beanquery/BeanQuery.java) instance with one of the below methods.

```java
public static BeanQuery<Map<String, Object>> select(KeyValueMapSelector... selectors);
public static BeanQuery<Map<String, Object>> select(String selectString);
public static BeanQuery<Map<String, Object>> select(String... propertyStrings);
public static <T> BeanQuery<T> selectBean(Class<T> beanClass);
public static <T> BeanQuery<T> select(Selector<T> selector);
```

* 4. Setup filtering condition(optional) with below methods:

```java
public BeanQuery<T> where(Matcher matcher);
public BeanQuery<T> where(Matcher... matchers);
```

* 5. Setup comparators (optional) to sort the filtered result with below methods:

```java
public BeanQuery<T> orderBy(Comparator beanComparator);
public BeanQuery<T> orderBy(String orderByProperty);
public BeanQuery<T> orderBy(String orderByProperty, Comparator propertyValueComparator);
public BeanQuery<T> desc();
public BeanQuery<T> asc();
```

* 6. Execute the BeanQuery

```java
public BeanQuery<T> from(Collection<?> from);
public BeanQuery<T> from(Object bean);
public List<T> execute();
public List<T> executeFrom(Collection<?> from);
public T executeFrom(Object bean);
``` 

* 7. Composing all methods above together

```java
//on a collection of beans
List<Map<String,Object>> result=select("name,price").from(bookCollection).where(value("name",startsWith("abc"))).execute();
List<Map<String,Object>> result=select("name,price").where(value("name",startsWith("abc"))).executeFrom(bookCollection);
//on one bean
List<Map<String,Object>> result=select("name,price").from(book).where(value("name",startsWith("abc"))).execute();
Map<String,Object> result=select("name,price").where(value("name",startsWith("abc"))).executeFrom(book);
```

## Sorting

Bean Query allows to sort a collection of java beans easily. Currently 3 sorting types are supported.

### Sorting Beans with comparator applied on bean
```java
List<Book> sortedResult=selectBean(Book.class).orderBy(yourComparator).executeFrom(bookCollection);
```
Above codes sort `bookCollection` with `yourComparator`. It has the same effect as below code:
```java
List<Book> copied = new ArrayList<Book>(bookCollection);
Collections.sort(copied, yourComparator);
sortedResult=copied;
```
### Sorting Beans by comparator applied on bean property
```java
List<Book> sortedResult=selectBean(Book.class).orderBy("author.name",beanPropertyComparator).executeFrom(bookCollection);
```
Above codes sort `bookCollection` with `beanPropertyComparator` applied on items' `author.name` property value. They have the same effect as below code:
```java
List<Book> copied = new ArrayList<Book>(bookCollection);
Collections.sort(copied, new Comparator() {
  @Override
  public int compare(Object o1, Object o2) {
    return beanPropertyComparator.compare(getProperty(o1), getProperty(o2));
  }

  private Object getProperty(Object o) {
    try {
      return PropertyUtils.getProperty(o, "author.name");
    } catch (Exception ex) {
      return null;
    }
  }
});
sortedResult=copied;
```

The string object "author.name" is a property name to fetch property values from java beans, read section [BeanUtils property name](#beanutils-property-name) for more information.
### Sorting Beans by comparable bean property
```java
List<Book> sortedResult=selectBean(Book.class).orderBy("author.name").executeFrom(bookCollection);
```
Above codes assume that each item of the `bookCollection` has a comparable property `author.name`. For each `author.name` call their `compareTo` method to compare.
This function is implemented by providing a [`ComparableObjectComparator`](.././src/main/java/cn/jimmyshi/beanquery/comparators/ComparableObjectComparator.java) to compare the property value. The `compare` method of this `ComparableObjectComparator` works in the below way:

1. First convert the input objects to comparable instances(comparable1, comparable2). If they are null or not instance of comparable, the converted result is null. 
  * If both converted result are null, return 0. 
  * If comparable1 is null and comparable2 is not null, return -1 
  * If comparable1 is not null and comparable2 is null, return 1
2. Then return comparable1.compareTo(comparable2) 
3. If there is an exception in the above step, return 0 as the result 

### Multiple Comparators

```java
List<Book> sortedResult=select(Book.class).orderBy(orderByProperty("author.name").desc(), orderByProperty("bookName"),yourOwnComparator).executeFrom(bookCollection);
```
Code above compares items with their `author.name` property in DESC direction, if the result is equal, will compare with their `bookName` in ASC direction, and apply the `yourOwnComparator` comparator.


### DESC & ASC Sorting
For all sorting methods, the default sorting way is considered as `ASC` sorting, you can easily revert the sort method by calling `desc()` method. Sample code as below:
```java
List<Book> sortResult=selectBean(Book.class).orderBy("author.name",beanPropertyComparator).desc().executeFrom(bookCollection);
```
Above codes have the same effect as below code:
```java
List copied = new ArrayList(bookCollection);
Collections.sort(copied, new Comparator() {
  @Override
  public int compare(Object o1, Object o2) {
    return 0-beanPropertyComparator.compare(getProperty(o1), getProperty(o2));//to reverse the compare result
  }

  private Object getProperty(Object o) {
    try {
      return PropertyUtils.getProperty(o, "author.name");
    } catch (Exception ex) {
      return null;
    }
  }
});
sortedResult=copied;
```
You can apply the `desc()` method for other sorting methods like below:
```java
List<Book> sortResult=selectBean(Book.class).orderBy(yourComparator).desc().executeFrom(bookCollection);
List<Book> sortResult=selectBean(Book.class).orderBy("author.name").desc().executeFrom(bookCollection);
```
An `asc()` method is provided to rollback the calling of the `desc()` method so that you can sort different bean collections in different direction. Sample code as below:
```java
BeanQuery query=selectBean(Book.class).orderBy(yourComparator).desc();
List<Book> result1=query.executeFrom(bookCollection1);
List<Book> result2=query.asc().executeFrom(bookCollection2);
```
## Filtering
Filtering is implemented by
 
1. Converting Hamcrest Matchers to [commons-collections Predicate](http://commons.apache.org/proper/commons-collections/javadocs/api-release/org/apache/commons/collections4/Predicate.html),
2. Call [`CollectionUtils.filter`](http://commons.apache.org/proper/commons-collections/javadocs/api-release/org/apache/commons/collections4/CollectionUtils.html#filter(java.lang.Iterable, org.apache.commons.collections4.Predicate)) method
  
you can read section [Hamcrest matchers](#hamcrest-matchers) for more infomation.

Bean Query allows you to filter a collection of java beans with Hamcrest Matchers. The matchers can be applied on bean itself or on bean properties.
```java
List<Book> result=selectBean(Book.class)
                  .where(
                      //for books name is Book2 or starts with Book1
                      anyOf(value("name", startsWith("Book1")), value("name", is("Book2"))),
                      //for books price between (53,65)
                      allOf(value("price", greaterThan(53d)),value("price",lessThan(65d)))
                  )
                  .executeFrom(bookCollection);
```
After execution, object `result` contains books that their names started with "Book1" or is "Book2" and their prices between 53 & 65.

* Static methods `anyOf`, `startsWith`, `is`, `greaterThan` and `lessThan` are built-in Hamcrest matcher methods.
* Static method `BeanPropertyMatcher value(String property, Matcher<?> matcher)` is defined by Bean Query to allow Hamcrest matchers applied on bean properties.
* The string object "name" and "price" are a property name to fetch property value of a java bean, read section [BeanUtils property name](#beanutils-property-name) for more information.
 
## Converting
Bean Query allows to convert the sorted/filtered result into a (list of) other object(s). There are 2 predefined converting behavior.
### Converting to specified java type
```java
List<Book> sortResult=selectBean(Book.class).executeFrom(bookCollection);
```
Above codes convert the bookCollection into a list of `Book` instances. For items that are not instances of the `Book` class or its' subclass, the converted results are null.

If you don't want to change items types in the result, you just want to filter/sort a (collection of) java bean(s) that might be of different type of unknown types, you can create the BeanQuery instance using `Object.class` like below.
```java
BeanQuery<Object> beanQuery=selectBean(Object.class);
```
### Converting to Map
With the converting to map function, you can pick some (nested) properties of the (sorted/filtered result) bean(s) into a (list of) map(s).
```java
List<Map<String,Object>> result=select("price,name,mainAuthor.name as mainAuthorName").from(bookCollection).execute();
``` 
After executing above line, all items of the `result` object is a `LinkedHashMap` instance with 3 entries in below order:

* key=price, value=book.getPrice()
* key=name, value=book.getName()
* key=mainAuthorName, value=book.getMainAuthor().getName()

To execute on a single object, you should code like below:
```java
Map<String,Object> result=select("price,name,mainAuthor.name as mainAuthorName").executeFrom(book);
```
#### Converting to Map based on a java class
```java
List<Map<String, Object>> result = select(allOf(Book.class)).from(bookCollection).execute();
```
Assuming besides the `getClass()` method, the `Book` class has 3 public property read methods: `public String getName()`,`public Double getPrice()` and `public Author getMainAuthor()`,
After executing above codes, all items of the `result` object is a `LinkedHashMap` instance with 3 entries:

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=mainAuthor, value=book.getMainAuthor()

If you don't want to know the price and name, you don't have to included them in the result map items. It is very simple to `except` them when creating the BeanQuery instance. Sample code below:
```java
List<Map<String, Object>> result = select(allOf(Book.class).except("name","price")).from(bookCollection).execute();
``` 
You can also add more fields to the map items by using the `add` method. Sample code below:
```java
List<Map<String, Object>> result = select(allOf(Book.class).add("paperPrice","digitalPrice")).from(bookCollection).execute();
```
**The `except` and `add` methods are designed for scenario that the based class to construct the BeanQuery and items in the from collection are of different types.**

#### Converting to Map using property name and alias
```java
List<Map<String, Object>> result = select("name","price","price as p", "mainAuthor").executeFrom(bookCollection);
List<Map<String, Object>> result = select("name,price,price as p, mainAuthor").executeFrom(bookCollection);
List<Map<String, Object>> result = select(property("name"), property("price"), property("price").as("p"),property("mainAuthor")).executeFrom(bookCollection);
```
The above 3 lines of code has the same effect. After executing any line of them, all items of the `result` object is a `LinkedHashMap` instance with 4 entries:

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=p, value=book.getPrice()
* key=mainAuthor, value=book.getMainAuthor()

The string objects "name", "price", "mainAuthor" are property names to fetch property values of a java bean, read section [BeanUtils property name](#beanutils-property-name) for more information.

The token "as" is a separator between property name and alias, it is case sensitive.

#### Compose properties or alias with same prefix into a map

```java
List<Map<String,Object>> result= select("price,name,mainAuthor.name as author.name, mainAuthor.address as author.address").executeFrom(bookCollection);
```
After executing above line of code, all items of the `result` object is a `LinkedHashMap` instance with 4 entries:

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=author.name, value=book.getMainAuthor().getName()
* key=author.address, value=book.getMainAuthor().getAddress()

If we want to compose `author.name` and `author.address` into a map, we can call the nested() method, as shown below:

```java
List<Map<String,Object>> result= select("price,name,mainAuthor.name as author.name, mainAuthor.address as author.address").nested().executeFrom(bookCollection);
```

After executing above line of code, all items of the `result` object is a `LinkedHashMap` instance with 3 entries:

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=author, value=A LinkedHashMap with below items:
 * key=name, value=book.getMainAuthor().getName()
 * key=address, value=book.getMainAuthor().getAddress()

#### Converting to Map with mixed selectors
You can mix the java class based selector and property selector together. For example:
```java
List<Map<String, Object>> result = select(allOf(Book.class).except("authorList","authorMap","mainAuthor"),
                                          property("price").as("p"),
                                          property("mainAuthor")
                                         ).from(bookCollection).execute();
```
### Customize converting
By implementing a instance of interface [`Selector<T>`](.././src/main/java/cn/jimmyshi/beanquery/Selector.java) and create BeanQuery instance by calling the `public static <T> BeanQuery<T> select(Selector<T> selector)` method, You can customize the converting.
Sample code below:

```java
List<String> bookNames=select(new DefaultSelector<String>() {
      @Override
      public String select(Object item) {
        return ((Book)item).getName();
      }
    }).executeFrom(bookCollection);
```

The [`DefaultSelector<T>`](.././src/main/java/cn/jimmyshi/beanquery/selectors/DefaultSelector.java) in above code is a default implementation of `Selector<T>`.

# Concepts
## Hamcrest matchers
Hamcrest is a framework for writing matcher objects allowing 'match' rules to be defined declaratively. 

* Read [Java Hamcrest Tutorial](https://code.google.com/p/hamcrest/wiki/Tutorial) to learn it.
* Code repository of Hamcrest matchers: https://github.com/hamcrest/JavaHamcrest.

Since `BeanQuery<T>` is a subclass of the `org.hamcrest.Matchers` class, once you static import the `BeanQuery`, all the Hamcrest built-in matchers are available.

### Useful matchers
Below content of this section is copied from the Hamcrest tutorial.

Hamcrest comes with a library of useful matchers. Here are some of the most important ones.

* Core
  - anything - always matches, useful if you don't care what the object under test is
  - describedAs - decorator to adding custom failure description
  - is - decorator to improve readability - see "Sugar", below
* Logical
  - allOf - matches if all matchers match, short circuits (like Java &&)
  - anyOf - matches if any matchers match, short circuits (like Java ||)
  - not - matches if the wrapped matcher doesn't match and vice versa
* Object
  - equalTo - test object equality using Object.equals
  - hasToString - test Object.toString
  - instanceOf, isCompatibleType - test type
  - notNullValue, nullValue - test for null
  - sameInstance - test object identity
* Beans
  - hasProperty - test JavaBeans properties
* Collections
  - array - test an array's elements against an array of matchers
  - hasEntry, hasKey, hasValue - test a map contains an entry, key or value
  - hasItem, hasItems - test a collection contains elements
  - hasItemInArray - test an array contains an element
* Number
  - closeTo - test floating point values are close to a given value
  - greaterThan, greaterThanOrEqualTo, lessThan, lessThanOrEqualTo - test ordering
* Text
  - equalToIgnoringCase - test string equality ignoring case
  - equalToIgnoringWhiteSpace - test string equality ignoring differences in runs of whitespace
  - containsString, endsWith, startsWith - test string matching

## BeanUtils
In this BeanQuery lib, we use `BeanUtils` to fetch property value from bean to convert, compare, filter. 

* Official web site: http://commons.apache.org/proper/commons-beanutils/
* User Guide: http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#package_description

### BeanUtils property name
The feature we used a lot is the [`PropertyUtils.getProperty(Object bean, String name)`](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/PropertyUtils.html#getProperty(java.lang.Object, java.lang.String)) method. 
With this method, it is very easy to fetch simple/nested/indexed/mapped property value of a java bean at runtime. 

The official guide of this method is section [2.2 Basic Property Access](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#standard.basic) and [2.3 Nested Property Access](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#standard.nested).
Below sample codes illustrate how to use it in different scenarios. 

* **Get a simple property value**

```java
String authorName=(String)PropertyUtils.getProperty(book,"authorName");
//above line is the same with below code
String authorName=book.getAuthorName();
```

* **Get a nested property value**

```java
String authorName=(String)PropertyUtils.getProperty(book,"author.name");
//above line is the same with below code
String authorName=book.getAuthor().getName();
```

* **Get a indexed property value**

```java
Author firstAuthor=(Author)PropertyUtils.getProperty(book,"authors[0]");
//above line is the same with below code
Author firstAuthor=book.getAuthors().get(0);
```

* **Get a mapped property value**

```java
String postCode=(String)PropertyUtils.getProperty(book,"author.addresses(home).postCode");
//above line is the same with below code
Author postCode=book.getAuthor().getAddresses().get("home").getPostCode();
```