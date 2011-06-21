package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.shared.RoadLengthReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

public class TagAPI {

	private TagBO tagBO;
	
	public TagAPI()
	{
		tagBO = TagBO.get();
	}
	
	public ArrayList<TagDetails> getTagDetails(StudyRegion region) throws Exception
	{
		return tagBO.getTagDetails(region);
	}
	
	public TagDetails saveTagDetails(TagDetails tagDetails) throws Exception
	{
		return tagBO.saveTagDetails(tagDetails);
	}

	public void deleteTagDetails(ArrayList<String> tagDetailIds) throws Exception {
		tagBO.deleteTagDetails(tagDetailIds);
	}

	public void createRoadLengthReport() throws Exception {
		tagBO.createRoadLengthReport();
	}

	public RoadLengthReport getRoadLengthReport() {
		return tagBO.getRoadLengthReport();
	}

	public String downloadRoadLengthReport(String regionid) {
		return tagBO.downloadRoadLengthReport(regionid);
	}
}
