package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.GetDayTypePerYearOptionEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
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
	@UiField TagCountReport tagCountReport;
	@UiField DefaultFlowConfiguration defaultFlowConfiguration;
	
	public VehicleFlow(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	
		tabLayoutPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				int almostSelected = event.getItem();
				switch (almostSelected) {
				// 0 = traffic count
				case 0:
					getTrafficCountRecords();
					break;
				// 1 = day type configuration
				case 1:
					getDayTypePerYearOption();
					break;
				// 2 = default flow configuration
				case 2:
					getDefaultFlowConfiguration();
					break;
				case 3:
					getTagCountReport();
					break;					
				}
			}
		});
		
		// get the traffic count by default
		getTrafficCountRecords();
		
	}
	
	protected void getDefaultFlowConfiguration() {
		// need to fetch the default flow config from the first tag, or not at all?
	}

	protected void getDayTypePerYearOption() {
		eventBus.fireEvent(new GetDayTypePerYearOptionEvent());
	}

	private void getTagCountReport()
	{
		// need to fetch the report from the first tag, or not at all?
	}
	
	private void getTrafficCountRecords()
	{
		eventBus.fireEvent(new GetTrafficCountRecordsEvent());
	}
	
	@UiFactory TrafficCount initTrafficCount() {
		return new TrafficCount(this.eventBus);
	}	
	
	@UiFactory TagCountReport initTagCountReport() {
		return new TagCountReport(this.eventBus);
	}
	
	@UiFactory DayTypesPerYear initDayTypesPerYear() {
		return new DayTypesPerYear(this.eventBus);
	}

	@UiFactory DefaultFlowConfiguration initDefaultFlowConfiguration() {
		return new DefaultFlowConfiguration(this.eventBus);
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
