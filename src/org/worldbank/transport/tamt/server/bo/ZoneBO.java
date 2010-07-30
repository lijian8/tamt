package org.worldbank.transport.tamt.server.bo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.RoadDAO;
import org.worldbank.transport.tamt.server.dao.ZoneDAO;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

public class ZoneBO {

	private ZoneDAO zoneDAO;
	private RegionDAO regionDAO;
	private static Logger logger = Logger.getLogger(ZoneBO.class);
		
	private static ZoneBO singleton = null;
	public static ZoneBO get()
	{
		if(singleton == null)
		{
			singleton = new ZoneBO();
		}
		return singleton;		
	}
	
	public ZoneBO()
	{
		zoneDAO = ZoneDAO.get();
		regionDAO = RegionDAO.get();
	}
	
	public ArrayList<ZoneDetails> getZoneDetails(StudyRegion region) throws Exception
	{
		ArrayList<ZoneDetails> zoneDetails = new ArrayList<ZoneDetails>();
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
				zoneDetails = zoneDAO.getZoneDetails(region);
			}
		}
		return zoneDetails;		
	}

	public ZoneDetails saveZoneDetails(ZoneDetails zoneDetails) throws Exception {
		//TODO: validate tagDetails
		if( zoneDetails.getName().equalsIgnoreCase(""))
		{
			throw new Exception("Zone must have a name");
		}
		
		// we want to store the Vertex array list as a JTS linestring
		ArrayList<Vertex> vertices = zoneDetails.getVertices();
		if( vertices == null )
		{
			throw new Exception("Zone does not have an associated line");
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
		
		// Note: the centroid for the ZoneDetail vertices is never
		// stored in the database. It was just as easy to calculate
		// it on the fly in ZoneDAO.getZoneDetails()
		
		try {
			if( zoneDetails.getId().indexOf("TEMP") != -1 )
			{
				// create an id, and save it
				zoneDetails.setId( UUID.randomUUID().toString() );
				return zoneDAO.saveZoneDetails(zoneDetails, geometry);
			} else {
				// use the existing id to update it
				return zoneDAO.updateZoneDetails(zoneDetails, geometry);
			}			
		} catch (SQLException e)
		{
			if( e.getMessage().indexOf("duplicate key value violates unique constraint") != -1 )
			{
				throw new Exception("A zone with that name already exists");
			} else {
				throw new Exception(e.getMessage());
			}
		} catch (Exception e)
		{
			throw new Exception("An unknown error occured while trying to save a zone");
		}
		
	}

	public void deleteZoneDetails(ArrayList<String> zoneDetailIds) throws Exception {
		//TODO: validate roadDetailIds
		try {
			zoneDAO.deleteZoneDetails(zoneDetailIds);
		} catch (SQLException e) {
			logger.error("Could not delete zone details:" + e.getMessage());
			throw new Exception("Could not delete zone details");
		}
	}
}
