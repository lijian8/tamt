package org.worldbank.transport.tamt.client.importer;

import org.worldbank.transport.tamt.client.event.GetGPSTracesEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.tag.TagMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportModule extends Composite {

	private static ImportModuleUiBinder uiBinder = GWT.create(ImportModuleUiBinder.class);

	interface ImportModuleUiBinder extends UiBinder<Widget, ImportModule> {
	}
	
	private HandlerManager eventBus;
	
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField GPSTracesView gpsTracesView;
	
	public ImportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	}
	
	@UiFactory GPSTracesView initGPSTracesUI() {
		GWT.log("initializing GPSTraces from ImportModule");
		return new GPSTracesView(this.eventBus);
	}
	
	public void bind()
	{

		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		            if( event.getModule().equals(SwitchModuleEvent.IMPORT))
		            {
			            if(event.isVisible())
			            {
			            	showModule();
			            } else {
			            	hideModule();
			            }
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
