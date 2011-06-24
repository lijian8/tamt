package org.worldbank.transport.tamt.client.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.worldbank.transport.tamt.client.common.UUID;
import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.AddTrafficCountRecordEventHandler;
import org.worldbank.transport.tamt.client.event.CancelTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEvent;
import org.worldbank.transport.tamt.client.event.CurrentStudyRegionUpdatedEventHandler;
import org.worldbank.transport.tamt.client.event.EditTrafficCountRecordEvent;
import org.worldbank.transport.tamt.client.event.EditTrafficCountRecordEventHandler;
import org.worldbank.transport.tamt.client.event.FetchedTagsEvent;
import org.worldbank.transport.tamt.client.event.FetchedTagsEventHandler;
import org.worldbank.transport.tamt.client.event.GetTrafficCountRecordsEvent;
import org.worldbank.transport.tamt.client.event.OpenWaitModelDialogEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEvent;
import org.worldbank.transport.tamt.client.event.TAMTResizeEventHandler;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordService;
import org.worldbank.transport.tamt.client.services.TrafficCountRecordServiceAsync;
import org.worldbank.transport.tamt.client.util.Pattern;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class TrafficCountDetailView extends Composite {

	private static TrafficCountDetailViewUiBinder uiBinder = GWT
			.create(TrafficCountDetailViewUiBinder.class);

	interface TrafficCountDetailViewUiBinder extends
			UiBinder<Widget, TrafficCountDetailView> {
	}

	interface Style extends CssResource {
	    String textBoxSmall();
	    String textBoxLarge();
	    String clickable();
	}
	
	@UiField Style style;
	
	private final MultiWordSuggestOracle tagSuggestions = new MultiWordSuggestOracle();
	
	private HandlerManager eventBus;
	
	@UiField HorizontalPanel hpanel;
	@UiField VerticalPanel vpanel;
	@UiField VerticalPanel leftPane;
	@UiField VerticalPanel rightPane;
	@UiField FlexTable dateTimeTag;
	@UiField FlexTable vehicles;
	
	private DatePicker datePicker;
	private TextBox date;
	private ListBox dayType;
	private TextBox startTime;
	private TextBox endTime;
	private SuggestBox tag;
	private TextBox W2;
	private TextBox W3;
	private TextBox PC;
	private TextBox TX;
	private TextBox LDV;
	private TextBox LDC;
	private TextBox HDC;
	private TextBox MDB;
	private TextBox HDB;
	
	private Label dateLabel;
	private Label dayTypeLabel;
	private Label startTimeLabel;
	private Label endTimeLabel;
	private Label tagLabel;
	private Label W2Label;
	private Label W3Label;
	private Label PCLabel;
	private Label TXLabel;
	private Label LDVLabel;
	private Label LDCLabel;
	private Label HDCLabel;
	private Label MDBLabel;
	private Label HDBLabel;

	protected ArrayList<TagDetails> tags;

	protected PopupPanel popupPanel;

	private DateTimeFormat fmt;
	private DateTimeFormat fmtHHMM;

	private TrafficCountRecord currentTrafficCountRecord;
	private TrafficCountRecordServiceAsync trafficCountRecordService;

	protected Date lastChosenDate;

	protected StudyRegion currentStudyRegion;
	private DialogBox timeHelper;

	public TrafficCountDetailView(HandlerManager eventBus) {
		this.eventBus = eventBus;
		
		trafficCountRecordService = GWT.create(TrafficCountRecordService.class);

		initWidget(uiBinder.createAndBindUi(this));
		
		// set to match CSS for width of dateTimeTag
		hpanel.setCellWidth(leftPane, "325px");
		
		setLabelsAndTextBoxes();
		
		buildDateTimeTable();
		buildVehiclesTable();
		
		fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		fmtHHMM = DateTimeFormat.getFormat("HHmm");
		
		timeHelper = new DialogBox();
		timeHelper.setText("Time Format");
		VerticalPanel vp = new VerticalPanel();
		HTML timeHelperText = new HTML("Time format must be in 24 hour time (HHmm) with no colon. E.g. '1945' instead of '7:45pm'");
		Button vpOK = new Button("OK");
		vpOK.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				timeHelper.hide();
			}
		});
		vp.add(timeHelperText);
		vp.add(vpOK);
		timeHelper.add(vp);
		
		bind();
		
	}

	private void bind() {
		
		/*
		eventBus.addHandler(TAMTResizeEvent.TYPE, new TAMTResizeEventHandler() {
			
			@Override
			public void onTAMTResize(TAMTResizeEvent event) {
				GWT.log("TrafficCountDetailView event height=" + event.height);
				// 490 - 300 = 190
				int h = event.height - 200; // account for other query module UI
				if( h > -1)
				{
					String height = Integer.toString(h) + "px";
					scrollPanel.setHeight(height);
				}
			}
		});		
		*/
		
		eventBus.addHandler(EditTrafficCountRecordEvent.TYPE, 
				new EditTrafficCountRecordEventHandler() {
		
				@Override
				public void onEditTrafficCountRecord(EditTrafficCountRecordEvent event) {
					currentTrafficCountRecord = event.record;
					lastChosenDate = currentTrafficCountRecord.getDate();
					updateFormFields(currentTrafficCountRecord);
				}
		});
		
		eventBus.addHandler(CurrentStudyRegionUpdatedEvent.TYPE, new CurrentStudyRegionUpdatedEventHandler() {
			
			@Override
			public void onUpdate(CurrentStudyRegionUpdatedEvent event) {
				currentStudyRegion = event.studyRegion;
			}
		});
		
		eventBus.addHandler(AddTrafficCountRecordEvent.TYPE, 
				new AddTrafficCountRecordEventHandler() {
					
					@Override
					public void onAddTrafficCountRecord(AddTrafficCountRecordEvent event) {
						currentTrafficCountRecord = new TrafficCountRecord();
						String tempId = "TEMP-" + UUID.uuid(10);
						currentTrafficCountRecord.setId(tempId);
						GWT.log(currentTrafficCountRecord.toString());
						resetFormFields();
					}
				});
		
		eventBus.addHandler(FetchedTagsEvent.TYPE, new FetchedTagsEventHandler() {
			
			@Override
			public void onFetchedTags(FetchedTagsEvent event) {
				tags = event.getTags();
				for (Iterator iterator = tags.iterator(); iterator
						.hasNext();) {
					TagDetails td = (TagDetails) iterator.next();
					tagSuggestions.add(td.getName());
				}
			}
		});
	}

	protected void resetFormFields() {
		date.setValue("");
		dayType.setSelectedIndex(0);
		startTime.setValue("");
		endTime.setValue("");
		tag.setValue("");
		W2.setValue("");
		W3.setValue("");
		PC.setValue("");
		TX.setValue("");
		LDV.setValue("");
		LDC.setValue("");
		HDC.setValue("");
		MDB.setValue("");	
		HDB.setValue("");
	}

	@UiHandler("save")
	void onClickSave(ClickEvent e) {
		saveTrafficCountRecord();
	}	

	private void saveTrafficCountRecord() {
		
		/*
		 * Take the current traffic count record
		 * Update it with the form values
		 * Submit it
		 * 
		 * (Previously saved entities will have an ID)
		 * (New entities will have a temp ID)
		 */
		
		// last chosen date was set from the date picker
		currentTrafficCountRecord.setDate(lastChosenDate);
		
		// set the current study region id
		if( currentStudyRegion != null)
		{
			currentTrafficCountRecord.setRegion(currentStudyRegion.getId());
		}
		
		// get the selected day type
		String selectedDayType = dayType.getValue(dayType.getSelectedIndex());
		currentTrafficCountRecord.setDayType(selectedDayType);
		
		// start time
		/*
		 * The form should have HH:mm in 24 hr time.
		 * We need to make this a Date with the same day
		 * as the date field, but add the time factor
		 */
		Date startDate = mergeTimeWithLastChosenDate(startTime.getValue());
		if( startDate == null)
		{
			Window.alert("Start time must be formatted HHmm");
			return;
		}
		currentTrafficCountRecord.setStartTime(startDate);
		
		Date endDate = mergeTimeWithLastChosenDate(endTime.getValue());
		if( endDate == null)
		{
			Window.alert("End time must be formatted HHmm");
			return;
		}
		currentTrafficCountRecord.setEndTime(endDate);

		// swap out empty values for zeros
		swapEmptyValuesForZero();
		
		try 
		{
			currentTrafficCountRecord.setTag(tag.getValue());
			currentTrafficCountRecord.setW2(Integer.parseInt(W2.getValue()));
			currentTrafficCountRecord.setW3(Integer.parseInt(W3.getValue()));
			currentTrafficCountRecord.setPC(Integer.parseInt(PC.getValue()));
			currentTrafficCountRecord.setTX(Integer.parseInt(TX.getValue()));
			currentTrafficCountRecord.setLDV(Integer.parseInt(LDV.getValue()));
			currentTrafficCountRecord.setLDC(Integer.parseInt(LDC.getValue()));
			currentTrafficCountRecord.setHDC(Integer.parseInt(HDC.getValue()));
			currentTrafficCountRecord.setMDB(Integer.parseInt(MDB.getValue()));
			currentTrafficCountRecord.setHDB(Integer.parseInt(HDB.getValue()));
		} catch (NumberFormatException e)
		{
			Window.alert("Counts should only contain digits.");
			return;
		}
		
		// start time should be before end time
		if( startDate.after(endDate))
		{
			Window.alert("Start time should be before end time");
			return;
		}
		
		GWT.log(currentTrafficCountRecord.toString());
		
		// save the record
		trafficCountRecordService.saveTrafficCountRecord(currentTrafficCountRecord, new AsyncCallback<TrafficCountRecord>() {
			
			@Override
			public void onSuccess(TrafficCountRecord result) {
				currentTrafficCountRecord = result;
				eventBus.fireEvent(new GetTrafficCountRecordsEvent());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not save traffic count record. Please see server logs.");
			}
		});
		
	}

	@UiHandler("cancel")
	void onClickCancel(ClickEvent e) {
		eventBus.fireEvent(new CancelTrafficCountRecordEvent());
	}
	
	/*
	@UiHandler("delete")
	void onClickDelete(ClickEvent e) {
		if( Window.confirm("Delete this traffic count record?") )
		{
			// not implemented
		}
	}
	*/

	private void swapEmptyValuesForZero()
	{

		if( W2.getValue().isEmpty()) { W2.setValue("0"); }
		if( W3.getValue().isEmpty()) { W3.setValue("0"); }
		if( PC.getValue().isEmpty()) { PC.setValue("0"); }
		if( TX.getValue().isEmpty()) { TX.setValue("0"); }
		if( LDV.getValue().isEmpty()) { LDV.setValue("0"); }
		if( LDC.getValue().isEmpty()) { LDC.setValue("0"); }
		if( HDC.getValue().isEmpty()) { HDC.setValue("0"); }
		if( MDB.getValue().isEmpty()) { MDB.setValue("0"); }
		if( HDB.getValue().isEmpty()) { HDB.setValue("0"); }
		
	}
	
	private void updateFormFields(TrafficCountRecord trafficCountRecord)
	{
		
		// the trafficCountRecord ID is in currentTrafficRecordCount, not in the form
		
		Date d = trafficCountRecord.getDate();
		date.setValue(fmt.format(d));
		
		// select the correct dayType
		String dayTypeValue = trafficCountRecord.getDayType();
		for (int i = 0; i < dayType.getItemCount(); i++) {
			String value = dayType.getValue(i);
			if(value.equalsIgnoreCase(dayTypeValue))
			{
				dayType.setSelectedIndex(i);
			}
		}
		
		startTime.setValue(fmtHHMM.format(trafficCountRecord.getStartTime()));
		endTime.setValue(fmtHHMM.format(trafficCountRecord.getEndTime()));
		
		tag.setValue(trafficCountRecord.getTag());
		W2.setValue(Integer.toString(trafficCountRecord.getW2()));
		W3.setValue(Integer.toString(trafficCountRecord.getW3()));
		PC.setValue(Integer.toString(trafficCountRecord.getPC()));
		TX.setValue(Integer.toString(trafficCountRecord.getTX()));
		LDV.setValue(Integer.toString(trafficCountRecord.getLDV()));
		LDC.setValue(Integer.toString(trafficCountRecord.getLDC()));
		HDC.setValue(Integer.toString(trafficCountRecord.getHDC()));
		MDB.setValue(Integer.toString(trafficCountRecord.getMDB()));
		HDB.setValue(Integer.toString(trafficCountRecord.getHDB()));
		
		GWT.log("form fields updated from: " + trafficCountRecord);
		
	}
	
	private String createHHMMFromDate(Date d)
	{
		String hhmm = "";
		hhmm += d.getHours();
		hhmm += ":";
		hhmm += d.getMinutes();
		return hhmm;
	}
	
	private Date mergeTimeWithLastChosenDate(String hhmm)
	{
		
		/*
		 * Must be in the format HHmm (that is, 4 consecutive digits)
		 */
		Pattern pattern = new Pattern("\\d\\d\\d\\d");
		Date date = null;
		if( pattern.matches(hhmm))
		{
			GWT.log("pattern matches?: " + pattern.matches(hhmm));
			
			GWT.log("hhmm:" + hhmm);
			String hh = hhmm.substring(0, 2);
			GWT.log("hh:" + hh);
			String mm = hhmm.substring(2, 4);
			GWT.log("mm:" + mm);
			date = new Date();
			
			// copy YYYY-MM-DD from last chosen date
			date.setYear(lastChosenDate.getYear());
			date.setMonth(lastChosenDate.getMonth());
			date.setDate(lastChosenDate.getDate());
			
			date.setHours(Integer.parseInt(hh));
			date.setMinutes(Integer.parseInt(mm));
			date.setSeconds(0);
			GWT.log("set date:" + lastChosenDate.toString());
			GWT.log("set time:" + date.toString());	
			
		}
		
		return date;
		
	}
	
	private void setLabelsAndTextBoxes()
	{
		dateLabel = new Label("Date");
		dayTypeLabel = new Label("Day type");
		startTimeLabel = new Label("Start time");
		startTimeLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				timeHelper.center();
				timeHelper.show();
			}
		});
		startTimeLabel.setStylePrimaryName(style.clickable());
		
		endTimeLabel = new Label("End time");
		endTimeLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				timeHelper.center();
				timeHelper.show();
			}
		});
		endTimeLabel.setStylePrimaryName(style.clickable());
		
		tagLabel = new Label("Tag");
		W2Label = new Label("Two-wheeler (W2)");
		W3Label = new Label("Three-wheeler (W3)");
		PCLabel = new Label("Passenger car (PC)");
		TXLabel = new Label("Taxi (TX) ");
		LDVLabel = new Label("Pickups, vans, SUVs (LDV)");
		LDCLabel = new Label("Light-duty commercial (LDC)");
		HDCLabel = new Label("Heavy-duty commercial (HDC)");
		MDBLabel = new Label("Medium-duty minibuses (MDB)");
		HDBLabel = new Label("Heavy-duty buses (HDB)");	
		
		date = new TextBox();
		
		// force them to use the calendar widget
		// otherwise the startTime/endTime functions don't work
		date.setReadOnly(true);
		date.setStyleName(style.textBoxSmall());
		
		dayType = new ListBox();
		dayType.addItem("Weekday", TrafficCountRecord.DAYTYPE_WEEKDAY);
		dayType.addItem("Saturday", TrafficCountRecord.DAYTYPE_SATURDAY);
		dayType.addItem("Sunday/Holiday", TrafficCountRecord.DAYTYPE_SUNDAY_HOLIDAY);
		
		startTime = new TextBox();
		startTime.setStyleName(style.textBoxSmall());
		
		endTime = new TextBox();
		endTime.setStyleName(style.textBoxSmall());
		
		tag = new SuggestBox(tagSuggestions);
		
		W2 = new TextBox();
		W2.setStyleName(style.textBoxSmall());
		
		W3 = new TextBox();
		W3.setStyleName(style.textBoxSmall());
		
		PC = new TextBox();
		PC.setStyleName(style.textBoxSmall());
		
		TX = new TextBox();
		TX.setStyleName(style.textBoxSmall());
		
		LDV = new TextBox();
		LDV.setStyleName(style.textBoxSmall());
		
		LDC = new TextBox();
		LDC.setStyleName(style.textBoxSmall());
		
		HDC = new TextBox();
		HDC.setStyleName(style.textBoxSmall());
		
		MDB = new TextBox();
		MDB.setStyleName(style.textBoxSmall());
		
		HDB = new TextBox();	
		HDB.setStyleName(style.textBoxSmall());
	}
	
	private void buildVehiclesTable() {
		
		vehicles.setWidget(0, 0, W2Label);
		vehicles.setWidget(0, 1, W2);
		
		vehicles.setWidget(1, 0, W3Label);
		vehicles.setWidget(1, 1, W3);

		vehicles.setWidget(2, 0, PCLabel);
		vehicles.setWidget(2, 1, PC);

		vehicles.setWidget(3, 0, TXLabel);
		vehicles.setWidget(3, 1, TX);
		
		vehicles.setWidget(4, 0, LDVLabel);
		vehicles.setWidget(4, 1, LDV);
		
		vehicles.setWidget(5, 0, LDCLabel);
		vehicles.setWidget(5, 1, LDC);		
		
		vehicles.setWidget(6, 0, HDCLabel);
		vehicles.setWidget(6, 1, HDC);	
		
		vehicles.setWidget(7, 0, MDBLabel);
		vehicles.setWidget(7, 1, MDB);
		
		vehicles.setWidget(8, 0, HDBLabel);
		vehicles.setWidget(8, 1, HDB);	
	
	}

	private void buildDateTimeTable() {
		
		dateTimeTag.setWidget(0, 0, dateLabel);
		dateTimeTag.setWidget(0, 1, date);

		dateTimeTag.setWidget(1, 0, dayTypeLabel);
		dateTimeTag.setWidget(1, 1, dayType);
		
		dateTimeTag.setWidget(2, 0, startTimeLabel);
		dateTimeTag.setWidget(2, 1, startTime);
		
		dateTimeTag.setWidget(3, 0, endTimeLabel);
		dateTimeTag.setWidget(3, 1, endTime);
		
		dateTimeTag.setWidget(4, 0, tagLabel);
		dateTimeTag.setWidget(4, 1, tag);
		
		datePicker = new DatePicker();
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				lastChosenDate = event.getValue();
				String formattedDate = fmt.format(lastChosenDate);
				date.setValue(formattedDate);
				popupPanel.hide();
			}
		});
		date.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				popupPanel = new PopupPanel(true);
				popupPanel.add(datePicker);
				popupPanel.setPopupPosition(date.getAbsoluteLeft(), date.getAbsoluteTop() + date.getOffsetHeight());
				popupPanel.show();
			}
		});
		
	}	
}
