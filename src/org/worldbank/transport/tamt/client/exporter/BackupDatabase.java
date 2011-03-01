package org.worldbank.transport.tamt.client.exporter;

import java.util.HashMap;

import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.BackupService;
import org.worldbank.transport.tamt.client.services.BackupServiceAsync;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BackupDatabase extends Composite {

	private static BackupDatabaseUiBinder uiBinder = GWT
			.create(BackupDatabaseUiBinder.class);

	interface BackupDatabaseUiBinder extends UiBinder<Widget, BackupDatabase> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String dayType();
		String cell();
		String hour();
		String hourHeader();
		String cellHeaderInteger();
		String cellHeaderDouble();
		String checkbox();
	}

	@UiField Style style;
	private HandlerManager eventBus;
	
	@UiField(provided=true) HorizontalPanel outerHPanel;
	@UiField VerticalPanel rightPane;
	
	private StudyRegion currentStudyRegion;
	
	private BackupServiceAsync backupService;
	
	public BackupDatabase(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		backupService = GWT.create(BackupService.class);
		
		outerHPanel = new HorizontalPanel();
		outerHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				
		initWidget(uiBinder.createAndBindUi(this));
		
		resetScreen();
		
		//leftPane.setWidth("50%");
		rightPane.setWidth("50%");
		
		//outerHPanel.setCellHorizontalAlignment(leftPane, HasHorizontalAlignment.ALIGN_LEFT);
		outerHPanel.setCellHorizontalAlignment(rightPane, HasHorizontalAlignment.ALIGN_LEFT);
		
		bind();
		
	}
	
	
	private void resetScreen()
	{
		resetRightPane();
	}
	
	private void resetRightPane()
	{
		// nothing to do
	}
	
	private void bind() {
		
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				resetScreen();
			}
		});		
		
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: BackupDatabase width: " + event.width);
				int h = event.height - 250; // account for other query module UI
				int reportH = h - 20;
				int w = event.width - 90;
				GWT.log("SIZE: BackupDatabase adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: BackupDatabase adjusted height: " + height);
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: BackupDatabase adjusted width: " + width);
					rightPane.setWidth(width);
				}
				
			}
		});	
		
	}

}
