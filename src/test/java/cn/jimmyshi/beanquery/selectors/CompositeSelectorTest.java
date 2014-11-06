package cn.jimmyshi.beanquery.selectors;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.jimmyshi.beanquery.example.Book;

public class CompositeSelectorTest extends SelectorTestBase{

  @Test
  public void testConstructWithNull(){
    KeyValueMapSelector subSelector=null;
    CompositeSelector selector=new CompositeSelector(subSelector);
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testConstructWithNullArray(){
    CompositeSelector selector=new CompositeSelector((KeyValueMapSelector[])null);
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testConstructWithSelectorArray(){
    CompositeSelector selector=new CompositeSelector(new PropertySelector("abc"),new PropertySelector("edf"));
    assertSelectResultKeys(selector,"abc","edf");
  }

  @Test
  public void testDefaultConstructor(){
    CompositeSelector selector=new CompositeSelector();
    assertSelectResultKeySizeIsZero(selector);
  }



  @Test
  public void testConstructWithNullList(){
    List<KeyValueMapSelector> subSelectors=null;
    CompositeSelector selector=new CompositeSelector(subSelectors);
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testConstructWithEmptyList(){
    List<KeyValueMapSelector> subSelectors=Collections.emptyList();
    CompositeSelector selector=new CompositeSelector(subSelectors);
    assertSelectResultKeySizeIsZero(selector);
  }

  @Test
  public void testConstructWithSelectorList(){
    CompositeSelector selector=new CompositeSelector(Arrays.asList(new PropertySelector("abc"),null,new PropertySelector("edf")));
    assertSelectResultKeys(selector,"abc","edf");
  }

  @Test
  public void testAddSubSelector(){
    //given
    CompositeSelector selector=new CompositeSelector(new ClassSelector(Book.class));
    Map<String, Object> result = selector.select(new Book());
    int resultKeySizeBeforeAddSubSelector=result.size();
    assertThat(result,not(hasKey("abc")));
    //when
    selector.addSubSelector(new PropertySelector("abc"));
    //then
    result = selector.select(new Book());
    assertThat(result.keySet(),hasSize(resultKeySizeBeforeAddSubSelector+1));
    assertThat(result,hasKey("abc"));
  }

  @Test
  public void testAddNullSubSelector(){
  //given
    CompositeSelector selector=new CompositeSelector(new ClassSelector(Book.class));
    Map<String, Object> result = selector.select(new Book());
    int resultKeySizeBeforeAddSubSelector=result.size();

    //when
    selector.addSubSelector(null);
    //then
    result = selector.select(new Book());
    assertThat(result.keySet(),hasSize(resultKeySizeBeforeAddSubSelector));
  }
}
