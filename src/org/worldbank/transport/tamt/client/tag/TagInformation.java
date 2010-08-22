package org.worldbank.transport.tamt.client.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.ClearRoadOverlaysEvent;
import org.worldbank.transport.tamt.client.event.ClearZoneOverlaysEvent;
import org.worldbank.transport.tamt.client.event.ClearZoneOverlaysEventHandler;
import org.worldbank.transport.tamt.client.event.CreatePolyLineEvent;
import org.worldbank.transport.tamt.client.event.CreatePolyLineEventHandler;
import org.worldbank.transport.tamt.client.event.CreatePolygonEvent;
import org.worldbank.transport.tamt.client.event.CreatePolygonEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEventHandler;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.LoadCurrentStudyRegionEvent;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEvent;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.ShowRoadsEvent;
import org.worldbank.transport.tamt.client.event.ShowTagsEvent;
import org.worldbank.transport.tamt.client.event.ShowZonesEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.maps.client.event.MapDragEndHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TagInformation extends Composite {

	private final HandlerManager eventBus;
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private TabPanel panel;
	
	private TagListing tagListing;
	private ZoneListing zoneListing;
	private RoadListing roadListing;
	
	private HTML title;
	private HTML studyRegion;
	
	private TabBar tabBar;

	protected StudyRegion currentStudyRegion;
	
	public TagInformation(HandlerManager _eventBus)
	{
		eventBus = _eventBus;
		
		
		tagListing = new TagListing(eventBus);
		zoneListing = new ZoneListing(eventBus);
		roadListing = new RoadListing(eventBus);
		
		title = new HTML("Current study region:");
		title.setStyleName("studyRegionLabel");
		studyRegion = new HTML("< Not Set >");
		studyRegion.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.REGION, true));
				eventBus.fireEvent(new LoadCurrentStudyRegionEvent());
			}
		});
		
		studyRegion.setStyleName("studyRegionTitle");
		
		hPanel = new HorizontalPanel();
		hPanel.add(title);
		hPanel.add(studyRegion);
		
		vPanel = new VerticalPanel();
		vPanel.add(hPanel);
		
		panel = new TabPanel();
		
		panel.add(tagListing , "Tags");
		panel.add(roadListing, "Roads");
		panel.add(zoneListing, "Zones");
		
		panel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				
				try {
					// 0 = Tags, 1 = Roads, 2 = Zones
					// TODO: place before Tab select
	            	GWT.log("TAG get tags, roads and zones");
	            	int almostSelected = event.getItem();
					switch (almostSelected) {
					case 0:
						eventBus.fireEvent(new GetTagsEvent());
		        		if( !tagListing.isVisible())
						{
							showTags();
						}
						break;
					case 1:
						eventBus.fireEvent(new GetRoadsEvent());
						if( !roadListing.isVisible())
						{
							showRoads();
						}
						break;
					case 2:
						eventBus.fireEvent(new GetZonesEvent());
						if( !zoneListing.isVisible())
						{
							showZones();
						}
						break;
					default:
						// do nothing
						break;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
					GWT.log("An unknown exception occured:" + e.getMessage());
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
		
		// select the Tags tab (and trigger tag loading)
		// once the DOM is ready
		DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				// TODO Auto-generated method stub
				panel.selectTab(0);
			}
		});
		// 
		
	}
	
	public void bind()
	{
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				if (currentStudyRegion != null){
					studyRegion.setText(currentStudyRegion.getName());
				} else {
					studyRegion.setText("<Not Set>");
				}
				panel.selectTab(0);
	
			}
		});	
		
		/*
		eventBus.addHandler(ReceivedTagsEvent.TYPE,
			new ReceivedTagsEventHandler() {
				@Override
				public void onReceivedTags(ReceivedTagsEvent event) {
					// transfer tagDetailsList from TagListing to RoadListing
					ArrayList<TagDetails> tagDetailsList = tagListing.getTagDetailsList();
					roadListing.setTagDetails(tagDetailsList);
				}
		});
		*/
		
		eventBus.addHandler(CreatePolyLineEvent.TYPE,
			new CreatePolyLineEventHandler() {
		    	public void onCreatePolyLine(CreatePolyLineEvent event) {
		            // switch tab to roads
		    		panel.selectTab(1);
		    		roadListing.clearRoadEditView();
		    }
		});
		eventBus.addHandler(CreatePolygonEvent.TYPE,
			new CreatePolygonEventHandler() {
		    	public void onCreatePolygon(CreatePolygonEvent event) {
		            // switch tab to zones
		    		panel.selectTab(2);
		    }
		});		
		eventBus.addHandler(EditRoadDetailsBySegmentEvent.TYPE,
			new EditRoadDetailsBySegmentEventHandler() {
				public void onEditRoadDetailsBySegment(EditRoadDetailsBySegmentEvent event) {
					panel.selectTab(1); // switch to roads
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
	
	public HashMap<String, ArrayList<Vertex>> getRoadListingVertexHash() {
		return roadListing.getVertexHash();
	}
	
	public void setCurrentTagPolyline(TagPolyline polyline)
	{
		roadListing.currentPolyline = polyline;
	}
}
