package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FetchUpdatedPolylineEvent extends GwtEvent<FetchUpdatedPolylineEventHandler> {
  
  public static Type<FetchUpdatedPolylineEventHandler> TYPE = new Type<FetchUpdatedPolylineEventHandler>();
  
  public FetchUpdatedPolylineEvent() {
  }

@Override
  public Type<FetchUpdatedPolylineEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(FetchUpdatedPolylineEventHandler handler) {
    handler.onFetchUpdatedPolyline(this);
  }
}
