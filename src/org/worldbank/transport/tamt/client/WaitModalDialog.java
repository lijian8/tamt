package org.worldbank.transport.tamt.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class WaitModalDialog extends Composite {

	DialogBox dialog;
	HTML html;
	
	public WaitModalDialog() {
	
		dialog = new DialogBox();
		
		dialog.setAutoHideEnabled(false); // user can't click outside of it
		//dialog.setGlassEnabled(true); // glass the UI behind it
		dialog.center();
		
		dialog.setText("Waiting...");
		
		//TODO: set the HTML widget for the message;
		html = new HTML("insert message here");
		dialog.add(html);
		
		initWidget(dialog);

		dialog.hide();
		
	}
	
	public void setHTML(String html)
	{
		this.html.setHTML(html);
	}
	
	public void showWait()
	{
		dialog.show();
	}
	
	public void hideWait()
	{
		dialog.hide();
	}
}
