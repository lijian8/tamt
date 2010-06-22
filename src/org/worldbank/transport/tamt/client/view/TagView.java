package org.worldbank.transport.tamt.client.view;

import java.util.List;

import org.worldbank.transport.tamt.client.common.ColumnDefinition;
import org.worldbank.transport.tamt.client.presenter.TagPresenter;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.client.ui.Widget;

public interface TagView<T> {

  public interface Presenter<T> {
	/* from Contacts example
    void onAddButtonClicked();
    void onDeleteButtonClicked();
    void onItemClicked(T clickedItem);
    void onItemSelected(T selectedItem);
    */
  }

  Widget asWidget();
  void setPresenter(Presenter<T> presenter);
  void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions);
  void setRowData(List<T> rowData);
}
