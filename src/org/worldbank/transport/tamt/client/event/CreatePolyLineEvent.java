package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CreatePolyLineEvent extends GwtEvent<CreatePolyLineEventHandler> {
  
  public static Type<CreatePolyLineEventHandler> TYPE = new Type<CreatePolyLineEventHandler>();
  
  public CreatePolyLineEvent() {
  }

@Override
  public Type<CreatePolyLineEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CreatePolyLineEventHandler handler) {
    handler.onCreatePolyLine(this);
  }
}
