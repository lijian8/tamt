package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MatchingPointsCompletedEvent extends GwtEvent<MatchingPointsCompletedEventHandler> {
  
  public static Type<MatchingPointsCompletedEventHandler> TYPE = new Type<MatchingPointsCompletedEventHandler>();
  
  public MatchingPointsCompletedEvent() {
  }

@Override
  public Type<MatchingPointsCompletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(MatchingPointsCompletedEventHandler handler) {
    handler.onCompleted(this);
  }
}
