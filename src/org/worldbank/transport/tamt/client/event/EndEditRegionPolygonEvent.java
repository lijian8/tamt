package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.region.RegionPolygon;

import com.google.gwt.event.shared.GwtEvent;

public class EndEditRegionPolygonEvent extends GwtEvent<EndEditRegionPolygonEventHandler> {
  
  public static Type<EndEditRegionPolygonEventHandler> TYPE = new Type<EndEditRegionPolygonEventHandler>();
  
  private RegionPolygon polygon;
  
  public EndEditRegionPolygonEvent(RegionPolygon polygon) {
	  this.setPolygon(polygon);
  }

@Override
  public Type<EndEditRegionPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EndEditRegionPolygonEventHandler handler) {
    handler.onEndEditRegionPolygon(this);
  }

public void setPolygon(RegionPolygon polygon) {
	this.polygon = polygon;
}

public RegionPolygon getPolygon() {
	return polygon;
}

}
