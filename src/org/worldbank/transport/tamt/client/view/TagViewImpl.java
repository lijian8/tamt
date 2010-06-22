package org.worldbank.transport.tamt.client.view;

import java.util.List;

import org.worldbank.transport.tamt.client.common.ColumnDefinition;
import org.worldbank.transport.tamt.client.view.TagView.Presenter;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class TagViewImpl<T> extends Composite implements TagView<T> {

	@UiTemplate("TagView.ui.xml")
	interface TagViewUiBinder extends UiBinder<Widget, TagViewImpl> {}
	private static TagViewUiBinder uiBinder = GWT.create(TagViewUiBinder.class);
	private Presenter<T> presenter;
	
	@UiField HTML tagsTable;
	
	private List<T> rowData;
	private List<ColumnDefinition<T>> columnDefinitions;
	
	public TagViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setPresenter(Presenter<T> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setRowData(List<T> rowData) {
		// TODO Auto-generated method stub
		this.rowData = rowData;
		GWT.log(this.rowData.toString());
		for (int i = 0; i < rowData.size(); ++i) {
			T t = rowData.get(i);
			GWT.log("rowData t=" + t.toString());
			for (int j = 0; j < columnDefinitions.size(); ++j) {
				StringBuilder sb = new StringBuilder();
				columnDefinitions.get(j).render(t, sb);
				GWT.log(sb.toString()); // TODO: put this in inner HTML
				tagsTable.setHTML( tagsTable.getHTML() + sb.toString());
			}
		}
	}

	@Override
	public void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

}
