package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.client.services.TagServiceAsync;
import org.worldbank.transport.tamt.client.services.TestConnectionService;
import org.worldbank.transport.tamt.client.services.TestConnectionServiceAsync;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TransportActivityMeasurementToolkit implements EntryPoint {
	
	private HandlerManager eventBus = new HandlerManager(null);
	private TestConnectionServiceAsync testConnectionService;
	private HTML userNotice;
	private VerticalPanel panel;
	
	public void onModuleLoad() {
		
		userNotice = new HTML("TAMT is checking your internet connection.");
		userNotice.setStylePrimaryName("homepageWarning");
		RootLayoutPanel.get().add(userNotice);
		
		testConnectionService = GWT.create(TestConnectionService.class);
		testConnectionService.testConnection(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				if( result.equals("NOCONNECTION"))
				{
					preventApplicationLoad();
				} else {
					loadApplication();
				}			
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// this happens if the server call throws an exception
				// but our internet connection test swallows the exception
				// and should return a NOCONNECTION string to onSuccess
				preventApplicationLoad();
			}
		});
		
		//RootLayoutPanel.get().add(new Main(eventBus));
	}
	
	private void preventApplicationLoad()
	{
		userNotice.setText("It appears your internet connection is not working. Please make sure your TAMT" +
				" VirtualBox is connected to the Internet, then reload the page.");	
	}
	
	private void loadApplication()
	{
		/*
		 * We try to defer loading the Google Maps API, but fetching
		 * the JS from the internet when disconnected still takes
		 * a long time to timeout (it is an unconfigurable browser setting)
		 */
		AjaxLoader.loadApi("maps", "2", new Runnable() {
		    public void run() {
		    	RootLayoutPanel.get().remove(userNotice);
				RootLayoutPanel.get().add(new Main(eventBus));
		    }
		}, null);	
	}	
	
}
