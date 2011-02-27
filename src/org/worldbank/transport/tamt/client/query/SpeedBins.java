package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpeedBins extends Composite {

	private static SpeedBinsUiBinder uiBinder = GWT
			.create(SpeedBinsUiBinder.class);

	interface SpeedBinsUiBinder extends UiBinder<Widget, SpeedBins> {
	}
	
	private HandlerManager eventBus;

	@UiField TabLayoutPanel tabLayoutPanel;
	@UiField HorizontalPanel hpanel;
	@UiField SpeedBinDistribution speedBinDistribution;
	@UiField SpeedBinDistributionTrafficFlow combineSpeedBinDistributionTrafficFlow;
	@UiField SpeedBinDistributionAggregateByDayType reduceDayType;
	@UiField SpeedBinDistributionAggregateByTag reduceTag;
	
	public SpeedBins(HandlerManager eventBus) {
		this.eventBus = eventBus;
		initWidget(uiBinder.createAndBindUi(this));
		bind();
	}
	
	@UiFactory SpeedBinDistribution initSpeedBinDistribution() {
		return new SpeedBinDistribution(this.eventBus);
	}	

	@UiFactory SpeedBinDistributionTrafficFlow initSpeedBinDistributionTrafficFlow() {
		return new SpeedBinDistributionTrafficFlow(this.eventBus);
	}
	
	@UiFactory SpeedBinDistributionAggregateByDayType initSpeedBinDistributionAggregateByDayType() {
		return new SpeedBinDistributionAggregateByDayType(this.eventBus);
	}	

	@UiFactory SpeedBinDistributionAggregateByTag initSpeedBinDistributionAggregateByTag() {
		return new SpeedBinDistributionAggregateByTag(this.eventBus);
	}
	
	private void bind() {

		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: SpeedBins tabLayoutPanel height within: " + event.height);
				
				int h = event.height - 120; // account for other query module UI
				int w = event.width;
				
				if( h > -1 && w > -1)
				{
					String height = Integer.toString(h) + "px";
					String width = Integer.toString(w) + "px";
					GWT.log("SIZE: SpeedBins tabLayoutPanel height: " + height);
					GWT.log("SIZE: SpeedBins tabLayoutPanel width: " + width);
					tabLayoutPanel.setHeight(height);
					
					String hwidth = Integer.toString(w-34) + "px";
					hpanel.setWidth(hwidth);
				}
			}
		});	
		
	}
}
