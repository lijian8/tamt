package org.worldbank.transport.tamt.client.query;

import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.GetDayTypePerYearOptionEvent;
import org.worldbank.transport.tamt.client.event.GetDayTypePerYearOptionEventHandler;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.query.TagCountReport.Style;
import org.worldbank.transport.tamt.client.services.RegionService;
import org.worldbank.transport.tamt.client.services.RegionServiceAsync;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.StudyRegion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DayTypesPerYear extends Composite {

	private static DayTypesPerYearUiBinder uiBinder = GWT
			.create(DayTypesPerYearUiBinder.class);

	interface DayTypesPerYearUiBinder extends UiBinder<Widget, DayTypesPerYear> {
	}

	interface Style extends CssResource {
		String title();
		String subtitle();
		String clickable();
		String record();
		String hour();
		String inactive();
	}

	@UiField Style style;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	@UiField HTML setActiveOption1;
	@UiField HTML activeOption1;
	@UiField HTML setActiveOption2;
	@UiField HTML activeOption2;
	@UiField TextBox option1weekday;
	@UiField TextBox option2weekday;
	@UiField TextBox option2saturday;
	@UiField TextBox option2sundayHoliday;
	
	private int activeOption = 1;
	
	private HandlerManager eventBus;
	private RegionServiceAsync regionService;
	protected StudyRegion currentStudyRegion;
	protected DayTypePerYearOption currentOption;

	public DayTypesPerYear(HandlerManager eventBus) {
		
		this.eventBus = eventBus;
		
		regionService = GWT.create(RegionService.class);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		bind();
		
		// add the click handlers to the setActive links
		setActiveOption1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setOption1Active();
			}
		});
		
		setActiveOption2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setOption2Active();
			}
		});		
		
		// set option 1 as active and option 2 as inactive (by default)
		// TODO: change this to what the database has as default
		// if the db doesn't have one, then set option1 as active
		setOption1Active();
		
		// options should be attributes of the study region.
		// table definition: (region is unique id, default is option 1 with zero weekdays)
		// id, region id, option (1|2), weekday, saturday, sundayholiday
		
		
	}
	
	@UiHandler("reset")
	void onClickReset(ClickEvent e)
	{
		loadOption(currentOption);
	}
	
	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		
		DayTypePerYearOption dayTypePerYearOption = new DayTypePerYearOption();
		dayTypePerYearOption.setRegionId(currentStudyRegion.getId());
		
		if( currentOption != null)
		{
			dayTypePerYearOption.setId(currentOption.getId());
		}
		
		// if activeOption = 1, save weekday1 value
		if( activeOption == 1 )
		{
			dayTypePerYearOption.setOption1weekday(option1weekday.getValue());
		} else 
		// if activeOption = 2, save weekday2, saturday2, sundayHoliday2 values
		{
			dayTypePerYearOption.setOption2weekday(option2weekday.getValue());
			dayTypePerYearOption.setOption2saturday(option2saturday.getValue());
			dayTypePerYearOption.setOption2sundayHoliday(option2sundayHoliday.getValue());
		}
		dayTypePerYearOption.setActiveOption(Integer.toString(activeOption));
		
		GWT.log("save day type option=" + dayTypePerYearOption);
		regionService.saveDayTypePerYearOption(dayTypePerYearOption, new AsyncCallback<DayTypePerYearOption>() {
			
			@Override
			public void onSuccess(DayTypePerYearOption result) {
				Window.alert("Option saved");
				currentOption = result;
				loadOption(currentOption);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not save option:" + caught.getMessage());
			}
		});
		
	}	

	protected void loadOption(DayTypePerYearOption option) {
		
		// take values from option and put in UI
		resetForm();
		GWT.log("LOADOPTION=" + option);
		if(option.getActiveOption().equalsIgnoreCase("1"))
		{
			setOption1Active();
			option1weekday.setValue(option.getOption1weekday());
			
		} else {
			setOption2Active();
			option2weekday.setValue(option.getOption2weekday());
			option2saturday.setValue(option.getOption2saturday());
			option2sundayHoliday.setValue(option.getOption2sundayHoliday());
		}
	}

	private void bind() {

		eventBus.addHandler(GetDayTypePerYearOptionEvent.TYPE, new GetDayTypePerYearOptionEventHandler() {
			
			@Override
			public void onGetDayTypePerYearOption(GetDayTypePerYearOptionEvent event) {
				fetchDayTypePerYearOption();
			}
		});
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
				resetForm();
				//setOption1Active();
				fetchDayTypePerYearOption();
			}
		});
		
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("SIZE: DayTypesPerYear width: " + event.width);
				int h = event.height - 195; // account for other query module UI
				int w = event.width - 476;
				GWT.log("SIZE: DayTypesPerYear adjusted width: " + w);
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					GWT.log("SIZE: TagCountReport adjusted height: " + height);
					
					leftPane.setHeight("100px");
					rightPane.setHeight("160px");
					
					// hack because panes are not left-aligning
					String width = Integer.toString(w) + "px";
					rightPane.setWidth(width);
				}
				
			}
		});			
	}
	
	protected void fetchDayTypePerYearOption() {
		
		if( currentStudyRegion != null)
		{
			regionService.getDayTypePerYearOption(currentStudyRegion.getId(), new AsyncCallback<DayTypePerYearOption>() {
	
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Could not get option:" + caught.getMessage());
				}
	
				@Override
				public void onSuccess(DayTypePerYearOption result) {
					// there is the chance on the creation of new study region
					// that the option is null. if so, enable option 1 with nothing
					if( result != null)
					{
						currentOption = result;
						loadOption(currentOption);
					} else {
						setOption1Active();
					}
				}
			});
		} else 
		{
			setOption1Active();
		}
	}

	private void setOption1Active()
	{
		activeOption = 1;
		
		// swap the link texts
		setActiveOption1.setVisible(false);
		setActiveOption2.setVisible(true);
		
		activeOption1.setVisible(true);
		activeOption2.setVisible(false);
		
		// enable option 1
		option1weekday.setEnabled(true);
		
		// disable option 2
		option2weekday.setEnabled(false);
		option2saturday.setEnabled(false);
		option2sundayHoliday.setEnabled(false);
		
	}
	
	private void setOption2Active()
	{
		
		activeOption = 2;
		
		// swap the link texts
		setActiveOption1.setVisible(true);
		setActiveOption2.setVisible(false);
		
		activeOption1.setVisible(false);
		activeOption2.setVisible(true);
		
		// enable option 2
		option2weekday.setEnabled(true);
		option2saturday.setEnabled(true);
		option2sundayHoliday.setEnabled(true);
		
		// disable option 1
		option1weekday.setEnabled(false);
		
	}	

	private void resetForm()
	{
		option1weekday.setValue("");
		option2weekday.setValue("");
		option2saturday.setValue("");
		option2sundayHoliday.setValue("");
	}
}
