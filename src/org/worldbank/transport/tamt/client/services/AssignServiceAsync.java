package org.worldbank.transport.tamt.client.services;

import org.worldbank.transport.tamt.shared.AssignStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AssignServiceAsync {

	void assignTagToPoints(AssignStatus status, AsyncCallback<AssignStatus> asyncCallback);
	void checkStatus(AssignStatus status, AsyncCallback<AssignStatus> asyncCallback);
	void getAssignStatusInProcess(AsyncCallback<AssignStatus> asyncCallback);
}
