package org.worldbank.transport.tamt.client.importer;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ImportModule extends Composite {

	private static ImportModuleUiBinder uiBinder = GWT.create(ImportModuleUiBinder.class);

	interface ImportModuleUiBinder extends UiBinder<Widget, ImportModule> {
	}
	
	private HTML html;
	private HandlerManager eventBus;
	
	public ImportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	}
	
	public void bind()
	{
		/*
		eventBus.addHandler(SwitchImportModuleEvent.TYPE,
				new SwitchImportModuleEventHandler() {
			    	public void onSwitchImportModule(SwitchImportModuleEvent event) {
			            GWT.log("switch import module");
			            switchModule();
			        }
			});
		*/
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
