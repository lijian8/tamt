package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.RenderTripStatisticsEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.TripStatisticsReportService;
import org.worldbank.transport.tamt.client.services.TripStatisticsReportServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
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

public class TripStatistics extends Composite {

	private static EngineSoakTimesUiBinder uiBinder = GWT
			.create(EngineSoakTimesUiBinder.class);

	interface EngineSoakTimesUiBinder extends UiBinder<Widget, TripStatistics> {
	}
	
	private HandlerManager eventBus;
	private TripStatisticsReportServiceAsync tripStatisticsReportService;
	private DialogBox dialog;
	
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HorizontalPanel hpanel;
	@UiField HorizontalPanel buttonBar;
	@UiField Button generateTripStatisics;
	@UiField TripBin tripBin;
	@UiField SoakBin soakBin;
	
	
	public TripStatistics(final HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		dialog = new DialogBox();
		
		tripStatisticsReportService = GWT.create(TripStatisticsReportService.class);
		bind();
		
		tabLayoutPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				int almostSelected = event.getItem();
				/*
				 * TODO: We probably don't want to fire these every time, but
				 * we might need to check if they are loaded
				 */
				switch (almostSelected) {
					case 0:
						// trip bin
						// eventBus.fireEvent(new RenderTripStatisticsEvent());
						break;
					case 1:
						// engine soak times
						// eventBus.fireEvent(new RenderTripStatisticsEvent());
						break;
				}
			}
		});
	}
	
	@UiFactory TripBin initTripBin() {
		return new TripBin(this.eventBus);
	}

	@UiFactory SoakBin initSoakBin() {
		return new SoakBin(this.eventBus);
	}
	
	@UiHandler("generateTripStatisics")
	void onClickGenerateTripStatisics(ClickEvent e) {
		String confirm = "Create trip statistics report?";

		if( Window.confirm(confirm) )
		{
			generateTripStatistics();
		} else {
		
		}
	}
	
	private void generateTripStatistics() {

		// open a modal dialog;
		dialog.setText("Generating reports");
		dialog.setWidget(new HTML("Generating trip statistics report for this study region. <br/>This may take a few minutes"));
		dialog.setAutoHideEnabled(false);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
		
		tripStatisticsReportService.createTripStatisticsReport(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				dialog.hide();
				//resetScreen();
				Window.alert("Trip statistics report has been generated");
				
				// fire an event
				eventBus.fireEvent(new RenderTripStatisticsEvent());
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dialog.hide();
				//resetScreen();
				Window.alert(caught.getMessage());
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
