package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.region.RegionPolygon;
import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class EndEditRegionPolygonEvent extends GwtEvent<EndEditRegionPolygonEventHandler> {
  
  public static Type<EndEditRegionPolygonEventHandler> TYPE = new Type<EndEditRegionPolygonEventHandler>();
  
  public RegionPolygon polygon;
  
  public EndEditRegionPolygonEvent(RegionPolygon polygon) {
	  this.polygon = polygon;
  }

@Override
  public Type<EndEditRegionPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EndEditRegionPolygonEventHandler handler) {
    handler.onEndEditRegionPolygon(this);
  }

}
