package org.worldbank.transport.tamt.client;

import org.worldbank.transport.tamt.client.event.CloseWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.CloseWaitModelDialogEventHandler;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class Main extends Composite {

	private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);
	private HandlerManager eventBus;
	
	interface MainUiBinder extends UiBinder<Widget, Main> {
	}
	
	@UiField DialogBox dialogBox;

	public interface Resources extends ClientBundle {
		@Source("wblogo.png")
		ImageResource logo();
	}
	public Main(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		
		dialogBox.setVisible(false);
		
		bind();
		
	}

	private void bind() {
		
		// listen for an OpenWaitModalDialogEvent
		eventBus.addHandler(OpenWaitModelDialogEvent.TYPE, new OpenWaitModelDialogEventHandler() {
			
			@Override
			public void onOpen(OpenWaitModelDialogEvent event) {
				GWT.log("get the message and put it in the dialog...");
				
				dialogBox.setText(event.title);
				dialogBox.setWidget(new HTML(event.message));
				dialogBox.setAutoHideEnabled(false);
				dialogBox.setGlassEnabled(true);
				dialogBox.center();
				dialogBox.show();
			}
		});
		
		// listen for a CloseWaitModalDialogEvent
		eventBus.addHandler(CloseWaitModelDialogEvent.TYPE, new CloseWaitModelDialogEventHandler() {
			
			@Override
			public void onClose(CloseWaitModelDialogEvent event) {
				GWT.log("close the dialog box");
				dialogBox.hide();
			}
		});
		
	}

	@UiFactory ApplicationContainer initAppContainer() {
		return new ApplicationContainer(this.eventBus);
	}
	
	@UiFactory ApplicationNavigation initAppNav() {
		return new ApplicationNavigation(this.eventBus);
	}

}
