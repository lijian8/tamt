package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.region.RegionPolygon;

import com.google.gwt.event.shared.GwtEvent;

public class BindPolygonToRegionEvent extends GwtEvent<BindPolygonToRegionEventHandler> {
  
  public static Type<BindPolygonToRegionEventHandler> TYPE = new Type<BindPolygonToRegionEventHandler>();
  
  private RegionPolygon poly;
  
  public BindPolygonToRegionEvent(RegionPolygon poly) {
	  this.setPolygon(poly);
  }

@Override
  public Type<BindPolygonToRegionEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(BindPolygonToRegionEventHandler handler) {
    handler.onBindPolygonToRegion(this);
  }

	public void setPolygon(RegionPolygon poly) {
		this.poly = poly;
	}
	
	public RegionPolygon getPolygon() {
		return poly;
	}
}
