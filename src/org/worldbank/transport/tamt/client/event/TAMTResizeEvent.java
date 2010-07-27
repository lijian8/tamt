package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

public class TAMTResizeEvent extends GwtEvent<TAMTResizeEventHandler> {
  
  public static Type<TAMTResizeEventHandler> TYPE = new Type<TAMTResizeEventHandler>();
  
  public int height;
  public int width;
  public TAMTResizeEvent(int height, int width) {
	 this.height = height;
	 this.width = width;
  }

@Override
  public Type<TAMTResizeEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(TAMTResizeEventHandler handler) {
    handler.onTAMTResize(this);
  }
}
