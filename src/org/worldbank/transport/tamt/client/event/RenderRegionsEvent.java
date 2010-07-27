package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class RenderRegionsEvent extends GwtEvent<RenderRegionsEventHandler> {
  
  public static Type<RenderRegionsEventHandler> TYPE = new Type<RenderRegionsEventHandler>();
  
  public HashMap<String, ArrayList<Vertex>> vertexHash;
  
  public RenderRegionsEvent(HashMap<String, ArrayList<Vertex>> vertexHash) {
	  this.vertexHash = vertexHash;
  }

@Override
  public Type<RenderRegionsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RenderRegionsEventHandler handler) {
    handler.onRenderRegions(this);
  }
}
