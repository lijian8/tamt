package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelRoadEvent extends GwtEvent<CancelRoadEventHandler> {
  
  public static Type<CancelRoadEventHandler> TYPE = new Type<CancelRoadEventHandler>();
  
  public String id;
  public CancelRoadEvent(String id) {
	  this.id = id;
  }

@Override
  public Type<CancelRoadEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CancelRoadEventHandler handler) {
    handler.onCancelRoad(this);
  }
}
