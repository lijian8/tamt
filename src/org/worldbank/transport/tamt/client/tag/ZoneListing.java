package org.worldbank.transport.tamt.client.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEvent;
import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEventHandler;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEventHandler;
import org.worldbank.transport.tamt.client.event.CancelRoadEvent;
import org.worldbank.transport.tamt.client.event.CancelZoneEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.DebugEvent;
import org.worldbank.transport.tamt.client.event.DebugEventHandler;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEvent;
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
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEventHandler;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.GetZonesEventHandler;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.RenderZonesEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.RoadService;
import org.worldbank.transport.tamt.client.services.RoadServiceAsync;
import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.client.services.ZoneServiceAsync;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ZoneListing extends Composite {

	public static final String ZONETYPE_RESIDENTIAL = "#RES";
	public static final String ZONETYPE_COMMERCIAL = "#COM";
	public static final String ZONETYPE_INDUSTRIAL = "#IND";
	
	private static ZoneListingUiBinder uiBinder = GWT
			.create(ZoneListingUiBinder.class);

	interface ZoneListingUiBinder extends UiBinder<Widget, ZoneListing> {
	}

	interface ZoneStyle extends CssResource {
	    String zoneList();
	    String checkbox();
	    String clickable();
	}

	@UiField ZoneStyle style;

	@UiField Label select;
	@UiField Label all;
	@UiField Label none;
	@UiField Button save;
	@UiField Button delete;
	@UiField Label refresh;
	
	@UiField TextBox name;
	@UiField TextBox description;
	@UiField(provided=true) ListBox zoneTypes;
	@UiField Label polyline;
	@UiField Label vertices;
	
	@UiField FlexTable zoneList;
	@UiField ScrollPanel scrollPanel;
	
	private HandlerManager eventBus;
	private HashMap<String, TagPolygon> polygonHash;
	
	private String currentZoneDetailId;
	private ZoneServiceAsync zoneService;
	private boolean refreshZoneDetails = true;
	private ArrayList<ZoneDetails> zoneDetailsList;
	private ArrayList<CheckBox> checkboxes;

	private HashMap<String, ArrayList<Vertex>> vertexHash;

	protected TagPolygon currentPolygon;

	protected StudyRegion currentStudyRegion;
	
	public ZoneListing(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		polygonHash = new HashMap<String, TagPolygon>();
		checkboxes = new ArrayList<CheckBox>();
		
		zoneDetailsList = new ArrayList<ZoneDetails>();
		
		zoneService = GWT.create(ZoneService.class);
		
		zoneTypes = new ListBox();
		zoneTypes.addItem("Residential", ZONETYPE_RESIDENTIAL);
		zoneTypes.addItem("Commercial", ZONETYPE_COMMERCIAL);
		zoneTypes.addItem("Industrial", ZONETYPE_INDUSTRIAL);
		zoneTypes.setSelectedIndex(0);
		
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
		refreshZoneDetails = true;
		fetchZoneDetails();
	}	
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		saveZoneDetails();
	}

	@UiHandler("cancel")
	void onClickCancel(ClickEvent e) {
		if( currentZoneDetailId.indexOf("TEMP") != -1)
		{
			eventBus.fireEvent(new CancelZoneEvent(currentZoneDetailId));
		} else {
			clearZoneEditView();
			refreshZoneDetails = true;
			fetchZoneDetails();
		}		
	}	
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		if( Window.confirm("Delete all checked zones?") )
		{
			deleteZoneDetails();
		}
	}	

	private void bind()
	{
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
			}
		});	
		
		eventBus.addHandler(BindPolygonToZoneEvent.TYPE, 
			new BindPolygonToZoneEventHandler() {
			public void onBindPolygonToRoad(BindPolygonToZoneEvent event) {
				TagPolygon p = event.getPolygon();
				currentPolygon = p;
				polygonHash.put(p.getZoneDetailsId(), p);
	    		String n = "";
	    		for (Iterator iterator = zoneDetailsList.iterator(); iterator
						.hasNext();) {
					ZoneDetails zoneDetails = (ZoneDetails) iterator.next();
					if(zoneDetails.getId().equals(currentPolygon.getZoneDetailsId()))
					{
						n = zoneDetails.getName();
					}
				}
	    		vertices.setText("Current polygon in ZoneListing=" + n);	    		
			}
		});
			
		eventBus.addHandler(EndEditPolygonEvent.TYPE,
			new EndEditPolygonEventHandler() {
		    	public void onEndEditPolygon(EndEditPolygonEvent event) {
		    		
		    		TagPolygon p = event.getPolygon();
		    		currentPolygon = p;
		    		currentZoneDetailId = p.getZoneDetailsId();
		    		
		    		polygonHash.put(p.getZoneDetailsId(), p);
		    		polyline.setText(p.getZoneDetailsId());
		    		String n = "";
		    		for (Iterator iterator = zoneDetailsList.iterator(); iterator
							.hasNext();) {
						ZoneDetails zoneDetails = (ZoneDetails) iterator.next();
						if(zoneDetails.getId().equals(currentPolygon.getZoneDetailsId()))
						{
							n = zoneDetails.getName();
						}
					}
		    		vertices.setText("Current polyline in ZoneListing=" + n);
		    		
		    		String zoneName = Window.prompt("Name this zone", "");
		    		if( zoneName == null)
		    		{
		    			eventBus.fireEvent(new CancelZoneEvent(p.getZoneDetailsId()));
		    		} else {
		    			name.setText(zoneName);
			    		description.setText("");
		    		}
		    	}
		});
		
		eventBus.addHandler(GetZonesEvent.TYPE, new GetZonesEventHandler() {
			public void onGetZones(GetZonesEvent event) {
	    		clearZoneEditView();
	    		refreshZoneDetails = true;
	    		fetchZoneDetails();
			}
		});	
		
		eventBus.addHandler(SentUpdatedPolygonEvent.TYPE,
			new SentUpdatedPolygonEventHandler() {
				@Override
				public void onSentUpdatedPolygon(SentUpdatedPolygonEvent event) {
					currentPolygon = event.tagPolygon;
				}
		});
		
		/*
		eventBus.addHandler(DebugEvent.TYPE,
			new DebugEventHandler() {
		    	public void onDebug(DebugEvent event) {
		    		GWT.log("DEBUG ========================= BEGIN DEBUG EVENT IN ROADLISTING ===============");
		    		GWT.log("DEBUG RoadListing: polylineHash keys=" + polygonHash.keySet());
		    		GWT.log("DEBUG RoadListing: currentPolyline=" + currentPolygon);
		    		
		        }
		});	
		*/
		
		eventBus.addHandler(EditZoneDetailsBySegmentEvent.TYPE,
			new EditZoneDetailsBySegmentEventHandler() {
				@Override
				public void onEditZoneDetailsBySegment(EditZoneDetailsBySegmentEvent event) {
					String id = event.getId();
					if( id == null )
					{
						clearZoneEditView();
						return;
					}
					// find the roadDetails with this id in 
					for (Iterator iterator = zoneDetailsList.iterator(); iterator
							.hasNext();) {
						ZoneDetails zoneDetails = (ZoneDetails) iterator.next();
						//GWT.log("EDIT road details loop, working on id=" + roadDetails.getId());
						if( zoneDetails.getId().equals(id))
						{
							loadZoneDetails(zoneDetails);
							break;
						}
					}
				}
			});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				int h = event.height - 280; // account for other UI
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: ZoneListing scroll panel height: " + height);
					scrollPanel.setHeight(height);
				}
			}
		});		
	
		
	}
	
	
	private void deleteZoneDetails()
	{
		ArrayList<String> zoneDetailIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				zoneDetailIds.add(cb.getFormValue());
			}
			
		}
		// now send the list into the async
		zoneService.deleteZoneDetails(zoneDetailIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Zone details deleted");
				refreshZoneDetails = true;
				fetchZoneDetails();
			}
		});
		
	}	
	
	private void saveZoneDetails() {
		
		refreshZoneDetails = true;
		
		ZoneDetails zoneDetails = new ZoneDetails();
		zoneDetails.setName(name.getText());
		zoneDetails.setDescription(description.getText());
		zoneDetails.setId(currentZoneDetailId);
		zoneDetails.setZoneType(zoneTypes.getValue(zoneTypes.getSelectedIndex())); // match to UI bit for zoneType
		zoneDetails.setRegion(currentStudyRegion);
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		try {
			if( currentZoneDetailId == null)
			{
				currentPolygon = null;
			}
			vertices = polylgonToVertexArrayList(currentPolygon);
			zoneDetails.setVertices(vertices);
			
			zoneService.saveZoneDetails(zoneDetails, new AsyncCallback<ZoneDetails>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failed saving ZoneDetails: " + caught.getMessage());
					Window.alert("Failed saving zone details: " + caught.getMessage());
				}

				@Override
				public void onSuccess(ZoneDetails result) {
					GWT.log("ZoneDetails saved");
					eventBus.fireEvent(new DisableZoneEditingEvent());
					clearZoneEditView();
					zoneTypes.setSelectedIndex(0);
					fetchZoneDetails();
				}
				});	
			
		} catch (Exception e) {
			GWT.log(e.getMessage());
			Window.alert(e.getMessage());
			clearZoneEditView();
		}
		
			
	}

	public static ArrayList<Vertex> polylgonToVertexArrayList(TagPolygon polygon) throws Exception
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
			throw new Exception("Please draw a zone before saving zone details");
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

	protected void clearZoneEditView() {
		name.setText("");
		description.setText("");
		currentZoneDetailId = null;
		currentPolygon = null;
		polygonHash = new HashMap<String, TagPolygon>();
		polyline.setText("");
		vertices.setText("");
		save.setText("Save");
	}
	
	private void fetchZoneDetails() {
		
		//if( refreshZoneDetails )
		//{
			//zoneList.removeAllRows();
    		//zoneList.setWidget(0, 0, new HTML("Reloading zones..."));
            
    		//StudyRegion region = new StudyRegion();
    		//region.setName("default");
    		
			zoneService.getZoneDetails(currentStudyRegion, new AsyncCallback<ArrayList<ZoneDetails>>() {
		      
				public void onSuccess(ArrayList<ZoneDetails> result) {
		          
				  // store the result
				  zoneDetailsList = result;
		          
				  refreshZoneDetails = false;
		          clearZoneEditView();
		          
		          GWT.log("zoneDetailsList=" + zoneDetailsList);
		          zoneList.removeAllRows();
		          
		          // create a hash of <roadId>|vertex array to throw over to TagMap for rendering
		          vertexHash = new HashMap<String, ArrayList<Vertex>>();
		          
		          for (int i = 0; i < zoneDetailsList.size(); i++) {
		        	final int count = i;
					final ZoneDetails zoneDetails = zoneDetailsList.get(i);
					
					// stick the vertices for this zoneDetails in the vertexHash
					vertexHash.put(zoneDetails.getId(), zoneDetails.getVertices());
					
					CheckBox cb = new CheckBox();
					cb.setFormValue(zoneDetails.getId()); //store the id in the checkbox value
					checkboxes.add(cb); // keep track for selecting all|none to delete
					cb.setStyleName(style.checkbox());
					cb.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							GWT.log("handle click for checkbox of zoneDetail("+count+")");
						}
					});
					Label name = new Label(zoneDetails.getName());
					name.setStyleName(style.zoneList());
					name.addStyleName(style.clickable());
					name.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							loadZoneDetails(zoneDetails);
						}
					});
					zoneList.setWidget(i, 0, cb);
					zoneList.getCellFormatter().setWidth(i, 0, "20px");
					zoneList.setWidget(i, 1, name);
					
		          }
		          
		          // now tell TagMap to pick up the vertexHash and render the shapes
		          eventBus.fireEvent(new RenderZonesEvent(vertexHash));
		          
		      }
		     
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching zone details");
		        GWT.log(caught.getMessage());
		      }
		    });
		//}
	}	
	
	public void loadZoneDetails(ZoneDetails zoneDetails)
	{
		
		// send a message to TagMap to set the associated polygon to editable
		eventBus.fireEvent(new EditZoneSegmentEvent(zoneDetails.getId()));
		
		name.setText(zoneDetails.getName());
		description.setText(zoneDetails.getDescription());
		currentZoneDetailId = zoneDetails.getId();
		polyline.setText(zoneDetails.getId());
		
		// synchronize the zoneType
		for (int i = 0; i < zoneTypes.getItemCount(); i++) {
			String value = zoneTypes.getValue(i);
			if( zoneDetails.getZoneType().equalsIgnoreCase(value))
			{
				zoneTypes.setSelectedIndex(i);
				break;
			}
		}
		
		save.setText("Update");
		
	}

	public HashMap<String, ArrayList<Vertex>> getVertexHash() {
		return vertexHash;
	}

	public void setVertexHash(HashMap<String, ArrayList<Vertex>> vertexHash) {
		this.vertexHash = vertexHash;
	}
	
}
