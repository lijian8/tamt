package org.worldbank.transport.tamt.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TestConnectionServiceAsync {

	void testConnection(AsyncCallback<String> asyncCallback);
	
}
