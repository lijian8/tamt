package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ClearZoneOverlaysEvent extends GwtEvent<ClearZoneOverlaysEventHandler> {
  
  public static Type<ClearZoneOverlaysEventHandler> TYPE = new Type<ClearZoneOverlaysEventHandler>();
  
  public ClearZoneOverlaysEvent() {
  }

@Override
  public Type<ClearZoneOverlaysEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ClearZoneOverlaysEventHandler handler) {
    handler.onClearZoneOverlays(this);
  }
}
