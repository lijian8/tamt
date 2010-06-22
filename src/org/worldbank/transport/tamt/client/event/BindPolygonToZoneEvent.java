package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolygon;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.overlay.Polygon;

public class BindPolygonToZoneEvent extends GwtEvent<BindPolygonToZoneEventHandler> {
  
  public static Type<BindPolygonToZoneEventHandler> TYPE = new Type<BindPolygonToZoneEventHandler>();
  
  private TagPolygon poly;
  
  public BindPolygonToZoneEvent(TagPolygon poly) {
	  this.setPolygon(poly);
  }

@Override
  public Type<BindPolygonToZoneEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(BindPolygonToZoneEventHandler handler) {
    handler.onBindPolygonToRoad(this);
  }

	public void setPolygon(TagPolygon poly) {
		this.poly = poly;
	}
	
	public TagPolygon getPolygon() {
		return poly;
	}
}
