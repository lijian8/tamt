package org.worldbank.transport.tamt.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TagDetails;

public class TagDAO extends DAO {

	static Logger logger = Logger.getLogger(TagDAO.class);
	
	public TagDAO()
	{
		init();
	}
	
	public ArrayList<TagDetails> getTagDetails(StudyRegion region)
	{
		/*
		 * Query the tagDetails table where regionName = region.name
		 * 
		 * select * from "tagDetails" where region = 'default'
		 * 
		 */
		ArrayList<TagDetails> tagDetailsList = new ArrayList<TagDetails>();
		try {
			Statement s = connection.createStatement();
			String sql = "select * from \"tagDetails\" where region = '"+region.getName()+"' ORDER BY name";
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
			      
			      TagDetails tagDetails = new TagDetails();
			      tagDetails.setId(id);
			      tagDetails.setName(name);
			      tagDetails.setDescription(description);
			      
					/*
					Vertex t1c1 = new Vertex();
					t1c1.setLat(40.78);
					t1c1.setLng(-111.05);
					
					Vertex t1c2 = new Vertex();
					t1c2.setLat(40.73);
					t1c2.setLng(-111.10);
					*/
			      
			      //TODO: include StudyRegion inside TagDetails?
			      
			      tagDetailsList.add(tagDetails);
			} 
    
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return tagDetailsList;
		//return stubList();
		
	}
	
	public TagDetails saveTagDetails(TagDetails tagDetails) throws SQLException {

		try {
			Statement s = connection.createStatement();
			// INSERT INTO films VALUES ('UA502', 'Bananas', 105, DEFAULT, 'Comedy', '82 minutes');
			// name, description, regionName
			// TODO: extend the model to include regionName string or region StudyRegion as property of TagDetails
			// for now we just use 'default'
			String sql = "INSERT INTO \"tagDetails\" (id, name, description, region) VALUES ('"+tagDetails.getId()+"', '"+tagDetails.getName()+"','"+tagDetails.getDescription()+"','default')";
			logger.debug("sql=" + sql);
			logger.debug("native sql=" + connection.nativeSQL(sql));
			s.executeUpdate(sql); 
			
		} 
	    catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			throw e;
			
		} 
	    
	    return tagDetails;
	    
	}
	
	public TagDetails updateTagDetails(TagDetails tagDetails) throws SQLException {
		try {
			Statement s = connection.createStatement();
			// TODO: extend the model to include regionName string or region StudyRegion as property of TagDetails
			// for now we just use 'default'
			String sql = "UPDATE \"tagDetails\" SET " +
					" name = '"+tagDetails.getName()+"'," +
					" description = '"+tagDetails.getDescription()+"'," +
					" region = 'default' " +
					"WHERE id = '"+tagDetails.getId()+"'";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql); 
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} 
	    
	    return tagDetails;
	}	
	
	public void deleteTagDetails(ArrayList<String> tagDetailIds) throws SQLException {
		for (Iterator iterator = tagDetailIds.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			deleteTagDetailById(id);
		}
	}
	
	public void deleteTagDetailById(String id) throws SQLException
	{
		try {
			Statement s = connection.createStatement();
			String sql = "DELETE FROM \"tagDetails\" WHERE id = '"+id+"'";
			logger.debug("sql=" + sql);
			s.execute(sql); 
			
		} 
		catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}		
	}
	
	private ArrayList<TagDetails> stubList()
	{
		ArrayList<TagDetails> tagDetails = new ArrayList<TagDetails>();
		int sample = 10;
		for (int i = 0; i < sample; i++) {
			UUID id = UUID.randomUUID();
			TagDetails t = new TagDetails();
			t.setName("Name-" + id.toString());
			t.setDescription("Desc-"+id.toString());
			tagDetails.add(t);
		}
		return tagDetails;
	}
	 
}
