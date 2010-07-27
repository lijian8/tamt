package org.worldbank.transport.tamt.client.importer;

import org.worldbank.transport.tamt.client.event.GetGPSTracesEvent;
import org.worldbank.transport.tamt.client.event.GetGPSTracesEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.tag.TagMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportModule extends Composite {

	private static ImportModuleUiBinder uiBinder = GWT.create(ImportModuleUiBinder.class);

	interface ImportModuleUiBinder extends UiBinder<Widget, ImportModule> {
	}
	
	private HandlerManager eventBus;
	
	@UiField HorizontalPanel hpanel;
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField GPSTracesView gpsTracesView;
	
	public ImportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		hpanel.setWidth("100%");
		tabLayoutPanel.setWidth("100%");
		
		bind();
	}
	
	@UiFactory GPSTracesView initGPSTracesUI() {
		GWT.log("initializing GPSTraces from ImportModule");
		return new GPSTracesView(this.eventBus);
	}
	
	public void bind()
	{

		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: ImportModule tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 40; // account for other study region UI
				int w = event.width;
				
				String height = Integer.toString(h) + "px";
				String width = Integer.toString(w) + "px";
				GWT.log("SIZE: ImportModule tabLayoutPanel height: " + height);
				GWT.log("SIZE: ImportModule tabLayoutPanel width: " + width);
				
				
				tabLayoutPanel.setHeight(height);
				//tabLayoutPanel.setWidth(width);
				
			}
		});		
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
