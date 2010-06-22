package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.ZoneDetails;

public class ZoneAPI {

	private ZoneBO zoneBO;
	
	public ZoneAPI()
	{
		zoneBO = new ZoneBO();
	}
	
	public ArrayList<ZoneDetails> getZoneDetails(StudyRegion region) throws Exception
	{
		return zoneBO.getZoneDetails(region);
	}
	
	public ZoneDetails saveZoneDetails(ZoneDetails roadDetails) throws Exception
	{
		return zoneBO.saveZoneDetails(roadDetails);
	}

	public void deleteZoneDetails(ArrayList<String> roadDetailIds) throws Exception {
		zoneBO.deleteZoneDetails(roadDetailIds);
	}
}
