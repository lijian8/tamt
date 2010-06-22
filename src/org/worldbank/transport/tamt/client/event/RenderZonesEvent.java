package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class RenderZonesEvent extends GwtEvent<RenderZonesEventHandler> {
  
  public static Type<RenderZonesEventHandler> TYPE = new Type<RenderZonesEventHandler>();
  
  public HashMap<String, ArrayList<Vertex>> vertexHash;
  
  public RenderZonesEvent(HashMap<String, ArrayList<Vertex>> vertexHash) {
	  this.vertexHash = vertexHash;
  }

@Override
  public Type<RenderZonesEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RenderZonesEventHandler handler) {
    handler.onRenderZones(this);
  }
}
