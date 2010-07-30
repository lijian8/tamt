package org.worldbank.transport.tamt.client.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.worldbank.transport.tamt.client.Main;
import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.event.BindPolyLineToRoadEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToRegionEvent;
import org.worldbank.transport.tamt.client.event.BindPolygonToZoneEvent;
import org.worldbank.transport.tamt.client.event.CacheRegionMapMetaDataEvent;
import org.worldbank.transport.tamt.client.event.CancelRegionEvent;
import org.worldbank.transport.tamt.client.event.CancelRegionEventHandler;
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
import org.worldbank.transport.tamt.client.event.CreateRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.CreateRegionPolygonEventHandler;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.DebugEvent;
import org.worldbank.transport.tamt.client.event.DebugEventHandler;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableLineEditingEventHandler;
import org.worldbank.transport.tamt.client.event.DisableRegionEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableRegionEditingEventHandler;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEvent;
import org.worldbank.transport.tamt.client.event.DisableZoneEditingEventHandler;
import org.worldbank.transport.tamt.client.event.EditRegionDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRegionSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRegionSegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditRoadDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditRoadSegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EditZoneDetailsBySegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneSegmentEvent;
import org.worldbank.transport.tamt.client.event.EditZoneSegmentEventHandler;
import org.worldbank.transport.tamt.client.event.EndEditPolyLineEvent;
import org.worldbank.transport.tamt.client.event.EndEditPolygonEvent;
import org.worldbank.transport.tamt.client.event.EndEditRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.FetchUpdatedPolylineEventHandler;
import org.worldbank.transport.tamt.client.event.GetRegionsEvent;
import org.worldbank.transport.tamt.client.event.GetRegionsEventHandler;
import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.GetZonesEventHandler;
import org.worldbank.transport.tamt.client.event.RenderRegionsEvent;
import org.worldbank.transport.tamt.client.event.RenderRegionsEventHandler;
import org.worldbank.transport.tamt.client.event.RenderRoadsEvent;
import org.worldbank.transport.tamt.client.event.RenderRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.RenderZonesEvent;
import org.worldbank.transport.tamt.client.event.RenderZonesEventHandler;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolygonEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedPolylineEvent;
import org.worldbank.transport.tamt.client.event.SentUpdatedRegionPolygonEvent;
import org.worldbank.transport.tamt.client.event.ShowRegionsEvent;
import org.worldbank.transport.tamt.client.event.ShowRegionsEventHandler;
import org.worldbank.transport.tamt.client.event.ShowRoadsEvent;
import org.worldbank.transport.tamt.client.event.ShowRoadsEventHandler;
import org.worldbank.transport.tamt.client.event.ShowZonesEvent;
import org.worldbank.transport.tamt.client.event.ShowZonesEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import com.google.gwt.maps.client.event.MapDragEndHandler;
import com.google.gwt.maps.client.event.MapRightClickHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
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
import com.google.gwt.maps.client.event.MapDragEndHandler.MapDragEndEvent;
import com.google.gwt.maps.client.event.MapRightClickHandler.MapRightClickEvent;
import com.google.gwt.maps.client.event.MapZoomEndHandler.MapZoomEndEvent;
import com.google.gwt.maps.client.event.PolygonClickHandler.PolygonClickEvent;
import com.google.gwt.maps.client.event.PolygonMouseOverHandler.PolygonMouseOverEvent;
import com.google.gwt.maps.client.event.PolylineCancelLineHandler.PolylineCancelLineEvent;
import com.google.gwt.maps.client.event.PolylineClickHandler.PolylineClickEvent;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler.PolylineMouseOutEvent;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler.PolylineMouseOverEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegionMap extends Composite implements RequiresResize {

	private String polygonColor = "green";
	private double opacity = 0.6;
	private int weight = 4;
	private HandlerManager eventBus;
	
	private MapWidget map;
	protected RegionPolygon currentPolygon;
	private ArrayList<RegionPolygon> polygons;
	private HashMap<String, ArrayList<Vertex>> zoneListingVertexHash;
	protected StudyRegion currentStudyRegion;
	private boolean mapFirstLoad;
	
	// context menu
	private VerticalPanel contextMenu;
	protected boolean polyIsLoadedFromPanel = false;
	
	
	public RegionMap(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		//TODO: get center of map from DB
		LatLng center = LatLng.newInstance(0.0, 0.0); 
        map = new MapWidget(center, 2); 
        mapFirstLoad = true;
        
        map.addMapType(MapType.getNormalMap());
        map.addMapType(MapType.getHybridMap());
        map.addMapType(MapType.getPhysicalMap());
        map.addControl(new MenuMapTypeControl());
        
        map.addControl(new DrawingToolsControl(this.eventBus));
		map.addControl(new LargeMapControl3D());
		
		map.setSize("500px", "500px"); 
		
		initWidget(map);
		
		polygons = new ArrayList<RegionPolygon>();
		
		// context menu
		contextMenu = new VerticalPanel();
		
		
        bind();
        
	}
	
	public void bind()
	{
		
		/*
		 * Faking a right-clickhandler for RegionPolygon
		 */
		map.addMapRightClickHandler(new MapRightClickHandler() {
			
			@Override
			public void onRightClick(MapRightClickEvent event) {
				final HandlerManager eb = eventBus;
				if( event.getOverlay() != null)
				{
					GWT.log("MapRightClickHandler OVERLAY:" + event.getOverlay().getClass());
					if( event.getOverlay() instanceof RegionPolygon )
					{
						RegionPolygon polygon = (RegionPolygon) event.getOverlay();
						// TODO: only add the control if the polygon information is already loaded in the edit panel
						if( polyIsLoadedFromPanel )
						{
							GWT.log("Clicked on RegionPolygon:" + polygon.getRegionDetailsId());
							Point clickedPoint = event.getPoint();
							GWT.log("Clicked point:" + clickedPoint.toString());
							map.addControl(new RegionContextMenuControl(eb, clickedPoint));
						}
						
					}
				}
			}
		});
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				
				// only change the map after first load, not during runtime of app
				if( mapFirstLoad )
				{
					currentStudyRegion = event.studyRegion;
					Vertex v = currentStudyRegion.getMapCenter();
					final LatLng center = LatLng.newInstance(v.getLat(), v.getLng());
					
					// do this as a deferred command
	            	DeferredCommand.addCommand(new Command() {
	          	      public void execute() {
	          	    	  GWT.log("RegionMap - (on first load only) update to current study region" + currentStudyRegion);
	          	    	  map.setZoomLevel(currentStudyRegion.getMapZoomLevel());
	          	    	  map.setCenter(center);
	          	    	  map.checkResizeAndCenter();
	          	       }
	          	    });				
				}
			}
		});	

		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: RegionMap height: " + event.height);
				GWT.log("SIZE: RegionMap width: " + event.width);
				int mapW = event.width - (400 + 50); // set in RegionInformation
				
				int mapH = event.height - 40; // margins
				String mapHeight = Integer.toString(mapH) + "px";
				String mapWidth = Integer.toString(mapW) + "px";
				
				GWT.log("SIZE: RegionMap resize map height to: " + mapHeight);
				GWT.log("SIZE: RegionMap resize map width to: " + mapWidth);
	    	  	GWT.log("SIZE: RegionMap center: " + map.getCenter());
				
				map.setWidth(mapWidth);
				map.setHeight(mapHeight);
				
				// workaround for bad alignment
            	DeferredCommand.addCommand(new Command() {
            	      public void execute() {
            	    	  	map.checkResizeAndCenter();
            	    	  	GWT.log("SIZE: RegionMap checkResizeCenter: " + map.getCenter());
            	       }
            	    });	
			}
		});
		
		eventBus.addHandler(SwitchModuleEvent.TYPE,
				new SwitchModuleEventHandler() {
			    	public void onSwitchModule(SwitchModuleEvent event) {
			            if( event.getModule().equals(SwitchModuleEvent.REGION))
			            {
			            	
			            	//if( event.isVisible())
			            	//{
				            	// workaround to init map properly
			            		DeferredCommand.addCommand(new Command() {
				            	      public void execute() {
				            	            //map.checkResizeAndCenter();
				            	            map.checkResize();
				            	       }
			            	    });
			            	//}
			            }
			        }
			});
		

		eventBus.addHandler(CreateRegionPolygonEvent.TYPE,
			new CreateRegionPolygonEventHandler() {
		    	public void onCreateRegionPolygon(CreateRegionPolygonEvent event) {
		            createRegionPolygon();
		    }
		});	
		
		eventBus.addHandler(ShowRegionsEvent.TYPE,
			new ShowRegionsEventHandler() {
				@Override
				public void onShowRegions(ShowRegionsEvent event) {
					renderRegions(zoneListingVertexHash);
				}
		});
			
		eventBus.addHandler(EditRegionSegmentEvent.TYPE,
			new EditRegionSegmentEventHandler() {
		    	public void onEditRegionSegment(EditRegionSegmentEvent event) {

		    	   /* We don't mess this for StudyRegions
		    	    * because they have their own centering data
		    	   if(event.center != null)
		    	   {
		    		   map.setCenter(event.center);
		    	   }
		    	   if( event.zoomLevel != null)
		    	   {
		    		   map.setZoomLevel(event.zoomLevel);
		    	   }
		    	   */
		           setEditingRegionsEnabled(event.id);
		           polyIsLoadedFromPanel = true;
		    }
		});
		
		eventBus.addHandler(GetRegionsEvent.TYPE,
			new GetRegionsEventHandler() {
		    	public void onGetRegions(GetRegionsEvent event) {
		    		polygons = new ArrayList<RegionPolygon>();
		        }
		});
		
		eventBus.addHandler(DisableRegionEditingEvent.TYPE,
			new DisableRegionEditingEventHandler() {
		    	public void onDisableRegionEditing(DisableRegionEditingEvent event) {
		    		setEditingRegionsEnabled("");
		    		polyIsLoadedFromPanel = false;
		        }
		});
		
		eventBus.addHandler(RenderRegionsEvent.TYPE,
			new RenderRegionsEventHandler() {
		    	public void onRenderRegions(RenderRegionsEvent event) {
		            final RenderRegionsEvent e = event;
		            renderRegions(e.vertexHash);
		    }
		});
		
		eventBus.addHandler(CancelRegionEvent.TYPE, new CancelRegionEventHandler() {
			@Override
			public void onCancelRegion(CancelRegionEvent event) {
				setEditingRegionsDisabled(event.id);
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
	private void setEditingRegionsDisabled(String id) {
		for (Iterator iterator = polygons.iterator(); iterator.hasNext();) {
			RegionPolygon polygon = (RegionPolygon) iterator.next();
			if(polygon.getRegionDetailsId().equals(id))
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
	private void setEditingRegionsEnabled(String id) {
		for (Iterator iterator = polygons.iterator(); iterator.hasNext();) {
			RegionPolygon polygon = (RegionPolygon) iterator.next();
			if(polygon.getRegionDetailsId().equals(id))
			{
				  polygon.setEditingEnabled(true);
				  LatLngBounds bounds = polygon.getBounds();
				  // For Regions, we don't want to recenter on the polygon
				  // map.panTo(bounds.getCenter());
				  currentPolygon = polygon;
				  LatLng points[] = new LatLng[currentPolygon.getVertexCount()];
		    	  for (int i = 0; i < currentPolygon.getVertexCount(); i++) {
		    		  LatLng v = currentPolygon.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final RegionPolygon p = new RegionPolygon(points);
		  	  	  p.setRegionDetailsId(currentPolygon.getRegionDetailsId());
		  	      eventBus.fireEvent(new SentUpdatedRegionPolygonEvent(p));
			} else {
				polygon.setEditingEnabled(false);
			}
		}
	}

	private void createRegionPolygon() {
		
		String tempId = UUID.uuid(10);
		tempId = "TEMP-" + tempId; // to be replaced on first save in TagBO
		GWT.log("Temporary polygon ID:" + tempId);
		PolyStyleOptions strokeStyle = PolyStyleOptions.newInstance(polygonColor, weight, opacity);
		PolyStyleOptions fillStyle = PolyStyleOptions.newInstance(polygonColor, weight, opacity);

	    final RegionPolygon poly = new RegionPolygon(new LatLng[0]);
	    poly.setRegionDetailsId(tempId);
	    
	    map.addOverlay(poly);
	    poly.setDrawingEnabled();
	    poly.setStrokeStyle(strokeStyle);
	    poly.setFillStyle(fillStyle);
	    
	    bindPolygonHandlers(poly, true);
	    
	}


  public interface ToolResources extends ClientBundle {
		public static final ToolResources INSTANCE = GWT.create(ToolResources.class);

		@Source("Tpd.png")
		ImageResource polygonDown();

		@Source("Tpu.png")
		ImageResource polygonUp();

  }
  
  private static class RegionContextMenuControl extends CustomControl {

	private HandlerManager eventBus;
	private VerticalPanel container;
	private RegionContextMenuControl self;
	private HTML setMapView;
	private HTML setAsCurrentRegion;
	private HTML simplify;
	
	public RegionContextMenuControl(HandlerManager eventBus, Point clickedPoint) {
		super(new ControlPosition(ControlAnchor.TOP_LEFT, clickedPoint.getX(), clickedPoint.getY()));
		this.eventBus = eventBus;
		this.self = this;
	}
	
	@Override
	protected Widget initialize(MapWidget map) {
		final HandlerManager bus = this.eventBus;
		final MapWidget m = map;
		container = new VerticalPanel();
		container.setStyleName("mapContextMenuContainer");
		
		setMapView = new HTML("Set map view");
		setMapView.setStyleName("mapContextMenuItem");
		setMapView.addStyleName("contextDisabled");
		setMapView.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				setMapView.removeStyleName("contextDisabled");
			}
		});
		setMapView.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					bus.fireEvent(new CacheRegionMapMetaDataEvent(m.getCenter(), m.getZoomLevel()));
					m.removeControl(self);
				}
		});
	      
		setAsCurrentRegion = new HTML("Set as current region");
		setAsCurrentRegion.setStyleName("mapContextMenuItem");
		setAsCurrentRegion.addStyleName("contextDisabled");
		setAsCurrentRegion.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				setAsCurrentRegion.removeStyleName("contextDisabled");
			}
		});
		setAsCurrentRegion.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					Window.alert("fire the set as current region event");
					m.removeControl(self);
				}
		});	      
	    
		simplify = new HTML("Simplify geometry");
		simplify.setStyleName("mapContextMenuItem");
		simplify.addStyleName("contextDisabled");
		simplify.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				simplify.removeStyleName("contextDisabled");
			}
		});
		simplify.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					Window.alert("fire the simplify event");
					m.removeControl(self);
				}
		});	      
		
		
	    container.add(setMapView);
	    
	    // TODO: add these when the features are ready
	    //container.add(setAsCurrentRegion);
	    //container.add(simplify);
	    
		return container;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
	  
  }
  
  private static class DrawingToolsControl extends CustomControl {

	private HandlerManager eventBus;
	private HTML saveMapView;
	
	public DrawingToolsControl(HandlerManager eventBus) {
		super(new ControlPosition(ControlAnchor.TOP_LEFT, 80, 7));
		this.eventBus = eventBus;
	}
	    
	@Override
	protected Widget initialize(final MapWidget map) {
		//GWT.log("initialize DrawingToolsControls with map=" + map);
		final HandlerManager bus = this.eventBus;
		HorizontalPanel container = new HorizontalPanel();

	      Image shapeButton = new Image(ToolResources.INSTANCE.polygonUp());
	      shapeButton.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent clickEvent) {
	        	bus.fireEvent(new CreateRegionPolygonEvent());
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
	      
	    saveMapView = new HTML("Set this map view (location and zoom) as the region's default view");
	    saveMapView.setStyleName("saveMapView");
	    saveMapView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GWT.log("DUPE: fire CacheRegionMapMetaDataEvent");
				eventBus.fireEvent(new CacheRegionMapMetaDataEvent(
						map.getCenter(), map.getZoomLevel()));
				
			}

		});
	    saveMapView.setVisible(false);
	    
	    container.add(shapeButton);
	    container.add(saveMapView);
	    
	    bind();
	    
	    return container;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}
	
	private void bind()
	{
		/*
		 * TODO: remove saveMapView from DrawingToolsControl,
		 * we have moved it to the right-click handler and
		 * tucked it inside RegionContextMenuControl
		 * 
		 * eventBus.addHandler(EditRegionSegmentEvent.TYPE,
				new EditRegionSegmentEventHandler() {
			    	public void onEditRegionSegment(EditRegionSegmentEvent event) {
			          saveMapView.setVisible(true);
			    }
		});
		*/
		eventBus.addHandler(DisableRegionEditingEvent.TYPE,
				new DisableRegionEditingEventHandler() {
			    	public void onDisableRegionEditing(DisableRegionEditingEvent event) {
			    		saveMapView.setVisible(false);
			        }
		});		
		
		eventBus.addHandler(CancelRegionEvent.TYPE, new CancelRegionEventHandler() {

			@Override
			public void onCancelRegion(CancelRegionEvent event) {
				saveMapView.setVisible(false);
			}

		});
		
		eventBus.addHandler(RenderRegionsEvent.TYPE,
				new RenderRegionsEventHandler() {
			    	public void onRenderRegions(RenderRegionsEvent event) {
			            saveMapView.setVisible(false);
			    }
		});
		
	}
	  
  }

	public void renderRegions(
			HashMap<String, ArrayList<Vertex>> zoneListingVertexHash) {
	
		GWT.log("DUPE renderRegions");
		if( zoneListingVertexHash == null )
		{
			eventBus.fireEvent(new GetRegionsEvent());
		} else 
		{
			currentPolygon = null;	
			polygons = new ArrayList<RegionPolygon>();
			this.zoneListingVertexHash = zoneListingVertexHash;
			
			Set<String> keys = this.zoneListingVertexHash.keySet();
			map.clearOverlays();
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String zoneDetailsId = (String) iterator.next();
				ArrayList<Vertex> vertices = this.zoneListingVertexHash.get(zoneDetailsId);
				RegionPolygon newPolygon = null;
				try {
					newPolygon = createPolygonFromVertexArray(zoneDetailsId, vertices);
					polygons.add(newPolygon);// TODO: newPolygon is getting added twice!
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
	
	public void bindPolygonHandlers(final RegionPolygon polygon, boolean isNew)
	{
		/**
		 * For new regions ONLY, we need to handle a PolygonEndLineHandler
		 */
		GWT.log("isNew=" + isNew);
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
		  	  	  final RegionPolygon p = new RegionPolygon(points);
		  	  	  p.setRegionDetailsId(currentPolygon.getRegionDetailsId());
		  	  	  
		  	  	  // add a mapCenter and mapZoomLevel to the polygon
		  	  	  LatLng center = event.getSender().getBounds().getCenter();
		  	  	  Vertex mapCenter = new Vertex();
		  	  	  mapCenter.setLat(center.getLatitude());
		  	  	  mapCenter.setLng(center.getLongitude());
		  	  	  int mapZoomLevel = map.getZoomLevel();
		  	  	  p.setMapCenter(mapCenter);
		  	  	  p.setMapZoomLevel(mapZoomLevel);
		  	  	  
		  	  	  eventBus.fireEvent(new EndEditRegionPolygonEvent(p));
		      }
		    });	
		} else {
			
			/**
		     * Send a copy of the RegionPolygon to RoadListing
		     */
			polygon.addPolygonLineUpdatedHandler(new PolygonLineUpdatedHandler() {
		      
			public void onUpdate(PolygonLineUpdatedEvent event) {
		    	  /*
		    	   * If a shape is being edited, it is the current shape
		    	   */
		    	  currentPolygon = polygon;
		    	  
		    	  /*
		    	   * Copy the currentPolygon into a new RegionPolygon
		    	   * in order to send it across the wire.
		    	   */
		    	  LatLng points[] = new LatLng[currentPolygon.getVertexCount()];
		    	  for (int i = 0; i < currentPolygon.getVertexCount(); i++) {
		    		  LatLng v = currentPolygon.getVertex(i);
		    		  LatLng p = LatLng.newInstance(v.getLatitude(), v.getLongitude());
		    		  points[i] = p;
		    	  }
		  	  	  final RegionPolygon p = new RegionPolygon(points);
		  	  	  p.setRegionDetailsId(currentPolygon.getRegionDetailsId());
		  	  	  
		  	  	  eventBus.fireEvent(new BindPolygonToRegionEvent(p));
		      }
		    });
			
			/**
			 * Click to edit the shape (and make sure the details are loaded in the edit view)
			 */
			polygon.addPolygonClickHandler(new PolygonClickHandler() {
				@Override
				public void onClick(PolygonClickEvent event) {
					polygon.setEditingEnabled(true);
					eventBus.fireEvent(new EditRegionDetailsBySegmentEvent(polygon));
				}
			});
			
		}
	}
	
	public RegionPolygon createPolygonFromVertexArray(String zoneDetailsId, ArrayList<Vertex> vertices) throws Exception
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
			final RegionPolygon polygon = new RegionPolygon(points, polygonColor, weight, opacity, polygonColor, opacity);
			polygon.setRegionDetailsId(zoneDetailsId);
			
			bindPolygonHandlers(polygon, false);
			
		    PolyStyleOptions style = PolyStyleOptions.newInstance(polygonColor, weight, opacity);
		    polygon.setStrokeStyle(style);
		    
		    //polygons.add(polygon);
		    
		    return polygon;
		    
		} else {
			throw new Exception("Vertex array cannot be null");
		}
		
	}

	@Override
	public void onResize() {
		// what is the width of the map pane?
		GWT.log("SIZE: width of regionMap container=" + this.getOffsetWidth());
		GWT.log("SIZE: width of map widgeth=" + map.getOffsetWidth());
		
	}

		  
}
