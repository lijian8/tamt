package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowTagsEvent extends GwtEvent<ShowTagsEventHandler> {
  
  public static Type<ShowTagsEventHandler> TYPE = new Type<ShowTagsEventHandler>();
  
  public ShowTagsEvent() {
  }

@Override
  public Type<ShowTagsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ShowTagsEventHandler handler) {
    handler.onShowTags(this);
  }
}
