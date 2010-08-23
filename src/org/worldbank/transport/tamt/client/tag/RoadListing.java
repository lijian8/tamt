package org.worldbank.transport.tamt.client.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEvent;
import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEventHandler;
import org.worldbank.transport.tamt.client.event.CancelRoadEvent;
import org.worldbank.transport.tamt.client.event.CancelRoadEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.DebugEvent;
import org.worldbank.transport.tamt.client.event.DebugEventHandler;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEventHandler;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEventHandler;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.RoadService;
import org.worldbank.transport.tamt.client.services.RoadServiceAsync;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.Vertex;

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
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RoadListing extends Composite {

	private static RoadListingUiBinder uiBinder = GWT
			.create(RoadListingUiBinder.class);

	interface RoadListingUiBinder extends UiBinder<Widget, RoadListing> {
	}

	interface RoadStyle extends CssResource {
	    String roadList();
	    String checkbox();
	    String clickable();
	}

	@UiField RoadStyle style;

	@UiField Button save;
	@UiField Button delete;
	@UiField Button refresh;
	@UiField CheckBox toggleAllCheckboxes;
	
	@UiField TextBox name;
	@UiField TextBox description;
	@UiField Label polyline;
	@UiField Label vertices;
	
	@UiField FlexTable roadList;
	@UiField ScrollPanel scrollPanel;
	
	private HandlerManager eventBus;
	private HashMap<String, TagPolyline> polylineHash;
	private HashMap<String, Polygon> polygonHash;
	
	private final MultiWordSuggestOracle tagSuggestions = new MultiWordSuggestOracle();

	@UiField(provided=true) SuggestBox tagSuggestBox;
	
	private String currentRoadDetailId;
	private RoadServiceAsync roadService;
	private boolean refreshRoadDetails = true;
	private ArrayList<RoadDetails> roadDetailsList;
	private ArrayList<CheckBox> checkboxes;

	private ArrayList<TagDetails> tagDetailsList;

	private HashMap<String, ArrayList<Vertex>> vertexHash;

	protected TagPolyline currentPolyline;

	protected StudyRegion currentStudyRegion;
	
	public RoadListing(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		polygonHash = new HashMap<String, Polygon>();
		polylineHash = new HashMap<String, TagPolyline>();
		tagSuggestBox = new SuggestBox(tagSuggestions);
		checkboxes = new ArrayList<CheckBox>();
		
		roadDetailsList = new ArrayList<RoadDetails>();
		
		roadService = GWT.create(RoadService.class);
		
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
		refreshRoadDetails = true;
		roadList.removeAllRows();
		uncheckMasterCheckBox();
		fetchRoadDetails();
	}	
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		boolean refresh = false;
		if( save.getText().equalsIgnoreCase("save" ))
		{
			refresh = true;
			saveRoadDetails(refresh);
		} else {
			refresh = true; // workaround for now because line is not updated properly without refresh
			saveRoadDetails(refresh);
		}
		
	}

	@UiHandler("cancel")
	void onClickCancel(ClickEvent e) {
		if( currentRoadDetailId != null && currentRoadDetailId.indexOf("TEMP") != -1)
		{
			eventBus.fireEvent(new CancelRoadEvent(currentRoadDetailId));
		} else {
			refreshRoadDetails = true;
			fetchRoadDetails();
		}
	}	
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		if( Window.confirm("Delete all checked roads?") )
		{
			deleteRoadDetails();
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
		
		eventBus.addHandler(FetchedTagsEvent.TYPE, new FetchedTagsEventHandler() {
			
			@Override
			public void onFetchedTags(FetchedTagsEvent event) {
				// update the oracle with the tags
				tagSuggestions.clear();
				tagDetailsList = event.getTags();
				for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
					TagDetails td = (TagDetails) iterator.next();
					tagSuggestions.add(td.getName());
				}
			}
		});
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
			}
		});	
		
		eventBus.addHandler(BindPolyLineToRoadEvent.TYPE,
			new BindPolyLineToRoadEventHandler() {
		    	public void onBindPolyLineToRoad(BindPolyLineToRoadEvent event) {
		    		TagPolyline p = event.getPolyline();
		    		currentPolyline = p;
		    		polylineHash.put(p.getRoadDetailsId(), p);
		    		/* FOR DEBUGGING ONLY
		    		GWT.log("BindPolyLineToRoadEvent...");
		    		GWT.log("roadlisting has p=" + p);
		    		GWT.log("roadlisting has p.getRoadDetailsId=" + p.getRoadDetailsId());
		    		GWT.log("Inserting polyline into hash");
		    		GWT.log("DEBUG BindPolyLineToRoadEvent put key("+p.getRoadDetailsId()+") in polylineHash");
		    		polyline.setText(p.getRoadDetailsId());
		    		String n = "";
		    		for (Iterator iterator = roadDetailsList.iterator(); iterator
							.hasNext();) {
						RoadDetails roadDetails = (RoadDetails) iterator.next();
						if(roadDetails.getId().equals(currentPolyline.getRoadDetailsId()))
						{
							n = roadDetails.getName();
						}
					}
		    		vertices.setText("Current polyline in RoadListing=" + n);
		    		*/
		    	}
		});	
		
		eventBus.addHandler(EndEditPolyLineEvent.TYPE,
			new EndEditPolyLineEventHandler() {
		    	public void onEndEditPolyLine(EndEditPolyLineEvent event) {
		    		
		    		TagPolyline p = event.getPolyline();
		    		currentPolyline = p;
		    		currentRoadDetailId = p.getRoadDetailsId();
		    		
		    		GWT.log("roadlisting has p=" + p);
		    		GWT.log("roadlisting has p.getRoadDetailsId=" + p.getRoadDetailsId());
		    		GWT.log("Inserting polyline into hash");
		    		polylineHash.put(p.getRoadDetailsId(), p);
		    		polyline.setText(p.getRoadDetailsId());
		    		String n = "";
		    		for (Iterator iterator = roadDetailsList.iterator(); iterator
							.hasNext();) {
						RoadDetails roadDetails = (RoadDetails) iterator.next();
						if(roadDetails.getId().equals(currentPolyline.getRoadDetailsId()))
						{
							n = roadDetails.getName();
						}
					}
		    		vertices.setText("Current polyline in RoadListing=" + n);
		    		
		    		String roadName = Window.prompt("Name this road", "");
		    		if( roadName == null)
		    		{
		    			eventBus.fireEvent(new CancelRoadEvent(p.getRoadDetailsId()));
		    			//refreshRoadDetails = true;
		    			//fetchRoadDetails();
		    			
		    		} else {
		    			name.setText(roadName);
			    		description.setText("");
		    		}
		    		
		    	}
		});
		
		eventBus.addHandler(GetRoadsEvent.TYPE,
			new GetRoadsEventHandler() {
		    	public void onGetRoads(GetRoadsEvent event) {
		    		clearRoadEditView();
		    		// TODO: remove force reload after testing is complete
		    		refreshRoadDetails = true;
		    		fetchRoadDetails();
		        }
		});		
		
		
		eventBus.addHandler(SentUpdatedPolylineEvent.TYPE,
			new SentUpdatedPolylineEventHandler() {
		    	public void onSentUpdatedPolyline(SentUpdatedPolylineEvent event) {
		    		currentPolyline = event.tagPolyline;
		    		//Window.alert("SentUpdatedPolylineEvent id=" + currentPolyline.getRoadDetailsId());
		    		//saveRoadDetails();
		        }
		});	
		
		eventBus.addHandler(DebugEvent.TYPE,
			new DebugEventHandler() {
		    	public void onDebug(DebugEvent event) {
		    		GWT.log("DEBUG ========================= BEGIN DEBUG EVENT IN ROADLISTING ===============");
		    		GWT.log("DEBUG RoadListing: polylineHash keys=" + polylineHash.keySet());
		    		GWT.log("DEBUG RoadListing: currentPolyline=" + currentPolyline);
		    		
		        }
		});	
		
		eventBus.addHandler(EditRoadDetailsBySegmentEvent.TYPE,
			new EditRoadDetailsBySegmentEventHandler() {
				@Override
				public void onEditRoadDetailsBySegment(EditRoadDetailsBySegmentEvent event) {
					String id = event.getId();
					//GWT.log("EDIT road details id=" + id);
					if( id == null )
					{
						clearRoadEditView();
						return;
					}
					// find the roadDetails with this id in 
					for (Iterator iterator = roadDetailsList.iterator(); iterator
							.hasNext();) {
						RoadDetails roadDetails = (RoadDetails) iterator.next();
						//GWT.log("EDIT road details loop, working on id=" + roadDetails.getId());
						if( roadDetails.getId().equals(id))
						{
							loadRoadDetails(roadDetails);
							break;
						}
					}
				}
			});
		
			eventBus.addHandler(CancelRoadEvent.TYPE, new CancelRoadEventHandler() {
				@Override
				public void onCancelRoad(CancelRoadEvent event) {
					clearRoadEditView();
				}
			});
			
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				int h = event.height - 284; // account for other UI
				if( h > -1 )
				{
				String height = Integer.toString(h) + "px";
				GWT.log("SIZE: RoadListing scroll panel height: " + height);
				scrollPanel.setHeight(height);
				}
			}
		});
	}
	
	private void deleteRoadDetails()
	{
		/*
		 * The string ids for the selected checkboxes are stored
		 * in the checkboxes array, but CheckBox cannot go over the wire,
		 * so we need to transfer them into a String array here
		 */
		ArrayList<String> roadDetailIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				roadDetailIds.add(cb.getFormValue());
			}
			
		}
		// now send the list into the async
		roadService.deleteRoadDetails(roadDetailIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				refreshRoadDetails = true;
				uncheckMasterCheckBox();
				fetchRoadDetails();
			}
		});
		
	}	
	
	private void saveRoadDetails(final boolean refresh) {
		
		//refreshRoadDetails = true;
		
		RoadDetails roadDetails = new RoadDetails();
		roadDetails.setName(name.getText());
		roadDetails.setDescription(description.getText());
		roadDetails.setId(currentRoadDetailId);
		roadDetails.setRegion(currentStudyRegion);
		
		/*
		 * Take tag field from suggest box, look up the tagID
		 */
		String selectedTag = tagSuggestBox.getText();
		for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) iterator.next();
			if( tagDetails.getName().equals(selectedTag))
			{
				roadDetails.setTagId(tagDetails.getId());
				break;
			}
		}
		GWT.log("selectedTag("+selectedTag+"), tagId("+roadDetails.getTagId()+")");
		
		/*
		 * Polyline can't RPC, so we need to convert the vertices
		 * to Vertex, and store in the RoadDetails
		 */
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		try {
			
			/*
			 * If we have are trying to save without first drawing
			 * a line (and thereby created a temp ID, then set
			 * the currentPolyline to null to prevent the last
			 * rendered polyline in TagMap from being duplicated
			 * as this road's new shape
			 */
			//Window.alert("currentRoadDetailId=" + currentRoadDetailId);
			if( currentRoadDetailId == null)
			{
				currentPolyline = null;
			}
			//Window.alert("currentPolyline=" + currentPolyline);
			
			vertices = polylineToVertexArrayList(currentPolyline);
			//Window.alert("vertex count=" + vertices.size());
			
			roadDetails.setVertices(vertices);
			
			roadService.saveRoadDetails(roadDetails, new AsyncCallback<RoadDetails>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					GWT.log("Failed saving RoadDetails: " + caught.getMessage());
					Window.alert("Failed saving road details: " + caught.getMessage());
				}

				@Override
				public void onSuccess(RoadDetails result) {
					// TODO Auto-generated method stub
					eventBus.fireEvent(new DisableLineEditingEvent());
					clearRoadEditView();
					
					refreshRoadDetails = true;
					fetchRoadDetails();
					/*
					if( refresh )
					{
						refreshRoadDetails = true;
						fetchRoadDetails();
					} else {
						// update the roadDetails in the local list
						int pos = -1;
						for (int i = 0; i < roadDetailsList.size(); i++) {
							RoadDetails rd = roadDetailsList.get(i);
							if( rd.getId().equalsIgnoreCase(result.getId()))
							{
								roadDetailsList.set(i, result);
								break;
							}
						}
						
						//TODO: if the name changed, we need to sort the list
						
						// and re-render the table
						renderRoadTable(roadDetailsList);
						
					}
					*/
				}
				});		
		} catch (Exception e) {
			GWT.log(e.getMessage());
			Window.alert(e.getMessage());
			clearRoadEditView();
		}
		
			
	}
	
	public void renderRoadTable(ArrayList<RoadDetails> roadDetailList)
	{
		// store the result
		this.roadDetailsList = roadDetailList;
        
		refreshRoadDetails = false;
        clearRoadEditView();
        
        GWT.log("roadDetailsList=" + roadDetailsList);
        roadList.removeAllRows();
        
        // create a hash of <roadId>|vertex array to throw over to TagMap for rendering
        vertexHash = new HashMap<String, ArrayList<Vertex>>();
        
        // clear out the checkboxes
        checkboxes.clear();
        uncheckMasterCheckBox();
        
        for (int i = 0; i < roadDetailsList.size(); i++) {
      	final int count = i;
			final RoadDetails roadDetails = roadDetailsList.get(i);
			
			// stick the vertices for this roadDetail in the vertexHash
			vertexHash.put(roadDetails.getId(), roadDetails.getVertices());
			
			GWT.log(roadDetails.getName());
			
			CheckBox cb = new CheckBox();
			cb.setFormValue(roadDetails.getId()); //store the id in the checkbox value
			checkboxes.add(cb); // keep track for selecting all|none to delete
			cb.setStyleName(style.checkbox());
			// if a checkbox is checked, deselect the master checkbox
			cb.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					uncheckMasterCheckBox();
				}
			});
			Label name = new Label(roadDetails.getName());
			name.setStyleName(style.roadList());
			name.addStyleName(style.clickable());
			name.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					/*
					 * There is no guarantee that the current polyline
					 * is the same as the one associated with these
					 * road details. So we fire the 
					 * eventBus.fireEvent(new EditRoadSegmentEvent(roadDetails.getId()));
					 * TagMap listens for this, and prepares the correct
					 * polyline for editing. We expect, in return, to
					 * receive a CurrentEditableRoadSentEvent to send
					 * back the right shape.
					 */
					loadRoadDetails(roadDetails);
				}
			});
			roadList.setWidget(i, 0, cb);
			roadList.getCellFormatter().setWidth(i, 0, "20px");
			roadList.setWidget(i, 1, name);
			
        }
        	
	}

	public static ArrayList<Vertex> polylineToVertexArrayList(TagPolyline polyline) throws Exception
	{
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		if( polyline != null)
		{
			for (int i = 0; i < polyline.getVertexCount(); i++) {
				LatLng latLng = polyline.getVertex(i);
				Vertex v = new Vertex();
				v.setLat(latLng.getLatitude());
				v.setLng(latLng.getLongitude());
				vertices.add(v);
			}
		} else {
			throw new Exception("Please draw a road before saving road details");
		}
		return vertices;
	}

	public void setTagDetails(ArrayList<TagDetails> tagDetails) {
		this.tagDetailsList = tagDetails;
		GWT.log("Update the suggest box with the tag details names");
		for (Iterator iterator = tagDetails.iterator(); iterator.hasNext();) {
			TagDetails td = (TagDetails) iterator.next();
			tagSuggestions.add(td.getName());
		}
	}


	protected void clearRoadEditView() {
		name.setText("");
		description.setText("");
		currentRoadDetailId = null;
		currentPolyline = null;
		polylineHash = new HashMap<String, TagPolyline>();
		tagSuggestBox.setText("");
		polyline.setText("");
		vertices.setText("");
		save.setText("Save");
	}
	
	private void fetchRoadDetails() {
		
		//if( refreshRoadDetails )
		//{
			//roadList.removeAllRows();
			//roadList.setWidget(0, 0, new HTML("Reloading roads..."));
            
    		// TODO: for now we just use a Default region
    		//StudyRegion region = new StudyRegion();
    		//region.setName("default");
    		
			roadService.getRoadDetails(currentStudyRegion, new AsyncCallback<ArrayList<RoadDetails>>() {
		      
				public void onSuccess(ArrayList<RoadDetails> result) {
		          
					renderRoadTable(result);
					
					// render road table updates global vertexHash, so go ahead
					// and tell TagMap to pick it up to render most recent lines
			        eventBus.fireEvent(new RenderRoadsEvent(vertexHash));
			       	
				}
		     
		      public void onFailure(Throwable caught) {
		        Window.alert("Error fetching road details");
		        GWT.log(caught.getMessage());
		      }
		    });
		//}
	}	
	
	public void loadRoadDetails(RoadDetails roadDetails)
	{
		
		// send a message to TagMap to set the associated polyline to editable
		eventBus.fireEvent(new EditRoadSegmentEvent(roadDetails.getId()));
		
		/*
		 * Where do I get the current polyline from?
		 */
		//GWT.log("EDIT road details: " + roadDetails.getId());
		//Window.alert("Load road details for: " + roadDetails.getId());
		name.setText(roadDetails.getName());
		description.setText(roadDetails.getDescription());
		currentRoadDetailId = roadDetails.getId();
		//Window.alert("load details currentRoadDetailId=" + currentRoadDetailId);
		polyline.setText(roadDetails.getId());
		
		String n = "";
		for (Iterator iterator = roadDetailsList.iterator(); iterator
				.hasNext();) {
			RoadDetails rd = (RoadDetails) iterator.next();
			if(rd.getId().equals(currentPolyline.getRoadDetailsId()))
			{
				n = rd.getName();
			}
		}
		vertices.setText("Current polyline in RoadListing=" + n);
		
		// i don't think this is current
		//vertices.setText(currentPolyline.getRoadDetailsId());
		
		// need to find the tag name from tagDetailsList
		for (Iterator iterator = tagDetailsList.iterator(); iterator.hasNext();) {
			TagDetails tagDetails = (TagDetails) iterator.next();
			if(tagDetails.getId().equals(roadDetails.getTagId()))
			{
				tagSuggestBox.setText(tagDetails.getName());
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
