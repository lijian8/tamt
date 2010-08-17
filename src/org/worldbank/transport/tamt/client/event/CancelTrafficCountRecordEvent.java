package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelTrafficCountRecordEvent extends GwtEvent<CancelTrafficCountRecordEventHandler> {
  
  public static Type<CancelTrafficCountRecordEventHandler> TYPE = new Type<CancelTrafficCountRecordEventHandler>();
  
  public CancelTrafficCountRecordEvent() {
  }

@Override
  public Type<CancelTrafficCountRecordEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CancelTrafficCountRecordEventHandler handler) {
	 handler.onCancelTrafficCountRecord(this);
  }
}
