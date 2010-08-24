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
import org.worldbank.transport.tamt.shared.DayTypePerYearOption;
import org.worldbank.transport.tamt.shared.DefaultFlow;
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
	
	private static RegionDAO singleton = null;
	public static RegionDAO get() {
		if (singleton == null) {
			singleton = new RegionDAO();
		}
		return singleton;
	}
	
	public RegionDAO()
	{
	
	}

	public void deleteStudyRegionById(String id) throws SQLException {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			// first, delete the study region
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
			String sql = "select id, name, description, AsText(geometry) geom, mapzoomlevel, AsText(mapcenter) center, iscurrentregion, default_zone_type from \"studyregion\" ORDER BY name";
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
			      String defaultZoneType = r.getString(8);
			      
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
			      studyRegion.setDefaultZoneType(defaultZoneType);
			      
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
					" iscurrentregion = "+studyRegion.isCurrentRegion()+"," +
					" default_zone_type = '"+studyRegion.getDefaultZoneType()+"' " +
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
			String sql = "INSERT INTO \"studyregion\" (pid, id, name, description, geometry, mapzoomlevel, mapcenter, iscurrentregion, default_zone_type) " +
					"VALUES (" + 
					"(SELECT nextval('studyregion_pid_seq'))," +
					"'"+studyRegion.getId()+"', " +
					"'"+studyRegion.getName()+"'," +
					"'"+studyRegion.getDescription()+"'," +
					"GeometryFromText('"+geometry.toText()+"', 4326)," +
					"'"+studyRegion.getMapZoomLevel()+"'," +
					"GeometryFromText('"+mapCenter.toText()+"', 4326)," +
					" "+studyRegion.isCurrentRegion()+"," +
					" '"+studyRegion.getDefaultZoneType()+"' " +
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
	
	public TagDetails getStudyRegion(TagDetails tagDetails) throws Exception
	{
		
		StudyRegion studyRegion = null;
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT t.id, t.name, s.id, s.name " +
					"FROM tagdetails t, studyregion s " +
					"WHERE t.region = s.id " +
					"AND t.id = '"+tagDetails.getId()+"'";
			logger.debug("sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			while( r.next() ) { 
				
				// we will use the tag name
				tagDetails.setName(r.getString(2));
				
				// and we really want the study name
				studyRegion = new StudyRegion();
				studyRegion.setId(r.getString(3));
				studyRegion.setName(r.getString(4));
				
				tagDetails.setRegion(studyRegion);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		}
		return tagDetails; 
		
	}

	public void deleteStudyRegions(ArrayList<String> studyRegionIds) throws SQLException {
		for (Iterator iterator = studyRegionIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			deleteStudyRegionById(id);
		}
	}

	public DayTypePerYearOption getDayTypePerYearOption(String studyRegionId) 
		throws Exception {
		DayTypePerYearOption dayTypePerYearOption = null;
		/*
		 * Granted, this method does not access the study region table, but it
		 * was only one option and creating a new API/BO/DAO structure for it
		 * seemed overkill. Since it is most closely related to a StudyRegion,
		 * we sunk it in this DAO.
		 */
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT id, regionid, activeoption, option1weekday, " +
					"option2weekday, option2saturday, option2sundayholiday " +
					"FROM daytypeperyearoption";
			logger.debug("getDayTypePerYearOption sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			while( r.next() ) { 
				dayTypePerYearOption = new DayTypePerYearOption();
				dayTypePerYearOption.setId(r.getString(1));
				dayTypePerYearOption.setRegionId(r.getString(2));
				dayTypePerYearOption.setActiveOption(r.getString(3));
				dayTypePerYearOption.setOption1weekday(r.getString(4));
				dayTypePerYearOption.setOption2weekday(r.getString(5));
				dayTypePerYearOption.setOption2saturday(r.getString(6));
				dayTypePerYearOption.setOption2sundayHoliday(r.getString(7));
			}
			connection.close(); // returns connection to the pool

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 		
		return dayTypePerYearOption;
	}

	public DayTypePerYearOption saveDayTypePerYearOption(DayTypePerYearOption option) throws Exception {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO daytypeperyearoption (id, regionid, " +
					"activeoption, option1weekday, option2weekday, " +
					"option2saturday, option2sundayholiday) " +
			"VALUES (" + 
			"'"+option.getId()+"', " +
			"'"+option.getRegionId()+"'," +
			"'"+option.getActiveOption()+"'," +
			option.getOption1weekday()+"," +
			option.getOption2weekday()+"," +
			option.getOption2saturday()+"," +
			option.getOption2sundayHoliday()+" " +
			")";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			connection.close(); // returns connection to the pool

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    return option;
	}

	public DayTypePerYearOption updateDayTypePerYearOption(
			DayTypePerYearOption option) throws SQLException {

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE daytypeperyearoption SET " +
					"activeoption = "+option.getActiveOption()+", " +
					"option1weekday = "+option.getOption1weekday()+", " +
					"option2weekday = "+option.getOption2weekday()+", " +
					"option2saturday = "+option.getOption2saturday()+", " +
					"option2sundayholiday = "+option.getOption2sundayHoliday()+" " +
					"WHERE id = '"+option.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql);
			connection.close(); // returns connection to the pool				
		} catch (SQLException e)
		{
			logger.error(e.getMessage());
			throw e;
		}
		
		return option;
	}
	
	public DefaultFlow saveDefaultFlow(DefaultFlow defaultFlow) throws Exception {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO defaulttrafficflow (id, regionid, tagid, " +
							"w2wk, w2sa, w2sh, " +
							"w3wk, w3sa, w3sh, " +
							"pcwk, pcsa, pcsh, " +
							"txwk, txsa, txsh, " +
							"ldvwk, ldvsa, ldvsh, " +
							"ldcwk, ldcsa, ldcsh, " +
							"hdcwk, hdcsa, hdcsh, " +
							"mdbwk, mdbsa, mdbsh, " +
							"hdbwk, hdbsa, hdbsh) " +
			"VALUES (" + 
			"'"+defaultFlow.getId()+"', " +
			"'"+defaultFlow.getTagDetails().getRegion().getId()+"'," +
			"'"+defaultFlow.getTagDetails().getId()+"'," +
			
			defaultFlow.getW2Weekday()+"," +
			defaultFlow.getW2Saturday()+"," +
			defaultFlow.getW2SundayHoliday()+"," +
			
			defaultFlow.getW3Weekday()+"," +
			defaultFlow.getW3Saturday()+"," +
			defaultFlow.getW3SundayHoliday()+"," +
			
			defaultFlow.getPcWeekday()+"," +
			defaultFlow.getPcSaturday()+"," +
			defaultFlow.getPcSundayHoliday()+"," +
			
			defaultFlow.getTxWeekday()+"," +
			defaultFlow.getTxSaturday()+"," +
			defaultFlow.getTxSundayHoliday()+"," +
			
			defaultFlow.getLdvWeekday()+"," +
			defaultFlow.getLdvSaturday()+"," +
			defaultFlow.getLdvSundayHoliday()+"," +
			
			defaultFlow.getLdcWeekday()+"," +
			defaultFlow.getLdcSaturday()+"," +
			defaultFlow.getLdcSundayHoliday()+"," +
			
			defaultFlow.getHdcWeekday()+"," +
			defaultFlow.getHdcSaturday()+"," +
			defaultFlow.getHdcSundayHoliday()+"," +
			
			defaultFlow.getMdbWeekday()+"," +
			defaultFlow.getMdbSaturday()+"," +
			defaultFlow.getMdbSundayHoliday()+"," +
			
			defaultFlow.getHdbWeekday()+"," +
			defaultFlow.getHdbSaturday()+"," +
			defaultFlow.getHdbSundayHoliday()+" " +
			
			")";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			connection.close(); // returns connection to the pool

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    return defaultFlow;
	}
	
	public DefaultFlow updateDefaultFlow(
			DefaultFlow defaultFlow) throws SQLException {

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE defaulttrafficflow SET " +
			
					"w2wk = "+defaultFlow.getW2Weekday()+", " +
					"w2sa = "+defaultFlow.getW2Saturday()+", " +
					"w2sh = "+defaultFlow.getW2SundayHoliday()+", " +
					
					"w3wk = "+defaultFlow.getW3Weekday()+", " +
					"w3sa = "+defaultFlow.getW3Saturday()+", " +
					"w3sh = "+defaultFlow.getW3SundayHoliday()+", " +
					
					"pcwk = "+defaultFlow.getPcWeekday()+", " +
					"pcsa = "+defaultFlow.getPcSaturday()+", " +
					"pcsh = "+defaultFlow.getPcSundayHoliday()+", " +
					
					"txwk = "+defaultFlow.getTxWeekday()+", " +
					"txsa = "+defaultFlow.getTxSaturday()+", " +
					"txsh = "+defaultFlow.getTxSundayHoliday()+", " +
					
					"ldvwk = "+defaultFlow.getLdvWeekday()+", " +
					"ldvsa = "+defaultFlow.getLdvSaturday()+", " +
					"ldvsh = "+defaultFlow.getLdvSundayHoliday()+", " +
					
					"ldcwk = "+defaultFlow.getLdcWeekday()+", " +
					"ldcsa = "+defaultFlow.getLdcSaturday()+", " +
					"ldcsh = "+defaultFlow.getLdcSundayHoliday()+", " +
					
					"hdcwk = "+defaultFlow.getHdcWeekday()+", " +
					"hdcsa = "+defaultFlow.getHdcSaturday()+", " +
					"hdcsh = "+defaultFlow.getHdcSundayHoliday()+", " +
					
					"mdbwk = "+defaultFlow.getMdbWeekday()+", " +
					"mdbsa = "+defaultFlow.getMdbSaturday()+", " +
					"mdbsh = "+defaultFlow.getMdbSundayHoliday()+", " +
					
					"hdbwk = "+defaultFlow.getHdbWeekday()+", " +
					"hdbsa = "+defaultFlow.getHdbSaturday()+", " +
					"hdbsh = "+defaultFlow.getHdbSundayHoliday()+" " +
					
					"WHERE id = '"+defaultFlow.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql);
			connection.close(); // returns connection to the pool				
		} catch (SQLException e)
		{
			logger.error(e.getMessage());
			throw e;
		}
		
		return defaultFlow;
	}

	public DefaultFlow getDefaultFlow(DefaultFlow defaultFlow) throws Exception {

		DefaultFlow fetched = null;
		String tagId = defaultFlow.getTagDetails().getId();
		String regionId = defaultFlow.getTagDetails().getRegion().getId();
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "SELECT id, " +
					"w2wk, w2sa, w2sh, " +
					"w3wk, w3sa, w3sh, " +
					"pcwk, pcsa, pcsh, " +
					"txwk, txsa, txsh, " +
					"ldvwk, ldvsa, ldvsh, " +
					"ldcwk, ldcsa, ldcsh, " +
					"hdcwk, hdcsa, hdcsh, " +
					"mdbwk, mdbsa, mdbsh, " +
					"hdbwk, hdbsa, hdbsh " +
					"FROM defaulttrafficflow " +
					"WHERE regionid = '"+regionId+"' " +
					"AND tagid = '"+tagId+"'";
			logger.debug("getDefaultFlow sql=" + sql);
			ResultSet r = s.executeQuery(sql); 
			while( r.next() ) { 
				
				fetched = new DefaultFlow();
				fetched.setId(r.getString(1));
				fetched.setTagDetails(defaultFlow.getTagDetails());
				
				fetched.setW2Weekday(r.getString(2));
				fetched.setW2Saturday(r.getString(3));
				fetched.setW2SundayHoliday(r.getString(4));
				
				fetched.setW3Weekday(r.getString(5));
				fetched.setW3Saturday(r.getString(6));
				fetched.setW3SundayHoliday(r.getString(7));
				
				fetched.setPcWeekday(r.getString(8));
				fetched.setPcSaturday(r.getString(9));
				fetched.setPcSundayHoliday(r.getString(10));
				
				fetched.setTxWeekday(r.getString(11));
				fetched.setTxSaturday(r.getString(12));
				fetched.setTxSundayHoliday(r.getString(13));
				
				fetched.setLdvWeekday(r.getString(14));
				fetched.setLdvSaturday(r.getString(15));
				fetched.setLdvSundayHoliday(r.getString(16));
				
				fetched.setLdcWeekday(r.getString(17));
				fetched.setLdcSaturday(r.getString(18));
				fetched.setLdcSundayHoliday(r.getString(19));
				
				fetched.setHdcWeekday(r.getString(20));
				fetched.setHdcSaturday(r.getString(21));
				fetched.setHdcSundayHoliday(r.getString(22));
				
				fetched.setMdbWeekday(r.getString(23));
				fetched.setMdbSaturday(r.getString(24));
				fetched.setMdbSundayHoliday(r.getString(25));
				
				fetched.setHdbWeekday(r.getString(26));
				fetched.setHdbSaturday(r.getString(27));
				fetched.setHdbSundayHoliday(r.getString(28));
				
			}
			connection.close(); // returns connection to the pool

		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 		
	    logger.debug("fetched flow=" + fetched);
		return fetched;
	}
	
	public void deleteDefaultFlow(DefaultFlow defaultFlow) throws Exception
	{
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			
			String sql = "DELETE FROM defaulttrafficflow WHERE id = '"+defaultFlow.getId()+"'";
			logger.debug("deleteDefaultFlow sql=" + sql);
			s.execute(sql); 
			connection.close(); // returns connection to the pool
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}		
	}

	
}
