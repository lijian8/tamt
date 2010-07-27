package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowRegionsEvent extends GwtEvent<ShowRegionsEventHandler> {
  
  public static Type<ShowRegionsEventHandler> TYPE = new Type<ShowRegionsEventHandler>();
  
  public ShowRegionsEvent() {
  }

@Override
  public Type<ShowRegionsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ShowRegionsEventHandler handler) {
    handler.onShowRegions(this);
  }
}
