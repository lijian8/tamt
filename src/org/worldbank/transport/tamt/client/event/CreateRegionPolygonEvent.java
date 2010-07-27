package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CreateRegionPolygonEvent extends GwtEvent<CreateRegionPolygonEventHandler> {
  
  public static Type<CreateRegionPolygonEventHandler> TYPE = new Type<CreateRegionPolygonEventHandler>();
  
  public CreateRegionPolygonEvent() {
  }

@Override
  public Type<CreateRegionPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CreateRegionPolygonEventHandler handler) {
    handler.onCreateRegionPolygon(this);
  }
}
