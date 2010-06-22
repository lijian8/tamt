package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DisableLineEditingEvent extends GwtEvent<DisableLineEditingEventHandler> {
  
  public static Type<DisableLineEditingEventHandler> TYPE = new Type<DisableLineEditingEventHandler>();
  
  public DisableLineEditingEvent() {
  }

@Override
  public Type<DisableLineEditingEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DisableLineEditingEventHandler handler) {
    handler.onDisableLineEditing(this);
  }
}
