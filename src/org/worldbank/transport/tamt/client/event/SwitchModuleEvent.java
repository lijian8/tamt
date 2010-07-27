package org.worldbank.transport.tamt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SwitchModuleEvent extends GwtEvent<SwitchModuleEventHandler> {
  
  public static final String TAG = "TAG";
  public static final String IMPORT = "IMPORT";
  public static final String REGION = "REGION";
  public static final String QUERY = "QUERY";
  public static final String EXPORT = "EXPORT";
  
  public static Type<SwitchModuleEventHandler> TYPE = new Type<SwitchModuleEventHandler>();
  private String module;
private boolean visible;
  
  public SwitchModuleEvent(String module, boolean visible) {
	this.module = module;
	this.visible = visible;
  }

@Override
  public Type<SwitchModuleEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(SwitchModuleEventHandler handler) {
    handler.onSwitchModule(this);
  }

public String getModule() {
	return module;
}

public void setModule(String module) {
	this.module = module;
}

public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}
}
