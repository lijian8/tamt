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
import org.worldbank.transport.tamt.server.api.SpeedDistributionReportAPI;
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

public class DownloadSpeedBinAggregateByDayTypeReport extends HttpServlet {

	private static final long serialVersionUID = 3019963294878178516L;
	static final Logger logger = Logger.getLogger(DownloadSpeedBinAggregateByDayTypeReport.class);
	
	private SpeedDistributionReportAPI api = new SpeedDistributionReportAPI();
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		resp.setContentType("text/csv");
		
		// fetch the report for the incoming tagid
		String tagId = req.getParameter("tagid");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date downloaded = new Date();
		String ts = format.format(downloaded);
		String csvFileName = "speeddistribution-aggregatebydaytype-" + ts + ".csv";
		resp.setHeader("Content-disposition", "attachment;filename="+csvFileName);
		
		if( tagId != null)
		{
			try {
				
				String data = api.downloadSpeedBinAggregateByDayTypeReport(tagId);
				resp.getOutputStream().print(data);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			try {
				
				String data = api.downloadSpeedBinAggregateByDayTypeReportForRegion();
				resp.getOutputStream().print(data);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		
	}
	
}
