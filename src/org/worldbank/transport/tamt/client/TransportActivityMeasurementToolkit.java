package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.client.services.TagServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TransportActivityMeasurementToolkit implements EntryPoint {
	
	private HandlerManager eventBus = new HandlerManager(null);
	
	public void onModuleLoad() {
		RootLayoutPanel.get().add(new Main(eventBus));
	}
}
