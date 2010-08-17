package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddTrafficCountRecordEvent extends GwtEvent<AddTrafficCountRecordEventHandler> {
  
  public static Type<AddTrafficCountRecordEventHandler> TYPE = new Type<AddTrafficCountRecordEventHandler>();
  
  public AddTrafficCountRecordEvent() {
  }

@Override
  public Type<AddTrafficCountRecordEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddTrafficCountRecordEventHandler handler) {
	 handler.onAddTrafficCountRecord(this);
  }
}
