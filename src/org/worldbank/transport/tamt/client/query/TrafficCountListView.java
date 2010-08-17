package org.worldbank.transport.tamt.client.query;

import java.util.ArrayList;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.EditTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEvent;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEventHandler;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrafficCountListView extends Composite {

	private static TrafficCountListViewUiBinder uiBinder = GWT
			.create(TrafficCountListViewUiBinder.class);

	interface TrafficCountListViewUiBinder extends
			UiBinder<Widget, TrafficCountListView> {
	}
	
	interface Style extends CssResource {
	    String columnHeader();
	    String checkbox();
	    String record();
	    String clickable();
	}

	@UiField Style style;
	
	@UiField CheckBox toggleAllCheckboxes;
	@UiField FlexTable trafficCountRecordsTable;
	@UiField ScrollPanel scrollPanel;
	
	private HandlerManager eventBus;
	private TrafficCountRecordServiceAsync trafficCountRecordService;
	protected StudyRegion currentStudyRegion;

	private ArrayList<TrafficCountRecord> trafficCountRecords;

	private ArrayList<CheckBox> checkboxes;

	private DateTimeFormat fmt;
	private DateTimeFormat fmtHHMM;
	
	public TrafficCountListView(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		trafficCountRecordService = GWT.create(TrafficCountRecordService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
		
		fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		fmtHHMM = DateTimeFormat.getFormat("HH:mm");
		
	}
	
	private void bind() {
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
			}
		});
		
		eventBus.addHandler(GetTrafficCountRecordsEvent.TYPE, new GetTrafficCountRecordsEventHandler() {
			
			@Override
			public void onGetTrafficCountRecords(GetTrafficCountRecordsEvent event) {
				fetchTrafficCountRecords();
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: TrafficCountListView height: " + event.height);
				GWT.log("SIZE: TrafficCountListView width: " + event.width);
				
				int h = event.height - 200; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: TrafficCountListView height: " + height);
					GWT.log("SIZE: TrafficCountListView width: " + width);
					scrollPanel.setHeight(height);
				}
			}
		});				
	}

	@UiHandler("add")
	void onClickAdd(ClickEvent e) {
		eventBus.fireEvent(new AddTrafficCountRecordEvent());
	}		
	
	@UiHandler("toggleAllCheckboxes")
	void onClickToggleAllCheckboxes(ClickEvent e) {
		CheckBox master = (CheckBox) e.getSource();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(master.getValue());
		}
	}
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e)
	{
		if( Window.confirm("Delete all checked traffic count records?") )
		{
			deleteTrafficRecordCounts();
		}		
	}

	private void deleteTrafficRecordCounts() {
		ArrayList<String> trafficRecordCountIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				trafficRecordCountIds.add(cb.getFormValue());
			}
		}

		// now delete based on the list of ids
		trafficCountRecordService.deleteTrafficCountRecords(trafficRecordCountIds, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleAllCheckboxes.setValue(false);
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						// refresh the list
						toggleAllCheckboxes.setValue(false);
						fetchTrafficCountRecords();
					}
		});
	}
	
	private void fetchTrafficCountRecords() {
		
		trafficCountRecordService.getTrafficCountRecords(currentStudyRegion, new AsyncCallback<ArrayList<TrafficCountRecord>>() {
			
			@Override
			public void onSuccess(ArrayList<TrafficCountRecord> result) {
				renderResults(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not get the traffic count records: " + caught.getMessage());
			}
		});
		
	}

	private void renderResults( ArrayList<TrafficCountRecord> records)
	{
	
		// store the results locally
		trafficCountRecords = records;
		
		// clear the previously rendered table
		trafficCountRecordsTable.removeAllRows();
		trafficCountRecordsTable.clear();
		
		// reset the checkboxes array
		checkboxes = new ArrayList<CheckBox>();
		
		// set the column headers
		HTML dateHeader = new HTML("Date");
		dateHeader.setStyleName(style.columnHeader());
		
		HTML startHeader = new HTML("Start time");
		startHeader.setStyleName(style.columnHeader());
		
		HTML endHeader = new HTML("End time");
		endHeader.setStyleName(style.columnHeader());
		
		HTML dayTypeHeader = new HTML("Day type");
		dayTypeHeader.setStyleName(style.columnHeader());
		
		HTML tagHeader = new HTML("Tag");
		tagHeader.setStyleName(style.columnHeader());
		
		HTML blank = new HTML("");
		blank.setStyleName(style.checkbox());
		
		trafficCountRecordsTable.setWidget(0, 0, blank );
		trafficCountRecordsTable.setWidget(0, 1, dateHeader );
		trafficCountRecordsTable.setWidget(0, 2, startHeader );
		trafficCountRecordsTable.setWidget(0, 3, endHeader );
		trafficCountRecordsTable.setWidget(0, 4, dayTypeHeader);
		trafficCountRecordsTable.setWidget(0, 5, tagHeader);
		
		// set the column header style
		trafficCountRecordsTable.getRowFormatter().setStyleName(0, style.columnHeader());
		
		for (int i = 0; i < trafficCountRecords.size(); i++) {
			final TrafficCountRecord trafficCountRecord = trafficCountRecords.get(i);
			
			final String id = trafficCountRecord.getId();
			
			// setup the checkbox
			CheckBox cb = new CheckBox();
			cb.setFormValue(id); //store the id in the checkbox value
			checkboxes.add(cb); // keep track for selecting all|none to delete
			cb.setStyleName(style.checkbox());
			
			// extract the attributes
			String date = fmt.format(trafficCountRecord.getDate());
			String startTime = fmtHHMM.format(trafficCountRecord.getStartTime());
			String endTime = fmtHHMM.format(trafficCountRecord.getEndTime());
			String dayType = trafficCountRecord.getDayType();
			String tag = trafficCountRecord.getTag();
			
			// create labels for each attribute we show in the table
			Label dateLabel = new Label(date);
			dateLabel.setStyleName(style.record());
			dateLabel.addStyleName(style.clickable());
			dateLabel.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new EditTrafficCountRecordEvent(trafficCountRecord));
				}
			});
			Label startTimeLabel = new Label(startTime);
			startTimeLabel.setStyleName(style.record());
			
			Label endTimeLabel = new Label(endTime);
			endTimeLabel.setStyleName(style.record());
			
			Label dayTypeLabel = new Label(dayType);
			dayTypeLabel.setStyleName(style.record());
			
			Label tagLabel = new Label(tag);
			tagLabel.setStyleName(style.record());
			
			
			int row = i + 1; // 1-index to account for column headers
			trafficCountRecordsTable.setWidget(row, 0, cb);
			trafficCountRecordsTable.getCellFormatter().setWidth(row, 0, "20px");
			trafficCountRecordsTable.setWidget(row, 1, dateLabel);
			trafficCountRecordsTable.setWidget(row, 2, startTimeLabel);
			trafficCountRecordsTable.setWidget(row, 3, endTimeLabel);
			trafficCountRecordsTable.setWidget(row, 4, dayTypeLabel);
			trafficCountRecordsTable.setWidget(row, 5, tagLabel);
			
			
		}
		
	}
}
