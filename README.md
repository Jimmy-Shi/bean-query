Bean-query
==========

[Click Here](./README_en.md) for English version.

Bean Query 复用[Apache Commons BeanUtils](http://commons.apache.org/proper/commons-beanutils/), [Apache Commons Collections](http://commons.apache.org/proper/commons-collections/), [Java Hamcrest](http://hamcrest.org/JavaHamcrest/) 
来简化对Bean(集合)的排序,过滤和转换。

# 文档

* 阅读 [使用说明](./docs/user_guide_cn.md)来学习怎么使用
* [BeanQueryExample.java](./src/test/java/cn/jimmyshi/beanquery/example/BeanQueryExample.java)用Junit测试用例的方式展示用法。

# 快速入门

BeanQuery的使用非常简单也很直接，例子代码如下：
```java
//静态导入BeanQuery
import static cn.jimmyshi.beanquery.BeanQuery.*;


//使用 select、from、where、orderBy、desc和asc来组装一个Query，然后执行execute方法来获得结果。
List<Map<String, Object>> result = select("price,name,mainAuthor.name, authors[1].name as secondAuthor.name")
    .nested()
    .from(bookCollection)
    .where(
        //选择name属性值是"Book2"或者以“Book1”开头
        anyOf(
            value("name", startsWith("Book1")),
            value("name", is("Book2"))
        ),
        //并且prince的值位于区间(53,65)
        allOf(
            value("price", greaterThan(53d)),
            value("price",lessThan(65d))
        )
    )
    .orderBy("name").desc()//根据"name"属性按照倒序对结果进行排列
    .execute();
```
执行完以上代码后，`result`列表中的每个Map都是`java.util.LinkedHashMap`实例，每个Map的都由下面的这三个Entry组成：

* key=price, value=book.getPrice()
* key=name, value=book.getName()
* Key=mainAuthor, value=a map with { key=name, value=book.getMainAuthor().getName() }
* key=secondsAuthor, value=a map with { key=name,  value=book.getAuthors().get(1).getName() }

