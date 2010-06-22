package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.client.tag.TagPolyline;

import com.google.gwt.event.shared.GwtEvent;

public class SentUpdatedPolylineEvent extends GwtEvent<SentUpdatedPolylineEventHandler> {
  
  public static Type<SentUpdatedPolylineEventHandler> TYPE = new Type<SentUpdatedPolylineEventHandler>();
  public TagPolyline tagPolyline;
  
  public SentUpdatedPolylineEvent(TagPolyline polyline) {
	  this.tagPolyline = polyline;
  }

@Override
  public Type<SentUpdatedPolylineEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SentUpdatedPolylineEventHandler handler) {
    handler.onSentUpdatedPolyline(this);
  }
}
