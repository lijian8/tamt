package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LoadCurrentStudyRegionEvent extends GwtEvent<LoadCurrentStudyRegionEventHandler> {
  
  public static Type<LoadCurrentStudyRegionEventHandler> TYPE = new Type<LoadCurrentStudyRegionEventHandler>();
  
  public LoadCurrentStudyRegionEvent() {
  }

@Override
  public Type<LoadCurrentStudyRegionEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LoadCurrentStudyRegionEventHandler handler) {
    handler.onLoad(this);
  }
}
