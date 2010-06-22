package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ClearRoadOverlaysEvent extends GwtEvent<ClearRoadOverlaysEventHandler> {
  
  public static Type<ClearRoadOverlaysEventHandler> TYPE = new Type<ClearRoadOverlaysEventHandler>();
  
  public ClearRoadOverlaysEvent() {
  }

@Override
  public Type<ClearRoadOverlaysEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ClearRoadOverlaysEventHandler handler) {
    handler.onClearRoadOverlays(this);
  }
}
