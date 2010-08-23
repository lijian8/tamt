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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
		String checkbox();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	private TrafficFlowReportServiceAsync trafficFlowReportService;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	@UiField ScrollPanel tagsScroll;
	@UiField ScrollPanel reportScroll;
	@UiField FlexTable tagsTable;
	@UiField FlexTable reportTable;
	@UiField HTML selectedTag;
	
	@UiField ListBox dayTypes;
	@UiField CheckBox toggleAllCheckboxes;
	
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
	
	public TrafficFlowReportView(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		checkboxes = new ArrayList<CheckBox>();
		
		dialog = new DialogBox();
		
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_WEEKDAY, "Weekday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SATURDAY, "Saturday");
		niceDayTypeNames.put(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY, "Sunday/Holiday");
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		trafficFlowReportService = GWT.create(TrafficFlowReportService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		dayTypes.addItem(niceDayTypeNames.get(TrafficCountRecord.DAYTYPE_WEEKDAY), TrafficCountRecord.DAYTYPE_WEEKDAY);
		dayTypes.addItem(niceDayTypeNames.get(TrafficCountRecord.DAYTYPE_SATURDAY), TrafficCountRecord.DAYTYPE_SATURDAY);
		dayTypes.addItem(niceDayTypeNames.get(TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY), TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		dayTypes.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) event.getSource();
				GWT.log("selectedIndex("+lb.getSelectedIndex()+"), selected value("+lb.getValue(lb.getSelectedIndex())+")");
				String selectedDayType = lb.getValue(lb.getSelectedIndex());
				if( currentTagDetails != null)
				{
					getTrafficCountReport(currentTagDetails, selectedDayType);
				}
			}
		});
		
		selectedTag.setHTML(TAG_SELECTED_NONE);
		
		leftPane.setWidth("50%");
		rightPane.setWidth("50%");
		
		outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		bind();
		
	}

	@UiHandler("generateReport")
	void onClickGenerateReports(ClickEvent e) {

		if( Window.confirm("Create reports for all checked tags?") )
		{
			generateReports();
		} else {
			uncheckMasterCheckBox();
			for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
				CheckBox cb = (CheckBox) iterator.next();
				cb.setValue(false);
			}
		}
		

		
	}

	private void generateReports()
	{
		clearTable();
		currentTagDetails = null;
		selectedTag.setHTML(TAG_SELECTED_NONE);
		dayTypes.setSelectedIndex(0);
		toggleAllCheckboxes.setValue(false);
		
		// open a modal dialog;
		dialog.setText("Generating reports");
		dialog.setWidget(new HTML("Generating traffic flow reports for all selected tags. <br/>This may take a few minutes"));
		dialog.setAutoHideEnabled(false);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
		
		// populate the list of tag details selected for report generation
		ArrayList<TagDetails> tagDetailsSelectedForReportGeneration = new ArrayList<TagDetails>();
		
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			if( cb.getValue() )
			{
				TagDetails tagDetails = tagDetailsHash.get(cb.getFormValue());
				tagDetailsSelectedForReportGeneration.add(tagDetails);
			}
		}
		
		
		trafficFlowReportService.createTrafficFlowReport(tagDetailsSelectedForReportGeneration, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// close dialog
				dialog.hide();
				clearAllCheckBoxes();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				// close dialog
				dialog.hide();
				clearAllCheckBoxes();
				Window.alert("Traffic flow reports have been generated");
			}
		});
	}

	protected void clearAllCheckBoxes() {
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(false);
		}
	}

	@UiHandler("toggleAllCheckboxes")
	void onClickToggleAllCheckboxes(ClickEvent e) {
		CheckBox master = (CheckBox) e.getSource();
		GWT.log("toggleCheckboxes:" + master.getValue());
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(master.getValue());
			GWT.log("cb: form value("+cb.getFormValue()+"), checked value("+cb.getValue()+")");
		}	
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
				// selectedTag.setHTML("");
				reportTable.clear();
				reportTable.removeAllRows();
				
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
				GWT.log("SIZE: TrafficFlowReportView width: " + event.width);
				int h = event.height - 225; // account for other query module UI
				int reportH = h - 20;
				int w = event.width - 283; // x - 696 = 564; .: x = 564 + 696 = 1260; 1260 - x = 413
				GWT.log("SIZE: TrafficFlowReportView adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: TrafficFlowReportView adjusted height: " + height);
					tagsScroll.setHeight(height);
					
					String reportHeight = Integer.toString(reportH) + "px";
					reportScroll.setHeight(reportHeight);
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: TrafficFlowReportView adjusted width: " + width);
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
		
        // clear out the checkboxes
        checkboxes.clear();
        uncheckMasterCheckBox();
        
		for (int i = 0; i < tags.size(); i++) {
			final TagDetails tagDetails = tags.get(i);
			
			CheckBox cb = new CheckBox();
			cb.setFormValue(tagDetails.getId()); //store the id in the checkbox value
			checkboxes.add(cb); // keep track for selecting all|none to delete
			cb.setStyleName(style.checkbox());
			
			// if a checkbox is checked, deselect the master checkbox
			cb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					uncheckMasterCheckBox();
				}
			});
			
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
			tagsTable.setWidget(i, 0, cb);
			tagsTable.getCellFormatter().setWidth(i, 0, "20px");
			tagsTable.setWidget(i, 1, name);
		}
		
	}
	
	private void uncheckMasterCheckBox()
	{
		toggleAllCheckboxes.setValue(false);
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
		
		selectedTag.setHTML( TAG_SELECTED + " <b>"+tagDetails.getName()+"</b>");
		
		GWT.log("tagDetails prior to report fetch=" + tagDetails);
		
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
					Window.alert("There was no report for this tag. Please create a report and try again.");
					selectedTag.setHTML( TAG_SELECTED_NONE );
				} else {
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
