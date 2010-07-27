package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CheckApplicationBusyEvent extends GwtEvent<CheckApplicationBusyEventHandler> {
  
  public static Type<CheckApplicationBusyEventHandler> TYPE = new Type<CheckApplicationBusyEventHandler>();
  
  public CheckApplicationBusyEvent() {
  }

@Override
  public Type<CheckApplicationBusyEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CheckApplicationBusyEventHandler handler) {
    handler.onCheckApplicationBusy(this);
  }
}
