package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelRegionEvent extends GwtEvent<CancelRegionEventHandler> {
  
  public static Type<CancelRegionEventHandler> TYPE = new Type<CancelRegionEventHandler>();
  
  public String id;
  public CancelRegionEvent(String id) {
	  this.id = id;
  }

@Override
  public Type<CancelRegionEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CancelRegionEventHandler handler) {
    handler.onCancelRegion(this);
  }
}
