package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class OpenWaitModelDialogEvent extends GwtEvent<OpenWaitModelDialogEventHandler> {
  
  public static Type<OpenWaitModelDialogEventHandler> TYPE = new Type<OpenWaitModelDialogEventHandler>();
  
  public String title;
  public String message;
  
  public OpenWaitModelDialogEvent(String title, String message) {
	this.title = title;
	this.message = message;
  }

@Override
  public Type<OpenWaitModelDialogEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(OpenWaitModelDialogEventHandler handler) {
    handler.onOpen(this);
  }
}
