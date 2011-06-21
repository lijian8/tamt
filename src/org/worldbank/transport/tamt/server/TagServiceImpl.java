package org.worldbank.transport.tamt.server;

import java.util.ArrayList;
import java.util.UUID;

import org.worldbank.transport.tamt.client.services.TagService;
import org.worldbank.transport.tamt.server.api.TagAPI;
import org.worldbank.transport.tamt.shared.RoadLengthReport;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TagServiceImpl extends RemoteServiceServlet implements TagService {

	private TagAPI tagAPI;
	
	public TagServiceImpl()
	{
		tagAPI = new TagAPI();
	}

	@Override
	public ArrayList<TagDetails> getTagDetails(StudyRegion region) throws Exception {
		
		return tagAPI.getTagDetails(region);
	}
	
	@Override
	public TagDetails saveTagDetails(TagDetails tagDetails) throws Exception {
		return tagAPI.saveTagDetails(tagDetails);
	}

	@Override
	public void deleteTagDetails(ArrayList<String> tagDetailIds) throws Exception {
		tagAPI.deleteTagDetails(tagDetailIds);
	}

	@Override
	public void createRoadLengthReport() throws Exception {
		tagAPI.createRoadLengthReport();
	}

	@Override
	public RoadLengthReport getRoadLengthReport() throws Exception {
		return tagAPI.getRoadLengthReport();
	}
}
