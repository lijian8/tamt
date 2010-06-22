package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowMapEvent extends GwtEvent<ShowMapEventHandler> {
  
  public static Type<ShowMapEventHandler> TYPE = new Type<ShowMapEventHandler>();
  
  public ShowMapEvent() {
  }

@Override
  public Type<ShowMapEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ShowMapEventHandler handler) {
    handler.onShowMap(this);
  }
}
