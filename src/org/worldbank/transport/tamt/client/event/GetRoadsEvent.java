package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetRoadsEvent extends GwtEvent<GetRoadsEventHandler> {
  
  public static Type<GetRoadsEventHandler> TYPE = new Type<GetRoadsEventHandler>();
  
  public GetRoadsEvent() {
  }

@Override
  public Type<GetRoadsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetRoadsEventHandler handler) {
    handler.onGetRoads(this);
  }
}
