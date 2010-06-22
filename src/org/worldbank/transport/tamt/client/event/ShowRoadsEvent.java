package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowRoadsEvent extends GwtEvent<ShowRoadsEventHandler> {
  
  public static Type<ShowRoadsEventHandler> TYPE = new Type<ShowRoadsEventHandler>();
  
  public ShowRoadsEvent() {
  }

@Override
  public Type<ShowRoadsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ShowRoadsEventHandler handler) {
    handler.onShowRoads(this);
  }
}
