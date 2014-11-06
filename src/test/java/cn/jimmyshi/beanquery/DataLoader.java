package cn.jimmyshi.beanquery;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Assert;

import cn.jimmyshi.beanquery.example.Book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DataLoader {
  private static ObjectMapper mapper = new ObjectMapper();
  static {
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public List<Book> loadSourceData(String fileName) {
    final String fullFileName = "jsonData/source/" + fileName;
    try {
      return mapper.readValue(ClassLoader.getSystemResource(fullFileName), new TypeReference<List<Book>>() {
      });
    } catch (Exception e) {
      final String errorMessage = String.format("Exception while loading file [%s] as a list of Book", fullFileName);
      throw new IllegalStateException(errorMessage, e);
    }
  }

  public List<Book> loadMainSourcetData() {
    return loadSourceData("main.json");
  }

  public void assertDataToJsonEqualsExpectedFileContent(String fileName, Object data) {
    final String fullFileName = "jsonData/expected/" + fileName;
    try {
      Object fileContentAsListOfMap = mapper.readValue(ClassLoader.getSystemResource(fullFileName), List.class);
      // transfer to list of map to avoid assert failure caused by class field order
      Object dataValueAsListOfMap = mapper.readValue(mapper.writer().writeValueAsString(data), List.class);
      Assert.assertEquals(fileContentAsListOfMap, dataValueAsListOfMap);
    } catch (Exception ex) {
      throw new IllegalStateException(String.format("Exception while comparing file [%s] with object [%s]", fullFileName,
          data), ex);
    }
  }

}
