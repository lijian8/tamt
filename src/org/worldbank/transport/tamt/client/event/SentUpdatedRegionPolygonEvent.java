package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.region.RegionPolygon;

import com.google.gwt.event.shared.GwtEvent;

public class SentUpdatedRegionPolygonEvent extends GwtEvent<SentUpdatedRegionPolygonEventHandler> {
  
  public static Type<SentUpdatedRegionPolygonEventHandler> TYPE = new Type<SentUpdatedRegionPolygonEventHandler>();
  public RegionPolygon regionPolygon;
  
  public SentUpdatedRegionPolygonEvent(RegionPolygon polygon) {
	  this.regionPolygon = polygon;
  }

@Override
  public Type<SentUpdatedRegionPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SentUpdatedRegionPolygonEventHandler handler) {
    handler.onSentUpdatedPolygon(this);
  }
}
