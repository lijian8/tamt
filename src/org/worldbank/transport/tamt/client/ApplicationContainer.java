package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.event.CheckApplicationBusyEvent;
import org.worldbank.transport.tamt.client.event.CheckApplicationBusyEventHandler;
import org.worldbank.transport.tamt.client.event.MatchingPointsBusyEvent;
import org.worldbank.transport.tamt.client.event.MatchingPointsBusyEventHandler;
import org.worldbank.transport.tamt.client.event.MatchingPointsCompletedEvent;
import org.worldbank.transport.tamt.client.event.MatchingPointsCompletedEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.importer.ImportModule;
import org.worldbank.transport.tamt.client.query.QueryModule;
import org.worldbank.transport.tamt.client.region.RegionModule;
import org.worldbank.transport.tamt.client.services.AssignService;
import org.worldbank.transport.tamt.client.services.AssignServiceAsync;
import org.worldbank.transport.tamt.client.tag.TagModule;
import org.worldbank.transport.tamt.shared.AssignStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationContainer extends Composite implements RequiresResize {

	private static ApplicationContainerUiBinder uiBinder = GWT
			.create(ApplicationContainerUiBinder.class);

	interface ApplicationContainerUiBinder extends
			UiBinder<Widget, ApplicationContainer> {
	}

	@UiField VerticalPanel vpanel;
	@UiField RegionModule regionModule;
	@UiField TagModule tagModule;
	@UiField ImportModule importModule;
	@UiField QueryModule queryModule;
	@UiField ExportModule exportModule;

	private HandlerManager eventBus;
	
	private AssignServiceAsync assignService;
	protected Timer checkApplicationBusyTimer;
	protected int timerInterval;
	protected boolean thisClientMatchingPointsBusy = false;
	private boolean disablePolling = true; // set to true during development only
	
	public ApplicationContainer(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		assignService = GWT.create(AssignService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		vpanel.setWidth("100%");
		
		regionModule.setVisible(false);
		tagModule.setVisible(true);
		importModule.setVisible(false);
		queryModule.setVisible(false);
		exportModule.setVisible(false);
		
		// set up the timer, but don't fire it yet
		timerInterval = 4000; // check for busy app every 4 seconds
		checkApplicationBusyTimer = new Timer() {
			@Override
			public void run() {
				if( !thisClientMatchingPointsBusy  )
				{
					checkApplicationBusy();
				}
			}
		};
		
		// hook up the event handling
		bind();
		
		/*
		 * For new clients, we need to make sure
		 * that no background processing is 
		 * happening, so now we can fire the timer.
		 */
		if( !disablePolling  )
		{
			checkApplicationBusy(); // comment out to stop the polling
		}
		
	}
	
	private void checkApplicationBusy()
	{
		assignService.getAssignStatusInProcess(new AsyncCallback<AssignStatus>() {
			
			@Override
			public void onSuccess(AssignStatus result) {
				/*
				 * If result is NOT null, then we have an assignment
				 * in progress. Throw up the wait dialog
				 */
				if( result != null)
				{
					applicationBusy(result);
				} else {
					// only put up the timer if 
					if( !thisClientMatchingPointsBusy )
					{
						checkApplicationBusyTimer.schedule(timerInterval);
					}
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				GWT.log("failed:" + caught.getMessage());
			}
		});		
	}
	
	private void applicationBusy(AssignStatus statusInProcess)
	{
		
		//TODO: put the GPS trace name and stats in the dialog too
		
		DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Warning");
		dialogBox.setWidget(new HTML("The application is currently not available. Another user is matching GPS points to roads. Please reload the page again later."));
		
		//TODO: change to false for production
		dialogBox.setAutoHideEnabled(true); 
		
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		dialogBox.show();
	}
	
	private void bind() {

		eventBus.addHandler(CheckApplicationBusyEvent.TYPE, new CheckApplicationBusyEventHandler() {
			
			@Override
			public void onCheckApplicationBusy(CheckApplicationBusyEvent event) {
				checkApplicationBusy();
			}
		});
		
		eventBus.addHandler(MatchingPointsBusyEvent.TYPE, new MatchingPointsBusyEventHandler() {
			
			@Override
			public void onBusy(MatchingPointsBusyEvent event) {
				// turn off the checkApplicationBusy timer for this client
				// (but does not affect other clients)
				GWT.log("EVENT: cancelling timer");
				checkApplicationBusyTimer.cancel();
				
				// since the timer might already be scheduled, also set the flag
				GWT.log("EVENT: thisClientMatchingPointsBusy = true");
				thisClientMatchingPointsBusy = true;
			}
		});
		
		eventBus.addHandler(MatchingPointsCompletedEvent.TYPE, new MatchingPointsCompletedEventHandler() {
			
			@Override
			public void onCompleted(MatchingPointsCompletedEvent event) {
				// turn the checkApplicationBusy timer back on for this client
				GWT.log("EVENT: rescheduling timer");
				
				if( !disablePolling )
				{
					checkApplicationBusyTimer.schedule(timerInterval);
				}
				
				GWT.log("EVENT: thisClientMatchingPointsBusy = false");
				// since the timer might already be scheduled, also set the flag
				thisClientMatchingPointsBusy = false;
			}
		});
		
	}

	@UiFactory TagModule initTag() {
		return new TagModule(this.eventBus);
	}

	@UiFactory ImportModule initImport() {
		return new ImportModule(this.eventBus);
	}
	
	@UiFactory RegionModule initRegion() {
		return new RegionModule(this.eventBus);
	}
	
	@UiFactory QueryModule initQuery() {
		return new QueryModule(this.eventBus);
	}	
	
	@UiFactory ExportModule initExport() {
		return new ExportModule(this.eventBus);
	}

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		
	}
}