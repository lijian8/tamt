package org.worldbank.transport.tamt.client.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEvent;
import org.worldbank.transport.tamt.client.event.CancelRoadEvent;
import org.worldbank.transport.tamt.client.event.CancelRoadEventHandler;
import org.worldbank.transport.tamt.client.event.CancelZoneEvent;
import org.worldbank.transport.tamt.client.event.CancelZoneEventHandler;
import org.worldbank.transport.tamt.client.event.ClearZoneOverlaysEvent;
import org.worldbank.transport.tamt.client.event.ClearZoneOverlaysEventHandler;
import org.worldbank.transport.tamt.client.event.CreatePolyLineEvent;
import org.worldbank.transport.tamt.client.event.CreatePolyLineEventHandler;
import org.worldbank.transport.tamt.client.event.CreatePolygonEvent;
import org.worldbank.transport.tamt.client.event.CreatePolygonEventHandler;
import org.worldbank.transport.tamt.client.event.DebugEvent;
import org.worldbank.transport.tamt.client.event.DebugEventHandler;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEventHandler;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditZoneDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneSegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolygonEvent;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEventHandler;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.GetZonesEventHandler;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.RenderZonesEvent;
import org.worldbank.transport.tamt.client.event.RenderZonesEventHandler;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.ShowRoadsEvent;
import org.worldbank.transport.tamt.client.event.ShowRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.ShowZonesEvent;
import org.worldbank.transport.tamt.client.event.ShowZonesEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.tag.TagModule.TagModuleUiBinder;
import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapUIOptions;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.LargeMapControl3D;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.MenuMapTypeControl;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.event.MapAddOverlayHandler;
import com.google.gwt.maps.client.event.PolygonCancelLineHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolygonEndLineHandler;
import com.google.gwt.maps.client.event.PolygonLineUpdatedHandler;
import com.google.gwt.maps.client.event.PolygonMouseOutHandler;
import com.google.gwt.maps.client.event.PolygonMouseOverHandler;
import com.google.gwt.maps.client.event.PolylineCancelLineHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.PolylineLineUpdatedHandler;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.event.MapAddOverlayHandler.MapAddOverlayEvent;
import com.google.gwt.maps.client.event.PolylineCancelLineHandler.PolylineCancelLineEvent;
import com.google.gwt.maps.client.event.PolylineClickHandler.PolylineClickEvent;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler.PolylineMouseOutEvent;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler.PolylineMouseOverEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.PolyEditingOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TagMap extends Composite {

	private String polylineColor = "blue";
	private String polygonColor = "red";
	private boolean makePolygon = false;
	private Label message1;
	private Label message2;
	private double opacity = 0.6;
	private int weight = 4;
	private boolean fillFlag = true;
	private Polyline lastPolyline = null;
	private Polygon lastPolygon = null;
	private DialogBox addPolyDialog = null;
	private Button editPolylineButton = new Button("Edit Last Polyline");
	private Button editPolygonButton = new Button("Edit Last Polygon");
	private HandlerManager eventBus;
	private DockLayoutPanel dock;
	
	/*
	private static MapUIUiBinder uiBinder = GWT.create(MapUIUiBinder.class);

	interface MapUIUiBinder extends UiBinder<Widget, TagMap> {
	}
	
	@UiField MapWidget map;
	*/
	
	private MapWidget map;
	private HashMap<String, ArrayList<Vertex>> roadListingVertexHash;
	private String currentPolyEditId;
	protected TagPolyline currentPolyline;
	private ArrayList<TagPolyline> polylines;
	protected TagPolygon currentPolygon;
	private ArrayList<TagPolygon> polygons;
	private HashMap<String, ArrayList<Vertex>> zoneListingVertexHash;
	private TagPolyline backupEditPolyline;
	
	// handlers
	private TagPolylineEndLineHandler tagPolylineEndLineHandler = new TagPolylineEndLineHandler();
	
	public TagMap(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		// Accra 5.555717,-0.196306
		//TODO: find a way to let the user decide what the default map center should be
		LatLng center = LatLng.newInstance(5.555717, -0.196306); 
        map = new MapWidget(center, 12); 
        //Window.alert("Initial map widget hashcode:" + map.hashCode());
        
        map.setSize("500px", "500px"); 
		
        map.addMapType(MapType.getNormalMap());
        map.addMapType(MapType.getHybridMap());
        map.addMapType(MapType.getPhysicalMap());
        map.addControl(new MenuMapTypeControl());
        
        map.addControl(new DrawingToolsControl(this.eventBus));
		map.addControl(new LargeMapControl3D());
		
		initWidget(map);
		
		polylines = new ArrayList<TagPolyline>();
		polygons = new ArrayList<TagPolygon>();
		
        bind();
        
	}
	
	public void bind()
	{
		eventBus.addHandler(CreatePolyLineEvent.TYPE,
			new CreatePolyLineEventHandler() {
		    	public void onCreatePolyLine(CreatePolyLineEvent event) {
		            createPolyline();
		    }
		});
		eventBus.addHandler(CreatePolygonEvent.TYPE,
			new CreatePolygonEventHandler() {
		    	public void onCreatePolygon(CreatePolygonEvent event) {
		            createPolygon();
		    }
		});	
		
		/* polyline (road) event handling */
		eventBus.addHandler(EditRoadSegmentEvent.TYPE,
			new EditRoadSegmentEventHandler() {
		    	public void onEditRoadSegment(EditRoadSegmentEvent event) {
		    	   setEditingEnabled(event.getId());
		    }
		});
		eventBus.addHandler(GetRoadsEvent.TYPE,
			new GetRoadsEventHandler() {
		    	public void onGetRoads(GetRoadsEvent event) {
		    		//Window.alert("Handling GetRoadsEvent clearing overlays for map hashcode:" + map.hashCode());
		            //map.clearOverlays();
		            //Window.alert("Handling GetRoadsEvent cleared");
		            polylines = new ArrayList<TagPolyline>();
		        }
		});
		eventBus.addHandler(DisableLineEditingEvent.TYPE,
			new DisableLineEditingEventHandler() {
		    	public void onDisableLineEditing(DisableLineEditingEvent event) {
		    		setEditingEnabled("");
		        }
		});
		eventBus.addHandler(RenderRoadsEvent.TYPE,
			new RenderRoadsEventHandler() {
		    	public void onRenderRoads(RenderRoadsEvent event) {
		            final RenderRoadsEvent e = event;
		            renderRoads(e.vertexHash);
		    }
		});
		eventBus.addHandler(ShowRoadsEvent.TYPE,
			new ShowRoadsEventHandler() {
				@Override
				public void onShowRoads(ShowRoadsEvent event) {
					renderRoads(roadListingVertexHash);
				}
		});
		eventBus.addHandler(ShowZonesEvent.TYPE,
			new ShowZonesEventHandler() {
				@Override
				public void onShowZones(ShowZonesEvent event) {
					renderZones(zoneListingVertexHash);
				}
		});
		
		
		/* polygon (zone) event handling */
		eventBus.addHandler(ClearZoneOverlaysEvent.TYPE,
				new ClearZoneOverlaysEventHandler() {
			    	public void onClearZoneOverlays(ClearZoneOverlaysEvent event) {
			            
			    }
			});
			
		eventBus.addHandler(EditZoneSegmentEvent.TYPE,
			new EditZoneSegmentEventHandler() {
		    	public void onEditZoneSegment(EditZoneSegmentEvent event) {
		           setEditingZonesEnabled(event.getId());
		    }
		});
		eventBus.addHandler(GetZonesEvent.TYPE,
			new GetZonesEventHandler() {
		    	public void onGetZones(GetZonesEvent event) {
		    		polygons = new ArrayList<TagPolygon>();
		        }
		});
		eventBus.addHandler(DisableZoneEditingEvent.TYPE,
			new DisableZoneEditingEventHandler() {
		    	public void onDisableZoneEditing(DisableZoneEditingEvent event) {
		    		setEditingZonesEnabled("");
		        }
		});
		eventBus.addHandler(RenderZonesEvent.TYPE,
			new RenderZonesEventHandler() {
		    	public void onRenderZones(RenderZonesEvent event) {
		            final RenderZonesEvent e = event;
		            renderZones(e.vertexHash);
		    }
		});		
		
		eventBus.addHandler(CancelRoadEvent.TYPE, new CancelRoadEventHandler() {
			@Override
			public void onCancelRoad(CancelRoadEvent event) {
				//Window.alert("cancel event.id=" + event.id);
				setEditingDisabled(event.id);
				if( event.id.indexOf("TEMP") != -1)
				{
					// remove it from the map
					map.removeOverlay(currentPolyline);
				}
				currentPolyline = null; 
				
			}
		});	
		
		eventBus.addHandler(CancelZoneEvent.TYPE, new CancelZoneEventHandler() {
			@Override
			public void onCancelZone(CancelZoneEvent event) {
				setEditingZonesDisabled(event.id);
				if( event.id.indexOf("TEMP") != -1)
				{
					// remove it from the map
					map.removeOverlay(currentPolygon);
				}
				currentPolygon = null; 
			}
		});			
	}

	/**
	 * Disable editing of a specific polygon
	 * @param id
	 */
	private void setEditingZonesDisabled(String id) {
		for (Iterator iterator = polygons.iterator(); iterator.hasNext();) {
			TagPolygon polygon = (TagPolygon) iterator.next();
			if(polygon.getZoneDetailsId().equals(id))
			{
				polygon.setEditingEnabled(false);
			}
		}
	}
	
	/**
	 * For all polygon overlays except the identified overlay,
	 * setEditingEnabled(false)
	 * @param id
	 */
	private void setEditingZonesEnabled(String id) {
		for (Iterator iterator = polygons.iterator(); iterator.hasNext();) {
			TagPolygon polygon = (TagPolygon) iterator.next();
			if(polygon.getZoneDetailsId().equals(id))
			{
				  polygon.setEditingEnabled(true);
				  LatLngBounds bounds = polygon.getBounds();
				  map.panTo(bounds.getCenter());
				  currentPolygon = polygon;
				  LatLng points[] = new LatLng[currentPolygon.getVertexCount()];
		    	  for (int i = 0; i < currentPolygon.getVertexCount(); i++) {
		    		  LatLng v = currentPolygon.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolygon p = new TagPolygon(points);
		  	  	  p.setZoneDetailsId(currentPolygon.getZoneDetailsId());
		  	  	  eventBus.fireEvent(new SentUpdatedPolygonEvent(p));
			} else {
				polygon.setEditingEnabled(false);
			}
		}
	}

	/**
	 * Disable editing of a specific line
	 * @param id
	 */
	private void setEditingDisabled(String id) {
		for (Iterator iterator = polylines.iterator(); iterator.hasNext();) {
			TagPolyline polyline = (TagPolyline) iterator.next();
			if(polyline.getRoadDetailsId().equals(id))
			{
				  polyline.setEditingEnabled(false);
			}
		}
	}
	
	/**
	 * For all polyline overlays except the identified overlay,
	 * setEditingEnabled(false)
	 * @param id
	 */
	private void setEditingEnabled(String id) {
		//Window.alert("Set editing enabled for id=" + id);
		for (Iterator iterator = polylines.iterator(); iterator.hasNext();) {
			TagPolyline polyline = (TagPolyline) iterator.next();
			//Window.alert("Check polyline id=" + polyline.getRoadDetailsId());
			if(polyline.getRoadDetailsId().equals(id))
			{
				  polyline.setEditingEnabled(true);
				  LatLngBounds bounds = polyline.getBounds();
				  map.panTo(bounds.getCenter());
				  currentPolyline = polyline;
				  
				  // make a back up and store it for a single level of Undo (via CancelRoadEvent)
				  //backupEditPolyline = createBackUpPolyline(currentPolyline);
				  
				  // now send the currentPolyline back to RoadListing (via a copy)
				  LatLng points[] = new LatLng[currentPolyline.getVertexCount()];
		    	  for (int i = 0; i < currentPolyline.getVertexCount(); i++) {
		    		  LatLng v = currentPolyline.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolyline p = new TagPolyline(points);
		  	  	  p.setRoadDetailsId(currentPolyline.getRoadDetailsId());
		  	  	  eventBus.fireEvent(new SentUpdatedPolylineEvent(p));
			} else {
				polyline.setEditingEnabled(false);
			}
		}
	}

	  private TagPolyline createBackUpPolyline(TagPolyline polyline) {
		  	LatLng[] points = new LatLng[polyline.getVertexCount()];
			int count = 0;
			for (int i = 0; i < polyline.getVertexCount(); i++) {
				LatLng o = polyline.getVertex(i);
				LatLng n = LatLng.newInstance(o.getLatitude(), o.getLongitude());
				points[i] = n;
			}
			TagPolyline newPoly = new TagPolyline(points, polylineColor, weight, opacity);
			return newPoly;
	  }

	private void createPolyline() {
		
		String tempId = UUID.uuid(10);
		tempId = "TEMP-" + tempId; // to be replaced on first save in TagBO
		
	    PolyStyleOptions style = PolyStyleOptions.newInstance(polylineColor, weight, opacity);

	    final TagPolyline poly = new TagPolyline(new LatLng[0]);
	    poly.setRoadDetailsId(tempId);
	    
	    map.addOverlay(poly); // SFM FIRST OVERLAY TO GET ON THE MAP....
	    poly.setDrawingEnabled();
	    poly.setStrokeStyle(style);
	    
	    bindPolylineHandlers(poly, true);
	    
	    
	  }

	  private void createPolygon() {
		
		String tempId = UUID.uuid(10);
		tempId = "TEMP-" + tempId; // to be replaced on first save in TagBO
			
		PolyStyleOptions strokeStyle = PolyStyleOptions.newInstance(polygonColor, weight, opacity);
		PolyStyleOptions fillStyle = PolyStyleOptions.newInstance(polygonColor, weight, opacity);

	    final TagPolygon poly = new TagPolygon(new LatLng[0]);
	    poly.setZoneDetailsId(tempId);
	    
	    map.addOverlay(poly);
	    poly.setDrawingEnabled();
	    poly.setStrokeStyle(strokeStyle);
	    poly.setFillStyle(fillStyle);
	    
	    bindPolygonHandlers(poly, true);
	    
	  }


  public interface ToolResources extends ClientBundle {
		public static final ToolResources INSTANCE = GWT.create(ToolResources.class);

		@Source("Tld.png")
		ImageResource lineDown();

		@Source("Tlu.png")
		ImageResource lineUp();

		@Source("Tpd.png")
		ImageResource polygonDown();

		@Source("Tpu.png")
		ImageResource polygonUp();

  }
  
  private static class DrawingToolsControl extends CustomControl {

	private HandlerManager eventBus;
	public DrawingToolsControl(HandlerManager eventBus) {
		super(new ControlPosition(ControlAnchor.TOP_LEFT, 80, 7));
		this.eventBus = eventBus;
	}
	    
	@Override
	protected Widget initialize(final MapWidget map) {
		//GWT.log("initialize DrawingToolsControls with map=" + map);
		final HandlerManager bus = this.eventBus;
		Panel container = new FlowPanel();
		  Image lineButton = new Image(ToolResources.INSTANCE.lineUp());
	      lineButton.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent clickEvent) {
	        	//GWT.log("create polyline...");
	        	bus.fireEvent(new CreatePolyLineEvent());
	        }
	      });
	      
	      Image shapeButton = new Image(ToolResources.INSTANCE.polygonUp());
	      shapeButton.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent clickEvent) {
	        	//GWT.log("create polygon...");
	        	bus.fireEvent(new CreatePolygonEvent());
	        }
	      });
	      
	      HTML debug = new HTML("debug");
	      debug.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
					//GWT.log("Report on stored polylines");	
					eventBus.fireEvent(new DebugEvent());
				}
			});
	     
	    container.add(lineButton);
	    container.add(shapeButton);
	    //container.add(debug);
	    return container;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
	  
  }

	public void renderZones(
			HashMap<String, ArrayList<Vertex>> zoneListingVertexHash) {
	
		if( zoneListingVertexHash == null )
		{
			eventBus.fireEvent(new GetZonesEvent());
		} else 
		{
			currentPolygon = null;	
			polygons = new ArrayList<TagPolygon>();
			this.zoneListingVertexHash = zoneListingVertexHash;
			
			Set<String> keys = this.zoneListingVertexHash.keySet();
			map.clearOverlays();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String zoneDetailsId = (String) iterator.next();
				ArrayList<Vertex> vertices = this.zoneListingVertexHash.get(zoneDetailsId);
				TagPolygon newPolygon = null;
				try {
					newPolygon = createPolygonFromVertexArray(zoneDetailsId, vertices);
					polygons.add(newPolygon);
				} catch (Exception e) {
					GWT.log("An error occured converting the zone vertices to a polygon: " + e.getMessage());
				}
				try{
			        map.addOverlay(newPolygon);
				} catch (Exception e)
				{
					GWT.log(e.getMessage());
				}
			}			
		}
		
	}
	
	public void renderRoads(
			HashMap<String, ArrayList<Vertex>> roadListingVertexHash) {
	
		if( roadListingVertexHash == null)
		{
			eventBus.fireEvent(new GetRoadsEvent());
		} else {
			currentPolyline = null;	
			polylines = new ArrayList<TagPolyline>();
			this.roadListingVertexHash = roadListingVertexHash;
			
			GWT.log("TIMER start building polylines from hash");
			Set<String> keys = this.roadListingVertexHash.keySet();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String roadDetailsId = (String) iterator.next();
				ArrayList<Vertex> vertices = this.roadListingVertexHash.get(roadDetailsId);
				TagPolyline newPolyline = null;
				try {
					newPolyline = createPolylineFromVertexArray(roadDetailsId, vertices);
					polylines.add(newPolyline);
				} catch (Exception e) {
					GWT.log("An error occured converting the road vertices to a polyline: " + e.getMessage());
				}
			}
			GWT.log("TIMER stop building polylines from hash");
			
			GWT.log("TIMER start clearing overlays");
			map.clearOverlays();
			GWT.log("TIMER stop clearing overlays");
			
			GWT.log("TIMER start adding overlays");
			for (Iterator iterator = polylines.iterator(); iterator.hasNext();) {
				TagPolyline p = (TagPolyline) iterator.next();
				map.addOverlay(p);
			}
			GWT.log("TIMER stop adding overlays");			
		}
		
	}

	public class TagPolylineEndLineHandler implements PolylineEndLineHandler
	{
		
		public TagPolylineEndLineHandler(){}
		
		@Override
		public void onEnd(PolylineEndLineEvent event) {
			currentPolyline = (TagPolyline) event.getSender();
			LatLng points[] = new LatLng[currentPolyline.getVertexCount()];
			for (int i = 0; i < currentPolyline.getVertexCount(); i++) {
				LatLng v = currentPolyline.getVertex(i);
				LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
				points[i] = p;
			}
  	  	 	TagPolyline p = new TagPolyline(points);
  	  	 	p.setRoadDetailsId(currentPolyline.getRoadDetailsId());
  	  	 	eventBus.fireEvent(new EndEditPolyLineEvent(p));
		}
	}
	
	public void bindPolylineHandlers(final TagPolyline polyline, boolean isNew)
	{
		/**
		 * For new lines ONLY, send a copy across the wire
		 */
		if( isNew )
		{
			//
			//Window.alert("Attaching PolylineEndLineHandler to new line");
			polyline.addPolylineEndLineHandler(new PolylineEndLineHandler() {
		      public void onEnd(PolylineEndLineEvent event) {
		    	  
		    	  currentPolyline = polyline;
		    	  
		    	  //Window.alert("TagMap: PolylineEndLineHandler currentPolyline getVertexCount=" + currentPolyline.getVertexCount());
		  		  LatLng points[] = new LatLng[currentPolyline.getVertexCount()];
		    	  for (int i = 0; i < currentPolyline.getVertexCount(); i++) {
		    		  LatLng v = currentPolyline.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolyline p = new TagPolyline(points);
		  	  	  p.setRoadDetailsId(currentPolyline.getRoadDetailsId());
		    	  eventBus.fireEvent(new EndEditPolyLineEvent(p));
		      }
		    });	
		    //
			// instead of anonymous functions, try an instantiated object
			//polyline.addPolylineEndLineHandler(new TagPolylineEndLineHandler());
		} else {
			/**
		     * Send a copy of the TagPolyline to RoadListing
		     */
			polyline.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {
		      public void onUpdate(PolylineLineUpdatedEvent event) {
		    	  /*
		    	   * If a line is being edited, it is the current line
		    	   */
		    	  currentPolyline = polyline;
		    	  
		    	  /*
		    	   * Copy the currentPolyline into a new TagPolyline
		    	   * in order to send it across the wire.
		    	   */
		    	  //Window.alert("TagMap: PolylineLineUpdatedEvent currentPolyline getVertexCount=" + currentPolyline.getVertexCount());
		  		  LatLng points[] = new LatLng[currentPolyline.getVertexCount()];
		    	  for (int i = 0; i < currentPolyline.getVertexCount(); i++) {
		    		  LatLng v = currentPolyline.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolyline p = new TagPolyline(points);
		  	  	  p.setRoadDetailsId(currentPolyline.getRoadDetailsId());
		    	  eventBus.fireEvent(new BindPolyLineToRoadEvent(p)); //sfm not sure if this has vertices
		      }
		    });
			
			polyline.addPolylineCancelLineHandler(new PolylineCancelLineHandler() {
				
				@Override
				public void onCancel(PolylineCancelLineEvent event) {
					Window.alert("Undo the changes");
				}
			});
			
			/**
			 * Click to edit the line (and make sure the details are loaded in the edit view
			 */
			polyline.addPolylineClickHandler(new PolylineClickHandler() {
				@Override
				public void onClick(PolylineClickEvent event) {
					polyline.setEditingEnabled(true);
					eventBus.fireEvent(new EditRoadDetailsBySegmentEvent(polyline.getRoadDetailsId()));
				}
			});
			
		}
	}

	public void bindPolygonHandlers(final TagPolygon polygon, boolean isNew)
	{
		/**
		 * For new lines ONLY, send a copy across the wire
		 */
		if( isNew )
		{
			polygon.addPolygonEndLineHandler(new PolygonEndLineHandler() {
		      public void onEnd(PolygonEndLineEvent event) {
		    	  
		    	  currentPolygon = polygon;
		    	  
		    	  LatLng points[] = new LatLng[currentPolygon.getVertexCount()];
		    	  for (int i = 0; i < currentPolygon.getVertexCount(); i++) {
		    		  LatLng v = currentPolygon.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolygon p = new TagPolygon(points);
		  	  	  p.setZoneDetailsId(currentPolygon.getZoneDetailsId());
		  	  	  //Window.alert("Fire EndEditPolygonEvent with p=" + p);
		    	  eventBus.fireEvent(new EndEditPolygonEvent(p));
		      }
		    });	
		} else {
			/**
		     * Send a copy of the TagPolygon to RoadListing
		     */
			polygon.addPolygonLineUpdatedHandler(new PolygonLineUpdatedHandler() {
		      
			public void onUpdate(PolygonLineUpdatedEvent event) {
		    	  /*
		    	   * If a shape is being edited, it is the current shape
		    	   */
		    	  currentPolygon = polygon;
		    	  
		    	  /*
		    	   * Copy the currentPolygon into a new TagPolygon
		    	   * in order to send it across the wire.
		    	   */
		    	  LatLng points[] = new LatLng[currentPolygon.getVertexCount()];
		    	  for (int i = 0; i < currentPolygon.getVertexCount(); i++) {
		    		  LatLng v = currentPolygon.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final TagPolygon p = new TagPolygon(points);
		  	  	  p.setZoneDetailsId(currentPolygon.getZoneDetailsId());
		  	  	  //Window.alert("Fire BindPolygonToZoneEvent with p=" + p);
		    	  eventBus.fireEvent(new BindPolygonToZoneEvent(p));
		      }
		    });
			
			/**
			 * Click to edit the shape (and make sure the details are loaded in the edit view)
			 */
			polygon.addPolygonClickHandler(new PolygonClickHandler() {
				@Override
				public void onClick(PolygonClickEvent event) {
					polygon.setEditingEnabled(true);
					eventBus.fireEvent(new EditZoneDetailsBySegmentEvent(polygon.getZoneDetailsId()));
				}
			});
			
		}
	}
	
	public TagPolygon createPolygonFromVertexArray(String zoneDetailsId, ArrayList<Vertex> vertices) throws Exception
	{
		if( vertices != null)
		{
			LatLng[] points = new LatLng[vertices.size()];
			int count = 0;
			for (Iterator iterator = vertices.iterator(); iterator.hasNext();) {
				Vertex v = (Vertex) iterator.next();
				LatLng p = LatLng.newInstance(v.getLat(), v.getLng());
				points[count] = p;
				count++;
			}
			final TagPolygon polygon = new TagPolygon(points, polygonColor, weight, opacity, polygonColor, opacity);
			polygon.setZoneDetailsId(zoneDetailsId);
			
			bindPolygonHandlers(polygon, false);
			
		    PolyStyleOptions style = PolyStyleOptions.newInstance(polygonColor, weight, opacity);
		    polygon.setStrokeStyle(style);
		    
		    polygons.add(polygon);
		    
		    return polygon;
		    
		} else {
			throw new Exception("Vertex array cannot be null");
		}
		
	}
	
	public TagPolyline createPolylineFromVertexArray(String roadDetailsId, ArrayList<Vertex> vertices) throws Exception
	{
		if( vertices != null)
		{
			LatLng[] points = new LatLng[vertices.size()];
			int count = 0;
			for (Iterator iterator = vertices.iterator(); iterator.hasNext();) {
				Vertex v = (Vertex) iterator.next();
				LatLng p = LatLng.newInstance(v.getLat(), v.getLng());
				points[count] = p;
				count++;
			}
			final TagPolyline polyline = new TagPolyline(points, polylineColor, weight, opacity);
			polyline.setRoadDetailsId(roadDetailsId);
			
			bindPolylineHandlers(polyline, false);
			
		    PolyStyleOptions style = PolyStyleOptions.newInstance(polylineColor, weight, opacity);
		    polyline.setStrokeStyle(style);
		    
		    polylines.add(polyline);
		    
		    return polyline;
		    
		} else {
			throw new Exception("Vertex array cannot be null");
		}
		
	}

public void setCurrentPolyline(TagPolyline currentPolyline) {
	this.currentPolyline = currentPolyline;
}

public TagPolyline getCurrentPolyline() {
	return currentPolyline;
}

		  
}
