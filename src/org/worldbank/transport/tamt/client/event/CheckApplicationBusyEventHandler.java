package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface CheckApplicationBusyEventHandler extends EventHandler {
  void onCheckApplicationBusy(CheckApplicationBusyEvent event);
}
