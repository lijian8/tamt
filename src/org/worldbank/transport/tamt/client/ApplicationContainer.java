package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.assign.AssignModule;
import org.worldbank.transport.tamt.client.exporter.ExportModule;
import org.worldbank.transport.tamt.client.importer.ImportModule;
import org.worldbank.transport.tamt.client.query.QueryModule;
import org.worldbank.transport.tamt.client.tag.TagModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationContainer extends Composite {

	private static ApplicationContainerUiBinder uiBinder = GWT
			.create(ApplicationContainerUiBinder.class);

	interface ApplicationContainerUiBinder extends
			UiBinder<Widget, ApplicationContainer> {
	}

	@UiField TagModule tagModule;
	@UiField ImportModule importModule;
	@UiField AssignModule assignModule;
	@UiField QueryModule queryModule;
	@UiField ExportModule exportModule;

	private HandlerManager eventBus;
	
	public ApplicationContainer(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		tagModule.setVisible(true);
		importModule.setVisible(false);
		assignModule.setVisible(false);
		queryModule.setVisible(false);
		exportModule.setVisible(false);
		
	}

	@UiFactory TagModule initTag() {
		return new TagModule(this.eventBus);
	}

	@UiFactory ImportModule initImport() {
		return new ImportModule(this.eventBus);
	}
	
	@UiFactory AssignModule initAssign() {
		return new AssignModule(this.eventBus);
	}
	
	@UiFactory QueryModule initQuery() {
		return new QueryModule(this.eventBus);
	}	
	
	@UiFactory ExportModule initExport() {
		return new ExportModule(this.eventBus);
	}
}