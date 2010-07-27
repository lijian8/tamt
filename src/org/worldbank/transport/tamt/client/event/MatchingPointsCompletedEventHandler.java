package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface MatchingPointsCompletedEventHandler extends EventHandler {
  void onCompleted(MatchingPointsCompletedEvent event);
}
