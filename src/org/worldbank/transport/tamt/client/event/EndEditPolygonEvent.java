package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolygon;
import org.worldbank.transport.tamt.client.tag.TagPolyline;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.overlay.Polyline;

public class EndEditPolygonEvent extends GwtEvent<EndEditPolygonEventHandler> {
  
  public static Type<EndEditPolygonEventHandler> TYPE = new Type<EndEditPolygonEventHandler>();
  
  private TagPolygon polygon;
  
  public EndEditPolygonEvent(TagPolygon polygon) {
	  this.setPolygon(polygon);
  }

@Override
  public Type<EndEditPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EndEditPolygonEventHandler handler) {
    handler.onEndEditPolygon(this);
  }

public void setPolygon(TagPolygon polygon) {
	this.polygon = polygon;
}

public TagPolygon getPolygon() {
	return polygon;
}

}
