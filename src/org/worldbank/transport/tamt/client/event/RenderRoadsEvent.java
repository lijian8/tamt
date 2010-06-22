package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class RenderRoadsEvent extends GwtEvent<RenderRoadsEventHandler> {
  
  public static Type<RenderRoadsEventHandler> TYPE = new Type<RenderRoadsEventHandler>();
  
  public HashMap<String, ArrayList<Vertex>> vertexHash;
  
  public RenderRoadsEvent(HashMap<String, ArrayList<Vertex>> vertexHash) {
	  this.vertexHash = vertexHash;
  }

@Override
  public Type<RenderRoadsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(RenderRoadsEventHandler handler) {
    handler.onRenderRoads(this);
  }
}
