package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DebugEvent extends GwtEvent<DebugEventHandler> {
  
  public static Type<DebugEventHandler> TYPE = new Type<DebugEventHandler>();
  
  public DebugEvent() {
  }

@Override
  public Type<DebugEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DebugEventHandler handler) {
    handler.onDebug(this);
  }
}
