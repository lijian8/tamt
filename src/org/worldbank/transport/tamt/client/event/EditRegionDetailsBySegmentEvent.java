package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.region.RegionPolygon;

import com.google.gwt.event.shared.GwtEvent;

public class EditRegionDetailsBySegmentEvent extends GwtEvent<EditRegionDetailsBySegmentEventHandler> {
  
  public static Type<EditRegionDetailsBySegmentEventHandler> TYPE = new Type<EditRegionDetailsBySegmentEventHandler>();
  
  public RegionPolygon regionPolygon;
  
  public EditRegionDetailsBySegmentEvent(RegionPolygon regionPolygon) {
	 this.regionPolygon = regionPolygon;
  }

@Override
  public Type<EditRegionDetailsBySegmentEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditRegionDetailsBySegmentEventHandler handler) {
    handler.onEditRegionDetailsBySegment(this);
  }

}
