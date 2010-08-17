package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.shared.TrafficCountRecord;

import com.google.gwt.event.shared.GwtEvent;

public class EditTrafficCountRecordEvent extends GwtEvent<EditTrafficCountRecordEventHandler> {
  
  public static Type<EditTrafficCountRecordEventHandler> TYPE = new Type<EditTrafficCountRecordEventHandler>();
  
  public TrafficCountRecord record;
  
  public EditTrafficCountRecordEvent(TrafficCountRecord record) {
	  this.record = record;
  }

@Override
  public Type<EditTrafficCountRecordEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditTrafficCountRecordEventHandler handler) {
    handler.onEditTrafficCountRecord(this);
  }

}
