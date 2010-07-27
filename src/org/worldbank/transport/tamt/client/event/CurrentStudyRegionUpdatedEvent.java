package org.worldbank.transport.tamt.client.event;

import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.event.shared.GwtEvent;

public class CurrentStudyRegionUpdatedEvent extends GwtEvent<CurrentStudyRegionUpdatedEventHandler> {
  
  public static Type<CurrentStudyRegionUpdatedEventHandler> TYPE = new Type<CurrentStudyRegionUpdatedEventHandler>();
  
  public StudyRegion studyRegion;
  
  public CurrentStudyRegionUpdatedEvent(StudyRegion studyRegion) {
	  // contains only ID, mapCenter and mapZoomLevel
	  this.studyRegion = studyRegion; 
  }

@Override
  public Type<CurrentStudyRegionUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(CurrentStudyRegionUpdatedEventHandler handler) {
    handler.onUpdate(this);
  }
}
