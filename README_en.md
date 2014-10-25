Bean-query
==========

[点击这里](./README.md) 查看中文版.

BeanQuery is a Java lib used to transfer a collection of java bean to a list of map. You use it to query some properties of the beans, sort the result and query using conditions. Not only for the beans, it also working for the nested objects.

Usage of BeanQuery is simple, sample code below:
```java
//import the BeanQuery
import static cn.jimmyshi.beanquery.BeanQuery.*;


//use select/from/where/orderBy/desc/asc to composite a query and execute it to get the result
List<Map<String, Object>> result = select("price,name,mainAuthor.name as mainAuthorName")
    .from(bookCollection)
    .where(
        //for books name is Book2 or starts with Book1
        anyOf(
            value("name", startsWith("Book1")),
            value("name", is("Book2"))
        ),
        //for books price between (53,65)
        allOf(
            value("price", greaterThan(53d)),
            value("price",lessThan(65d))
        )
    )
    .orderBy("name").desc()
    .execute();
```
In this sample, the content of bookCollection as below(in json format)
```json
[
  {
    "price":55.55,
    "name":"Book1",
    "mainAuthor":{
      "name":"Book1-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518000"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  },
  {
    "price":52.55,
    "name":"Book12",
    "mainAuthor":{
      "name":"Book1-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518000"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  },
  {
    "price":53.55,
    "name":"Book13",
    "mainAuthor":{
      "name":"Book13-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518000"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  },
  {
    "price":60.0,
    "name":"Book14",
    "mainAuthor":{
      "name":"Book14-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518000"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  },
  {
    "price":50.55,
    "name":"Book15",
    "mainAuthor":{
      "name":"Book1-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518000"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  },
  {
    "price":77.77,
    "name":"Book3",
    "mainAuthor":{
      "name":"Book3-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518005"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  }
  ,
  {
    "price":66.66,
    "name":"Book2",
    "mainAuthor":{
      "name":"Book2-MainAuthor",
      "address":{
        "address":"Shenzhen Guangdong China",
        "postCode":"518005"
      },
      "birthDate":"1982-01-30T14:52:39"
    }
  }
]
```

After execution of the sample code, value of result as below(in json format)
```json
[
  {
    "price":60.0,
    "name":"Book14",
    "mainAuthorName":"Book14-MainAuthor"
  },
  {
    "price":53.55,
    "name":"Book13",
    "mainAuthorName":"Book13-MainAuthor"
  },
  {
    "price":55.55,
    "name":"Book1",
    "mainAuthorName":"Book1-MainAuthor"
  }
]
```



