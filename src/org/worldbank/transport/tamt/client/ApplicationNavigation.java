package org.worldbank.transport.tamt.client;

import java.util.HashMap;

import org.worldbank.transport.tamt.client.event.SwitchModuleEvent;
import org.worldbank.transport.tamt.client.event.SwitchModuleEventHandler;

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
	    String selectedSettings();
	    String unselected();
	  }

	@UiField NavigationStyle style;

	@UiField HTML regionNav;
	@UiField HTML tagNav;
	@UiField HTML importNav;
	@UiField HTML queryNav;
	@UiField HTML exportNav;

	private HandlerManager eventBus;
	
	private HashMap<String, Integer> navs;

	public ApplicationNavigation(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		navs = new HashMap<String, Integer>();
		navs.put(SwitchModuleEvent.REGION, 0);
		navs.put(SwitchModuleEvent.TAG, 1);
		navs.put(SwitchModuleEvent.IMPORT, 2);
		navs.put(SwitchModuleEvent.QUERY, 3);
		navs.put(SwitchModuleEvent.EXPORT, 4);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
		
		// select Home
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.REGION, true));
	}

	private void bind()
	{
		eventBus.addHandler(SwitchModuleEvent.TYPE,
			new SwitchModuleEventHandler() {
		    	public void onSwitchModule(SwitchModuleEvent event) {
		    		String module = event.getModule();
		    		GWT.log("NAV: module=" + module);
		    		Integer m = navs.get(module);
		    		GWT.log("NAV: m=" + m);
		    		switch (m) {
		    		case 0:
						// region
		    			setEnabled(regionNav, true);
		    			setEnabled(tagNav, false);
		    			setEnabled(importNav, false);
		    			setEnabled(queryNav, false);
		    			setEnabled(exportNav, false);
						break;
		    		case 1:
						// tag
		    			setEnabled(regionNav, false);
		    			setEnabled(tagNav, true);
		    			setEnabled(importNav, false);
		    			setEnabled(queryNav, false);
		    			setEnabled(exportNav, false);
						break;
		    		case 2:
						// import
		    			setEnabled(regionNav, false);
		    			setEnabled(tagNav, false);
		    			setEnabled(importNav, true);
		    			setEnabled(queryNav, false);
		    			setEnabled(exportNav, false);
						break;
		    		case 3:
						// query
		    			setEnabled(regionNav, false);
		    			setEnabled(tagNav, false);
		    			setEnabled(importNav, false);
		    			setEnabled(queryNav, true);
		    			setEnabled(exportNav, false);
						break;						
		    		case 4:
						// export
		    			setEnabled(regionNav, false);
		    			setEnabled(tagNav, false);
		    			setEnabled(importNav, false);
		    			setEnabled(queryNav, false);
		    			setEnabled(exportNav, true);
						break;						
		    		}
		    	}
		});
	}
	
	@UiHandler("tagNav")
	void onClickTag(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.TAG, true));
	}
	
	@UiHandler("importNav")
	void onClickImport(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.IMPORT, true));
	}
	
	@UiHandler("regionNav")
	void onClickRegion(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.REGION, true));
	}
	
	@UiHandler("queryNav")
	void onClickQuery(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.QUERY, true));
	}
	
	@UiHandler("exportNav")
	void onClickExport(ClickEvent e) {
		eventBus.fireEvent(new SwitchModuleEvent(SwitchModuleEvent.EXPORT, true));
	}	
	
	void setEnabled(HTML element, boolean enabled) {
		if(enabled)
		{
			/*GWT.log("NAV: element=" + element);
			if( element == regionNav)
			{
				element.addStyleName(style.selectedSettings());
			} else {
				element.addStyleName(style.selected());
			}
			*/
			element.addStyleName(style.selected());
			
		} else 
		{
			/*if( element == regionNav)
			{
				element.removeStyleName(style.selectedSettings());
			} else {
				element.removeStyleName(style.selected());
			}*/
			element.removeStyleName(style.selected());
		}
		//getElement().addStyle(enabled ? : style.selected() : style.unselected());
	    //getElement().removeStyle(enabled ? : style.unselected() : style.selected());
	}


}
