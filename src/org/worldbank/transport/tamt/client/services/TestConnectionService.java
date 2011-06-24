package org.worldbank.transport.tamt.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("testConnectionService")
public interface TestConnectionService extends RemoteService {

	String testConnection() throws Exception;

}
