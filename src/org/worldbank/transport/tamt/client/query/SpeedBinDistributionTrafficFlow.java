package org.worldbank.transport.tamt.client.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.ClearSpeedBinReportsEvent;
import org.worldbank.transport.tamt.client.event.ClearSpeedBinReportsEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.FetchedTagsEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.query.TrafficCountListView.Style;
import org.worldbank.transport.tamt.client.services.SpeedDistributionReportService;
import org.worldbank.transport.tamt.client.services.SpeedDistributionReportServiceAsync;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.client.services.TrafficFlowReportService;
import org.worldbank.transport.tamt.client.services.TrafficFlowReportServiceAsync;
import org.worldbank.transport.tamt.shared.SpeedDistributionReport;
import org.worldbank.transport.tamt.shared.SpeedDistributionTrafficFlowReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficCountReport;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpeedBinDistributionTrafficFlow extends Composite {

	private static SpeedBinDistributionTrafficFlowUiBinder uiBinder = GWT
			.create(SpeedBinDistributionTrafficFlowUiBinder.class);

	interface SpeedBinDistributionTrafficFlowUiBinder extends UiBinder<Widget, SpeedBinDistributionTrafficFlow> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String dayType();
		String cell();
		String hour();
		String cellHeaderInteger();
		String cellHeaderDouble();
		String checkbox();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	private SpeedDistributionReportServiceAsync speedDistributionReportService;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	@UiField ScrollPanel tagsScroll;
	@UiField ScrollPanel reportScroll;
	@UiField FlexTable tagsTable;
	@UiField FlexTable reportTable;
	@UiField HTML selectedTag;
	@UiField Label downloadReport;
	
	private StudyRegion currentStudyRegion;
	
	private DateTimeFormat fmt24Hr = DateTimeFormat.getFormat("HH:mm");
	private Date dummy = new Date();
	protected TagDetails currentTagDetails;
	private DialogBox dialog;
	
	private HashMap<String, String> niceDayTypeNames = new HashMap<String, String>();
	private ArrayList<CheckBox> checkboxes;
	private ArrayList<TagDetails> tagDetailsList;
	protected HashMap<String, TagDetails> tagDetailsHash;

	private final String TAG_SELECTED_NONE = "No tag selected";
	private final String TAG_SELECTED = "Selected tag: ";
	private final String DOWNLOAD_REPORT = "Download";
	
	
	public SpeedBinDistributionTrafficFlow(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		checkboxes = new ArrayList<CheckBox>();

		dialog = new DialogBox();
		
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_WEEKDAY, "Weekday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SATURDAY, "Saturday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY, "Sunday/Holiday");
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		//trafficFlowReportService = GWT.create(TrafficFlowReportService.class);
		speedDistributionReportService = GWT.create(SpeedDistributionReportService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		resetScreen();
		
		leftPane.setWidth("50%");
		rightPane.setWidth("50%");
		
		outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		bind();
		
	}
	
	@UiHandler("downloadReport")
	void onClickDownloadReport(ClickEvent e){
		
		if( currentTagDetails != null)
		{
			// create the url string for the download
			String url = "/download/speeddistributiontrafficflowreport?tagid=" + currentTagDetails.getId();
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Please select a tag to download a report");
			resetScreen();
		}
	}

	@UiHandler("generateReport")
	void onClickGenerateReports(ClickEvent e) {

		String confirm = 	"Create speed distribution x traffic flow report?\n" +
							"This will delete subsequent reports. You will need\n" +
							"to recreate them after this delete operation is completed.";

		if( Window.confirm(confirm) )
		{
			generateReports();
		} else {
			
		}
		
	}

	private void generateReports()
	{
		clearTable();
		currentTagDetails = null;
		
		// open a modal dialog;
		dialog.setText("Generating reports");
		dialog.setWidget(new HTML("Generating speed distribution x traffic report for all tags. <br/>This may take a few minutes"));
		dialog.setAutoHideEnabled(false);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
		
		speedDistributionReportService.createSpeedDistributionTrafficFlowReport(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				resetScreen();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				dialog.hide();
				resetScreen();
				Window.alert("Speed distribution x traffic flow reports have been generated");
			}
		});
		
	}

	private void resetScreen()
	{
		resetRightPane();
	}
	
	private void resetRightPane()
	{
		selectedTag.setHTML(TAG_SELECTED_NONE);
		currentTagDetails = null;
		downloadReport.setVisible(false);
		reportTable.removeAllRows();
		reportTable.clear();
	}
	
	private void bind() {
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				resetScreen();
			}
		});		
		
		eventBus.addHandler(ClearSpeedBinReportsEvent.TYPE, new ClearSpeedBinReportsEventHandler() {
			
			@Override
			public void onClearSpeedBinReports(ClearSpeedBinReportsEvent event) {
				resetScreen();
			}
		});
		
		eventBus.addHandler(FetchedTagsEvent.TYPE, new FetchedTagsEventHandler() {
			
			@Override
			public void onFetchedTags(FetchedTagsEvent event) {
				
				resetScreen();
				
				// build a hash of tags for ease of use in report generation
				tagDetailsHash = new HashMap<String, TagDetails>();
				for (Iterator iterator = event.getTags().iterator(); iterator
						.hasNext();) {
					TagDetails tagDetails = (TagDetails) iterator.next();
					tagDetailsHash.put(tagDetails.getId(), tagDetails);
				}
				
				renderTags(event.getTags());
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: SpeedBinDistributionTrafficFlow width: " + event.width);
				int h = event.height - 225; // account for other query module UI
				int reportH = h - 20;
				int w = event.width - 283; // x - 696 = 564; .: x = 564 + 696 = 1260; 1260 - x = 413
				GWT.log("SIZE: SpeedBinDistributionTrafficFlow adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: SpeedBinDistributionTrafficFlow adjusted height: " + height);
					tagsScroll.setHeight(height);
					
					String reportHeight = Integer.toString(reportH) + "px";
					reportScroll.setHeight(reportHeight);
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: SpeedBinDistributionTrafficFlow adjusted width: " + width);
					rightPane.setWidth(width);
				}
				
			}
		});	
	}

	protected void renderTags(ArrayList<TagDetails> tags) {
		
		// keep track of the tags for report generation
		tagDetailsList = tags;
		
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
					resetRightPane();
					currentTagDetails = tagDetails;
					getSpeedDistributionTrafficFlowReport(tagDetails);
				}
			});
			tagsTable.getCellFormatter().setWidth(i, 0, "20px");
			tagsTable.setWidget(i, 1, name);
		}
		
	}
	
	private void clearTable()
	{
		reportTable.removeAllRows();
		reportTable.clear();
	}

	protected void getSpeedDistributionTrafficFlowReport(TagDetails tagDetails) {
		
		reportTable.removeAllRows();
		reportTable.clear();
		
		currentTagDetails = tagDetails;
		
		selectedTag.setHTML( TAG_SELECTED + " <b>"+tagDetails.getName()+"</b>");
		downloadReport.setVisible(true);
		
		GWT.log("tagDetails prior to report fetch=" + tagDetails);
		
		speedDistributionReportService.getSpeedDistributionTrafficFlowReport(tagDetails, new AsyncCallback<SpeedDistributionTrafficFlowReport>() {

			public void onSuccess(SpeedDistributionTrafficFlowReport result) {
				// TODO Auto-generated method stub
				GWT.log("getSpeedDistributionTrafficFlowReport result:" + result);
				if( result.getCreated() == null)
				{
					Window.alert("There was no report for this tag. Please create a report and try again.");
					resetScreen();
				} else {
					GWT.log("renderReport");
					renderReport(result);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
	}

	protected void renderReport(SpeedDistributionTrafficFlowReport result) {
		
		GWT.log("begin render report");
		
		reportTable.setCellPadding(2);
		reportTable.setCellSpacing(1);
		
		// clear out the old table
		reportTable.removeAllRows();
		reportTable.clear();
		
		ArrayList<ArrayList> reportValues = result.getReporValues();
		
		GWT.log("row count=" + reportValues.size());
		int row = 0;

		double doubleValue = 0.0;
		for (Iterator iterator = reportValues.iterator(); iterator.hasNext();) {
			
			int column = 0;
			ArrayList thisRow = (ArrayList) iterator.next();
			for (Iterator iter = thisRow.iterator(); iter.hasNext();) {
				String value = (String) iter.next();
				Label l = new Label(value);
				if( column == 0 || column == 1 || column == 2)
				{
					l.setStyleName(style.dayType());
				} else {
					if( !l.getText().equalsIgnoreCase("0"))
					{
						doubleValue = Double.parseDouble(l.getText());
						l.setText(NumberFormat.getFormat("#0.00000").format(doubleValue)); // reformat double values to 
					}
					l.setStyleName(style.cellHeaderDouble());
				}
				reportTable.setWidget(row, column, l);
				column++;
			}
			
			row++;
		}
		
		GWT.log("end render report");
		reportScroll.setScrollPosition(0);
		
	}

}
