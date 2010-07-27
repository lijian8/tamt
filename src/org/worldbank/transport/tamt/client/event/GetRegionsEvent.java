package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetRegionsEvent extends GwtEvent<GetRegionsEventHandler> {
  
  public static Type<GetRegionsEventHandler> TYPE = new Type<GetRegionsEventHandler>();
  
  public GetRegionsEvent() {
  }

@Override
  public Type<GetRegionsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetRegionsEventHandler handler) {
    handler.onGetRegions(this);
  }
}
