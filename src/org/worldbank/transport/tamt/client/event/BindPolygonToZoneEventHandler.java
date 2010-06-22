package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface BindPolygonToZoneEventHandler extends EventHandler {
  void onBindPolygonToRoad(BindPolygonToZoneEvent event);
}
