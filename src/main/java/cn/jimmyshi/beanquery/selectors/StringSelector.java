package cn.jimmyshi.beanquery.selectors;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selector to support create property selector via String parameter.
 */
public class StringSelector extends KeyValueMapSelector {
  private transient Logger logger = LoggerFactory.getLogger(StringSelector.class);
  private CompositeSelector compositeSelector = new CompositeSelector();

  /**
   * Create selectors with String.
   *
   * @param selectString
   *          String in format "propertyName as alias[,propertyName as alias]"
   */
  public StringSelector(String selectString) {
    logger.info("Constructing StringSelector with String [{}]", selectString);
    if (StringUtils.isBlank(selectString)) {
      logger.info("Constructing StringSelector with blank selectString");
      return;
    }
    String[] propertyStrings = StringUtils.split(selectString, ',');
    initCompositeSelector(propertyStrings);
  }

  /**
   * Create selectors with property string
   *
   * @param propertyStrings
   *          Strings in format "propertyName[as alias]"
   */
  public StringSelector(String... propertyStrings) {
    logger.info("Construct StringSelector with propertyStrings [{}]", (Object) propertyStrings);
    initCompositeSelector(propertyStrings);
  }

  private void initCompositeSelector(String[] propertyStrings) {
    if (ArrayUtils.isEmpty(propertyStrings)) {
      logger.debug("Initing StringSelector with empty property String.");
      return;
    }

    for (String propertyString : propertyStrings) {
      if (StringUtils.isNotBlank(propertyString)) {
        final PropertySelector propertySelector = createPropertySelector(propertyString);
        compositeSelector.addSubSelector(propertySelector);
      }
    }
    logger.info("StringSelector [{}] initilized.", this);
  }

  private PropertySelector createPropertySelector(String propertyString) {
    String[] propertyTokens = StringUtils.splitByWholeSeparator(propertyString, " as ", 2);
    final PropertySelector propertySelector;
    String propertySelectorPropertyName = propertyTokens[0].trim();
    if (propertyTokens.length == 2) {
      String propertySelectorAlias = propertyTokens[1].trim();
      propertySelector = new PropertySelector(propertySelectorPropertyName, propertySelectorAlias);
    } else {
      propertySelector = new PropertySelector(propertySelectorPropertyName);
    }
    return propertySelector;
  }

  @Override
  protected Map<String, Object> doSelect(Object item) {
    return compositeSelector.select(item);
  }

}
