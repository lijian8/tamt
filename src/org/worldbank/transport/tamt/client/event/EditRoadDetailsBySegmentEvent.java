package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditRoadDetailsBySegmentEvent extends GwtEvent<EditRoadDetailsBySegmentEventHandler> {
  
  public static Type<EditRoadDetailsBySegmentEventHandler> TYPE = new Type<EditRoadDetailsBySegmentEventHandler>();
  
  private String id;
  
  public EditRoadDetailsBySegmentEvent(String id) {
	  this.setId(id);
  }

@Override
  public Type<EditRoadDetailsBySegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRoadDetailsBySegmentEventHandler handler) {
    handler.onEditRoadDetailsBySegment(this);
  }

public void setId(String id) {
	this.id = id;
}

public String getId() {
	return id;
}
}
