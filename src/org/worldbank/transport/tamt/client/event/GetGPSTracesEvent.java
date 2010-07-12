package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetGPSTracesEvent extends GwtEvent<GetGPSTracesEventHandler> {
  
  public static Type<GetGPSTracesEventHandler> TYPE = new Type<GetGPSTracesEventHandler>();
  
  public GetGPSTracesEvent() {
  }

  @Override
  public Type<GetGPSTracesEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetGPSTracesEventHandler handler) {
    handler.onGetGPSTraces(this);
  }
}
