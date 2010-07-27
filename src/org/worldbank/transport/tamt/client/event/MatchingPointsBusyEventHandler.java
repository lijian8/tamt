package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MatchingPointsBusyEventHandler extends EventHandler {
  void onBusy(MatchingPointsBusyEvent event);
}
