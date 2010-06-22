package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelZoneEvent extends GwtEvent<CancelZoneEventHandler> {
  
  public static Type<CancelZoneEventHandler> TYPE = new Type<CancelZoneEventHandler>();
  
  public String id;
  public CancelZoneEvent(String id) {
	  this.id = id;
  }

@Override
  public Type<CancelZoneEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CancelZoneEventHandler handler) {
    handler.onCancelZone(this);
  }
}
