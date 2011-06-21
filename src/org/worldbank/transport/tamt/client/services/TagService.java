package org.worldbank.transport.tamt.client.services;

import java.util.ArrayList;

import org.worldbank.transport.tamt.shared.RoadLengthReport;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("tagService")
public interface TagService extends RemoteService {

	ArrayList<TagDetails> getTagDetails(StudyRegion region) throws Exception;
	TagDetails saveTagDetails(TagDetails tagDetails) throws Exception;
	void deleteTagDetails(ArrayList<String> tagDetailIds) throws Exception;
	
	void createRoadLengthReport() throws Exception;
	RoadLengthReport getRoadLengthReport() throws Exception;
	
}
