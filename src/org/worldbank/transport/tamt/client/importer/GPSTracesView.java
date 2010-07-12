package org.worldbank.transport.tamt.client.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.event.CloseWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEventHandler;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.services.GPSTraceService;
import org.worldbank.transport.tamt.client.services.GPSTraceServiceAsync;
import org.worldbank.transport.tamt.client.tag.TagMap;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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

	@UiField Label select;
	@UiField Label all;
	@UiField Label none;
	@UiField Label refresh;
	@UiField Button delete;
	@UiField Button process;
	
	@UiField VerticalPanel panel;
	@UiField ScrollPanel scroller;
	
	@UiField FlexTable gpsTraceList;
	@UiField FileUpload fileUpload;
	@UiField TextBox name;
	@UiField TextBox description;
	@UiField FormPanel uploadForm;
	@UiField Button submit;
	
	private HandlerManager eventBus;
	
	private GPSTraceServiceAsync traceService;
	private ArrayList<GPSTrace> gpsTraces;
	private ArrayList<CheckBox> checkboxes;
	
	public GPSTracesView(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		checkboxes = new ArrayList<CheckBox>();
		traceService = GWT.create(GPSTraceService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		bind();
		
		GWT.log("post init uploadForm=" + uploadForm);
		GWT.log("post init panel=" + panel);
		
		// load the data
		//TODO: move this so it only fires when the 
		// user clicks the Import nav item
		eventBus.fireEvent(new GetGPSTracesEvent());
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
		f.add(description);
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
	
	@UiHandler("refresh")
	void onClickRefresh(ClickEvent e) {
		fetchGPSTraces();
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
		}
	}	
	
	@UiHandler("submit")
	void onClickSubmit(ClickEvent e)
	{
		eventBus.fireEvent( new OpenWaitModelDialogEvent("Uploading GPS traces", "This may take a few minutes.") );
		uploadForm.submit();
	}
	
	public void bind()
	{
		eventBus.addHandler(GetGPSTracesEvent.TYPE, new GetGPSTracesEventHandler() {
			
			@Override
			public void onGetGPSTraces(GetGPSTracesEvent event) {
				fetchGPSTraces();
			}
		});	
	}
	
	private void fetchGPSTraces()
	{
		//gpsTraceList.removeAllRows();
		//gpsTraceList.setWidget(0, 0, new HTML("Reloading GPS traces..."));
		// TODO: for now we just use a Default region
		StudyRegion region = new StudyRegion();
		region.setName("default");
		
		traceService.getGPSTraces(region, new AsyncCallback<ArrayList<GPSTrace>>() {
			
			@Override
			public void onSuccess(ArrayList<GPSTrace> result) {
				
				gpsTraces = result;
				
				// clear out the table
				gpsTraceList.removeAllRows();
				gpsTraceList.clear();
				
				checkboxes = new ArrayList<CheckBox>();
				
				gpsTraceList.setWidget(0, 1, new HTML("Name"));
				gpsTraceList.setWidget(0, 2, new HTML("Description"));
				gpsTraceList.setWidget(0, 3, new HTML("GPS records"));
				gpsTraceList.setWidget(0, 4, new HTML("Upload date"));
				//gpsTraceList.setWidget(0, 4, new HTML("Processed?"));
				//gpsTraceList.setWidget(0, 5, new HTML("Processed date"));
				gpsTraceList.getRowFormatter().setStyleName(0, style.columnHeader());
				
				for (int i = 0; i < gpsTraces.size(); i++) {
		        	final int count = i;
					final GPSTrace gpsTrace = gpsTraces.get(i);
					GWT.log(gpsTrace.getName());
					
					CheckBox cb = new CheckBox();
					cb.setFormValue(gpsTrace.getId()); //store the id in the checkbox value
					checkboxes.add(cb); // keep track for selecting all|none to delete
					cb.setStyleName(style.checkbox());
					cb.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							GWT.log("handle click for checkbox of gpsTrace("+count+")");
						}
					});
					Label name = new Label(gpsTrace.getName());
					name.setStyleName(style.list());
					name.addStyleName(style.clickable());
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
					
					int row = i + 1; // to account for column headers
					gpsTraceList.setWidget(row, 0, cb);
					gpsTraceList.getCellFormatter().setWidth(row, 0, "20px");
					gpsTraceList.setWidget(row, 1, name);
					gpsTraceList.setWidget(row, 2, desc);
					gpsTraceList.setWidget(row, 3, records);
					gpsTraceList.setWidget(row, 4, upload);
					//gpsTraceList.setWidget(row, 4, processed);
					//gpsTraceList.setWidget(row, 5, processedDate);
					
		          }				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error fetching GPS traces");
		        GWT.log("Error fetching GPS traces: " + caught.getMessage());
			}
		});
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
				fetchGPSTraces();
				eventBus.fireEvent( new CloseWaitModelDialogEvent() );
			}
		});
	}

}
