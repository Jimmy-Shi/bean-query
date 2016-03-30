使用说明
===

# 目录
1. [用法](#usage)  
1.1. [使用Bean Query](#using-bean-query)  
1.2. [排序](#sorting)  
1.3. [过滤](#filtering)  
1.4. [转换](#converting)   
2. [概念](#concepts)  
2.1. [Hamcrest matchers](#hamcrest-matchers)  
2.2. [BeanUtils](#beanutils)  

# <a name="usage"/>用法
Bean Query 复用[Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils/), [Apache Commons Collections](http://commons.apache.org/proper/commons-collections/), [Java Hamcrest](http://hamcrest.org/JavaHamcrest/) 
来简化对Bean(集合)的[排序](#sorting), [过滤](#filtering)和 [转换](#converting)。

* 第一节 [使用Bean Query](#using-bean-query) 引导你了解BeanQuery所有重要的方法。 
* [排序](#sorting), [过滤](#filtering)和 [转换](#converting) 这三节根据使用场景分类介绍用法。
* 如果你喜欢基于测试用例的使用介绍,[BeanQueryExample.java](.././src/test/java/cn/jimmyshi/beanquery/example/BeanQueryExample.java)用Junit测试用例的方式介绍了BeanQuery的使用方式。

## <a name="using-bean-query"/>使用Bean Query
要使用BeanQuery，你需要知道如何在项目中启用BeanQuery， 如何创建BeanQuery实例，如何设置过滤条件，如何设置`Comparator`用于排序以及如何执行BeanQuery。这一节引导你遍历一下BeanQuery库中所有重要的方法。

* 1. 添加BeanQuery到你的项目中

```xml
<dependency>
  <groupId>cn.jimmyshi</groupId>
  <artifactId>bean-query</artifactId>
  <version>${Bean-Query-Version}</version>
</dependency>
```

* 2. 静态导入BeanQuery

```java
import static cn.jimmyshi.beanquery.BeanQuery.*;
```

* 3. 使用下面方法中的一个来创建[`BeanQuery<T>`](.././src/main/java/cn/jimmyshi/beanquery/BeanQuery.java)实例

```java
public static BeanQuery<Map<String, Object>> select(KeyValueMapSelector... selectors);
public static BeanQuery<Map<String, Object>> select(String selectString);
public static BeanQuery<Map<String, Object>> select(String... propertyStrings);
public static <T> BeanQuery<T> select(Class<T> beanClass);
public static <T> BeanQuery<T> select(Selector<T> selector);
```

* 4. 使用下面的方法设置过滤条件（可选）:

```java
public BeanQuery<T> where(Matcher matcher);
public BeanQuery<T> where(Matcher... matchers);
```

* 5. 使用下面的方法来设置comparators（可选）以对过滤完的结果进行排序:

```java
public BeanQuery<T> orderBy(Comparator beanComparator);
public BeanQuery<T> orderBy(String orderByProperty);
public BeanQuery<T> orderBy(String orderByProperty, Comparator propertyValueComparator);
public BeanQuery<T> desc();
public BeanQuery<T> asc();
```

* 6. 执行BeanQuery

```java
public BeanQuery<T> from(Collection<?> from);
public BeanQuery<T> from(Object bean);
public List<T> execute();
public List<T> executeFrom(Collection<?> from);
public T executeFrom(Object bean);
``` 

* 7. 把上面提到的这些方法组合到一起

```java
//针对Bean集合
List<Map<String,Object>> result=select("name,price").from(bookCollection).where(value("name",startsWith("abc"))).execute();
List<Map<String,Object>> result=select("name,price").where(value("name",startsWith("abc"))).executeFrom(bookCollection);
//针对一个Bean
List<Map<String,Object>> result=select("name,price").from(book).where(value("name",startsWith("abc"))).execute();
Map<String,Object> result=select("name,price").where(value("name",startsWith("abc"))).executeFrom(book);
```

## <a name="sorting"/>排序
Bean Query使得Java Bean集合的排序变得简单。当前支持三种排序方式。
### 使用应用在Bean上的Comparator来排序
```java
List<Book> sortedResult=select(Book.class).orderBy(yourComparator).executeFrom(bookCollection);
```
上面的代码使用`yourComparator`对`bookCollection`进行排序。它和下面的这些代码有同样的效果：
```java
List<Book> copied = new ArrayList<Book>(bookCollection);
Collections.sort(copied, yourComparator);
sortedResult=copied;
```
### 使用应用在Bean属性上的Comparator来排序
```java
List<Book> sortedResult=select(Book.class).orderBy("author.name",beanPropertyComparator).executeFrom(bookCollection);
```
上面的代码针对`beanPropertyComparator`Bean集合中的每个Bean的`author.name`属性值来进行排序。它和下面的这些代码有同样的效果：
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

字符串 "author.name" 是一个用于从Bean中获取属性值的属性名，阅读章节[BeanUtils属性名](#beanutils-property-name) 以获取更多信息。
### 使用Bean的Comparable的属性来排序
```java
List<Book> sortedResult=select(Book.class).orderBy("author.name").executeFrom(bookCollection);
```
假设`bookCollection`中的每个Bean有一个实现了`Comparable`的名称为`author.name`的属性，上面的代码针对每个Bean的`author.name`调用他们的`compareTo`方法来比较从而进行排序
这个功能其实就是使用一个[`ComparableObjectComparator`](.././src/main/java/cn/jimmyshi/beanquery/comparators/ComparableObjectComparator.java) 应用在Bean的属性值上进行排序。`ComparableObjectComparator`类的`compare`方法的执行逻辑如下所示：

1. 首先把输入的参数转换为`Comparable`实例（comparable1，comparable2）。 如果输入参数是null或者不是`Comparable`的实现类，则转换结果视为null。
 * 如果转换结果都为null，则返回0
 * 如果comparable1是null，而comparable2不是null，则返回-1
 * 如果comparable1不是null，而comparable2是null，则返回1
2. 然后返回comparable1.compareTo(comparable2)
3. 如果在上面的步骤中有异常发生，则返回0。

### 使用多个Comparator

```java
List<Book> sortedResult=select(Book.class).orderBy(orderByProperty("author.name").desc(), orderByProperty("bookName"),yourOwnComparator).executeFrom(bookCollection);
```
上面的代码先使用默认`author.name`进行倒序比较，如果比较结果是一致的话，再使用`bookName`进行正序比较，如果还一致的话，就使用自定义的`yourOwnComparator`进行比较。

### 正序和逆序
在上面所述的这些排序中，默认情况下都视为正序，你可以使用`desc()`方法来逆转顺序。例子代码如下所示：
```java
List<Book> sortResult=select(Book.class).orderBy("author.name",beanPropertyComparator).desc().executeFrom(bookCollection);
```
上面的代码是下面代码的简化：
```java
List copied = new ArrayList(bookCollection);
Collections.sort(copied, new Comparator() {
  @Override
  public int compare(Object o1, Object o2) {
    return 0-beanPropertyComparator.compare(getProperty(o1), getProperty(o2));//逆转比较结果
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
`desc()`方法也可以用于其他的排序方式，如下所示：
```java
List<Book> sortResult=select(Book.class).orderBy(yourComparator).desc().executeFrom(bookCollection);
List<Book> sortResult=select(Book.class).orderBy("author.name").desc().executeFrom(bookCollection);
```
我们还提供了一个`asc()`方法用于抵消`desc()`方法，因此你可以使用不同的顺序来对不用的Bean集合进行排序。示例代码如下：
```java
BeanQuery query=select(Book.class).orderBy(yourComparator).desc();
List<Book> result1=query.executeFrom(bookCollection1);
List<Book> result2=query.asc().executeFrom(bookCollection2);
```
## <a name="filtering"/>过滤
过滤是这样实现的：
 
1. 首先把Hamcrest Matchers 转换成 [commons-collections Predicate](http://commons.apache.org/proper/commons-collections/javadocs/api-release/org/apache/commons/collections4/Predicate.html),
2. 调用[`CollectionUtils.filter`](http://commons.apache.org/proper/commons-collections/javadocs/api-release/org/apache/commons/collections4/CollectionUtils.html#filter(java.lang.Iterable, org.apache.commons.collections4.Predicate)) 方法来过滤。
  
更多信息请阅读[Hamcrest matchers](#hamcrest-matchers) 。

Bean Query允许用户使用Hamcrest Matchers来对Bean集合进行过滤。Hamcrest Matchers可以应用在Bean本身也可以应用是Bean属性上。
```java
List<Book> result=select(Book.class)
                  .where(
                      //for books name is Book2 or starts with Book1
                      anyOf(value("name", startsWith("Book1")), value("name", is("Book2"))),
                      //for books price between (53,65)
                      allOf(value("price", greaterThan(53d)),value("price",lessThan(65d)))
                  )
                  .executeFrom(bookCollection);
```
执行完上面的代码之后，集合`result`中的book都是`name`属性都是以`Book1`开头，或者是等于`Book2`，并且其`price`属性的值界于53和65中间。


* 静态方法`anyOf`, `startsWith`, `is`, `greaterThan`和`lessThan`都是Hamcrest matcher自带的方法。
* 静态方法`BeanPropertyMatcher value(String property, Matcher<?> matcher)`是Bean Query实现的用于把Hamcrest matchers应用到Bean属性上。
* 字符串"name"和"price"都是用于获取Bean属性值的属性名，参考[BeanUtils属性名](#beanutils-property-name)以获取更多信息。

## <a name="converting"/>转换
Bean Query可以用于把排序/过滤过的Bean（集合）转换成一种其他格式的Bean（集合）。当前提供了两种转换方式。

### 转换成特定的Java类型
```java
List<Book> sortResult=select(Book.class).executeFrom(bookCollection);
```
上面的代码把bookCollection转换成一个子项为`Book`类型的列表。对于bookCollection中非`Book`及其子类的对象，其转换结果为null。

如果你不想改变子项的类型，只是对对象集合进行过滤、排序的话，那么你可以使用`Object.class`来创建BeanQuery实例。代码如下所示:
```java
BeanQuery<Object> beanQuery=select(Object.class);
```
### 转换成Map对象
使用转换成Map对象这个功能，你可以抽取对象（集合中）对象的（嵌套的）属性形成Map对象（列表）。
```java
List<Map<String,Object>> result=select("price,name,mainAuthor.name as mainAuthorName").from(bookCollection).execute();
``` 
执行完上述代码之后，`result`列表中的每个Map都是`java.util.LinkedHashMap`实例，每个Map的都由下面的这三个Entry组成：

* key=price, value=book.getPrice()
* key=name, value=book.getName()
* key=mainAuthorName, value=book.getMainAuthor().getName()

把一个对象转换成一个Map的代码如下所示：
```java
Map<String,Object> result=select("price,name,mainAuthor.name as mainAuthorName").executeFrom(book);
```
#### 基于Java类信息把对象转换成Map
```java
List<Map<String, Object>> result = select(allOf(Book.class)).from(bookCollection).execute();
```
假设除了`getClass()`方法之外，类`Book`还有三个`public`的读方法：`public String getName()`,`public Double getPrice()`和`public Author getMainAuthor()`。在执行完上面的这块代码之后，`result`列表中的每个Map都是`java.util.LinkedHashMap`实例，每个Map的都由下面的这三个Entry组成：

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=mainAuthor, value=book.getMainAuthor()

如果你需要在结果Map中排除`price`和`name`，可以在创建BeanQuery实例时候使用`except`方法来排除之，例子代码如下：
```java
List<Map<String, Object>> result = select(allOf(Book.class).except("name","price")).from(bookCollection).execute();
``` 
你也可以使用`add`方法来添加其他的属性，例子代码如下：
```java
List<Map<String, Object>> result = select(allOf(Book.class).add("paperPrice","digitalPrice")).from(bookCollection).execute();
```
**`except`和`add`方法是设计来用于这种场景的：用于构建BeanQuery的对象类型和用于执行的对象集合中的对象类型不是同一个类型。**

#### 使用属性和别名把对象转换成Map
```java
List<Map<String, Object>> result = select("name","price","price as p", "mainAuthor").executeFrom(bookCollection);
List<Map<String, Object>> result = select("name,price,price as p, mainAuthor").executeFrom(bookCollection);
List<Map<String, Object>> result = select(property("name"), property("price"), property("price").as("p"),property("mainAuthor")).executeFrom(bookCollection);
```
上面的三行代码都是一样的效果，执行完其中的任意一行之后,`result`列表中的每个Map都是`java.util.LinkedHashMap`实例，每个Map的都由下面的这四个Entry组成：

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=p, value=book.getPrice()
* key=mainAuthor, value=book.getMainAuthor()


字符串"name"，"price"和"mainAuthor"是用于获取Java Bean的属性名称，请阅读[BeanUtils属性名](#beanutils-property-name)以获取更多信息。

字符"as"是用于隔离属性名称和别名的，必须是全小写的。

#### 具有同样前缀的属性或者别名合成在一个Map中
```java
List<Map<String,Object>> result= select("price,name,mainAuthor.name as author.name, mainAuthor.address as author.address").executeFrom(bookCollection);
```
上面这行代码执行完之后`result` 列表中的每个Map都由下面的这四个Entry组成：

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=author.name, value=book.getMainAuthor().getName()
* key=author.address, value=book.getMainAuthor().getAddress()

如果我们想把上面的`author.name`和`author.aaddress`放到同一个Map中，我们可以调用`nested()`方法，如下所示：

```java
List<Map<String,Object>> result= select("price,name,mainAuthor.name as author.name, mainAuthor.address as author.address").nested().executeFrom(bookCollection);
```
上面这行代码执行完之后`result` 列表中的每个Map都由下面的这三个Entry组成：

* key=name, value=book.getName()
* key=price, value=book.getPrice()
* key=author, value=一个Map,内容如下：
 * key=name, value=book.getMainAuthor().getName()
 * key=address, value=book.getMainAuthor().getAddress()

#### 混合使用Selector把对象转换成Map
可以混合使用基于Java类型的Selector和属性别名的Selector，比如：
```java
List<Map<String, Object>> result = select(allOf(Book.class).except("authorList","authorMap","mainAuthor"),
                                          property("price").as("p"),
                                          property("mainAuthor")
                                         ).from(bookCollection).execute();
```
### 自定义转换
通过实现接口[`Selector<T>`](.././src/main/java/cn/jimmyshi/beanquery/Selector.java)的一个子类并使用`public static <T> BeanQuery<T> select(Selector<T> selector)`方法来创建BeanQuery实例，就可以实现自定义转换。 例子代码如下:

```java
List<String> bookNames=select(new DefaultSelector<String>() {
      @Override
      public String select(Object item) {
        return ((Book)item).getName();
      }
    }).executeFrom(bookCollection);
```

上面代码中的[`DefaultSelector<T>`](.././src/main/java/cn/jimmyshi/beanquery/selectors/DefaultSelector.java)是`Selector<T>`的一个默认实现。

# <a name="concepts"/>概念
## Hamcrest matchers
Hamcrest是一个用于编写matcher对象来声明式的定义匹配规则的框架。

* 教程地址：https://code.google.com/p/hamcrest/wiki/Tutorial
* 代码仓库: https://github.com/hamcrest/JavaHamcrest

由于`BeanQuery<T>`继承了`org.hamcrest.Matchers`，当你静态导入BeanQuery之后，所有Hamcrest内置的matcher就都可以直接使用了。

### 一些比较有用的matcher
下面这些内容是拷贝之Hamcrest的教程的。

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
在这个BeanQuery库中，我们使用`BeanUtils`来获取Java Bean的属性值用于转换、比较和过滤。

* 官方网址：http://commons.apache.org/proper/commons-beanutils/
* 使用说明： http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#package_description

### <a name="beanutils-property-name"/>BeanUtils属性名
我们大量的使用了BeanUtils的[`PropertyUtils.getProperty(Object bean, String name)`](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/PropertyUtils.html#getProperty(java.lang.Object, java.lang.String)) 这个方法。使用这个方法，我们可以很方便在运行时的获取Java Bean基本的、嵌套的、有索引的、映射型的Java Bean的属性值。 

对于这个方法的官方指南是[2.2 Basic Property Access](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#standard.basic)和[2.3 Nested Property Access](http://commons.apache.org/proper/commons-beanutils/javadocs/v1.9.2/apidocs/org/apache/commons/beanutils/package-summary.html#standard.nested)这两章。

下面的例子代码也说明了针对不同的使用场景的属性名的格式是怎么样的。
* **获取基本属性的值**

```java
String authorName=(String)PropertyUtils.getProperty(book,"authorName");
//上面这行代码和下面这行代码是一样的
String authorName=book.getAuthorName();
```

* **获取嵌套属性的值**

```java
String authorName=(String)PropertyUtils.getProperty(book,"author.name");
//上面这行代码和下面这行代码是一样的
String authorName=book.getAuthor().getName();
```

* **获取有索引的属性的值**

```java
Author firstAuthor=(Author)PropertyUtils.getProperty(book,"authors[0]");
//上面这行代码和下面这行代码是一样的
Author firstAuthor=book.getAuthors().get(0);
```

* **获取Map中的Value**

```java
String postCode=(String)PropertyUtils.getProperty(book,"author.addresses(home).postCode");
//上面这行代码和下面这行代码是一样的
Author postCode=book.getAuthor().getAddresses().get("home").getPostCode();
```