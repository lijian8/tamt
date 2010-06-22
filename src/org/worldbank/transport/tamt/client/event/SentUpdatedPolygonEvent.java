package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolygon;
import org.worldbank.transport.tamt.client.tag.TagPolyline;

import com.google.gwt.event.shared.GwtEvent;

public class SentUpdatedPolygonEvent extends GwtEvent<SentUpdatedPolygonEventHandler> {
  
  public static Type<SentUpdatedPolygonEventHandler> TYPE = new Type<SentUpdatedPolygonEventHandler>();
  public TagPolygon tagPolygon;
  
  public SentUpdatedPolygonEvent(TagPolygon polygon) {
	  this.tagPolygon = polygon;
  }

@Override
  public Type<SentUpdatedPolygonEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SentUpdatedPolygonEventHandler handler) {
    handler.onSentUpdatedPolygon(this);
  }
}
