package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MatchingPointsBusyEvent extends GwtEvent<MatchingPointsBusyEventHandler> {
  
  public static Type<MatchingPointsBusyEventHandler> TYPE = new Type<MatchingPointsBusyEventHandler>();
  
  public MatchingPointsBusyEvent() {
  }

@Override
  public Type<MatchingPointsBusyEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MatchingPointsBusyEventHandler handler) {
    handler.onBusy(this);
  }
}
