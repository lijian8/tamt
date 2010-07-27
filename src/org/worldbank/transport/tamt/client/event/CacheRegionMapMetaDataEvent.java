package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.maps.client.geom.LatLng;

public class CacheRegionMapMetaDataEvent extends GwtEvent<CacheRegionMapMetaDataEventHandler> {
  
  public static Type<CacheRegionMapMetaDataEventHandler> TYPE = new Type<CacheRegionMapMetaDataEventHandler>();
 
  public LatLng center;
  public int zoomLevel;
  
  public CacheRegionMapMetaDataEvent(LatLng center, int zoomLevel) {
	  // transfers map center and zoom level to RegionListing
	  // will be applied to currently selected region id
	  this.center = center;
	  this.zoomLevel = zoomLevel;
  }

@Override
  public Type<CacheRegionMapMetaDataEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CacheRegionMapMetaDataEventHandler handler) {
    handler.onCache(this);
  }
}
