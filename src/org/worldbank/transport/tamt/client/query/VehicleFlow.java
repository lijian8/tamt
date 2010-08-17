package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class VehicleFlow extends Composite {

	private static VehicleFlowUiBinder uiBinder = GWT
			.create(VehicleFlowUiBinder.class);

	interface VehicleFlowUiBinder extends UiBinder<Widget, VehicleFlow> {
	}
	
	private HandlerManager eventBus;
	
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HorizontalPanel hpanel;
	@UiField TrafficCount trafficCount;

	public VehicleFlow(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	}
	
	@UiFactory TrafficCount initTrafficCount() {
		return new TrafficCount(this.eventBus);
	}	

	private void bind() {

		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: VehicleFlow tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 120; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: VehicleFlow tabLayoutPanel height: " + height);
					GWT.log("SIZE: VehicleFlow tabLayoutPanel width: " + width);
					tabLayoutPanel.setHeight(height);
					
					String hwidth = Integer.toString(w-34) + "px";
					hpanel.setWidth(hwidth);
				}
			}
		});	
	}

}
