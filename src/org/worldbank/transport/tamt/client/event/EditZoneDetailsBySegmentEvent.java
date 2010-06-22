package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditZoneDetailsBySegmentEvent extends GwtEvent<EditZoneDetailsBySegmentEventHandler> {
  
  public static Type<EditZoneDetailsBySegmentEventHandler> TYPE = new Type<EditZoneDetailsBySegmentEventHandler>();
  
  private String id;
  
  public EditZoneDetailsBySegmentEvent(String id) {
	  this.setId(id);
  }

@Override
  public Type<EditZoneDetailsBySegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditZoneDetailsBySegmentEventHandler handler) {
    handler.onEditZoneDetailsBySegment(this);
  }

public void setId(String id) {
	this.id = id;
}

public String getId() {
	return id;
}
}
