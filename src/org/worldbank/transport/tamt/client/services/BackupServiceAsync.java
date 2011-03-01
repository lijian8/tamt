package org.worldbank.transport.tamt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BackupServiceAsync {

	void backupDatabase(AsyncCallback<String> asyncCallback);
	
}
