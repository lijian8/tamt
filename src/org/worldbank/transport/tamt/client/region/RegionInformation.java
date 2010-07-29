package org.worldbank.transport.tamt.client.region;

import org.worldbank.transport.tamt.client.event.CreatePolygonEvent;
import org.worldbank.transport.tamt.client.event.CreatePolygonEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.GetRegionsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.ShowRoadsEvent;
import org.worldbank.transport.tamt.client.event.ShowTagsEvent;
import org.worldbank.transport.tamt.client.event.ShowZonesEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegionInformation extends Composite {

	private HandlerManager eventBus;
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private TabPanel panel;
	
	private RegionListing regionListing;
	
	private TabBar tabBar;
	
	private HTML title;
	private HTML curentStudyRegionName;
	
	public RegionInformation(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		regionListing = new RegionListing(this.eventBus);
		
		title = new HTML("Current study region:");
		title.setStyleName("studyRegionLabel");
		curentStudyRegionName = new HTML("< Not Set >");
		curentStudyRegionName.setStyleName("studyRegionTitleNoLink");
		
		hPanel = new HorizontalPanel();
		hPanel.add(title);
		hPanel.add(curentStudyRegionName);
		
		vPanel = new VerticalPanel();
		vPanel.add(hPanel);
		
		panel = new TabPanel();
		
		panel.add(regionListing, "Study Regions");
		
		panel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				
				// 0 = Tags, 1 = Roads, 2 = Zones
				int almostSelected = event.getItem();
				switch (almostSelected) {
				case 0:
					if( !regionListing.isVisible())
					{
						showZones();
					}
					break;
				default:
					// do nothing
					break;
				}
			}
		});
		
		panel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				
			}
		});
		
		panel.setWidth("400px");
		
		vPanel.add(panel);
		
		initWidget(vPanel);
	
		
		bind();
		
		// select the first tab
		panel.selectTab(0);
		
		// load the data
		eventBus.fireEvent(new GetRegionsEvent());
		
	}
	
	public void bind()
	{
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				StudyRegion currentStudyRegion = event.studyRegion;
				curentStudyRegionName.setText(currentStudyRegion.getName());
			}
		});	
		
	}
	
	public void showTags()
	{
		eventBus.fireEvent(new ShowTagsEvent());
	}
	
	public void showRoads()
	{
		eventBus.fireEvent(new ShowRoadsEvent());
	}
	
	public void showZones()
	{
		eventBus.fireEvent(new ShowZonesEvent());
	}

}
