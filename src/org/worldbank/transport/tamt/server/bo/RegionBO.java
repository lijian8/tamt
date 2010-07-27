package org.worldbank.transport.tamt.server.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.ZoneDAO;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class RegionBO {

	private RegionDAO dao;
	static Logger logger = Logger.getLogger(RegionBO.class);
	
	public RegionBO()
	{
		dao = new RegionDAO();
	}

	public void deleteStudyRegions(ArrayList<String> studyRegionIds) throws Exception {
		try {
			dao.deleteStudyRegions(studyRegionIds);
		} catch (SQLException e) {
			logger.error("Could not delete study regions:" + e.getMessage());
			throw new Exception("Could not delete study regions");
		}
	}

	public void deleteStudyRegion(String studyRegionId) throws SQLException {
		// TODO: verify there are no null ids, and catch exceptions
		dao.deleteStudyRegionById(studyRegionId);
	}
	
	public ArrayList<StudyRegion> getStudyRegions() throws Exception {
		return dao.getStudyRegions();
	}

	public StudyRegion saveStudyRegion(StudyRegion studyRegion) throws Exception {
		
		logger.debug("Preparing to save study region:" + studyRegion);
		if( studyRegion.getName().equalsIgnoreCase(""))
		{
			throw new Exception("Study region must have a name");
		}
		
		// we want to store the Vertex array list as a JTS linestring
		ArrayList<Vertex> vertices = studyRegion.getVertices();
		if( vertices == null )
		{
			throw new Exception("Study region does not have an associated shape");
		}
		
		int vertexCount = vertices.size();
		//TODO: handle null vertices
		Coordinate[] coords = new Coordinate[vertexCount];
		for (int i = 0; i < vertexCount; i++) {
			Vertex v = vertices.get(i);
			Coordinate c = new Coordinate(v.getLng(), v.getLat());
			coords[i] = c;
		}
		
		// now create a line string from the coordinates array where null = no holes in the polygon
		LinearRing ring = new GeometryFactory().createLinearRing(coords);
		Geometry geometry = new GeometryFactory().createPolygon(ring, null);
		
		// create a point from the mapCenter vertex
		// TODO: mapCenter is null when creating a new study region.
		Vertex mapCenterVertex = studyRegion.getMapCenter();
		logger.debug("mapCenter=" + studyRegion.getMapCenter());
		Coordinate mapCenterCoord = new Coordinate(mapCenterVertex.getLng(), mapCenterVertex.getLat());
		Geometry mapCenter = new GeometryFactory().createPoint(mapCenterCoord);
		
		try {
			if( studyRegion.getId().indexOf("TEMP") != -1 )
			{
				// create an id, and save it
				studyRegion.setId( UUID.randomUUID().toString() );
				return dao.saveStudyRegion(studyRegion, geometry, mapCenter);
			} else {
				// use the existing id to update it
				return dao.updateStudyRegion(studyRegion, geometry, mapCenter);
			}			
		} catch (SQLException e)
		{
			if( e.getMessage().indexOf("duplicate key value violates unique constraint") != -1 )
			{
				throw new Exception("A study region with that name already exists");
			} else {
				throw new Exception(e.getMessage());
			}
		} catch (Exception e)
		{
			logger.error("An unknown error occured while trying to save a study region");
			throw new Exception("An unknown error occured while trying to save a study region");
		}
	}
	
}
