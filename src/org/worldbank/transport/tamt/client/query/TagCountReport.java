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
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TagCountReport extends Composite {

	private static TagCountReportUiBinder uiBinder = GWT
			.create(TagCountReportUiBinder.class);

	interface TagCountReportUiBinder extends UiBinder<Widget, TagCountReport> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String hour();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	private TrafficCountRecordServiceAsync trafficCountRecordService;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField HorizontalPanel rightPaneHPanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	//@UiField HTML midPane;
	@UiField ScrollPanel tagsScroll;
	@UiField ScrollPanel reportScroll;
	@UiField FlexTable tagsTable;
	@UiField FlexTable reportTable;
	@UiField HTML totalCount;
	@UiField HTML selectedTag;
	
	private StudyRegion currentStudyRegion;
	
	private DateTimeFormat fmt24Hr = DateTimeFormat.getFormat("HH:mm");
	private Date dummy = new Date();
	
	public TagCountReport(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		trafficCountRecordService = GWT.create(TrafficCountRecordService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		
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
				selectedTag.setHTML("");
				renderTags(event.getTags());
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: TagCountReport width: " + event.width);
				int h = event.height - 195; // account for other query module UI
				int w = event.width - 476;
				GWT.log("SIZE: TagCountReport adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: TagCountReport adjusted height: " + height);
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
					getTrafficCountReport(tagDetails.getName());
				}
			});
			tagsTable.setWidget(i, 0, name);
		}
		
	}

	protected void getTrafficCountReport(String tagName) {
		
		// hack: give a visual clue that this table is being refreshed
		// normally, you don't want a rash, but here we do, so empty the table
		reportTable.removeAllRows();
		reportTable.clear();
		
		TagDetails tagDetails = new TagDetails();
		tagDetails.setName(tagName);
		
		selectedTag.setHTML("Selected tag: <b>"+tagName+"</b>");
		
		tagDetails.setRegion(currentStudyRegion);

		//TODO: put up a glass-modal dialog saying we are fetching the report
		trafficCountRecordService.getTrafficCountReport(tagDetails, new AsyncCallback<TrafficCountReport>() {
			
			@Override
			public void onSuccess(TrafficCountReport result) {
				// TODO: close the glass-modal dialog
				renderReport(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not retrieve report:" + caught.getMessage());
			}
		});
		
	}

	protected void renderReport(TrafficCountReport result) {
		
		// force the width for the left pane
		// leftPane.setWidth("360px");
		//midPane.setWidth("20px");
		
		// clear out the old table
		reportTable.removeAllRows();
		reportTable.clear();
		
		ArrayList<Integer> weekdayTotals = result.getWeekdayTotals();
		ArrayList<Integer> saturdayTotals = result.getSaturdayTotals();
		ArrayList<Integer> sundayHolidayTotals = result.getSundayHolidayTotals();
		
		HTML hourHeader = new HTML("Hour of day");
		HTML hourRangeHeader = new HTML("Start time range");
		HTML weekdayHeader = new HTML("Weekday");
		HTML saturdayHeader = new HTML("Saturday");
		HTML sundayHolidayHeader = new HTML("Sunday / Holiday");
		
		/* PUT THESE OUTSIDE THE SCROLL PANEL
		reportTable.setWidget(0, 0, hourHeader);
		reportTable.setWidget(0, 1, hourRangeHeader);
		reportTable.setWidget(0, 2, weekdayHeader);
		reportTable.setWidget(0, 3, saturdayHeader);
		reportTable.setWidget(0, 4, sundayHolidayHeader);
		*/
		
		int row = 0;
		
		// there are always 24 rows (0..23)
		for (int i = 0; i < 24; i++) {
			
			Label hour = new Label(Integer.toString(i));
			hour.setStyleName(style.hour());
			
			Label currentWeekdayTotal = new Label(Integer.toString(weekdayTotals.get(i)));
			currentWeekdayTotal.setStyleName(style.record());
			
			Label currentSaturdayTotal = new Label(Integer.toString(saturdayTotals.get(i)));
			currentSaturdayTotal.setStyleName(style.record());
			
			Label currentSundayHolidayTotal = new Label(Integer.toString(sundayHolidayTotals.get(i)));
			currentSundayHolidayTotal.setStyleName(style.record());
			
			// use the dummy date to format an hour range "hh:mm-hh:mm"
			dummy.setHours(i);
			dummy.setMinutes(0);
			String startHour = fmt24Hr.format(dummy);
			dummy.setMinutes(59);
			String endHour = fmt24Hr.format(dummy);
			Label hourRange = new Label(startHour + "-" + endHour);
			hourRange.setStyleName(style.record());
			
			// row = row + 1; // account for headers *** REMOVED HEADERS ***
			row = i;
			
			reportTable.setWidget(row, 0, hour);
			reportTable.setWidget(row, 1, hourRange);
			reportTable.setWidget(row, 2, currentWeekdayTotal);
			reportTable.setWidget(row, 3, currentSaturdayTotal);
			reportTable.setWidget(row, 4, currentSundayHolidayTotal);
			
		}		
		
		// always put the scroll bar at the top
		reportScroll.setScrollPosition(0);
		
	}

}
