package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEventHandler;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TrafficCountListView extends Composite {

	private static TrafficCountListViewUiBinder uiBinder = GWT
			.create(TrafficCountListViewUiBinder.class);

	interface TrafficCountListViewUiBinder extends
			UiBinder<Widget, TrafficCountListView> {
	}

	private HandlerManager eventBus;
	private TrafficCountRecordServiceAsync trafficCountRecordService;

	public TrafficCountListView(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		trafficCountRecordService = GWT.create(TrafficCountRecordService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
		
	}
	
	private void bind() {
		
		eventBus.addHandler(GetTrafficCountRecordsEvent.TYPE, new GetTrafficCountRecordsEventHandler() {
			
			@Override
			public void onGetTrafficCountRecords(GetTrafficCountRecordsEvent event) {
				
			}
		});
	}

	@UiHandler("add")
	void onClickAdd(ClickEvent e) {
		eventBus.fireEvent(new AddTrafficCountRecordEvent());
	}	

}
