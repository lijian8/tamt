package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class RenderTripStatisticsEvent extends GwtEvent<RenderTripStatisticsEventHandler> {
  
  public static Type<RenderTripStatisticsEventHandler> TYPE = new Type<RenderTripStatisticsEventHandler>();
  
  
  public RenderTripStatisticsEvent() {
  }

  @Override
  public Type<RenderTripStatisticsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RenderTripStatisticsEventHandler handler) {
    handler.onRenderTripStatistics(this);
  }
}
