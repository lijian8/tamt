package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ReceivedTagsEventHandler extends EventHandler {
  void onReceivedTags(ReceivedTagsEvent event);
}
