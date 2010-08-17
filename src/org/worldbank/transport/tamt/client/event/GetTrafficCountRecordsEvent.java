package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetTrafficCountRecordsEvent extends GwtEvent<GetTrafficCountRecordsEventHandler> {
  
  public static Type<GetTrafficCountRecordsEventHandler> TYPE = new Type<GetTrafficCountRecordsEventHandler>();
  
  public GetTrafficCountRecordsEvent() {
  }

@Override
  public Type<GetTrafficCountRecordsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetTrafficCountRecordsEventHandler handler) {
	 handler.onGetTrafficCountRecords(this);
  }
}
