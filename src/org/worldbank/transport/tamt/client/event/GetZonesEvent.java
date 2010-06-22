package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetZonesEvent extends GwtEvent<GetZonesEventHandler> {
  
  public static Type<GetZonesEventHandler> TYPE = new Type<GetZonesEventHandler>();
  
  public GetZonesEvent() {
  }

@Override
  public Type<GetZonesEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetZonesEventHandler handler) {
    handler.onGetZones(this);
  }
}
