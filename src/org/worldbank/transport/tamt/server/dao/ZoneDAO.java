package org.worldbank.transport.tamt.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.RoadDetails;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.Vertex;
import org.worldbank.transport.tamt.shared.ZoneDetails;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class ZoneDAO extends DAO {
	
	static Logger logger = Logger.getLogger(ZoneDAO.class);
	
	public ZoneDAO()
	{
		init();
	}
	
	public ArrayList<ZoneDetails> getZoneDetails(StudyRegion region) throws Exception
	{
		ArrayList<ZoneDetails> roadDetailsList = new ArrayList<ZoneDetails>();
		try {
			Statement s = connection.createStatement();
			String sql = "select id, name, description, region, zoneType, AsText(geometry) from \"zoneDetails\" where region = '"+region.getName()+"' ORDER BY name";
			ResultSet r = s.executeQuery(sql); 
			while( r.next() ) { 
			      /* 
			      * Retrieve the geometry as an object then cast it to the geometry type. 
			      * Print things out. 
			      */ 
				  String id = r.getString(1);
			      String name = r.getString(2);
			      String description = r.getString(3);
			      String regionName = r.getString(4);
			      String zoneType = r.getString(5);
			      String lineString = r.getString(6);
			      
			      // convert a linestring to a JTS geometry
			      WKTReader reader = new WKTReader();
			      Geometry geometry = reader.read(lineString);
			      Point centroidJTS = geometry.getCentroid();
			      
			      ZoneDetails zoneDetails = new ZoneDetails();
			      zoneDetails.setId(id);
			      zoneDetails.setName(name);
			      zoneDetails.setDescription(description);
			      zoneDetails.setZoneType(zoneType);
			      
			      // now convert the geometry to an ArrayList<Vertex> and
			      // set in the roadDetails
			      ArrayList<Vertex> vertices = Utils.geometryToVertexArrayList(geometry);
			      zoneDetails.setVertices(vertices);
			      
			      // convert the centroid point into a Vertex
			      Vertex centroid = new Vertex();
			      centroid.setLat(centroidJTS.getY());
			      centroid.setLng(centroidJTS.getX());
			      zoneDetails.setCentroid(centroid);
			      
			      //TODO: do I need to include the study region id, description here?
			      StudyRegion sr = new StudyRegion();
			      sr.setName(regionName);
			      
			      roadDetailsList.add(zoneDetails);
			} 
    
		} 
	    catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: " + e.getMessage());
		} 
	    catch (ParseException e) {
	    	logger.error(e.getMessage());
			throw new Exception("Cannot convert geometry string to geometry object: " + e.getMessage());
		}
		
		return roadDetailsList;
		
	}
	
	public ZoneDetails saveZoneDetails(ZoneDetails zoneDetails, Geometry geometry) throws SQLException {

		try {
			Statement s = connection.createStatement();
			String sql = "INSERT INTO \"zoneDetails\" (id, name, description, region, zoneType, geometry) " +
					"VALUES ('"+zoneDetails.getId()+"', " +
					"'"+zoneDetails.getName()+"'," +
					"'"+zoneDetails.getDescription()+"'," +
					"'default'," +
					"'"+zoneDetails.getZoneType()+"'," +
					"GeometryFromText('"+geometry.toText()+"', 4326)" +
					")";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    
	    return zoneDetails;
	    
	}
	
	public ZoneDetails updateZoneDetails(ZoneDetails zoneDetails, Geometry geometry) throws SQLException {
		try {
			Statement s = connection.createStatement();
			// TODO: extend the model to include regionName string or region StudyRegion as property of ZoneDetails
			// for now we just use 'default'
			String sql = "UPDATE \"zoneDetails\" SET " +
					" name = '"+zoneDetails.getName()+"'," +
					" description = '"+zoneDetails.getDescription()+"'," +
					" region = 'default', " +
					" zoneType = '"+zoneDetails.getZoneType()+"', " +
					" geometry = GeometryFromText('"+geometry.toText()+"', 4326) " + 
					"WHERE id = '"+zoneDetails.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} 
	    
	    return zoneDetails;
	}	
	
	public void deleteZoneDetails(ArrayList<String> roadDetailIds) throws SQLException {
		for (Iterator iterator = roadDetailIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			deleteZoneDetailById(id);
		}
	}
	
	public void deleteZoneDetailById(String id) throws SQLException
	{
		try {
			Statement s = connection.createStatement();
			String sql = "DELETE FROM \"zoneDetails\" WHERE id = '"+id+"'";
			logger.debug("sql=" + sql);
			s.execute(sql); 
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}		
	}
	
	private ArrayList<ZoneDetails> stubList()
	{
		ArrayList<ZoneDetails> zoneDetails = new ArrayList<ZoneDetails>();
		int sample = 10;
		for (int i = 0; i < sample; i++) {
			UUID id = UUID.randomUUID();
			ZoneDetails t = new ZoneDetails();
			t.setName("Name-" + id.toString());
			t.setDescription("Desc-"+id.toString());
			zoneDetails.add(t);
		}
		return zoneDetails;
	}

}
