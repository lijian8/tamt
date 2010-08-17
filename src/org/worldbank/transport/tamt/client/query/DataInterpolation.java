package org.worldbank.transport.tamt.client.query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DataInterpolation extends Composite {

	private static DataInterpolationUiBinder uiBinder = GWT
			.create(DataInterpolationUiBinder.class);

	interface DataInterpolationUiBinder extends
			UiBinder<Widget, DataInterpolation> {
	}
	
	private HandlerManager eventBus;

	public DataInterpolation(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
	}

}
