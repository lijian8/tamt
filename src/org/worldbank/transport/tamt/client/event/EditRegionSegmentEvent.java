package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.geom.LatLng;

public class EditRegionSegmentEvent extends GwtEvent<EditRegionSegmentEventHandler> {
  
  public static Type<EditRegionSegmentEventHandler> TYPE = new Type<EditRegionSegmentEventHandler>();
  
  public String id;
  public LatLng center;
  public Integer zoomLevel;
  
  public EditRegionSegmentEvent(String id, LatLng center, Integer zoomLevel) {
	 this.id = id;
	 this.center = center;
	 this.zoomLevel = zoomLevel;
  }

@Override
  public Type<EditRegionSegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRegionSegmentEventHandler handler) {
    handler.onEditRegionSegment(this);
  }

}
