Bean-query
==========

[点击这里](./README.md) 查看中文版.

Bean Query reuses [Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils/), [Apache Commons Collections](http://commons.apache.org/proper/commons-collections/), [Java Hamcrest](http://hamcrest.org/JavaHamcrest/) to make sorting, filtering and converting a (collection of) java bean(s) easily.

# Documents

* Read [User Guide](./docs/user_guide.md) to study the usage.
* Read [BeanQueryExample.java](./src/test/java/cn/jimmyshi/beanquery/example/BeanQueryExample.java) for Junit test case based code illustration.

# Quick Start

Usage of BeanQuery is simple, sample code below:
```java
//import the BeanQuery
import static cn.jimmyshi.beanquery.BeanQuery.*;


//use select/from/where/orderBy/desc/asc to composite a query and execute it to get the result
List<Map<String, Object>> result = select("price,name,mainAuthor.name, authors[1].name as secondAuthor.name")
    .nested()
    .from(bookCollection)
    .where(
        //select books that name is Book2 or starts with Book1
        anyOf(
            value("name", startsWith("Book1")),
            value("name", is("Book2"))
        ),
        //and price is between (53,65)
        allOf(
            value("price", greaterThan(53d)),
            value("price",lessThan(65d))
        )
    )
    .orderBy("name").desc()//sort the result in desc direction according to the name property
    .execute();
```
After executing above codes, all items of the `result` object is a `java.util.LinkedHashMap` instance with 3 entries in below order:

* key=price, value=book.getPrice()
* key=name, value=book.getName()
* Key=mainAuthor, value=a map with { key=name, value=book.getMainAuthor().getName() }
* key=secondsAuthor, value=a map with { key=name,  value=book.getAuthors().get(1).getName() }

