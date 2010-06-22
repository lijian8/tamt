package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetTagsEvent extends GwtEvent<GetTagsEventHandler> {
  
  public static Type<GetTagsEventHandler> TYPE = new Type<GetTagsEventHandler>();
  
  public GetTagsEvent() {
  }

@Override
  public Type<GetTagsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(GetTagsEventHandler handler) {
    handler.onGetTags(this);
  }
}
