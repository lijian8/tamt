package org.worldbank.transport.tamt.client.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.FetchedTagsEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.query.TrafficCountListView.Style;
import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.client.services.RegionServiceAsync;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.shared.DefaultFlow;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultFlowConfiguration extends Composite {

	private static DefaultFlowConfigurationUiBinder uiBinder = GWT
			.create(DefaultFlowConfigurationUiBinder.class);

	interface DefaultFlowConfigurationUiBinder extends UiBinder<Widget, DefaultFlowConfiguration> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String hour();
		String textbox();
		String header();
		String button();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	private RegionServiceAsync regionService;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	@UiField ScrollPanel tagsScroll;
	@UiField ScrollPanel defaultFlowScroll;
	@UiField FlexTable tagsTable;
	@UiField FlexTable defaultFlowTable;
	@UiField HTML selectedTag;
	
	// textboxes for default flow configuration
	
	private TextBox weekdayW2 = new TextBox();
	private TextBox weekdayW3 = new TextBox();
	private TextBox weekdayPC = new TextBox();
	private TextBox weekdayTX = new TextBox();
	private TextBox weekdayLDV = new TextBox();
	private TextBox weekdayLDC = new TextBox();
	private TextBox weekdayHDC = new TextBox();
	private TextBox weekdayMDB = new TextBox();
	private TextBox weekdayHDB = new TextBox();
	
	private TextBox saturdayW2 = new TextBox();
	private TextBox saturdayW3 = new TextBox();
	private TextBox saturdayPC = new TextBox();
	private TextBox saturdayTX = new TextBox();
	private TextBox saturdayLDV = new TextBox();
	private TextBox saturdayLDC = new TextBox();
	private TextBox saturdayHDC = new TextBox();
	private TextBox saturdayMDB = new TextBox();
	private TextBox saturdayHDB = new TextBox();
	
	private TextBox sundayHolidayW2 = new TextBox();
	private TextBox sundayHolidayW3 = new TextBox();
	private TextBox sundayHolidayPC = new TextBox();
	private TextBox sundayHolidayTX = new TextBox();
	private TextBox sundayHolidayLDV = new TextBox();
	private TextBox sundayHolidayLDC = new TextBox();
	private TextBox sundayHolidayHDC = new TextBox();
	private TextBox sundayHolidayMDB = new TextBox();
	private TextBox sundayHolidayHDB = new TextBox();
	
	// headers etc for the form
	private HTML vehicleTypeHeader = new HTML("Vehicle Type");
	private HTML weekdayHeader = new HTML("Weekday");
	private HTML saturdayHeader = new HTML("Saturday");
	private HTML sundayHolidayHeader = new HTML("Sunday / Holiday");
	
	private Label W2Label = new Label("Two-wheeler");
	private Label W3Label = new Label("Three-wheeler");
	private Label PCLabel = new Label("Passenger car");
	private Label TXLabel = new Label("Taxi");
	private Label LDVLabel = new Label("Pickups, vans, SUVs");
	private Label LDCLabel = new Label("Light-duty commercial");
	private Label HDCLabel = new Label("Heavy-duty commercial");
	private Label MDBLabel = new Label("Medium-duty minibuses");
	private Label HDBLabel = new Label("Heavy-duty buses");
	
	private Button save = new Button("Save");
	private Button cancel = new Button("Cancel");
	
	private StudyRegion currentStudyRegion;
	
	private DateTimeFormat fmt24Hr = DateTimeFormat.getFormat("HH:mm");
	private Date dummy = new Date();
	private DefaultFlow currentDefaultFlow;
	private TagDetails currentTagDetails;
	
	public DefaultFlowConfiguration(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		regionService = GWT.create(RegionService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		createTable();
		
		bind();
		
	}

	private void bind() {
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
			}
		});		
		
		eventBus.addHandler(FetchedTagsEvent.TYPE, new FetchedTagsEventHandler() {
			
			@Override
			public void onFetchedTags(FetchedTagsEvent event) {
				
				// if the tags have been updated, clear out the UI
				selectedTag.setHTML("Selected tag: None selected");
				resetForm();
				renderTags(event.getTags());
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: DefaultFlowConfiguration width: " + event.width);
				int h = event.height - 195; // account for other query module UI
				int w = event.width - 476;
				GWT.log("SIZE: DefaultFlowConfiguration adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: DefaultFlowConfiguration adjusted height: " + height);
					tagsScroll.setHeight(height);
					defaultFlowScroll.setHeight(height);
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					rightPane.setWidth(width);
				}
				
			}
		});	
	}

	protected void renderTags(ArrayList<TagDetails> tags) {
		
		// clear out the old table
		tagsTable.removeAllRows();
		tagsTable.clear();
		
		for (int i = 0; i < tags.size(); i++) {
			final TagDetails tagDetails = tags.get(i);
			Label name = new Label(tagDetails.getName());
			name.addStyleName(style.clickable());
			name.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GWT.log("TAG DETAILS after click=" + tagDetails);
					resetForm();
					getDefaultFlowConfiguration(tagDetails);
				}
			});
			tagsTable.setWidget(i, 0, name);
		}
		
	}

	protected void getDefaultFlowConfiguration(TagDetails tagDetails) {
		
		selectedTag.setHTML("Selected tag: <b>"+tagDetails.getName()+"</b>");
		
		// we will need tagDetails later, so keep track of it
		currentTagDetails = tagDetails;
		
		DefaultFlow query = new DefaultFlow();
			
		StudyRegion region = new StudyRegion();
		region.setId(currentStudyRegion.getId());
		tagDetails.setRegion(region);
		query.setTagDetails(tagDetails);
		
		regionService.getDefaultFlow(query, new AsyncCallback<DefaultFlow>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not get default flow configuration: " + caught.getMessage());
			}

			@Override
			public void onSuccess(DefaultFlow result) {
				if( result != null) // might be for many tags
				{
					currentDefaultFlow = result;
					renderDefaultFlowConfiguration(currentDefaultFlow);
				} else {
					currentDefaultFlow = null;
					resetForm();
				}
			}
		});
	}

	private void createTable()
	{
		// add styles to textboxes
		weekdayW2.setStyleName(style.textbox());
		weekdayW3.setStyleName(style.textbox());
		weekdayPC.setStyleName(style.textbox());
		weekdayTX.setStyleName(style.textbox());
		weekdayLDV.setStyleName(style.textbox());
		weekdayLDC.setStyleName(style.textbox());
		weekdayHDC.setStyleName(style.textbox());
		weekdayMDB.setStyleName(style.textbox());
		weekdayHDB.setStyleName(style.textbox());
		
		saturdayW2.setStyleName(style.textbox());
		saturdayW3.setStyleName(style.textbox());
		saturdayPC.setStyleName(style.textbox());
		saturdayTX.setStyleName(style.textbox());
		saturdayLDV.setStyleName(style.textbox());
		saturdayLDC.setStyleName(style.textbox());
		saturdayHDC.setStyleName(style.textbox());
		saturdayMDB.setStyleName(style.textbox());
		saturdayHDB.setStyleName(style.textbox());
		
		sundayHolidayW2.setStyleName(style.textbox());
		sundayHolidayW3.setStyleName(style.textbox());
		sundayHolidayPC.setStyleName(style.textbox());
		sundayHolidayTX.setStyleName(style.textbox());
		sundayHolidayLDV.setStyleName(style.textbox());
		sundayHolidayLDC.setStyleName(style.textbox());
		sundayHolidayHDC.setStyleName(style.textbox());
		sundayHolidayMDB.setStyleName(style.textbox());
		sundayHolidayHDB.setStyleName(style.textbox());		
		
		vehicleTypeHeader.setStyleName(style.header());
		weekdayHeader.setStyleName(style.header());
		saturdayHeader.setStyleName(style.header());
		sundayHolidayHeader.setStyleName(style.header());
		
		// clear the table
		defaultFlowTable.clear();
		defaultFlowTable.removeAllRows();
		
		// add the headers
		defaultFlowTable.setWidget(0, 0, vehicleTypeHeader);
		defaultFlowTable.setWidget(0, 1, weekdayHeader);
		defaultFlowTable.setWidget(0, 2, saturdayHeader);
		defaultFlowTable.setWidget(0, 3, sundayHolidayHeader);
		
		// two wheeler
		defaultFlowTable.setWidget(1, 0, W2Label);
		defaultFlowTable.setWidget(1, 1, weekdayW2);
		defaultFlowTable.setWidget(1, 2, saturdayW2);
		defaultFlowTable.setWidget(1, 3, sundayHolidayW2);
		
		// three wheeler
		defaultFlowTable.setWidget(2, 0, W3Label);
		defaultFlowTable.setWidget(2, 1, weekdayW3);
		defaultFlowTable.setWidget(2, 2, saturdayW3);
		defaultFlowTable.setWidget(2, 3, sundayHolidayW3);
		
		// passenger car
		defaultFlowTable.setWidget(3, 0, PCLabel);
		defaultFlowTable.setWidget(3, 1, weekdayPC);
		defaultFlowTable.setWidget(3, 2, saturdayPC);
		defaultFlowTable.setWidget(3, 3, sundayHolidayPC);
		
		// taxi
		defaultFlowTable.setWidget(4, 0, TXLabel);
		defaultFlowTable.setWidget(4, 1, weekdayTX);
		defaultFlowTable.setWidget(4, 2, saturdayTX);
		defaultFlowTable.setWidget(4, 3, sundayHolidayTX);
		
		// light duty vehicle
		defaultFlowTable.setWidget(5, 0, LDVLabel);
		defaultFlowTable.setWidget(5, 1, weekdayLDV);
		defaultFlowTable.setWidget(5, 2, saturdayLDV);
		defaultFlowTable.setWidget(5, 3, sundayHolidayLDV);
		
		// light duty commercial
		defaultFlowTable.setWidget(6, 0, LDCLabel);
		defaultFlowTable.setWidget(6, 1, weekdayLDC);
		defaultFlowTable.setWidget(6, 2, saturdayLDC);
		defaultFlowTable.setWidget(6, 3, sundayHolidayLDC);
		
		// heavy duty commercial
		defaultFlowTable.setWidget(7, 0, HDCLabel);
		defaultFlowTable.setWidget(7, 1, weekdayHDC);
		defaultFlowTable.setWidget(7, 2, saturdayHDC);
		defaultFlowTable.setWidget(7, 3, sundayHolidayHDC);
		
		// medium duty buses
		defaultFlowTable.setWidget(8, 0, MDBLabel);
		defaultFlowTable.setWidget(8, 1, weekdayMDB);
		defaultFlowTable.setWidget(8, 2, saturdayMDB);
		defaultFlowTable.setWidget(8, 3, sundayHolidayMDB);
		
		// heavy duty buses
		defaultFlowTable.setWidget(9, 0, HDBLabel);
		defaultFlowTable.setWidget(9, 1, weekdayHDB);
		defaultFlowTable.setWidget(9, 2, saturdayHDB);
		defaultFlowTable.setWidget(9, 3, sundayHolidayHDB);
		
		// add the buttons
		save.setStyleName(style.button());
		save.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveDefaultFlow();
			}
		});
		
		cancel.setStyleName(style.button());
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
					currentDefaultFlow = null;
					currentTagDetails = null;
					selectedTag.setHTML("Selected tag: None selected");
					resetForm();
			}
		});
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.add(save);
		buttonsPanel.add(cancel);
		defaultFlowTable.setWidget(10, 0, buttonsPanel);
		
	}
	
	private void saveDefaultFlow() {
		
		// prep an entity to save
		DefaultFlow defaultFlow = new DefaultFlow();
		
		if( currentDefaultFlow != null)
		{
			defaultFlow.setId( currentDefaultFlow.getId());
		}
		
		TagDetails tagDetails = new TagDetails();
		if( currentTagDetails != null) // should never be null, because we just clicked on a tag to load the config
		{
			tagDetails.setId(currentTagDetails.getId());
		}
		GWT.log("TAG DETAILS just id for saving flow:" + tagDetails);
		
		// get the study region id too (but just the id)
		StudyRegion region = new StudyRegion();
		region.setId(currentStudyRegion.getId());
		tagDetails.setRegion(region);
		defaultFlow.setTagDetails(tagDetails);
		
		defaultFlow.setW2Weekday(weekdayW2.getValue());
		defaultFlow.setW2Saturday(saturdayW2.getValue());
		defaultFlow.setW2SundayHoliday(sundayHolidayW2.getValue());
		
		defaultFlow.setW3Weekday(weekdayW3.getValue());
		defaultFlow.setW3Saturday(saturdayW3.getValue());
		defaultFlow.setW3SundayHoliday(sundayHolidayW3.getValue());
		
		defaultFlow.setPcWeekday(weekdayPC.getValue());
		defaultFlow.setPcSaturday(saturdayPC.getValue());
		defaultFlow.setPcSundayHoliday(sundayHolidayPC.getValue());
		
		defaultFlow.setTxWeekday(weekdayTX.getValue());
		defaultFlow.setTxSaturday(saturdayTX.getValue());
		defaultFlow.setTxSundayHoliday(sundayHolidayTX.getValue());
		
		defaultFlow.setLdvWeekday(weekdayLDV.getValue());
		defaultFlow.setLdvSaturday(saturdayLDV.getValue());
		defaultFlow.setLdvSundayHoliday(sundayHolidayLDV.getValue());
		
		defaultFlow.setLdcWeekday(weekdayLDC.getValue());
		defaultFlow.setLdcSaturday(saturdayLDC.getValue());
		defaultFlow.setLdcSundayHoliday(sundayHolidayLDC.getValue());
		
		defaultFlow.setHdcWeekday(weekdayHDC.getValue());
		defaultFlow.setHdcSaturday(saturdayHDC.getValue());
		defaultFlow.setHdcSundayHoliday(sundayHolidayHDC.getValue());
		
		defaultFlow.setMdbWeekday(weekdayMDB.getValue());
		defaultFlow.setMdbSaturday(saturdayMDB.getValue());
		defaultFlow.setMdbSundayHoliday(sundayHolidayMDB.getValue());
		
		defaultFlow.setHdbWeekday(weekdayHDB.getValue());
		defaultFlow.setHdbSaturday(saturdayHDB.getValue());
		defaultFlow.setHdbSundayHoliday(sundayHolidayHDB.getValue());
		
		GWT.log("Default flow to save:" + defaultFlow);
		
		regionService.saveDefaultFlow(defaultFlow, new AsyncCallback<DefaultFlow>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not save default flow configuration: " + caught.getMessage());
			}

			@Override
			public void onSuccess(DefaultFlow result) {
				Window.alert("Default flow saved");
				if( result != null) // might be for many tags
				{
					currentDefaultFlow = result;
					GWT.log("TAG DETAILS post save:" + currentDefaultFlow.getTagDetails());
					renderDefaultFlowConfiguration(currentDefaultFlow);
				} else {
					resetForm();
				}
			}
		});
	}

	private void resetForm() {
		
		weekdayW2.setValue("");
		weekdayW3.setValue("");
		weekdayPC.setValue("");
		weekdayTX.setValue("");
		weekdayLDV.setValue("");
		weekdayLDC.setValue("");
		weekdayHDC.setValue("");
		weekdayMDB.setValue("");
		weekdayHDB.setValue("");
		
		saturdayW2.setValue("");
		saturdayW3.setValue("");
		saturdayPC.setValue("");
		saturdayTX.setValue("");
		saturdayLDV.setValue("");
		saturdayLDC.setValue("");
		saturdayHDC.setValue("");
		saturdayMDB.setValue("");
		saturdayHDB.setValue("");
		
		sundayHolidayW2.setValue("");
		sundayHolidayW3.setValue("");
		sundayHolidayPC.setValue("");
		sundayHolidayTX.setValue("");
		sundayHolidayLDV.setValue("");
		sundayHolidayLDC.setValue("");
		sundayHolidayHDC.setValue("");
		sundayHolidayMDB.setValue("");
		sundayHolidayHDB.setValue("");
		
	}

	protected void renderDefaultFlowConfiguration(DefaultFlow defaultFlow) {
		
		weekdayW2.setValue(defaultFlow.getW2Weekday());
		saturdayW2.setValue(defaultFlow.getW2Saturday());
		sundayHolidayW2.setValue(defaultFlow.getW2SundayHoliday());
		
		weekdayW3.setValue(defaultFlow.getW3Weekday());
		saturdayW3.setValue(defaultFlow.getW3Saturday());
		sundayHolidayW3.setValue(defaultFlow.getW3SundayHoliday());
		
		weekdayPC.setValue(defaultFlow.getPcWeekday());
		saturdayPC.setValue(defaultFlow.getPcSaturday());
		sundayHolidayPC.setValue(defaultFlow.getPcSundayHoliday());
		
		weekdayTX.setValue(defaultFlow.getTxWeekday());
		saturdayTX.setValue(defaultFlow.getTxSaturday());
		sundayHolidayTX.setValue(defaultFlow.getTxSundayHoliday());
		
		weekdayLDV.setValue(defaultFlow.getLdvWeekday());
		saturdayLDV.setValue(defaultFlow.getLdvSaturday());
		sundayHolidayLDV.setValue(defaultFlow.getLdvSundayHoliday());
		
		weekdayLDC.setValue(defaultFlow.getLdcWeekday());
		saturdayLDC.setValue(defaultFlow.getLdcSaturday());
		sundayHolidayLDC.setValue(defaultFlow.getLdcSundayHoliday());
		
		weekdayHDC.setValue(defaultFlow.getHdcWeekday());
		saturdayHDC.setValue(defaultFlow.getHdcSaturday());
		sundayHolidayHDC.setValue(defaultFlow.getHdcSundayHoliday());
		
		weekdayMDB.setValue(defaultFlow.getMdbWeekday());
		saturdayMDB.setValue(defaultFlow.getMdbSaturday());
		sundayHolidayMDB.setValue(defaultFlow.getMdbSundayHoliday());
		
		weekdayHDB.setValue(defaultFlow.getHdbWeekday());
		saturdayHDB.setValue(defaultFlow.getHdbSaturday());
		sundayHolidayHDB.setValue(defaultFlow.getHdbSundayHoliday());
		
	}

}
