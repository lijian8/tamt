package org.worldbank.transport.tamt.server.downloads;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.api.TagAPI;
import org.worldbank.transport.tamt.server.api.TrafficFlowReportAPI;
import org.worldbank.transport.tamt.server.bo.AssignStatusBO;
import org.worldbank.transport.tamt.server.bo.GPSTraceBO;
import org.worldbank.transport.tamt.server.bo.RegionBO;
import org.worldbank.transport.tamt.server.bo.RoadBO;
import org.worldbank.transport.tamt.server.bo.TagBO;
import org.worldbank.transport.tamt.server.bo.ZoneBO;
import org.worldbank.transport.tamt.server.dao.AssignStatusDAO;
import org.worldbank.transport.tamt.server.dao.GPSTraceDAO;
import org.worldbank.transport.tamt.server.dao.RegionDAO;
import org.worldbank.transport.tamt.server.dao.RoadDAO;
import org.worldbank.transport.tamt.server.dao.TagDAO;
import org.worldbank.transport.tamt.server.dao.ZoneDAO;
import org.worldbank.transport.tamt.shared.TagDetails;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.TrafficFlowReport;

public class DownloadRoadLengthReport extends HttpServlet {

	private static final long serialVersionUID = 1812867113892653770L;
	static final Logger logger = Logger.getLogger(DownloadRoadLengthReport.class);
	
	private TagAPI api = new TagAPI();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		resp.setContentType("text/csv");
		
		// fetch the report for the incoming tagid
		String regionid = req.getParameter("regionid");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date downloaded = new Date();
		String ts = format.format(downloaded);
		String csvFileName = "road-length-by-tag-" + ts + ".csv";
		
		// output
		resp.setHeader("Content-disposition", "attachment;filename="+csvFileName);

		try {
			
			// String data = api.downloadTrafficFlowReport(tagId);
			String data = api.downloadRoadLengthReport(regionid);
			resp.getOutputStream().print(data);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
