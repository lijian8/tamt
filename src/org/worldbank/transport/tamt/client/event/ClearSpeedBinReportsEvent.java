package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.event.shared.GwtEvent;

public class ClearSpeedBinReportsEvent extends GwtEvent<ClearSpeedBinReportsEventHandler> {
  
  public static Type<ClearSpeedBinReportsEventHandler> TYPE = new Type<ClearSpeedBinReportsEventHandler>();
  
  
  public ClearSpeedBinReportsEvent() {
  }

  @Override
  public Type<ClearSpeedBinReportsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ClearSpeedBinReportsEventHandler handler) {
    handler.onClearSpeedBinReports(this);
  }
}
