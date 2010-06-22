package org.worldbank.transport.tamt.client.common;

import java.util.ArrayList;
import java.util.List;

import org.worldbank.transport.tamt.shared.TagDetails;

public class TagColumnDefinitionsFactory<T> {
  public static List<ColumnDefinition<TagDetails>> 
      getTagColumnDefinitions() {
    return TagColumnDefinitionsImpl.getInstance();
  }

  public static List<ColumnDefinition<TagDetails>>
      getTestTagColumnDefinitions() {
    return new ArrayList<ColumnDefinition<TagDetails>>();
  }
}
