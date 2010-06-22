package org.worldbank.transport.tamt.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import java.util.ArrayList;
import java.util.List;

import org.worldbank.transport.tamt.client.common.ColumnDefinition;
import org.worldbank.transport.tamt.client.services.TagServiceAsync;
import org.worldbank.transport.tamt.client.view.TagView;
import org.worldbank.transport.tamt.shared.TagDetails;

public class TagPresenter implements Presenter, 
  TagView.Presenter<TagDetails> {  

  private List<TagDetails> tagDetails;
  private final TagServiceAsync tagService;
  private final HandlerManager eventBus;
  private final TagView<TagDetails> view;
  
  public TagPresenter(TagServiceAsync tagService, 
      HandlerManager eventBus, TagView<TagDetails> view,
      List<ColumnDefinition<TagDetails>> columnDefinitions) {
    this.tagService = tagService;
    this.eventBus = eventBus;
    this.view = view;
    this.view.setPresenter(this);
    this.view.setColumnDefinitions(columnDefinitions);
  }
  
  /*
  public void onAddButtonClicked() {
    eventBus.fireEvent(new SwitchModuleEvent());
  }
  
  public void onDeleteButtonClicked() {
    deleteSelectedContacts();
  }
  
  public void onItemClicked(ContactDetails contactDetails) {
    eventBus.fireEvent(new EditContactEvent(contactDetails.getId()));
  }

  public void onItemSelected(ContactDetails contactDetails) {
    if (selectionModel.isSelected(contactDetails)) {
      selectionModel.removeSelection(contactDetails);
    }
    else {
      selectionModel.addSelection(contactDetails);
    }
  }
  
  public void go(final HasWidgets container) {
    container.clear();
    container.add(view.asWidget());
    fetchContactDetails();
  }

  public void sortContactDetails() {
    
    // Yes, we could use a more optimized method of sorting, but the 
    //  point is to create a test case that helps illustrate the higher
    //  level concepts used when creating MVP-based applications. 
    //
    for (int i = 0; i < contactDetails.size(); ++i) {
      for (int j = 0; j < contactDetails.size() - 1; ++j) {
        if (contactDetails.get(j).getDisplayName().compareToIgnoreCase(contactDetails.get(j + 1).getDisplayName()) >= 0) {
          ContactDetails tmp = contactDetails.get(j);
          contactDetails.set(j, contactDetails.get(j + 1));
          contactDetails.set(j + 1, tmp);
        }
      }
    }
  }

  public void setContactDetails(List<ContactDetails> contactDetails) {
    this.contactDetails = contactDetails;
  }
  
  public List<ContactDetails> getContactDetails() {
    return contactDetails;
  }
  
  public ContactDetails getContactDetail(int index) {
    return contactDetails.get(index);
  }
  */
  
  public List<TagDetails> getTagDetails() {
	  return tagDetails;
  }
  
  public TagDetails getTagDetail(int index) {
	  return tagDetails.get(index);
  }
  
  private void fetchTagDetails() {
    tagService.getTagDetails(null, new AsyncCallback<ArrayList<TagDetails>>() {
      public void onSuccess(ArrayList<TagDetails> result) {
          tagDetails = result;
          //sortTagDetails();
          view.setRowData(tagDetails);
      }
      
      public void onFailure(Throwable caught) {
        Window.alert("Error fetching contact details");
      }
    });
  }

	@Override
	public void go(HasWidgets container) {
	    container.clear();
	    container.add(view.asWidget());
	    fetchTagDetails();
	}

  /*
  private void deleteSelectedContacts() {
    List<ContactDetails> selectedContacts = selectionModel.getSelectedItems();
    ArrayList<String> ids = new ArrayList<String>();
    
    for (int i = 0; i < selectedContacts.size(); ++i) {
      ids.add(selectedContacts.get(i).getId());
    }
    
    rpcService.deleteContacts(ids, new AsyncCallback<ArrayList<ContactDetails>>() {
      public void onSuccess(ArrayList<ContactDetails> result) {
        contactDetails = result;
        sortContactDetails();
        view.setRowData(contactDetails);
      }
      
      public void onFailure(Throwable caught) {
        System.out.println("Error deleting selected contacts");
      }
    });
  }
  */
}
