package org.worldbank.transport.tamt.client.exporter;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ExportModule extends Composite {

	private static ExportModuleUiBinder uiBinder = GWT.create(ExportModuleUiBinder.class);

	interface ExportModuleUiBinder extends UiBinder<Widget, ExportModule> {
	}

	private HTML html;
	private HandlerManager eventBus;
	
	public ExportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
	}
	
	public void bind()
	{
		eventBus.addHandler(SwitchModuleEvent.TYPE,
				new SwitchModuleEventHandler() {
			    	public void onSwitchModule(SwitchModuleEvent event) {
			            if( event.getModule().equals(SwitchModuleEvent.EXPORT))
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
