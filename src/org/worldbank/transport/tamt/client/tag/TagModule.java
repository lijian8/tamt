package org.worldbank.transport.tamt.client.tag;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TagModule extends Composite {

	private static TagModuleUiBinder uiBinder = GWT.create(TagModuleUiBinder.class);

	interface TagModuleUiBinder extends UiBinder<Widget, TagModule> {
	}
	
	@UiField TagMap tagMap;
	@UiField TagInformation tagInformation;
	
	private HandlerManager eventBus;
	  
	public TagModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		init();
		
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
	
	public void init()
	{
		// what do we need to do here?
		// fetch tags, roads, and zones?
		tagInformation.setHeight("500px");
	}
	
	public void bind()
	{
		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		            if( event.getModule().equals(SwitchModuleEvent.TAG))
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
		
		/*
		eventBus.addHandler(ShowTagsEvent.TYPE, new ShowTagsEventHandler() {
			
			@Override
			public void onShowTags(ShowTagsEvent event) {
				tagMap.setVisible(false);
			}
		});
		
		eventBus.addHandler(ShowZonesEvent.TYPE, new ShowZonesEventHandler() {
			
			@Override
			public void onShowZones(ShowZonesEvent event) {
				tagMap.setVisible(true);
			}
		});
		
		eventBus.addHandler(ShowRoadsEvent.TYPE, new ShowRoadsEventHandler() {
			
			@Override
			public void onShowRoads(ShowRoadsEvent event) {
				tagMap.setVisible(true);
			}
		});
		*/
		
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
