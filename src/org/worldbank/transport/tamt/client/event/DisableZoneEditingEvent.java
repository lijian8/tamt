package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DisableZoneEditingEvent extends GwtEvent<DisableZoneEditingEventHandler> {
  
  public static Type<DisableZoneEditingEventHandler> TYPE = new Type<DisableZoneEditingEventHandler>();
  
  public DisableZoneEditingEvent() {
  }

@Override
  public Type<DisableZoneEditingEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DisableZoneEditingEventHandler handler) {
    handler.onDisableZoneEditing(this);
  }
}
