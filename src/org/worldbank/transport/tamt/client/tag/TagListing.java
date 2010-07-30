package org.worldbank.transport.tamt.client.tag;

import java.util.ArrayList;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEventHandler;
import org.worldbank.transport.tamt.client.event.ReceivedTagsEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.client.services.TagServiceAsync;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TagListing extends Composite {

	private static TagListingUiBinder uiBinder = GWT
			.create(TagListingUiBinder.class);

	interface TagListingUiBinder extends UiBinder<Widget, TagListing> {
	}

	interface TagStyle extends CssResource {
	    String tagList();
	    String checkbox();
	    String clickable();
	}

	@UiField TagStyle style;
	
	@UiField Label select;
	@UiField Label all;
	@UiField Label none;
	@UiField Button delete;
	
	@UiField Button save;
	@UiField FlexTable tagList;
	@UiField TextBox name;
	@UiField TextBox description;
	
	@UiField ScrollPanel scrollPanel;
	
	private HandlerManager eventBus;
	private TagServiceAsync tagService;
	private ArrayList<TagDetails> tagDetailsList;
	private String currentTagDetailsId;
	
	private boolean refreshTagDetails = true;
	private boolean isUpdate = false;

	private ArrayList<CheckBox> checkboxes;

	protected StudyRegion currentStudyRegion;
	
	public TagListing(HandlerManager eventBus) {
		this.eventBus = eventBus;
		checkboxes = new ArrayList<CheckBox>();
		tagService = GWT.create(TagService.class);    
	    initWidget(uiBinder.createAndBindUi(this));
	    
	    tagList.removeAllRows();
		tagList.setWidget(0, 0, new HTML("Loading tags..."));
        
		bind();
	}

	public void bind()
	{
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				
			}
		});	
		
		eventBus.addHandler(GetTagsEvent.TYPE,
			new GetTagsEventHandler() {
		    	public void onGetTags(GetTagsEvent event) {
		    		clearTagDetailView();
		    		GWT.log("TAG TagListing GetTagsEventHandler; going to fetchTagDetails for currentStudyRegion" + currentStudyRegion);
		    		fetchTagDetails();
		        }
		});	
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				int h = event.height - 250; // account for other UI
				String height = Integer.toString(h) + "px";
				GWT.log("SIZE: TagListsing scroll panel height: " + height);
				scrollPanel.setHeight(height);
			}
		});					
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
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		refreshTagDetails = true;
		saveTagDetails();
	}
	
	@UiHandler("cancel")
	void onClickCancel(ClickEvent e) {
		clearTagDetailView();
	}
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		if( Window.confirm("Delete all checked tags?") )
		{
			deleteTagDetails();
		}
	}	
	
	private void saveTagDetails() {
		
		TagDetails tagDetails = new TagDetails();
		tagDetails.setName(name.getText());
		tagDetails.setDescription(description.getText());
		tagDetails.setId(currentTagDetailsId);
		tagDetails.setRegion(currentStudyRegion);
		

		/*
		 * Just a little client-side validation, the rest
		 * is done on the server
		 */
		ArrayList<String> reservedZoneTypes = new ArrayList<String>();
		reservedZoneTypes.add(ZoneListing.ZONETYPE_COMMERCIAL);
		reservedZoneTypes.add(ZoneListing.ZONETYPE_INDUSTRIAL);
		reservedZoneTypes.add(ZoneListing.ZONETYPE_RESIDENTIAL);
		if (reservedZoneTypes.contains(tagDetails.getName()))
		{
			Window.alert("The tag name is not valid");
			return;
		}
		
		tagService.saveTagDetails(tagDetails, new AsyncCallback<TagDetails>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				GWT.log("Failed saving TagDetails: " + caught.getMessage());
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(TagDetails result) {
				// TODO Auto-generated method stub
				GWT.log("TagDetails saved");
				fetchTagDetails();
			}
		});			
			
	}
	
	private void deleteTagDetails()
	{
		/*
		 * The string ids for the selected checkboxes are stored
		 * in the checkboxes array, but CheckBox cannot go over the wire,
		 * so we need to transfer them into a String array here
		 */
		ArrayList<String> tagDetailIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				tagDetailIds.add(cb.getFormValue());
			}
			
		}
		// now send the list into the async
		tagService.deleteTagDetails(tagDetailIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Tag details deleted");
				refreshTagDetails = true;
				fetchTagDetails();
			}
		});
		
	}
	
	private void fetchTagDetails() {
		
		tagService.getTagDetails(currentStudyRegion, new AsyncCallback<ArrayList<TagDetails>>() {
	      
			public void onSuccess(ArrayList<TagDetails> result) {
	          
			  // store the result
			  tagDetailsList = result;
	          
			  // tell others that we have the result
			  eventBus.fireEvent(new ReceivedTagsEvent());
			  
			  refreshTagDetails = false;
	          clearTagDetailView();
	          
	          GWT.log("tagDetailsList=" + tagDetailsList);
	          tagList.removeAllRows();
	          
	          for (int i = 0; i < tagDetailsList.size(); i++) {
	        	final int count = i;
				final TagDetails tagDetails = tagDetailsList.get(i);
				GWT.log(tagDetails.getName());
				
				CheckBox cb = new CheckBox();
				cb.setFormValue(tagDetails.getId()); //store the id in the checkbox value
				checkboxes.add(cb); // keep track for selecting all|none to delete
				cb.setStyleName(style.checkbox());
				cb.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						GWT.log("handle click for checkbox of tagDetail("+count+")");
					}
				});
				Label name = new Label(tagDetails.getName());
				name.setStyleName(style.tagList());
				name.addStyleName(style.clickable());
				name.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						loadTagDetails(tagDetails);
					}
				});
				tagList.setWidget(i, 0, cb);
				tagList.getCellFormatter().setWidth(i, 0, "20px");
				tagList.setWidget(i, 1, name);
	          }
	      }
	     
	      public void onFailure(Throwable caught) {
	        Window.alert("Error fetching tag details");
	        GWT.log(caught.getMessage());
	      }
	    });
	}	
	
	protected void clearTagDetailView() {
		name.setText("");
		description.setText("");
		currentTagDetailsId = null;
		save.setText("Save");
	}

	public void loadTagDetails(TagDetails tagDetails)
	{
		name.setText(tagDetails.getName());
		description.setText(tagDetails.getDescription());
		currentTagDetailsId = tagDetails.getId();
		save.setText("Update");
	}

	public ArrayList<TagDetails> getTagDetailsList() {
		return tagDetailsList;
	}

}
