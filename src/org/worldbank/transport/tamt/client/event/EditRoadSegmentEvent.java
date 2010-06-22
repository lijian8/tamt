package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditRoadSegmentEvent extends GwtEvent<EditRoadSegmentEventHandler> {
  
  public static Type<EditRoadSegmentEventHandler> TYPE = new Type<EditRoadSegmentEventHandler>();
  
  private String id;
  
  public EditRoadSegmentEvent(String id) {
	  this.setId(id);
  }

@Override
  public Type<EditRoadSegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRoadSegmentEventHandler handler) {
    handler.onEditRoadSegment(this);
  }

public void setId(String id) {
	this.id = id;
}

public String getId() {
	return id;
}
}
