package org.worldbank.transport.tamt.client.services;

import org.worldbank.transport.tamt.shared.AssignStatus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("assignService")
public interface AssignService extends RemoteService {

	AssignStatus assignTagToPoints(AssignStatus status) throws Exception;
	AssignStatus checkStatus(AssignStatus status) throws Exception;
	AssignStatus getAssignStatusInProcess() throws Exception;
}
