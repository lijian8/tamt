package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GetGPSTracesEventHandler extends EventHandler {
  void onGetGPSTraces(GetGPSTracesEvent event);
}
