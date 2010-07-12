package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CloseWaitModelDialogEvent extends GwtEvent<CloseWaitModelDialogEventHandler> {
  
  public static Type<CloseWaitModelDialogEventHandler> TYPE = new Type<CloseWaitModelDialogEventHandler>();
  
  public CloseWaitModelDialogEvent() {
	
  }

@Override
  public Type<CloseWaitModelDialogEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CloseWaitModelDialogEventHandler handler) {
    handler.onClose(this);
  }
}
