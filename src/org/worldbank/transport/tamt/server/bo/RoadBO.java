package org.worldbank.transport.tamt.server.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.RoadDAO;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class RoadBO {

	private RoadDAO roadDAO;
	private RegionDAO regionDAO;
	private static Logger logger = Logger.getLogger(RoadBO.class);
	
	private static RoadBO singleton = null;
	public static RoadBO get()
	{
		if(singleton == null)
		{
			singleton = new RoadBO();
		}
		return singleton;		
	}
	
	public RoadBO()
	{
		roadDAO = RoadDAO.get();
		regionDAO = RegionDAO.get();
	}
	
	public ArrayList<RoadDetails> getRoadDetails(StudyRegion region) throws Exception
	{
		ArrayList<RoadDetails> roadDetails = new ArrayList<RoadDetails>();
		if ( region == null )
		{
			ArrayList<StudyRegion> regions = regionDAO.getStudyRegions();
			for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
				StudyRegion studyRegion = (StudyRegion) iterator.next();
				if(studyRegion.isCurrentRegion())
				{
					region = studyRegion;
					break;
				}
			}
			if( region != null )
			{
				roadDetails = roadDAO.getRoadDetails(region);
			}
		}
		return roadDetails;
	}

	public RoadDetails saveRoadDetails(RoadDetails roadDetails) throws Exception {
		//TODO: validate tagDetails
		if( roadDetails.getName().equalsIgnoreCase(""))
		{
			throw new Exception("Road must have a name");
		}
		
		// we want to store the Vertex array list as a JTS linestring
		ArrayList<Vertex> vertices = roadDetails.getVertices();
		if( vertices == null )
		{
			throw new Exception("Road does not have an associated line");
		}
		
		int vertexCount = vertices.size();
		//TODO: handle null vertices
		Coordinate[] coords = new Coordinate[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			Vertex v = vertices.get(i);
			Coordinate c = new Coordinate(v.getLng(), v.getLat());
			coords[i] = c;
		}
		// now create a line string from the coordinates array
		Geometry geometry = new GeometryFactory().createLineString(coords);
		
		// Note: the centroid for the RoadDetail vertices is never
		// stored in the database. It was just as easy to calculate
		// it on the fly in RoadDAO.getRoadDetails()
		
		try {
			if( roadDetails.getId().indexOf("TEMP") != -1 )
			{
				// create an id, and save it
				roadDetails.setId( UUID.randomUUID().toString() );
				return roadDAO.saveRoadDetails(roadDetails, geometry);
			} else {
				// use the existing id to update it
				return roadDAO.updateRoadDetails(roadDetails, geometry);
			}			
		} catch (SQLException e)
		{
			logger.error("SQL Exception: " + e.getMessage());
			if( e.getMessage().indexOf("duplicate key value violates unique constraint") != -1 )
			{
				throw new Exception("A road with that name already exists");
			} else if (e.getMessage().indexOf("roaddetails_tag_id_fkey") != -1) {
				throw new Exception("The tag for this road is not valid");
			} else {
				throw new Exception(e.getMessage());
			}
		} catch (Exception e)
		{
			logger.error("Unknown exception: " + e.getMessage());
			throw new Exception("An unknown error occured while trying to save a road");
		}
		
	}

	public void deleteRoadDetails(ArrayList<String> roadDetailIds) throws Exception {
		//TODO: validate roadDetailIds
		try {
			roadDAO.deleteRoadDetails(roadDetailIds);
		} catch (SQLException e) {
			logger.error("SQL Exception: " + e.getMessage());
			throw new Exception("Could not delete road details");
		}
	}
}
