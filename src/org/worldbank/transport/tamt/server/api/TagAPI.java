package org.worldbank.transport.tamt.server.api;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

public class TagAPI {

	private TagBO tagBO;
	
	public TagAPI()
	{
		tagBO = new TagBO();
	}
	
	public ArrayList<TagDetails> getTagDetails(StudyRegion region)
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
}
