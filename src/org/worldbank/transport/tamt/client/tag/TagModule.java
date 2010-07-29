package org.worldbank.transport.tamt.client.tag;

import org.worldbank.transport.tamt.client.event.GetRoadsEvent;
import org.worldbank.transport.tamt.client.event.GetTagsEvent;
import org.worldbank.transport.tamt.client.event.GetZonesEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class TagModule extends Composite implements RequiresResize {

	private static TagModuleUiBinder uiBinder = GWT.create(TagModuleUiBinder.class);

	interface TagModuleUiBinder extends UiBinder<Widget, TagModule> {
	}
	
	@UiField HorizontalPanel hpanel;
	@UiField TagMap tagMap;
	@UiField TagInformation tagInformation;
	
	private HandlerManager eventBus;
	  
	public TagModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		hpanel.setWidth("100%");
		
		bind();
		
	}
	
	@UiFactory TagMap initMapUI() {
		GWT.log("initializing TagMap from TagModule");
		return new TagMap(this.eventBus);
	}	
	
	@UiFactory TagInformation initInformation() {
		GWT.log("initializing TagInformation from TagModule");
		return new TagInformation(this.eventBus);
	}
	
	public void bind()
	{
		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		            if( event.getModule().equals(SwitchModuleEvent.TAG))
		            {
		            	// data is loaded in TagInformation onBeforeSelection
		        		
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
