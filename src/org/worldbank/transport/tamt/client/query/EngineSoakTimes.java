package org.worldbank.transport.tamt.client.query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EngineSoakTimes extends Composite {

	private static EngineSoakTimesUiBinder uiBinder = GWT
			.create(EngineSoakTimesUiBinder.class);

	interface EngineSoakTimesUiBinder extends UiBinder<Widget, EngineSoakTimes> {
	}
	
	private HandlerManager eventBus;

	public EngineSoakTimes(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}

}
