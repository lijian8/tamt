package org.worldbank.transport.tamt.server;

import java.util.ArrayList;

import org.worldbank.transport.tamt.client.services.ZoneService;
import org.worldbank.transport.tamt.server.api.ZoneAPI;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ZoneServiceImpl extends RemoteServiceServlet implements ZoneService {

	private ZoneAPI zoneAPI;
	
	public ZoneServiceImpl()
	{
		zoneAPI = new ZoneAPI();
	}

	public ArrayList<ZoneDetails> getZoneDetails(StudyRegion region) throws Exception {
		return zoneAPI.getZoneDetails(region);
	}

	public ZoneDetails saveZoneDetails(ZoneDetails zoneDetails) throws Exception {
		return zoneAPI.saveZoneDetails(zoneDetails);
	}

	public void deleteZoneDetails(ArrayList<String> zoneDetailIds) throws Exception {
		zoneAPI.deleteZoneDetails(zoneDetailIds);
	}
	
	
}
