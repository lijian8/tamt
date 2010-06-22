package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationNavigation extends Composite {

	private static ApplicationNavigationUiBinder uiBinder = GWT
			.create(ApplicationNavigationUiBinder.class);

	interface ApplicationNavigationUiBinder extends
			UiBinder<Widget, ApplicationNavigation> {
	}
	
	interface NavigationStyle extends CssResource {
	    String selected();
	    String unselected();
	  }

	@UiField NavigationStyle style;

	@UiField HTML tagNav;
	@UiField HTML importNav;
	@UiField HTML assignNav;
	@UiField HTML queryNav;
	@UiField HTML exportNav;

	private HandlerManager eventBus;

	public ApplicationNavigation(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		// set names on modules
		// TODO: Add il8n
		tagNav.setText("Tag");
		importNav.setText("Import");
		assignNav.setText("Assign");
		queryNav.setText("Query");
		exportNav.setText("Export");
	}

	@UiHandler("tagNav")
	void onClickTag(ClickEvent e) {
		GWT.log("trigger tag module: " + this.eventBus.toString());
		
		// change ui in left nav
		setEnabled(tagNav, true);
		setEnabled(importNav, false);
		setEnabled(assignNav, false);
		setEnabled(queryNav, false);
		setEnabled(exportNav, false);
		
		// fire event so actual Module can show up too
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, true));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.ASSIGN, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, false));
		
	}
	
	@UiHandler("importNav")
	void onClickImport(ClickEvent e) {
		GWT.log("trigger import module: " + this.eventBus.toString());
		setEnabled(tagNav, false);
		setEnabled(importNav, true);
		setEnabled(assignNav, false);
		setEnabled(queryNav, false);
		setEnabled(exportNav, false);		
		
		// fire event so actual Module can show up too
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, true));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.ASSIGN, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, false));
		
	}
	
	@UiHandler("assignNav")
	void onClickAssign(ClickEvent e) {
		GWT.log("trigger assign module: " + this.eventBus.toString());
		setEnabled(tagNav, false);
		setEnabled(importNav, false);
		setEnabled(assignNav, true);
		setEnabled(queryNav, false);
		setEnabled(exportNav, false);		
		

		// fire event so actual Module can show up too
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.ASSIGN, true));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, false));
		
	}
	
	@UiHandler("queryNav")
	void onClickQuery(ClickEvent e) {
		GWT.log("trigger query module: " + this.eventBus.toString());
		setEnabled(tagNav, false);
		setEnabled(importNav, false);
		setEnabled(assignNav, false);
		setEnabled(queryNav, true);
		setEnabled(exportNav, false);

		// fire event so actual Module can show up too
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.ASSIGN, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, true));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, false));
	}
	
	@UiHandler("exportNav")
	void onClickExport(ClickEvent e) {
		GWT.log("trigger export module: " + this.eventBus.toString());
		setEnabled(tagNav, false);
		setEnabled(importNav, false);
		setEnabled(assignNav, false);
		setEnabled(queryNav, false);
		setEnabled(exportNav, true);		

		// fire event so actual Module can show up too
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.ASSIGN, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, false));
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, true));
	}	
	
	void setEnabled(HTML element, boolean enabled) {
		if(enabled)
		{
			element.addStyleName(style.selected());
		} else 
		{
			element.removeStyleName(style.selected());
		}
		//getElement().addStyle(enabled ? : style.selected() : style.unselected());
	    //getElement().removeStyle(enabled ? : style.unselected() : style.selected());
	}


}
