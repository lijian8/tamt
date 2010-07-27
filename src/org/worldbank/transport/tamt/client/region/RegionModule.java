package org.worldbank.transport.tamt.client.region;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class RegionModule extends Composite implements RequiresResize {

	private static RegionModuleUiBinder uiBinder = GWT.create(RegionModuleUiBinder.class);

	interface RegionModuleUiBinder extends UiBinder<Widget, RegionModule> {
	}
	
	@UiField HorizontalPanel hpanel;
	@UiField RegionMap regionMap;
	@UiField RegionInformation regionInformation;
	
	private HandlerManager eventBus;
	  
	public RegionModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		hpanel.setWidth("100%");
		
		bind();
		
	}
	
	@UiFactory RegionMap initMapUI() {
		GWT.log("initializing RegionMap from RegionModule");
		return new RegionMap(this.eventBus);
	}	
	
	@UiFactory RegionInformation initInformation() {
		GWT.log("initializing RegionInformation from RegionModule");
		return new RegionInformation(this.eventBus);
	}
	
	
	public void bind()
	{
		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		            if( event.getModule().equals(SwitchModuleEvent.REGION))
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

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		
	}
	

}
