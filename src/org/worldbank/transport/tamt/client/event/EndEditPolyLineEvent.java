package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolyline;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.overlay.Polyline;

public class EndEditPolyLineEvent extends GwtEvent<EndEditPolyLineEventHandler> {
  
  public static Type<EndEditPolyLineEventHandler> TYPE = new Type<EndEditPolyLineEventHandler>();
  
  private TagPolyline polyline;
  
  public EndEditPolyLineEvent(TagPolyline polyline) {
	  this.setPolyline(polyline);
  }

@Override
  public Type<EndEditPolyLineEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EndEditPolyLineEventHandler handler) {
    handler.onEndEditPolyLine(this);
  }

public void setPolyline(TagPolyline polyline) {
	this.polyline = polyline;
}

public TagPolyline getPolyline() {
	return polyline;
}
}
