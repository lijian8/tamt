package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface FetchedTagsEventHandler extends EventHandler {
  void onFetchedTags(FetchedTagsEvent event);
}
