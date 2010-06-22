package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowZonesEvent extends GwtEvent<ShowZonesEventHandler> {
  
  public static Type<ShowZonesEventHandler> TYPE = new Type<ShowZonesEventHandler>();
  
  public ShowZonesEvent() {
  }

@Override
  public Type<ShowZonesEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ShowZonesEventHandler handler) {
    handler.onShowZones(this);
  }
}
