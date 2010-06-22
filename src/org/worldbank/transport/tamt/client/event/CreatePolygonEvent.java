package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CreatePolygonEvent extends GwtEvent<CreatePolygonEventHandler> {
  
  public static Type<CreatePolygonEventHandler> TYPE = new Type<CreatePolygonEventHandler>();
  
  public CreatePolygonEvent() {
  }

@Override
  public Type<CreatePolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CreatePolygonEventHandler handler) {
    handler.onCreatePolygon(this);
  }
}
