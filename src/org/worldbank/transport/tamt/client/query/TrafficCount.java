package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEventHandler;
import org.worldbank.transport.tamt.client.event.CancelTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.CancelTrafficCountRecordEventHandler;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TrafficCount extends Composite {

	private static TrafficCountUiBinder uiBinder = GWT
			.create(TrafficCountUiBinder.class);

	interface TrafficCountUiBinder extends UiBinder<Widget, TrafficCount> {
	}

	private HandlerManager eventBus;
	
	@UiField TrafficCountListView trafficCountListView;
	@UiField TrafficCountDetailView trafficCountDetailView;

	public TrafficCount(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
		
		// list view is only item visible at first
		trafficCountDetailView.setVisible(false);
	}
	
	private void bind() {
		
		eventBus.addHandler(GetTrafficCountRecordsEvent.TYPE, new GetTrafficCountRecordsEventHandler() {
			
			@Override
			public void onGetTrafficCountRecords(GetTrafficCountRecordsEvent event) {
				trafficCountDetailView.setVisible(false);
				trafficCountListView.setVisible(true);
			}
		});
		
		eventBus.addHandler(AddTrafficCountRecordEvent.TYPE, 
				new AddTrafficCountRecordEventHandler() {
					
					@Override
					public void onAddTrafficCountRecord(AddTrafficCountRecordEvent event) {
						trafficCountListView.setVisible(false);
						trafficCountDetailView.setVisible(true);
					}
				});
		
		eventBus.addHandler(CancelTrafficCountRecordEvent.TYPE, 
				new CancelTrafficCountRecordEventHandler() {
					
					@Override
					public void onCancelTrafficCountRecord(CancelTrafficCountRecordEvent event) {
						trafficCountDetailView.setVisible(false);
						trafficCountListView.setVisible(true);
					}
				});
		
	}

	@UiFactory TrafficCountListView initTrafficCountListView() {
		return new TrafficCountListView(this.eventBus);
	}	
	
	@UiFactory TrafficCountDetailView initTrafficCountDetailView() {
		return new TrafficCountDetailView(this.eventBus);
	}		

}
