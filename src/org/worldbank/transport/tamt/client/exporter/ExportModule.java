package org.worldbank.transport.tamt.client.exporter;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.query.VehicleFlow;
import org.worldbank.transport.tamt.shared.StudyRegion;

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

public class ExportModule extends Composite {

	private static ExportModuleUiBinder uiBinder = GWT.create(ExportModuleUiBinder.class);

	interface ExportModuleUiBinder extends UiBinder<Widget, ExportModule> {
	}

	@UiField HorizontalPanel hpanel;
	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HTML studyRegionName;
	
	private HTML html;
	
	private HandlerManager eventBus;
	protected StudyRegion currentStudyRegion;
	
	public ExportModule(HandlerManager eventBus)
	{
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		hpanel.setWidth("100%");
		hpanel.setHeight("100%");
		
		bind();
	}

	@UiFactory DownloadCSV initDownloadCSV() {
		return new DownloadCSV(this.eventBus);
	}
	
	@UiFactory BackupDatabase initBackupDatabase() {
		return new BackupDatabase(this.eventBus);
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
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: ExportModule tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 60; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: ExportModule tabLayoutPanel height: " + height);
					GWT.log("SIZE: ExportModule tabLayoutPanel width: " + width);
					tabLayoutPanel.setHeight(height);
					
					String hwidth = Integer.toString(w-34) + "px";
					hpanel.setWidth(hwidth);
				}
			}
		});	
		
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
