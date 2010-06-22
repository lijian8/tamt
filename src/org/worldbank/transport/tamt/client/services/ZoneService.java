package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("zoneService")
public interface ZoneService extends RemoteService {

	ArrayList<ZoneDetails> getZoneDetails(StudyRegion region) throws Exception;
	ZoneDetails saveZoneDetails(ZoneDetails zoneDetails) throws Exception;
	void deleteZoneDetails(ArrayList<String> zoneDetailIds) throws Exception;
}
