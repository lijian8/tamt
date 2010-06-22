package org.worldbank.transport.tamt.client.common;

import java.util.ArrayList;
import java.util.Iterator;

import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.TagDetails;

@SuppressWarnings("serial")
public class TagColumnDefinitionsImpl extends 
    ArrayList<ColumnDefinition<TagDetails>> {
  
  private static TagColumnDefinitionsImpl instance = null;
  
  public static TagColumnDefinitionsImpl getInstance() {
    if (instance == null) {
      instance = new TagColumnDefinitionsImpl();
    }
    
    return instance;
  }
  
  protected TagColumnDefinitionsImpl() {
    this.add(new ColumnDefinition<TagDetails>() {
      public void render(TagDetails c, StringBuilder sb) {
        sb.append("<input type='checkbox'/>");
      }

      public boolean isSelectable() {
        return true;
      }
    });

    this.add(new ColumnDefinition<TagDetails>() {
      public void render(TagDetails tagDetails, StringBuilder sb) {   
    	  /*
    	  ArrayList<Vertex> coords = tagDetails.getCoords();
    	  String lineString = "";
    	  if (coords != null)
    	  {
    		  for (Iterator iterator = coords.iterator(); iterator.hasNext();) {
				Vertex coordinatePair = (Vertex) iterator
						.next();
				lineString += coordinatePair.getLat() + " " + coordinatePair.getLng() + ",";
			}
    	  }
    	  sb.append("<div id='" + tagDetails.getName() + "'>" + tagDetails.getName() + lineString + "</div>");
      		*/
      }

      public boolean isClickable() {
        return true;
      }
    });
  }
}
