package org.worldbank.transport.tamt.client.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEvent;
import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEventHandler;
import org.worldbank.transport.tamt.client.event.BindPolygonToRegionEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToRegionEventHandler;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEventHandler;
import org.worldbank.transport.tamt.client.event.CacheRegionMapMetaDataEvent;
import org.worldbank.transport.tamt.client.event.CacheRegionMapMetaDataEventHandler;
import org.worldbank.transport.tamt.client.event.CancelRegionEvent;
import org.worldbank.transport.tamt.client.event.CancelRegionEventHandler;
import org.worldbank.transport.tamt.client.event.CancelRoadEvent;
import org.worldbank.transport.tamt.client.event.CancelRoadEventHandler;
import org.worldbank.transport.tamt.client.event.CancelZoneEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.DebugEvent;
import org.worldbank.transport.tamt.client.event.DebugEventHandler;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableRegionEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEvent;
import org.worldbank.transport.tamt.client.event.EditRegionDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRegionDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditRegionSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditZoneSegmentEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEventHandler;
import org.worldbank.transport.tamt.client.event.EndEditPolygonEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.EndEditRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.EndEditRegionPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.GetRegionsEvent;
import org.worldbank.transport.tamt.client.event.GetRegionsEventHandler;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEventHandler;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.GetZonesEventHandler;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEvent;
import org.worldbank.transport.tamt.client.event.RenderRegionsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.RenderZonesEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.client.services.RegionServiceAsync;
import org.worldbank.transport.tamt.client.services.RoadService;
import org.worldbank.transport.tamt.client.services.RoadServiceAsync;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.client.services.ZoneServiceAsync;
import org.worldbank.transport.tamt.shared.RegionDetails;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegionListing extends Composite {

	private static RegionListingUiBinder uiBinder = GWT
			.create(RegionListingUiBinder.class);

	interface RegionListingUiBinder extends UiBinder<Widget, RegionListing> {
	}

	interface RegionStyle extends CssResource {
	    String zoneList();
	    String checkbox();
	    String clickable();
	}

	@UiField RegionStyle style;

	@UiField Label select;
	@UiField Label all;
	@UiField Label none;
	@UiField Button save;
	@UiField Button delete;
	@UiField Label refresh;
	
	@UiField TextBox name;
	@UiField TextBox description;
	@UiField Label polyline;
	@UiField Label vertices;
	
	@UiField FlexTable regionList;
	@UiField ScrollPanel scrollPanel;
	@UiField CheckBox currentStudyRegionCheckBox;
	
	private HandlerManager eventBus;
	private HashMap<String, RegionPolygon> polygonHash;
	//private HashMap<String, StudyRegion> cacheRegionMapMetaData;
	
	private String currentStudyRegionId;
	private RegionServiceAsync regionService;
	private boolean refreshStudyRegions = true;
	private ArrayList<StudyRegion> studyRegionList;
	private ArrayList<CheckBox> checkboxes;

	private HashMap<String, ArrayList<Vertex>> vertexHash;

	protected RegionPolygon currentPolygon;
	
	public RegionListing(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		polygonHash = new HashMap<String, RegionPolygon>();
		//cacheRegionMapMetaData = new HashMap<String, StudyRegion>();
		
		checkboxes = new ArrayList<CheckBox>();
		
		studyRegionList = new ArrayList<StudyRegion>();
		
		regionService = GWT.create(RegionService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	}

	@UiHandler("all")
	void onClickAll(ClickEvent e) {
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(true);
		}
	}

	@UiHandler("none")
	void onClickNone(ClickEvent e) {
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(false);
		}
	}
	
	@UiHandler("refresh")
	void onClickRefresh(ClickEvent e) {
		refreshStudyRegions = true;
		fetchStudyRegions();
	}	
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		saveStudyRegion();
	}

	@UiHandler("cancel")
	void onClickCancel(ClickEvent e) {
		if( currentStudyRegionId.indexOf("TEMP") != -1)
		{
			eventBus.fireEvent(new CancelRegionEvent(currentStudyRegionId));
		} else {
			clearRegionEditView();
			refreshStudyRegions = true;
			fetchStudyRegions();
		}		
	}	
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		if( Window.confirm("Delete all checked regions?") )
		{
			deleteRegionDetails();
		}
	}	

	private void bind()
	{
		
		eventBus.addHandler(CacheRegionMapMetaDataEvent.TYPE, new CacheRegionMapMetaDataEventHandler() {
			
			@Override
			public void onCache(CacheRegionMapMetaDataEvent event) {
				GWT.log("MAP CacheRegionMapMetaDataEventHandler");
				// TODO: apply zoomLevel and mapCenter to currently selected region
				GWT.log("Apply mapCenter and zoomLevel to currentStudyRegionId:" + currentStudyRegionId);
				for (Iterator iterator = studyRegionList.iterator(); iterator
						.hasNext();) {
					StudyRegion studyRegion = (StudyRegion) iterator.next();
					if( studyRegion.getId().equals(currentStudyRegionId))
					{
						LatLng center = event.center;
						int mapZoomLevel = event.zoomLevel;
						
						// convert LatLng to Vertex
						Vertex mapCenter = new Vertex();
						mapCenter.setLat(center.getLatitude());
						mapCenter.setLng(center.getLongitude());
						
						studyRegion.setMapCenter(mapCenter);
						studyRegion.setMapZoomLevel(mapZoomLevel);
						
						GWT.log("Study region now has map view:" + studyRegion);
						
						break;
					}
				}
			}
		});
		
		eventBus.addHandler(CancelRegionEvent.TYPE, new CancelRegionEventHandler() {

			@Override
			public void onCancelRegion(CancelRegionEvent event) {
				clearRegionEditView();
			}

		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: RegionListing scroll panel height within: " + event.height);
				
				int h = event.height - 278; // account for other study region UI
				
				String height = Integer.toString(h) + "px";
				GWT.log("SIZE: RegionListing scroll panel height: " + height);
				
				scrollPanel.setHeight(height);
				
			}
		});		
		
		eventBus.addHandler(BindPolygonToRegionEvent.TYPE, 
			new BindPolygonToRegionEventHandler() {
			public void onBindPolygonToRegion(BindPolygonToRegionEvent event) {
				RegionPolygon p = event.getPolygon();
				currentPolygon = p;
				polygonHash.put(p.getRegionDetailsId(), p);
	    		String n = "";
	    		for (Iterator iterator = studyRegionList.iterator(); iterator
						.hasNext();) {
	    			StudyRegion studyRegion = (StudyRegion) iterator.next();
					if(studyRegion.getId().equals(currentPolygon.getRegionDetailsId()))
					{
						n = studyRegion.getName();
					}
				}
	    		vertices.setText("Current polygon in RegionListing=" + n);   		
			}
		});
			
		eventBus.addHandler(EndEditRegionPolygonEvent.TYPE,
			new EndEditRegionPolygonEventHandler() {
		    	public void onEndEditRegionPolygon(EndEditRegionPolygonEvent event) {
		    		
		    		RegionPolygon p = event.getPolygon();
		    		currentPolygon = p;
		    		currentStudyRegionId = p.getRegionDetailsId();
		    		
		    		polygonHash.put(p.getRegionDetailsId(), p);
		    		
		    		polyline.setText(p.getRegionDetailsId());
		    		String n = "";
		    		for (Iterator iterator = studyRegionList.iterator(); iterator
							.hasNext();) {
		    			StudyRegion studyRegion = (StudyRegion) iterator.next();
						if(studyRegion.getId().equals(currentPolygon.getRegionDetailsId()))
						{
							n = studyRegion.getName();
						}
					}
		    		vertices.setText("Current polygon in RegionListing=" + n);
		    		
		    		String regionName = Window.prompt("Name this region", "");
		    		if( regionName == null)
		    		{
		    			eventBus.fireEvent(new CancelRegionEvent(p.getRegionDetailsId()));
		    		} else {
		    			name.setText(regionName);
			    		description.setText("");
			    		description.setFocus(true);
		    		}
		    	}
		});
		
		eventBus.addHandler(GetRegionsEvent.TYPE, new GetRegionsEventHandler() {
			public void onGetRegions(GetRegionsEvent event) {
	    		clearRegionEditView();
	    		refreshStudyRegions = true;
	    		fetchStudyRegions();
			}
		});	
		
		/*
		eventBus.addHandler(SentUpdatedPolygonEvent.TYPE,
			new SentUpdatedPolygonEventHandler() {
				@Override
				public void onSentUpdatedPolygon(SentUpdatedPolygonEvent event) {
					//TODO: update currentPolygon = event.tagPolygon;
				}
		});
		*/
		
		eventBus.addHandler(EditRegionDetailsBySegmentEvent.TYPE,
			new EditRegionDetailsBySegmentEventHandler() {
				@Override
				public void onEditRegionDetailsBySegment(EditRegionDetailsBySegmentEvent event) {
					String id = event.getId();
					GWT.log("RegionListing: Edit Region Deails By Segment");
					if( id == null )
					{
						clearRegionEditView();
						return;
					}
					// find the roadDetails with this id in 
					for (Iterator iterator = studyRegionList.iterator(); iterator
							.hasNext();) {
						StudyRegion studyRegion = (StudyRegion) iterator.next();
						//GWT.log("EDIT road details loop, working on id=" + roadDetails.getId());
						if( studyRegion.getId().equals(id))
						{
							loadRegionDetails(studyRegion);
							break;
						}
					}
				}
			});
	
		
	}
	
	
	private void deleteRegionDetails()
	{
		ArrayList<String> studyRegionIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				String studyRegionId = cb.getFormValue();
				
				// add the id to the list to be deleted
				studyRegionIds.add(studyRegionId);
				
				// also remove the associated study region from
				// the map meta data cache
				/*
				if( cacheRegionMapMetaData.containsKey(studyRegionId) )
				{
					cacheRegionMapMetaData.remove(studyRegionId);
				}
				*/
				
			}
			
		}
		// now send the list into the async
		regionService.deleteStudyRegions(studyRegionIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Region details deleted");
				refreshStudyRegions = true;
				fetchStudyRegions();
			}
		});
		
	}	

	private void saveStudyRegion() {
		
		/*
		 * The map meta-data is saved on every zoom of the RegionMap,
		 * and inserts the currentPolygon id, 
		 */
		
		refreshStudyRegions = true;
		
		StudyRegion studyRegion = new StudyRegion();
		studyRegion.setName(name.getText());
		studyRegion.setDescription(description.getText());
		studyRegion.setId(currentStudyRegionId);
		studyRegion.setCurrentRegion(currentStudyRegionCheckBox.getValue());
		
		GWT.log("Saving study region with id:" + currentStudyRegionId);
		
		/*
		 * We updated the study region in the list with map data, so,
		 * fetch it out of there
		 */
		for (Iterator iterator = studyRegionList.iterator(); iterator
				.hasNext();) {
			StudyRegion sr = (StudyRegion) iterator.next();
			if( sr.getId().equals(currentStudyRegionId))
			{
				// transfer mapCenter and mapZoomLevel
				studyRegion.setMapCenter(sr.getMapCenter());
				studyRegion.setMapZoomLevel(sr.getMapZoomLevel());
				GWT.log("Study region now has map view:" + studyRegion);
				
				break;
			}
		}
		
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		try {
			if( currentStudyRegionId == null)
			{
				currentPolygon = null;
			}
			vertices = polylgonToVertexArrayList(currentPolygon);
			studyRegion.setVertices(vertices);
			
			GWT.log("Current polygon:" + currentPolygon);
			GWT.log("Saving study region:" + studyRegion);
			
			
			regionService.saveStudyRegion(studyRegion, new AsyncCallback<StudyRegion>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed saving StudyRegion: " + caught.getMessage());
					Window.alert("Failed saving study region: " + caught.getMessage());
				}

				@Override
				public void onSuccess(StudyRegion result) {
					GWT.log("StudyRegion saved");
					eventBus.fireEvent(new DisableRegionEditingEvent());
					
					// put a copy in the cache to be used for map meta data
					//cacheRegionMapMetaData.put(result.getId(), result);
					
					clearRegionEditView();
					fetchStudyRegions();
				}
			});
			
		} catch (Exception e) {
			GWT.log(e.getMessage());
			Window.alert(e.getMessage());
			clearRegionEditView();
		}
		
			
	}

	public static ArrayList<Vertex> polylgonToVertexArrayList(RegionPolygon polygon) throws Exception
	{
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		if( polygon != null)
		{
			for (int i = 0; i < polygon.getVertexCount(); i++) {
				LatLng latLng = polygon.getVertex(i);
				Vertex v = new Vertex();
				v.setLat(latLng.getLatitude());
				v.setLng(latLng.getLongitude());
				vertices.add(v);
			}
		} else {
			throw new Exception("Please draw a study region on the map before trying to save");
		}
		return vertices;
	}

	/*
	public void setTagDetails(ArrayList<TagDetails> tagDetails) {
		this.tagDetailsList = tagDetails;
		GWT.log("Update the suggest box with the tag details names");
		for (Iterator iterator = tagDetails.iterator(); iterator.hasNext();) {
			TagDetails td = (TagDetails) iterator.next();
			tagSuggestions.add(td.getName());
		}
	}
	*/

	protected void clearRegionEditView() {
		name.setText("");
		description.setText("");
		currentStudyRegionId = null;
		currentPolygon = null;
		polygonHash = new HashMap<String, RegionPolygon>();
		polyline.setText("");
		vertices.setText("");
		currentStudyRegionCheckBox.setValue(false);
		save.setText("Save");
	}
	
	private void fetchStudyRegions() {
		
		if( refreshStudyRegions )
		{
			regionService.getStudyRegions(new AsyncCallback<ArrayList<StudyRegion>>() {

				@Override
			      public void onFailure(Throwable caught) {
			        Window.alert("Error fetching study regions");
			        GWT.log(caught.getMessage());
			      }

				@Override
				public void onSuccess(ArrayList<StudyRegion> result) {
					 // store the result
					  studyRegionList = result;
			          
					  refreshStudyRegions = false;
			          clearRegionEditView();
			          
			          GWT.log("studyRegionList=" + studyRegionList);
			          regionList.removeAllRows();
			          
			          // create a hash of <roadId>|vertex array to throw over to RegionMap for rendering
			          vertexHash = new HashMap<String, ArrayList<Vertex>>();
			          
			          for (int i = 0; i < studyRegionList.size(); i++) {
			        	final int count = i;
						final StudyRegion studyRegion = studyRegionList.get(i);
						
						// if this is the current study region, let all the widgets know
						if( studyRegion.isCurrentRegion() )
						{
							// fire a new event
							eventBus.fireEvent(new CurrentStudyRegionUpdatedEvent(studyRegion));
						}
						
						
						// stick the vertices for this regionDetails in the vertexHash
						vertexHash.put(studyRegion.getId(), studyRegion.getVertices());
						
						CheckBox cb = new CheckBox();
						cb.setFormValue(studyRegion.getId()); //store the id in the checkbox value
						checkboxes.add(cb); // keep track for selecting all|none to delete
						cb.setStyleName(style.checkbox());
						cb.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								GWT.log("handle click for checkbox of zoneDetail("+count+")");
							}
						});
						Label name = new Label(studyRegion.getName());
						name.setStyleName(style.zoneList());
						name.addStyleName(style.clickable());
						name.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								loadRegionDetails(studyRegion);
							}
						});
						regionList.setWidget(i, 0, cb);
						regionList.getCellFormatter().setWidth(i, 0, "20px");
						regionList.setWidget(i, 1, name);
						
						
			          }
			          
			          // now tell RegionMap to pick up the vertexHash and render the shapes
			          GWT.log("Fire RenderRegionsEvent");
			          eventBus.fireEvent(new RenderRegionsEvent(vertexHash));
				}
			});
			
		}
		
	}	
	
	public void loadRegionDetails(StudyRegion studyRegion)
	{
		
		// send a message to RegionMap to set the associated polygon to editable
		String id = studyRegion.getId();
		Vertex v = studyRegion.getMapCenter();
		LatLng center = LatLng.newInstance(v.getLat(), v.getLng());
		int zoomLevel = studyRegion.getMapZoomLevel();
		
		eventBus.fireEvent(new EditRegionSegmentEvent(id, center, zoomLevel));
		
		name.setText(studyRegion.getName());
		description.setText(studyRegion.getDescription());
		currentStudyRegionId = studyRegion.getId();
		polyline.setText(studyRegion.getId());
		currentStudyRegionCheckBox.setValue(studyRegion.isCurrentRegion());
		
		save.setText("Update");
		
	}

	public HashMap<String, ArrayList<Vertex>> getVertexHash() {
		return vertexHash;
	}

	public void setVertexHash(HashMap<String, ArrayList<Vertex>> vertexHash) {
		this.vertexHash = vertexHash;
	}
	
}
