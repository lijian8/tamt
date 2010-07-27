package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DisableRegionEditingEvent extends GwtEvent<DisableRegionEditingEventHandler> {
  
  public static Type<DisableRegionEditingEventHandler> TYPE = new Type<DisableRegionEditingEventHandler>();
  
  public DisableRegionEditingEvent() {
  }

@Override
  public Type<DisableRegionEditingEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DisableRegionEditingEventHandler handler) {
    handler.onDisableRegionEditing(this);
  }
}
