package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditZoneSegmentEvent extends GwtEvent<EditZoneSegmentEventHandler> {
  
  public static Type<EditZoneSegmentEventHandler> TYPE = new Type<EditZoneSegmentEventHandler>();
  
  private String id;
  
  public EditZoneSegmentEvent(String id) {
	  this.setId(id);
  }

@Override
  public Type<EditZoneSegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditZoneSegmentEventHandler handler) {
    handler.onEditZoneSegment(this);
  }

public void setId(String id) {
	this.id = id;
}

public String getId() {
	return id;
}
}
