package org.worldbank.transport.tamt.client.importer;

import org.worldbank.transport.tamt.client.event.CancelZoneEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEventHandler;
import org.worldbank.transport.tamt.client.event.LoadCurrentStudyRegionEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.tag.TagMap;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportModule extends Composite {

	private static ImportModuleUiBinder uiBinder = GWT.create(ImportModuleUiBinder.class);

	interface ImportModuleUiBinder extends UiBinder<Widget, ImportModule> {
	}
	
	private HandlerManager eventBus;

	@UiField HTML studyRegionName;
	@UiField VerticalPanel vPanel;
	@UiField HorizontalPanel hpanel;
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField GPSTracesView gpsTracesView;


	protected StudyRegion currentStudyRegion;
	
	public ImportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		vPanel.setWidth("100%");
		hpanel.setWidth("100%");
		tabLayoutPanel.setWidth("100%");
		
		bind();
	}
	
	@UiFactory GPSTracesView initGPSTracesUI() {
		GWT.log("initializing GPSTraces from ImportModule");
		return new GPSTracesView(this.eventBus);
	}
	
	@UiHandler("studyRegionName")
	void onClickCancel(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.REGION, true));
		eventBus.fireEvent(new LoadCurrentStudyRegionEvent());				
	}	
	
	public void bind()
	{

		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				studyRegionName.setText(currentStudyRegion.getName());
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: ImportModule tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 60; // account for other study region UI
				int w = event.width;
				
				String height = Integer.toString(h) + "px";
				
				String width = Integer.toString(w) + "px";
				GWT.log("SIZE: ImportModule tabLayoutPanel height: " + height);
				GWT.log("SIZE: ImportModule tabLayoutPanel width: " + width);
				
				
				tabLayoutPanel.setHeight(height);
				
			}
		});		
		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		            if( event.getModule().equals(SwitchModuleEvent.IMPORT))
		            {
		            	eventBus.fireEvent(new GetGPSTracesEvent());
		            	if(event.isVisible())
			            {
			            	showModule();
			            } else {
			            	hideModule();
			            }
		            } else {
		            	hideModule();
		            }
		        }
		});	
		
	}

	public void showModule()
	{
		this.setVisible(true);
	}
	
	public void hideModule()
	{
		this.setVisible(false);
	}
}
