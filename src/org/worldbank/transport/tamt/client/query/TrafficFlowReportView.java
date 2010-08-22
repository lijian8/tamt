package org.worldbank.transport.tamt.client.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.FetchedTagsEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.query.TrafficCountListView.Style;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.client.services.TrafficFlowReportService;
import org.worldbank.transport.tamt.client.services.TrafficFlowReportServiceAsync;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TrafficFlowReportView extends Composite {

	private static TrafficFlowReportUiBinder uiBinder = GWT
			.create(TrafficFlowReportUiBinder.class);

	interface TrafficFlowReportUiBinder extends UiBinder<Widget, TrafficFlowReportView> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String hour();
		String cell();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	private TrafficFlowReportServiceAsync trafficFlowReportService;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField HorizontalPanel rightPaneHPanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	//@UiField HTML midPane;
	@UiField ScrollPanel tagsScroll;
	@UiField ScrollPanel reportScroll;
	@UiField FlexTable tagsTable;
	@UiField FlexTable reportTable;
	@UiField HTML selectedTag;
	
	@UiField HTML weekdayLink;
	boolean weekdayLinkActive;
	
	@UiField HTML saturdayLink;
	boolean saturdayLinkActive;
	
	@UiField HTML sundayHolidayLink;
	boolean sundayHolidayLinkActive;
	
	private StudyRegion currentStudyRegion;
	
	private DateTimeFormat fmt24Hr = DateTimeFormat.getFormat("HH:mm");
	private Date dummy = new Date();
	protected TagDetails currentTagDetails;
	private DialogBox dialog;
	
	private HashMap<String, String> niceDayTypeNames = new HashMap<String, String>();
	
	public TrafficFlowReportView(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		dialog = new DialogBox();
		
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_WEEKDAY, "weekday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SATURDAY, "Saturday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY, "Sunday/Holiday");
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		trafficFlowReportService = GWT.create(TrafficFlowReportService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		disableWeekdayLink();
		hideDayTypeLinks();
		
		bind();
		
	}
	
	@UiHandler("weekdayLink")
	void onClickWeekdayLink(ClickEvent e)
	{
		if(weekdayLinkActive)
		{
			getTrafficCountReport(currentTagDetails, TrafficCountRecord.DAYTYPE_WEEKDAY);
		}
	}

	@UiHandler("saturdayLink")
	void onClickSaturdayLink(ClickEvent e)
	{
		if(saturdayLinkActive)
		{
			getTrafficCountReport(currentTagDetails, TrafficCountRecord.DAYTYPE_SATURDAY);
		}		
	}
	
	@UiHandler("sundayHolidayLink")
	void onClickSundayHolidayLink(ClickEvent e)
	{
		if(sundayHolidayLinkActive)
		{
			getTrafficCountReport(currentTagDetails, TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		}
	}
	
	private void disableWeekdayLink()
	{
		weekdayLinkActive = false;
		weekdayLink.removeStyleName(style.clickable());
		
		saturdayLinkActive = true;
		saturdayLink.addStyleName(style.clickable());
		
		sundayHolidayLinkActive = true;
		sundayHolidayLink.addStyleName(style.clickable());
		
	}

	private void disableSaturdayLink()
	{
		weekdayLinkActive = true;
		weekdayLink.addStyleName(style.clickable());
		
		saturdayLinkActive = false;
		saturdayLink.removeStyleName(style.clickable());
		
		sundayHolidayLinkActive = true;
		sundayHolidayLink.addStyleName(style.clickable());
	}
	
	private void disableSundayHolidayLink()
	{
		weekdayLinkActive = true;
		weekdayLink.addStyleName(style.clickable());
		
		saturdayLinkActive = true;
		saturdayLink.addStyleName(style.clickable());
		
		sundayHolidayLinkActive = false;
		sundayHolidayLink.removeStyleName(style.clickable());
	}
	
	
	@UiHandler("generateReports")
	void onClickGenerateReports(ClickEvent e) {

		if( Window.confirm("Generate reports for all tags?\nThis may take a few minutes") )
		{

			selectedTag.setHTML("");
			clearTable();
			currentTagDetails = null;
			hideDayTypeLinks();
			
			// open a modal dialog;
			dialog.setText("Generating reports");
			dialog.setWidget(new HTML("Generating traffic flow reports for all tags. <br/>This may take a few minutes"));
			dialog.setAutoHideEnabled(false);
			dialog.setGlassEnabled(true);
			dialog.center();
			dialog.show();
			
			trafficFlowReportService.createTrafficFlowReport(currentStudyRegion, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					// close dialog
					dialog.hide();
					Window.alert(caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					// close dialog
					dialog.hide();
					Window.alert("Traffic flow reports for all tags have been generated");
				}
			});			
		}

		
	}
	
	private void hideDayTypeLinks() {
		weekdayLink.setVisible(false);
		saturdayLink.setVisible(false);
		sundayHolidayLink.setVisible(false);
	}

	private void showDayTypeLinks() {
		weekdayLink.setVisible(true);
		saturdayLink.setVisible(true);
		sundayHolidayLink.setVisible(true);
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
				
				// clear out the old selected tag and report table
				selectedTag.setHTML("");
				reportTable.clear();
				reportTable.removeAllRows();
				
				renderTags(event.getTags());
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: TrafficFlowReportView width: " + event.width);
				int h = event.height - 195; // account for other query module UI
				int w = event.width - 476;
				GWT.log("SIZE: TrafficFlowReportView adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: TrafficFlowReportView adjusted height: " + height);
					tagsScroll.setHeight(height);
					reportScroll.setHeight(height);
					
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
					// by default, the first click on tag will
					// always fetch the WEEKDAY report
					currentTagDetails = tagDetails;
					getTrafficCountReport(tagDetails, TrafficCountRecord.DAYTYPE_WEEKDAY);
				}
			});
			tagsTable.setWidget(i, 0, name);
		}
		
	}
	
	private void clearTable()
	{
		reportTable.removeAllRows();
		reportTable.clear();
	}

	protected void getTrafficCountReport(TagDetails tagDetails, String dayType) {
		
		// hack: give a visual clue that this table is being refreshed
		// normally, you don't want a rash, but here we do, so empty the table
		reportTable.removeAllRows();
		reportTable.clear();
		
		currentTagDetails = tagDetails;
		showDayTypeLinks();
		
		selectedTag.setHTML("Selected tag: <b>"+tagDetails.getName()+"</b>");
		
		//tagDetails.setRegion(currentStudyRegion);
		GWT.log("tagDetails prior to report fetch=" + tagDetails);
		
		/*
		String niceDayType = niceDayTypeNames.get(dayType);
		dialog.setText("Please wait...");
		dialog.setWidget(new HTML("Getting "+niceDayType+" traffic flow report for \""+tagDetails.getName()+"\" tag"));
		dialog.setAutoHideEnabled(false);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
		*/
		
		trafficFlowReportService.getTrafficFlowReport(tagDetails, dayType, new AsyncCallback<TrafficFlowReport>() {

			@Override
			public void onFailure(Throwable caught) {
				//dialog.hide();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(TrafficFlowReport result) {
				// TODO Auto-generated method stub
				GWT.log("getTrafficFlowReport result:" + result);
				//dialog.hide();
				if( result.getCreated() == null)
				{
					Window.alert("There was no report for this tag. Please generate all reports.");
				} else {
					String dayType = result.getDayType();
					if( dayType.equalsIgnoreCase(TrafficCountRecord.DAYTYPE_WEEKDAY))
					{
						disableWeekdayLink();
					} else if (dayType.equalsIgnoreCase(TrafficCountRecord.DAYTYPE_SATURDAY)) {
						disableSaturdayLink();
					} else {
						disableSundayHolidayLink();
					}
					renderReport(result);
				}
			}
		});
		
	}

	protected void renderReport(TrafficFlowReport result) {
		
		// clear out the old table
		reportTable.removeAllRows();
		reportTable.clear();
		
		ArrayList<ArrayList> dayTypeValues = result.getDayTypeValues();
		
		HTML hourHeader = new HTML("Hour of day");
		HTML hourRangeHeader = new HTML("Time range");
		
		// one for each vehicle type
		/* PUT THESE OUTSIDE THE SCROLL PANEL
		HTML w2 = new HTML("W2");
		HTML w3 = new HTML("W3");
		HTML pc = new HTML("PC");
		HTML tx = new HTML("TX");
		HTML ldv = new HTML("LDV");
		HTML ldc = new HTML("LDC");
		HTML hdc = new HTML("HDC");
		HTML mdb = new HTML("MDB");
		HTML hdb = new HTML("HDB");
		*/
		
		int row = 0;

		for (Iterator iterator = dayTypeValues.iterator(); iterator.hasNext();) {
			
			int column = 0;
			ArrayList thisRow = (ArrayList) iterator.next();
			for (Iterator iter = thisRow.iterator(); iter.hasNext();) {
				String value = (String) iter.next();
				Label l = new Label(value);
				l.setStyleName(style.cell());
				reportTable.setWidget(row, column, l);
				column++;
			}
			
			row++;
		}
		
		reportScroll.setScrollPosition(0);
		
	}

}
