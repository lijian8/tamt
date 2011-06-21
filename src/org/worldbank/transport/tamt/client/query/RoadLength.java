package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.RenderRoadLengthReportEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.client.services.TagServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RoadLength extends Composite {

	private static RoadLengthUiBinder uiBinder = GWT
			.create(RoadLengthUiBinder.class);

	interface RoadLengthUiBinder extends UiBinder<Widget, RoadLength> {
	}
	
	private HandlerManager eventBus;
	private TagServiceAsync tagService;
	private DialogBox dialog;
	
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HorizontalPanel hpanel;
	@UiField HorizontalPanel buttonBar;
	@UiField Button generateRoadLengthReport;
	@UiField RoadLengthByTag roadLengthByTag;
	
	
	public RoadLength(final HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		dialog = new DialogBox();
		
		tagService = GWT.create(TagService.class);
		bind();
		
	}
	
	@UiFactory RoadLengthByTag initiRoadLengthByTag() {
		return new RoadLengthByTag(this.eventBus);
	}
	
	@UiHandler("generateRoadLengthReport")
	void onClickGenerateReport(ClickEvent e) {
		String confirm = "Create road length by tag report?";

		if( Window.confirm(confirm) )
		{
			generateReport();
		} else {
		
		}
	}
	
	private void generateReport() {

		// open a modal dialog;
		dialog.setText("Generating reports");
		dialog.setWidget(new HTML("Generating road length report for this study region. <br/>This may take a few minutes"));
		dialog.setAutoHideEnabled(false);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
		
		tagService.createRoadLengthReport(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				dialog.hide();
				Window.alert("Road length report has been generated");
				
				// fire an event
				eventBus.fireEvent(new RenderRoadLengthReportEvent());
			}
		});
		
	}

	private void bind() {

		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: TripStatistics tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 150; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: TripStatistics tabLayoutPanel height: " + height);
					GWT.log("SIZE: TripStatistics tabLayoutPanel width: " + width);
					tabLayoutPanel.setHeight(height);
					
					String hwidth = Integer.toString(w-60) + "px";
					hpanel.setWidth(hwidth);
					buttonBar.setWidth(hwidth);
				}
			}
		});	
		
	}

}
