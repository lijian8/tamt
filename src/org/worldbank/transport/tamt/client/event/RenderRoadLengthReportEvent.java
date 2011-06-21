package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class RenderRoadLengthReportEvent extends GwtEvent<RenderRoadLengthReportEventHandler> {
  
  public static Type<RenderRoadLengthReportEventHandler> TYPE = new Type<RenderRoadLengthReportEventHandler>();
  
  
  public RenderRoadLengthReportEvent() {
  }

  @Override
  public Type<RenderRoadLengthReportEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RenderRoadLengthReportEventHandler handler) {
    handler.onRenderRoadLengthReport(this);
  }
}
