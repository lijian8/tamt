package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DebugEventHandler extends EventHandler {
  void onDebug(DebugEvent event);
}
