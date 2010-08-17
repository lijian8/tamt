package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.importer.ImportModule;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryModule extends Composite {

	private static QueryModuleUiBinder uiBinder = GWT.create(QueryModuleUiBinder.class);

	interface QueryModuleUiBinder extends UiBinder<Widget, QueryModule> {
	}

	@UiField HorizontalPanel hpanel;
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HTML studyRegionName;
	
	private HTML html;
	
	private HandlerManager eventBus;
	protected StudyRegion currentStudyRegion;
	
	
	public QueryModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		hpanel.setWidth("100%");
		hpanel.setHeight("100%");
		
		//container.setWidth("100%");
		//container.setHeight("100%");
		
		bind();
	}
	
	@UiFactory VehicleFlow initVehicleFlow() {
		return new VehicleFlow(this.eventBus);
	}

	@UiFactory DataInterpolation initDataInterpolation() {
		return new DataInterpolation(this.eventBus);
	}
	
	@UiFactory SpeedBins initSpeedBins() {
		return new SpeedBins(this.eventBus);
	}	
	
	@UiFactory EngineSoakTimes initEngineSoakTimes() {
		return new EngineSoakTimes(this.eventBus);
	}	
	
	public void bind()
	{
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				if (currentStudyRegion != null){
					studyRegionName.setText(currentStudyRegion.getName());
				} else {
					studyRegionName.setText("<Not Set>");
				}
				
			}
		});
		
		eventBus.addHandler(SwitchModuleEvent.TYPE,
				new SwitchModuleEventHandler() {
			    	public void onSwitchModule(SwitchModuleEvent event) {
			            if( event.getModule().equals(SwitchModuleEvent.QUERY))
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
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: QueryModule tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 60; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: QueryModule tabLayoutPanel height: " + height);
					GWT.log("SIZE: QueryModule tabLayoutPanel width: " + width);
					tabLayoutPanel.setHeight(height);
					
					String hwidth = Integer.toString(w-34) + "px";
					hpanel.setWidth(hwidth);
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
