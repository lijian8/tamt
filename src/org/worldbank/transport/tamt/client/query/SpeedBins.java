package org.worldbank.transport.tamt.client.query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class SpeedBins extends Composite {

	private static SpeedBinsUiBinder uiBinder = GWT
			.create(SpeedBinsUiBinder.class);

	interface SpeedBinsUiBinder extends UiBinder<Widget, SpeedBins> {
	}
	
	private HandlerManager eventBus;

	public SpeedBins(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}

}
