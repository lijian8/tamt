package org.worldbank.transport.tamt.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class RegionDAO extends DAO {

	static Logger logger = Logger.getLogger(RegionDAO.class);
	
	public RegionDAO()
	{
	
	}

	public void deleteStudyRegionById(String id) throws SQLException {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "DELETE FROM \"studyregion\" WHERE id = '"+id+"'";
			logger.debug("sql=" + sql);
			s.execute(sql); 
			connection.close(); // returns connection to the pool
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public ArrayList<StudyRegion> getStudyRegions() throws Exception {
		
		ArrayList<StudyRegion> studyRegionList = new ArrayList<StudyRegion>();
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "select id, name, description, AsText(geometry) geom, mapzoomlevel, AsText(mapcenter) center, iscurrentregion from \"studyregion\" ORDER BY name";
			ResultSet r = s.executeQuery(sql); 
			while( r.next() ) { 
			      /* 
			      * Retrieve the geometry as an object then cast it to the geometry type. 
			      * Print things out. 
			      */ 
				  String id = r.getString(1);
			      String name = r.getString(2);
			      String description = r.getString(3);
			      String lineString = r.getString(4);
			      int mapZoomLevel = r.getInt(5);
			      String mapCenterWKT = r.getString(6);
			      boolean currentRegion = r.getBoolean(7);
			      
			      // convert a linestring to a JTS geometry
			      WKTReader reader = new WKTReader();
			      Geometry geometry = reader.read(lineString);
			      Point centroidJTS = geometry.getCentroid();
			      
			      StudyRegion studyRegion = new StudyRegion();
			      studyRegion.setId(id);
			      studyRegion.setName(name);
			      studyRegion.setDescription(description);
			      studyRegion.setCurrentRegion(currentRegion);
			      studyRegion.setMapZoomLevel(mapZoomLevel);
			      
			      // now convert the geometry to an ArrayList<Vertex> and
			      // set in the roadDetails
			      ArrayList<Vertex> vertices = Utils.geometryToVertexArrayList(geometry);
			      studyRegion.setVertices(vertices);
			      
			      // convert the centroid point into a Vertex
			      Vertex centroid = new Vertex();
			      centroid.setLat(centroidJTS.getY());
			      centroid.setLng(centroidJTS.getX());
			      studyRegion.setCentroid(centroid);
			      
			      // and get the map meta data too
			      Geometry mapCenterGeom = reader.read(mapCenterWKT);
			      Point mapCenterJTS = mapCenterGeom.getCentroid();
			      Vertex mapCenterVertex = new Vertex();
			      mapCenterVertex.setLat(mapCenterJTS.getY());
			      mapCenterVertex.setLng(mapCenterJTS.getX());
			      studyRegion.setMapCenter(mapCenterVertex);
			      
			      
			      studyRegionList.add(studyRegion);
			} 
			connection.close(); // returns connection to the pool
		} 
	    catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: " + e.getMessage());
		} 
	    catch (ParseException e) {
	    	logger.error(e.getMessage());
			throw new Exception("Cannot convert geometry string to geometry object: " + e.getMessage());
		}
		
		return studyRegionList;
	}

	public StudyRegion updateStudyRegion(StudyRegion studyRegion, Geometry geometry, Geometry mapCenter) throws SQLException {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			// TODO: extend the model to include regionName string or region StudyRegion as property of ZoneDetails
			// for now we just use 'default'
			String sql = "UPDATE \"studyregion\" SET " +
					" name = '"+studyRegion.getName()+"'," +
					" description = '"+studyRegion.getDescription()+"'," +
					" geometry = GeometryFromText('"+geometry.toText()+"', 4326), " + 
					" mapzoomlevel = "+studyRegion.getMapZoomLevel()+"," +
					" mapcenter = GeometryFromText('"+mapCenter.toText()+"', 4326)," +
					" iscurrentregion = "+studyRegion.isCurrentRegion()+" " +
					"WHERE id = '"+studyRegion.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			connection.close(); // returns connection to the pool
			
			// if it worked, and if this is set to current region, then update
			if( studyRegion.isCurrentRegion() )
			{
				updateCurrentRegion(studyRegion);	
			}
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} 
	    
	    return studyRegion;
	}	
	
	public void updateCurrentRegion(StudyRegion studyRegion) throws SQLException
	{
		// set any other current region to false because there can only be one at a time
		// UPDATE studyregion SET iscurrentregion = false WHERE id != 'studyregion.id'
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE \"studyregion\" SET iscurrentregion = false WHERE id != '"+studyRegion.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			connection.close(); // returns connection to the pool				
		} catch (SQLException e)
		{
			logger.error(e.getMessage());
			throw e;
		}
		
	}
	
	public StudyRegion saveStudyRegion(StudyRegion studyRegion, Geometry geometry, Geometry mapCenter) throws SQLException {

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO \"studyregion\" (pid, id, name, description, geometry, mapzoomlevel, mapcenter, iscurrentregion) " +
					"VALUES (" + 
					"(SELECT nextval('studyregion_pid_seq'))," +
					"'"+studyRegion.getId()+"', " +
					"'"+studyRegion.getName()+"'," +
					"'"+studyRegion.getDescription()+"'," +
					"GeometryFromText('"+geometry.toText()+"', 4326)," +
					"'"+studyRegion.getMapZoomLevel()+"'," +
					"GeometryFromText('"+mapCenter.toText()+"', 4326)," +
					" "+studyRegion.isCurrentRegion()+" " +
					")";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			connection.close(); // returns connection to the pool
			
			// if it worked, and if this is set to current region, then update
			if( studyRegion.isCurrentRegion() )
			{
				updateCurrentRegion(studyRegion);	
			}
			
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    
	    return studyRegion;

	}

	public void deleteStudyRegions(ArrayList<String> studyRegionIds) throws SQLException {
		for (Iterator iterator = studyRegionIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			deleteStudyRegionById(id);
		}
	}
	
}
