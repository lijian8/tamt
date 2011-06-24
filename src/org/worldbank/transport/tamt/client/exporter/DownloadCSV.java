package org.worldbank.transport.tamt.client.exporter;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DownloadCSV extends Composite {

	private static DownloadCSVUiBinder uiBinder = GWT
			.create(DownloadCSVUiBinder.class);

	interface DownloadCSVUiBinder extends UiBinder<Widget, DownloadCSV> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String dayType();
		String cell();
		String hour();
		String hourHeader();
		String cellHeaderInteger();
		String cellHeaderDouble();
		String checkbox();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField VerticalPanel rightPane;
	@UiField Label vehicleFlowTrafficFlowReport;
	
	private StudyRegion currentStudyRegion;
	
	public DownloadCSV(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
				
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		resetScreen();
		
		//leftPane.setWidth("50%");
		rightPane.setWidth("50%");
		
		//outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		bind();
		
	}
	
	@UiHandler("roadLengthByTagReport")
	void onClickRoadLengthByTagReport(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			// create the url string for the download
			String url = "/download/roadlength/roadlengthbytag?regionid=" + currentStudyRegion.getId();
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download road length by tag report at this time");
			resetScreen();
		}
	}
	
	@UiHandler("vehicleFlowTrafficFlowReport")
	void onClickVehicleFlowTrafficFlowReport(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			String url = "/download/vehicleflow/trafficflowreport";
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download traffic flow report at this time");
			resetScreen();
		}
	}

	@UiHandler("speedBinsSpeedDistribution")
	void onClickDownloadReport(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			String url = "/download/speedbin/speeddistribution";
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download speed distribution report at this time");
			resetScreen();
		}
	}
	
	@UiHandler("speedBinsSpeedDistributionTrafficFlow")
	void onClickSpeedBinsSpeedDistributionTrafficFlow(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			String url = "/download/speedbin/trafficflowreport";
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download speed distribution x traffic flow report at this time");
			resetScreen();
		}
	}	
	
	@UiHandler("speedBinsSpeedDistributionAggregateByDayType")
	void onClickSpeedBinsSpeedDistributionAggregateByDayType(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			String url = "/download/speedbin/aggregatebydaytypereport";
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download aggregate by day type report at this time");
			resetScreen();
		}
	}	
	
	@UiHandler("speedBinsSpeedDistributionAggregateByTag")
	void onClickSpeedBinsSpeedDistributionAggregateByTag(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			String url = "/download/speedbin/aggregatebytagreport";
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download aggregate by tag report at this time");
			resetScreen();
		}
	}	
	
	@UiHandler("tripStatsTripBin")
	void onClickTripStatsTripBin(ClickEvent e){		
		if( currentStudyRegion != null)
		{
			// create the url string for the download
			String url = "/download/tripstatistics/tripbin?regionid=" + currentStudyRegion.getId();
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download trip bin report at this time");
			resetScreen();
		}
	}
	
	@UiHandler("tripStatsSoakTimes")
	void onClickTripStatsSoakTimes(ClickEvent e){
		
		if( currentStudyRegion != null)
		{
			// create the url string for the download
			String url = "/download/tripstatistics/soakbin?regionid=" + currentStudyRegion.getId();
			Window.open(url, "_blank", null);
		} else {
			Window.alert("Cannot download soak bin report at this time");
			resetScreen();
		}
	}	
	
	private void resetScreen()
	{
		resetRightPane();
	}
	
	private void resetRightPane()
	{
		// nothing to do
	}
	
	private void bind() {
		
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				resetScreen();
			}
		});		
		
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: DownloadCSV width: " + event.width);
				int h = event.height - 250; // account for other query module UI
				int reportH = h - 20;
				int w = event.width - 90;
				GWT.log("SIZE: DownloadCSV adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: DownloadCSV adjusted height: " + height);
					//tagsScroll.setHeight(height);
					
					String reportHeight = Integer.toString(reportH) + "px";
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: DownloadCSV adjusted width: " + width);
					rightPane.setWidth(width);
				}
				
			}
		});	
	}

	

}
