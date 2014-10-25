package cn.jimmyshi.beanquery.example;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Book {
  private List<Author> authorList;
  private Map<String, Author> authorMap;
  private Author mainAuthor;
  private String name;
  private double price;

  public List<Author> getAuthorList() {
    return authorList;
  }

  public Map<String, Author> getAuthorMap() {
    return authorMap;
  }

  public Author getMainAuthor() {
    return mainAuthor;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public void setAuthorList(List<Author> authorList) {
    this.authorList = authorList;
  }

  public void setAuthorMap(Map<String, Author> authorMap) {
    this.authorMap = authorMap;
  }

  public void setMainAuthor(Author mainAuthor) {
    this.mainAuthor = mainAuthor;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
