package org.worldbank.transport.tamt.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("backupService")
public interface BackupService extends RemoteService {

	String backupDatabase() throws Exception;

}
