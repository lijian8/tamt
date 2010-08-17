package org.worldbank.transport.tamt.client.event;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.event.shared.GwtEvent;

public class FetchedTagsEvent extends GwtEvent<FetchedTagsEventHandler> {
  
  public static Type<FetchedTagsEventHandler> TYPE = new Type<FetchedTagsEventHandler>();

  private ArrayList<TagDetails> tags;
  
  public FetchedTagsEvent(ArrayList<TagDetails> tags) {
	  this.tags = tags;
  }

  @Override
  public Type<FetchedTagsEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(FetchedTagsEventHandler handler) {
    handler.onFetchedTags(this);
  }

  public ArrayList<TagDetails> getTags() {
	return tags;
  }
}
