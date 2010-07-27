package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditRegionDetailsBySegmentEvent extends GwtEvent<EditRegionDetailsBySegmentEventHandler> {
  
  public static Type<EditRegionDetailsBySegmentEventHandler> TYPE = new Type<EditRegionDetailsBySegmentEventHandler>();
  
  private String id;
  
  public EditRegionDetailsBySegmentEvent(String id) {
	  this.setId(id);
  }

@Override
  public Type<EditRegionDetailsBySegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRegionDetailsBySegmentEventHandler handler) {
    handler.onEditRegionDetailsBySegment(this);
  }

public void setId(String id) {
	this.id = id;
}

public String getId() {
	return id;
}
}
