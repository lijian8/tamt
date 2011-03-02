package org.worldbank.transport.tamt.client.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.BindPolygonToRegionEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToRegionEventHandler;
import org.worldbank.transport.tamt.client.event.CacheRegionMapMetaDataEvent;
import org.worldbank.transport.tamt.client.event.CacheRegionMapMetaDataEventHandler;
import org.worldbank.transport.tamt.client.event.CancelRegionEvent;
import org.worldbank.transport.tamt.client.event.CancelRegionEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.DisableRegionEditingEvent;
import org.worldbank.transport.tamt.client.event.EditRegionDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRegionDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditRegionSegmentEvent;
import org.worldbank.transport.tamt.client.event.EndEditRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.EndEditRegionPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.GetRegionsEvent;
import org.worldbank.transport.tamt.client.event.GetRegionsEventHandler;
import org.worldbank.transport.tamt.client.event.LoadCurrentStudyRegionEvent;
import org.worldbank.transport.tamt.client.event.LoadCurrentStudyRegionEventHandler;
import org.worldbank.transport.tamt.client.event.RenderRegionsEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedRegionPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.client.services.RegionServiceAsync;
import org.worldbank.transport.tamt.client.tag.ZoneListing;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.maps.client.geom.LatLng;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	@UiField(provided=true) ListBox zoneTypes;
	

	@UiField CheckBox toggleAllCheckboxes;
	@UiField Button save;
	@UiField Button copy;
	@UiField Button delete;
	@UiField Button refresh;
	
	@UiField TextBox name;
	@UiField TextBox description;
	@UiField TextBox utcOffset;
	
	@UiField TextBox commercialZoneBlockLength;
	@UiField TextBox industrialZoneBlockLength;
	@UiField TextBox residentialZoneBlockLength;
	
	@UiField TextBox minimumSoakInterval;
	
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
	private boolean wasDeletingCurrentStudyRegion;
	
	public RegionListing(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		polygonHash = new HashMap<String, RegionPolygon>();
		//cacheRegionMapMetaData = new HashMap<String, StudyRegion>();
		
		checkboxes = new ArrayList<CheckBox>();
		
		studyRegionList = new ArrayList<StudyRegion>();
		
		zoneTypes = new ListBox();
		zoneTypes.addItem("Residential", ZoneListing.ZONETYPE_RESIDENTIAL);
		zoneTypes.addItem("Commercial", ZoneListing.ZONETYPE_COMMERCIAL);
		zoneTypes.addItem("Industrial", ZoneListing.ZONETYPE_INDUSTRIAL);
		zoneTypes.setSelectedIndex(0);
		
		regionService = GWT.create(RegionService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
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
	
	@UiHandler("refresh")
	void onClickRefresh(ClickEvent e) {
		refreshStudyRegions = true;
		regionList.removeAllRows();
		uncheckMasterCheckBox();
		fetchStudyRegions();
	}	

	@UiHandler("copy")
	void onClickCopy(ClickEvent e) {
		copyStudyRegion();
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
		if( Window.confirm("Delete all checked regions?" +
				"\nThis will delete all associated roads, zones" +
				", GPS traces, any tagged points.") )
		{
			deleteRegionDetails();
		} else {
			uncheckMasterCheckBox();
			for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
				CheckBox cb = (CheckBox) iterator.next();
				cb.setValue(false);
			}
		}
	}	

	private void uncheckMasterCheckBox()
	{
		toggleAllCheckboxes.setValue(false);
	}
	
	private void bind()
	{
		
		eventBus.addHandler(LoadCurrentStudyRegionEvent.TYPE, new LoadCurrentStudyRegionEventHandler() {
			
			@Override
			public void onLoad(LoadCurrentStudyRegionEvent event) {
				for (Iterator iterator = studyRegionList.iterator(); iterator
						.hasNext();) {
					StudyRegion studyRegion = (StudyRegion) iterator.next();
					if( studyRegion.isCurrentRegion() )
					{
						loadRegionDetails(studyRegion);
					}
				}
			}
		});
		
		eventBus.addHandler(SentUpdatedRegionPolygonEvent.TYPE, new SentUpdatedRegionPolygonEventHandler() {
			
			@Override
			public void onSentUpdatedPolygon(SentUpdatedRegionPolygonEvent event) {
				currentPolygon = event.regionPolygon;
			}
		});
		
		eventBus.addHandler(CacheRegionMapMetaDataEvent.TYPE, new CacheRegionMapMetaDataEventHandler() {
			
			@Override
			public void onCache(CacheRegionMapMetaDataEvent event) {
				GWT.log("DUPE CacheRegionMapMetaDataEventHandler");
				GWT.log("DUPE Apply mapCenter and zoomLevel to currentStudyRegionId:" + currentStudyRegionId);
				for (Iterator<StudyRegion> iterator = studyRegionList.iterator(); iterator
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
						
						GWT.log("DUPE Study region now has map view:" + studyRegion);
						
						break;
					}
				}
				
				// now, it makes sense to save the study region here automatically
				// rather than forcing the user to do it
				GWT.log("DUPE call saveStudyRegion");
				saveStudyRegion();
				
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
				
				int h = event.height - 500; // account for other study region UI (was 392)
				
				if( h > -1 )
				{
				String height = Integer.toString(h) + "px";
				GWT.log("SIZE: RegionListing scroll panel height: " + height);
				scrollPanel.setHeight(height);
				}
				
			}
		});		
		
		eventBus.addHandler(BindPolygonToRegionEvent.TYPE, 
			new BindPolygonToRegionEventHandler() {
			public void onBindPolygonToRegion(BindPolygonToRegionEvent event) {
				RegionPolygon p = event.getPolygon();
				currentPolygon = p;
				GWT.log("DUPE BindPolygonToRegionEventHandler currentPolygon="+currentPolygon);
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
		    		
		    		RegionPolygon p = event.polygon;
		    		currentPolygon = p;
		    		GWT.log("DUPE EndEditRegionPolygonEventHandler currentPolygon="+currentPolygon);
		    		currentStudyRegionId = p.getRegionDetailsId();
		    		
		    		polygonHash.put(p.getRegionDetailsId(), p);
		    		
		    		// create a minimal StudyRegion with mapCenter and mapZoomLevel
		    		// and put it into the studyRegion list. This handler is only
		    		// run after a new StudyRegion is drawn, so we can safely add
		    		// it to the study region list
		    		StudyRegion minimal = new StudyRegion();
		    		minimal.setId(currentStudyRegionId);
		    		minimal.setMapCenter(p.getMapCenter());
		    		minimal.setMapZoomLevel(p.getMapZoomLevel());
		    		studyRegionList.add(minimal);
		    		
		    		/*
		    		 * Debug: is the currentStudyRegionId the same as the currentPolygon.getRegionDetailsId()?
		    		 */
		    		GWT.log("DUPE currentStudyRegionId("+currentStudyRegionId+"), currentPolygon.getRegionDetailsId("+currentPolygon.getRegionDetailsId()+") ");
		    		
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
		    		GWT.log("DUPE id("+currentPolygon.getRegionDetailsId()+"), name("+regionName+")");
		    	}
		});
		
		eventBus.addHandler(GetRegionsEvent.TYPE, new GetRegionsEventHandler() {
			public void onGetRegions(GetRegionsEvent event) {
	    		clearRegionEditView();
	    		refreshStudyRegions = true;
	    		fetchStudyRegions();
			}
		});	
		
		eventBus.addHandler(EditRegionDetailsBySegmentEvent.TYPE,
			new EditRegionDetailsBySegmentEventHandler() {
				@Override
				public void onEditRegionDetailsBySegment(EditRegionDetailsBySegmentEvent event) {
					currentPolygon = event.regionPolygon;
					GWT.log("DUPE EditRegionDetailsBySegmentEventHandler currentPolygon=" + currentPolygon);
					if( currentPolygon == null )
					{
						clearRegionEditView();
						return;
					}
					// find the roadDetails with this id in 
					for (Iterator iterator = studyRegionList.iterator(); iterator
							.hasNext();) {
						StudyRegion studyRegion = (StudyRegion) iterator.next();
						//GWT.log("EDIT road details loop, working on id=" + roadDetails.getId());
						if( studyRegion.getId().equals(currentPolygon.getRegionDetailsId()))
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
				uncheckMasterCheckBox();
				fetchStudyRegions();
			}
		});
		
	}	
	
	private void copyStudyRegion()
	{
		// ask the user for a new name
		String regionName = Window.prompt("New name for copied region", "");
		String regionIdToBeCopied = null;
		if( regionName != null)
		{
			ArrayList<String> studyRegionIds = new ArrayList<String>();
			for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
				CheckBox cb = (CheckBox) iterator.next();
				// boolean to see if it is checked
				if(cb.getValue())
				{
					String studyRegionId = cb.getFormValue();
					studyRegionIds.add(studyRegionId);
					regionIdToBeCopied = studyRegionId;
				}
			}
			if( studyRegionIds.size() > 1)
			{
				Window.alert("Only one region can be copied at a time");
			} else 
			{
				//Window.alert("Copy the region..." + regionIdToBeCopied + " to " + regionName);
				StudyRegion toCopy = new StudyRegion();
				toCopy.setId(regionIdToBeCopied);
				toCopy.setName(regionName);
				regionService.copyStudyRegion(toCopy, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Copy study region failed: " + caught);
					}

					@Override
					public void onSuccess(Void result) {
						Window.alert("Copy study region succeeded");
						eventBus.fireEvent(new GetRegionsEvent());
					}
				});
			}
			
		} else {
			// do nothing
		}		
	}

	private void saveStudyRegion() {
		
		if( currentPolygon == null || currentStudyRegionId == null )
		{
			Window.alert("Please draw a region on the map before saving.");
			clearRegionEditView();
			return;
		};
		
		refreshStudyRegions = true;
		// currentPolygon after clicking on BRASIL is still CANADA
		GWT.log("DUPE currentPolygon ID=" + currentPolygon.getRegionDetailsId());
		GWT.log("DUPE currentStudyRegionId ID=" + currentStudyRegionId);
		if( !currentStudyRegionId.equalsIgnoreCase( currentPolygon.getRegionDetailsId() ))
		{
			GWT.log("DUPE WARNING **** currentPolygon and currentStudyRegionId are NOT synchronized!");
		}
		
		StudyRegion studyRegion = new StudyRegion();
		studyRegion.setName(name.getText());
		studyRegion.setDescription(description.getText());
		studyRegion.setId(currentStudyRegionId);
		studyRegion.setCurrentRegion(currentStudyRegionCheckBox.getValue());
		studyRegion.setDefaultZoneType(zoneTypes.getValue(zoneTypes.getSelectedIndex()));
		studyRegion.setUtcOffset(utcOffset.getValue());
		studyRegion.setCommercialZoneBlockLength(commercialZoneBlockLength.getText());
		studyRegion.setIndustrialZoneBlockLength(industrialZoneBlockLength.getText());
		studyRegion.setResidentialZoneBlockLength(residentialZoneBlockLength.getText());
		studyRegion.setMinimumSoakInterval(minimumSoakInterval.getText());
		GWT.log("DUPE Saving study region with id:" + currentStudyRegionId);
		
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
				GWT.log("DUPE Study region now has map view:" + studyRegion);
				
				break;
			}
		}
		
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		try {
			if( currentStudyRegionId == null)
			{
				currentPolygon = null;
			}
			GWT.log("DUPE saveStudyRegion currentPolygon="+currentPolygon);
			
			vertices = polylgonToVertexArrayList(currentPolygon);
			studyRegion.setVertices(vertices);
			
			GWT.log("DUPE Saving study region:" + studyRegion);
			
			
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

	protected void clearRegionEditView() {
		name.setText("");
		description.setText("");
		utcOffset.setText("");
		commercialZoneBlockLength.setText("");
		industrialZoneBlockLength.setText("");
		residentialZoneBlockLength.setText("");
		minimumSoakInterval.setText("");
		currentStudyRegionId = null;
		currentPolygon = null;
		GWT.log("DUPE clearRegionEditView currentPolygon="+currentPolygon);
		
		polygonHash = new HashMap<String, RegionPolygon>();
		polyline.setText("");
		vertices.setText("");
		currentStudyRegionCheckBox.setValue(false);
		save.setText("Save");
		
		zoneTypes.setSelectedIndex(0);
		
	}
	
	private void fetchStudyRegions() {
		
		if( refreshStudyRegions )
		{
			regionService.getStudyRegions(new AsyncCallback<ArrayList<StudyRegion>>() {

				@Override
			      public void onFailure(Throwable caught) {
			        Window.alert("Error fetching study regions");
			        GWT.log("Error fetching study regions: " + caught.getMessage());
			      }

				@Override
				public void onSuccess(ArrayList<StudyRegion> result) {
					 // store the result
					  studyRegionList = result;
			          
					  
			          clearRegionEditView();
					  
					  refreshStudyRegions = false;
					  
			          GWT.log("studyRegionList=" + studyRegionList);
			          regionList.removeAllRows();
			          
					  /*
					   * If there are no study regions...
					   */
					  if( studyRegionList.size() == 0 )
					  {
						  GWT.log("CURRENT STUDY REGION: No study regions, set current study region to NULL");
						  eventBus.fireEvent(new CurrentStudyRegionUpdatedEvent(null));
					  }
					  
			          // create a hash of <roadId>|vertex array to throw over to RegionMap for rendering
			          vertexHash = new HashMap<String, ArrayList<Vertex>>();
			          
			          boolean hasCurrentStudyRegion = false;
			          
			          // clear out the checkboxes
			          checkboxes.clear();
			          uncheckMasterCheckBox();
			          
			          for (int i = 0; i < studyRegionList.size(); i++) {
			        	final int count = i;
						final StudyRegion studyRegion = studyRegionList.get(i);
						
						// if this is the current study region, let all the widgets know
						if( studyRegion.isCurrentRegion() )
						{
							// fire a new event with the minimal info required
							StudyRegion minimal = new StudyRegion();
							minimal.setId(studyRegion.getId());
							minimal.setName(studyRegion.getName());
							minimal.setMapCenter(studyRegion.getMapCenter());
							minimal.setMapZoomLevel(studyRegion.getMapZoomLevel());
							
							hasCurrentStudyRegion = true;
							
							GWT.log("RegionListing firing CurrentStudyRegionUpdatedEvent from RegionListing");
							eventBus.fireEvent(new CurrentStudyRegionUpdatedEvent(minimal));
						}
						
						
						// stick the vertices for this regionDetails in the vertexHash
						vertexHash.put(studyRegion.getId(), studyRegion.getVertices());
						
						CheckBox cb = new CheckBox();
						cb.setFormValue(studyRegion.getId()); //store the id in the checkbox value
						checkboxes.add(cb); // keep track for selecting all|none to delete
						cb.setStyleName(style.checkbox());
						
						// if a checkbox is checked, deselect the master checkbox
						cb.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								uncheckMasterCheckBox();
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
			          
			          /*
			           * If none of the study regions were set as current..
			           */
			          if( !hasCurrentStudyRegion )
			          {
			        	  GWT.log("CURRENT STUDY REGION: None of the study regions are set as current");
			        	  eventBus.fireEvent(new CurrentStudyRegionUpdatedEvent(null));
			          }
			          
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
		
		// synchronize the zoneType
		for (int i = 0; i < zoneTypes.getItemCount(); i++) {
			String value = zoneTypes.getValue(i);
			if( studyRegion.getDefaultZoneType().equalsIgnoreCase(value))
			{
				zoneTypes.setSelectedIndex(i);
				break;
			}
		}
		utcOffset.setText(studyRegion.getUtcOffset());
		commercialZoneBlockLength.setText(studyRegion.getCommercialZoneBlockLength());
		industrialZoneBlockLength.setText(studyRegion.getIndustrialZoneBlockLength());
		residentialZoneBlockLength.setText(studyRegion.getResidentialZoneBlockLength());
		minimumSoakInterval.setText(studyRegion.getMinimumSoakInterval());
		save.setText("Update");
		
	}

	public HashMap<String, ArrayList<Vertex>> getVertexHash() {
		return vertexHash;
	}

	public void setVertexHash(HashMap<String, ArrayList<Vertex>> vertexHash) {
		this.vertexHash = vertexHash;
	}
	
}
