package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolyline;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.overlay.Polyline;

public class BindPolyLineToRoadEvent extends GwtEvent<BindPolyLineToRoadEventHandler> {
  
  public static Type<BindPolyLineToRoadEventHandler> TYPE = new Type<BindPolyLineToRoadEventHandler>();
  
  private TagPolyline polyline;
  
  public BindPolyLineToRoadEvent(TagPolyline polyline) {
	  this.setPolyline(polyline);
  }

@Override
  public Type<BindPolyLineToRoadEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(BindPolyLineToRoadEventHandler handler) {
    handler.onBindPolyLineToRoad(this);
  }

public void setPolyline(TagPolyline polyline) {
	this.polyline = polyline;
}

public TagPolyline getPolyline() {
	return polyline;
}
}
