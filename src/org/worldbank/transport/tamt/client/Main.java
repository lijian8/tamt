package org.worldbank.transport.tamt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Main extends Composite {

	private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);
	private HandlerManager eventBus;
	
	interface MainUiBinder extends UiBinder<Widget, Main> {
	}

	public interface Resources extends ClientBundle {
		@Source("wblogo.png")
		ImageResource logo();
	}
	public Main(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiFactory ApplicationContainer initAppContainer() {
		return new ApplicationContainer(this.eventBus);
	}
	
	@UiFactory ApplicationNavigation initAppNav() {
		return new ApplicationNavigation(this.eventBus);
	}
}
