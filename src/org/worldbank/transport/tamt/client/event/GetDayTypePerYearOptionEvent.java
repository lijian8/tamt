package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetDayTypePerYearOptionEvent extends GwtEvent<GetDayTypePerYearOptionEventHandler> {
  
  public static Type<GetDayTypePerYearOptionEventHandler> TYPE = new Type<GetDayTypePerYearOptionEventHandler>();
  
  public GetDayTypePerYearOptionEvent() {
  }

@Override
  public Type<GetDayTypePerYearOptionEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetDayTypePerYearOptionEventHandler handler) {
	 handler.onGetDayTypePerYearOption(this);
  }
}
