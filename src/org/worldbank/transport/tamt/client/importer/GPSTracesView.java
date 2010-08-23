package org.worldbank.transport.tamt.client.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.CloseWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEventHandler;
import org.worldbank.transport.tamt.client.event.MatchingPointsBusyEvent;
import org.worldbank.transport.tamt.client.event.MatchingPointsCompletedEvent;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.AssignService;
import org.worldbank.transport.tamt.client.services.AssignServiceAsync;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.GPSTraceServiceAsync;
import org.worldbank.transport.tamt.shared.AssignStatus;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class GPSTracesView extends Composite {

	private static GPSTracesViewUiBinder uiBinder = GWT
			.create(GPSTracesViewUiBinder.class);

	interface GPSTracesViewUiBinder extends UiBinder<Widget, GPSTracesView> {
	}

	interface GPSTraceStyle extends CssResource {
	    String list();
	    String checkbox();
	    String clickable();
	    String columnHeader();
	}

	@UiField GPSTraceStyle style;

	@UiField CheckBox toggleAllCheckboxes;
	@UiField Button refresh;
	@UiField Button delete;
	@UiField Button process;
	@UiField Button assignToRoads;
	
	@UiField VerticalPanel panel;
	
	@UiField FlexTable gpsTraceList;
	@UiField ScrollPanel scrollPanel;
	
	@UiField FileUpload fileUpload;
	@UiField TextBox name;
	//@UiField TextBox description; // user can't edit; reserved for filename in list
	@UiField Hidden studyRegionId;
	@UiField FormPanel uploadForm;
	@UiField Button submit;
	
	private HandlerManager eventBus;
	
	private GPSTraceServiceAsync traceService;
	private AssignServiceAsync assignService;
	private ArrayList<GPSTrace> gpsTraces;
	private HashMap<String, String> gpsTraceNames;
	private ArrayList<CheckBox> checkboxes;

	private DialogBox dialogBox = new DialogBox();
	private FlexTable dialogTable = new FlexTable();
	private Button button;

	private Label dialogGpsTraceName;
	private Label dialogPointsTotal;
	private Label dialogPointsProcessed;
	private Label dialogPointsMatched;
	private Label dialogLastUpdated;
	private Label dialogStatus;
	private Button closeDialog;

	protected StudyRegion currentStudyRegion;
	
	
	public GPSTracesView(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		checkboxes = new ArrayList<CheckBox>();
		gpsTraceNames = new HashMap<String, String>();
		
		traceService = GWT.create(GPSTraceService.class);
		assignService = GWT.create(AssignService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		bind();
		
		GWT.log("post init uploadForm=" + uploadForm);
		GWT.log("post init panel=" + panel);
		
		// load the data
		//TODO: move this so it only fires when the 
		// user clicks the Import nav item
		// eventBus.fireEvent(new GetGPSTracesEvent());
		
		// Set up the status dialog layout
		dialogBox.setText("Matching GPS Points to Roads and Zones");
		VerticalPanel dialogVerticalPanel = new VerticalPanel();
		HTML dialogDesc = new HTML("The system is currently matching " +
				"GPS points to roads and zones." +
				"This may take some time depending on the number of points, roads and zones in the study region.<hr/>");
		dialogVerticalPanel.add(dialogDesc);
		
		// elements in the table
		dialogTable.setWidget(0, 0, new Label("GPS trace name:"));
		dialogTable.setWidget(1, 0, new Label("Total points:"));
		dialogTable.setWidget(2, 0, new Label("Processed points:"));
		dialogTable.setWidget(3, 0, new Label("Matched points:"));
		dialogTable.setWidget(4, 0, new Label("Last updated:"));
		dialogTable.setWidget(5, 0, new Label("Status:"));
		
		dialogVerticalPanel.add(dialogTable);
		
		
		dialogBox.setWidget(dialogVerticalPanel);
		
		
	}
	
	@UiFactory VerticalPanel initVertPanel() {
		GWT.log("pre init panel=" + panel);
		VerticalPanel p = new VerticalPanel();
		p.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		//p.setBorderWidth(1);
		return p;
	}
	
	@UiFactory FormPanel initFormPanel() {
		GWT.log("creating form panel");
		GWT.log("pre init uploadForm=" + uploadForm);
		FormPanel f = new FormPanel();
		f.setAction(GWT.getModuleBaseURL() + "uploadGPS");
		f.setEncoding(FormPanel.ENCODING_MULTIPART);
		f.setMethod(FormPanel.METHOD_POST);
		f.add(name);
		//f.add(description);
		f.add(fileUpload);
		f.add(submit);
		f.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				
				eventBus.fireEvent( new CloseWaitModelDialogEvent() );
				
				if(event.getResults().equals("200"))
				{
					// 200 is OK
					uploadForm.reset();
					fetchGPSTraces();
				} 
				else {
					Window.alert(event.getResults());
					//uploadForm.reset();
				}
			}
		});		
		return f;
	}
	@UiHandler("toggleAllCheckboxes")
	void onClickToggleAllCheckboxes(ClickEvent e) {
		CheckBox master = (CheckBox) e.getSource();
		GWT.log("toggleCheckboxes:" + master.getValue());
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(master.getValue());
			GWT.log("cb: form value("+cb.getFormValue()+"), checked value("+cb.getValue()+")");
		}	
	}
	
	@UiHandler("refresh")
	void onClickRefresh(ClickEvent e) {
		gpsTraceList.removeAllRows();
		uncheckMasterCheckBox();
		fetchGPSTraces();
	}
	
	@UiHandler("assignToRoads")
	void onClickAssignToRoads(ClickEvent e) {
		assignPointsToRoads();
	}
	
	@UiHandler("process")
	void onClickProcess(ClickEvent e) {
		//Window.alert("Process: " + e.toString());
		//TODO: process each selected gps trace
		//TODO: warn that processing more than one at a time 
		// can bog down the system
		processGPSTraces();
	}
	
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		//Window.alert("size of checkboxes:" + checkboxes.size());
		if( Window.confirm("Delete all checked GPS traces?") )
		{
			eventBus.fireEvent( new OpenWaitModelDialogEvent("Deleting GPS traces", "This may take a few minutes.") );
			deleteGPSTraces();
		} else {
			uncheckMasterCheckBox();
			for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
				CheckBox cb = (CheckBox) iterator.next();
				cb.setValue(false);
			}
		}
	}	
	
	@UiHandler("submit")
	void onClickSubmit(ClickEvent e)
	{
		eventBus.fireEvent( new OpenWaitModelDialogEvent("Uploading GPS traces", "This may take a few minutes.") );
		uploadForm.submit();
	}
	
	private void uncheckMasterCheckBox()
	{
		toggleAllCheckboxes.setValue(false);
	}
	
	public void bind()
	{
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				if( currentStudyRegion != null)
				{
					studyRegionId.setValue( currentStudyRegion.getId() );
				}
			}
		});
		
		eventBus.addHandler(GetGPSTracesEvent.TYPE, new GetGPSTracesEventHandler() {
			
			@Override
			public void onGetGPSTraces(GetGPSTracesEvent event) {
				fetchGPSTraces();
			}
		});	
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: GPSTracesView scroll panel height within: " + event.height);
				
				int h = event.height - 275; // account for other study region UI
				
				if( h > -1)
				{
				String height = Integer.toString(h) + "px";
				GWT.log("SIZE: GPSTracesView scroll panel height: " + height);
				
				scrollPanel.setHeight(height);
				panel.setHeight(height);
				}
			}
		});				
	}
	
	private void fetchGPSTraces()
	{
		//gpsTraceList.removeAllRows();
		//gpsTraceList.setWidget(0, 0, new HTML("Reloading GPS traces..."));
		// TODO: for now we just use a Default region
		//StudyRegion region = new StudyRegion();
		//region.setName("default");
		
		// replaced with currentStudyRegion
		traceService.getGPSTraces(currentStudyRegion, new AsyncCallback<ArrayList<GPSTrace>>() {
			
			@Override
			public void onSuccess(ArrayList<GPSTrace> result) {
				
				gpsTraces = result;
				
				// clear out the table
				gpsTraceList.removeAllRows();
				gpsTraceList.clear();
				
				
				gpsTraceList.setWidget(0, 1, new HTML("Name"));
				gpsTraceList.setWidget(0, 2, new HTML("Filename"));
				gpsTraceList.setWidget(0, 3, new HTML("Upload date"));
				gpsTraceList.setWidget(0, 4, new HTML("GPS records"));
				gpsTraceList.setWidget(0, 5, new HTML("Tagged GPS records"));
				gpsTraceList.setWidget(0, 6, new HTML("Tagged date"));
				//gpsTraceList.setWidget(0, 5, new HTML("Processed date"));
				gpsTraceList.getRowFormatter().setStyleName(0, style.columnHeader());
				
		        // clear out the checkboxes
		        checkboxes.clear();
		        uncheckMasterCheckBox();
		          
				for (int i = 0; i < gpsTraces.size(); i++) {
		        	final int count = i;
					final GPSTrace gpsTrace = gpsTraces.get(i);
					GWT.log(gpsTrace.getName());
					
					CheckBox cb = new CheckBox();
					cb.setFormValue(gpsTrace.getId()); //store the id in the checkbox value
					checkboxes.add(cb); // keep track for selecting all|none to delete
					cb.setStyleName(style.checkbox());

					// if a checkbox is checked, deselect the master checkbox
					cb.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							uncheckMasterCheckBox();
						}
					});
					
					Label name = new Label(gpsTrace.getName());
					name.setStyleName(style.list());
					
					// also store id/name for use later
					gpsTraceNames.put(gpsTrace.getId(), gpsTrace.getName());
					
					//TODO: add clickable only if we load the details to edit
					// name.addStyleName(style.clickable());
					name.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							//TODO: loadTagDetails(gpsTrace);
						}
					});
					
					String description = gpsTrace.getDescription();
					if( description.length() > 10)
					{
						description = description.substring(0, 10) + "...";
					}
					Label desc = new Label(description);
					desc.setTitle(gpsTrace.getDescription());
					
					Label upload = new Label(DateTimeFormat.getMediumDateTimeFormat().format(gpsTrace.getUploadDate()));
					Label processed = new Label("No");
					Label processedDate = new Label("n/a");
					if( gpsTrace.isProcessed() )
					{
						processed.setText("Yes");
						processedDate.setText(DateTimeFormat.getMediumDateTimeFormat().format(gpsTrace.getProcessDate()));
					}
					
					Label records = new Label(Integer.toString(gpsTrace.getRecordCount()));
					Label matchedRecords = new Label(Integer.toString(gpsTrace.getMatchedCount()));
					
					int row = i + 1; // to account for column headers
					gpsTraceList.setWidget(row, 0, cb);
					gpsTraceList.getCellFormatter().setWidth(row, 0, "20px");
					gpsTraceList.setWidget(row, 1, name);
					gpsTraceList.setWidget(row, 2, desc);
					gpsTraceList.setWidget(row, 3, upload);
					gpsTraceList.setWidget(row, 4, records);
					gpsTraceList.setWidget(row, 5, matchedRecords);
					gpsTraceList.setWidget(row, 6, processedDate);
					
					/* MOVED THIS FROM PER ROW BUTTON TO TABLE HEADER
					 * For Assign, add a button to trigger the assignment
					 *
					final AssignStatus status = new AssignStatus();
					status.setGpsTraceId(gpsTrace.getId());
					status.setGpsTraceName(gpsTrace.getName());
					status.setPointsTotal(gpsTrace.getRecordCount());
					Button assignButton = new Button("Assign");
					assignButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							
							/*
							 * Firing this will event will trigger
							 * the checkApplicationBusyTimer in ApplicationContainer to cancel
							 * so that the user is not contending with
							 * two waiting dialogs
							 *
							GWT.log("EVENT: fire MatchingPointsBusyEvent");
							eventBus.fireEvent(new MatchingPointsBusyEvent());
							
							assignService.assignTagToPoints(status, new AsyncCallback<AssignStatus>() {

								@Override
								public void onFailure(Throwable caught) {
									GWT.log("failed!", caught);
									Window.alert(caught.getMessage());
								}

								@Override
								public void onSuccess(AssignStatus result) {
									GWT.log("success!");
									showAssignDialog(result);
									//Window.alert(result.getUpdated().toString());
								}
							});
						}
					});
					gpsTraceList.setWidget(row, 5, assignButton);
					*/
					
		          }				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error fetching GPS traces");
		        GWT.log("Error fetching GPS traces: " + caught.getMessage());
			}
		});
	}
	
	private void showAssignDialog(final AssignStatus status)
	{
		dialogGpsTraceName = new Label(status.getGpsTraceName());
		dialogLastUpdated= new Label(status.getLastUpdated().toString());
		dialogPointsMatched= new Label(Integer.toString(status.getPointsMatched()));
		dialogPointsProcessed= new Label(Integer.toString(status.getPointsProcessed()));
		dialogPointsTotal= new Label(Integer.toString(status.getPointsTotal()));
		String statusMessage = "Incomplete";
		if( status.isComplete())
		{
			statusMessage = "Complete"; // dialog is actually closed by the time we get here
		}
		dialogStatus = new Label(statusMessage);
		
		dialogTable.setWidget(0, 1, dialogGpsTraceName);
		dialogTable.setWidget(1, 1, dialogPointsTotal);
		dialogTable.setWidget(2, 1, dialogPointsProcessed);
		dialogTable.setWidget(3, 1, dialogPointsMatched);
		dialogTable.setWidget(4, 1, dialogLastUpdated);
		dialogTable.setWidget(5, 1, dialogStatus);
		
		closeDialog = new Button("Processing...");
		closeDialog.setEnabled(false);
		closeDialog.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Firing this will event will trigger
				 * the checkApplicationBusyTimer in ApplicationContainer to restart
				 * so that the user can be interrupted 
				 * by another client issuing an assignment process
				 */
				GWT.log("EVENT: fire MatchingPointsCompletedEvent");
				eventBus.fireEvent(new MatchingPointsCompletedEvent());
				
				// uncheck the selected archive
				uncheckAll();
				
				// close the dialog
				dialogBox.hide();
				
				// and refetch to update the table with processed info
				fetchGPSTraces();
			}
		});
		//closeDialog.setVisible(false);
		dialogTable.setWidget(6, 1, closeDialog);
		
		dialogBox.setAutoHideEnabled(false);
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
		dialogBox.show();	
		
		pollStatus(status);
		
	}
	
	private void updateDialog(final AssignStatus status)
	{
		
		GWT.log("update dialog with status=" + status);
		
		dialogGpsTraceName.setText(status.getGpsTraceName());
		dialogLastUpdated.setText(status.getLastUpdated().toString());
		dialogPointsMatched.setText(Integer.toString(status.getPointsMatched()));
		dialogPointsProcessed.setText(Integer.toString(status.getPointsProcessed()));
		dialogPointsTotal.setText(Integer.toString(status.getPointsTotal()));
		
		String statusMessage = "Incomplete";
		if( status.isComplete())
		{
			statusMessage = "Complete"; // dialog is actually closed by the time we get here
		}
		dialogStatus.setText(statusMessage);
		
	}
	
	private void pollStatus(final AssignStatus status)
	{
		GWT.log("poll status on a 10 s timer....");
		Timer t = new Timer() {

			@Override
			public void run() {
				assignService.checkStatus(status, new AsyncCallback<AssignStatus>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to check assigned status: " + caught.getMessage());
					}

					@Override
					public void onSuccess(AssignStatus returnedStatus) {
						GWT.log("successful return from assignService.checkStatus");
						GWT.log("return status=" + returnedStatus.isComplete());
						if( returnedStatus.isComplete() )
						{
							updateDialog(returnedStatus);
							closeDialog.setEnabled(true);
							closeDialog.setText("OK");
							closeDialog.setVisible(true); // instead of auto hide with dialogBox.hide()
						} else {
							updateDialog(returnedStatus);
							pollStatus(returnedStatus); // poll againn 10 s
						}
						
					}
				});
			}
			
		};
		t.schedule(1000); // 10 seconds
			
	}
	
	private void processGPSTraces()
	{
		ArrayList<String> gpsTraceIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				gpsTraceIds.add(cb.getFormValue());
			}
			
			traceService.processGPSTraces(gpsTraceIds, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					fetchGPSTraces(); // will refresh UI with process status updated
				}
			});
		}		
	}

	private void assignPointsToRoads() {
		ArrayList<String> gpsTraceIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				gpsTraceIds.add(cb.getFormValue());
			}
		}
		
		if( gpsTraceIds.size() == 0 )
		{
			Window.alert("Please select a GPS archive.");
			return;
		}
		
		/*
		 * If more than one is checked, alert the user this is not 
		 * allowed due to heavy server processing
		 */
		if( gpsTraceIds.size() > 1)
		{
			Window.alert("Due to heavy loads on the server, only one" +
					"\n GPS archive at a time may have its GPS records tagged by road.");
			
			// deselect all checkboxes
			uncheckAll();
			return;
		}
		
		// ok, we have one and only one, so let's kick off the assignment process
		String gpsTraceId = gpsTraceIds.get(0);
		AssignStatus status = new AssignStatus();
		status.setGpsTraceId(gpsTraceId);
		
		// get the name from the hash
		String name = gpsTraceNames.get(gpsTraceId);
		status.setGpsTraceName(name);
		
		GWT.log("assignTagToPoints for:" + status);
		
		GWT.log("EVENT: fire MatchingPointsBusyEvent");
		eventBus.fireEvent(new MatchingPointsBusyEvent());
		
		// fire the async
		assignService.assignTagToPoints(status, new AsyncCallback<AssignStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failed!", caught);
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(AssignStatus result) {
				GWT.log("success!");
				showAssignDialog(result);
			}
		});
	}
	
	private void deleteGPSTraces() {
		ArrayList<String> gpsTraceIds = new ArrayList<String>();
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			// boolean to see if it is checked
			if(cb.getValue())
			{
				gpsTraceIds.add(cb.getFormValue());
			}
		}
		GWT.log("ERROR in ids to be deleted?: " + gpsTraceIds);
		traceService.deleteGPSTraces(gpsTraceIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				uncheckMasterCheckBox();
				fetchGPSTraces();
				eventBus.fireEvent( new CloseWaitModelDialogEvent() );
			}
		});
	}
	
	private void uncheckAll()
	{
		for (Iterator iterator = checkboxes.iterator(); iterator.hasNext();) {
			CheckBox cb = (CheckBox) iterator.next();
			cb.setValue(false);
		}
	}

}
