package org.worldbank.transport.tamt.client.assign;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AssignModule extends Composite {

	private static AssignModuleUiBinder uiBinder = GWT.create(AssignModuleUiBinder.class);

	interface AssignModuleUiBinder extends UiBinder<Widget, AssignModule> {
	}
	
	private HTML html;
	private HandlerManager eventBus;
	
	public AssignModule(HandlerManager eventBus)
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
			            if( event.getModule().equals(SwitchModuleEvent.ASSIGN))
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
