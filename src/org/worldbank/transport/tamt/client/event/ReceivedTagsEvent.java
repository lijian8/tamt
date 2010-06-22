package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ReceivedTagsEvent extends GwtEvent<ReceivedTagsEventHandler> {
  
  public static Type<ReceivedTagsEventHandler> TYPE = new Type<ReceivedTagsEventHandler>();
  
  public ReceivedTagsEvent() {
  }

@Override
  public Type<ReceivedTagsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ReceivedTagsEventHandler handler) {
    handler.onReceivedTags(this);
  }
}
