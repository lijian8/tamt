package org.worldbank.transport.tamt.server.dao;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.Vertex;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class Utils {

	static Logger logger = Logger.getLogger(Utils.class);
	
	public static ArrayList<Vertex> geometryToVertexArrayList(Geometry geometry)
	{
		//TODO: verify type of geometry (using geometry.getType()?)
		logger.debug("geometryToVertexArrayList...");
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		Coordinate[] coords = geometry.getCoordinates();
		for (int i = 0; i < coords.length; i++) {
			Coordinate c = coords[i];
			logger.debug("coordinate=" + c);
			Vertex v = new Vertex();
			v.setLat(c.y);
			v.setLng(c.x);
			logger.debug("vertex=" + v);
			vertices.add(v);
		}
		return vertices;
	}
}
