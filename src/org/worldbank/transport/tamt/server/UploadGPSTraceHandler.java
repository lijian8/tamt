package org.worldbank.transport.tamt.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.server.api.GPSTraceAPI;
import org.worldbank.transport.tamt.server.dao.NonGPSArchiveException;
import org.worldbank.transport.tamt.server.dao.NonZipFileException;
import org.worldbank.transport.tamt.shared.GPSTrace;
import org.worldbank.transport.tamt.shared.GPSTraceException;


public class UploadGPSTraceHandler extends HttpServlet {

	static Logger logger = Logger.getLogger(UploadGPSTraceHandler.class);
	private GPSTraceAPI gpsTraceAPI;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1512690378972095810L;
	private static final String UPLOAD_DIRECTORY = "/tamt-tmp-uploads/"; // TODO: create this directory, world read and write


	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
			super.doGet(req, resp);
		}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
		
			long start = System.currentTimeMillis();
			
			gpsTraceAPI = new GPSTraceAPI();
			
			logger.debug("posting to upload servlet");
			logger.debug("multipart=" + ServletFileUpload.isMultipartContent(req));
			
			logger.debug("content-type=" + req.getContentType());
			
			// set response to plain text
			resp.setContentType("text/html");
			
			// create a timestamp
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
			String timestamp = format.format(new java.util.Date());
			
			if(ServletFileUpload.isMultipartContent(req))
			{
				// we'll store them temporarily on disk, then move them to the DB
				FileItemFactory factory = new DiskFileItemFactory();
				
				logger.debug("factory=" + factory);
				
				// upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				logger.debug("upload=" + upload);
				
				String nameValue = "";
				String descriptionValue = "";
				
				try 
				{
					
					List<FileItem> items = upload.parseRequest(req);
					logger.debug("items=" + items);
					
					for(FileItem item : items)
					{
						logger.debug("item=" + item);
						logger.debug("item.name=" + item.getName());
						logger.debug("item.isFormField=" + item.isFormField());
						
						if( item.isFormField() )
						{
							logger.debug("formFieldName=" + item.getFieldName());
							if ("myName".equals(item.getFieldName()))
							{
					            nameValue = item.getString();
					            logger.debug("!! name=" + nameValue);
					            
							}
							if ("myDesc".equals(item.getFieldName()))
							{
					            descriptionValue = item.getString();
					            logger.debug("!! descriptionValue=" + descriptionValue);
					            
							}
						} else {
						
							String fileName = item.getName();
							if( fileName != null)
							{
								fileName = FilenameUtils.getName(fileName);
							}
							
							/*
							 * We have hijacked description to be used
							 * for the filename
							 */
							descriptionValue = fileName;
							
							// append a timestamp to the filename
							fileName = timestamp + fileName;
							
							File uploadedFile = new File(UPLOAD_DIRECTORY, fileName);
							if( uploadedFile.createNewFile())
							{
								item.write(uploadedFile);
								
								/*
								 * Now save the incoming data as a GPSTrace
								 * (along with the associated file)
								 */
								GPSTrace gpsTrace = new GPSTrace();
								gpsTrace.setId("TEMP");
								gpsTrace.setName(nameValue);
								gpsTrace.setDescription(descriptionValue);
								gpsTraceAPI.saveGPSTrace(gpsTrace, uploadedFile);
								
								// if it doesn't get thrown, then return 200
								resp.setStatus(HttpServletResponse.SC_OK);
								resp.getWriter().print(HttpServletResponse.SC_OK);
								resp.flushBuffer();
								
								// can we delete the file now from the UPLOAD_DIRECTORY?
								uploadedFile.delete();
								
							} else {
								logger.debug("file already exists in repository");
								throw new IOException("The file already exists in repository.");
							}
						}
					}
					
				}
				/*
				catch (GPSTraceException e)
				{
					logger.error("gps trace exception:" + e.getMessage());
					resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED); // 412
					resp.getWriter().print(e.getMessage());
				}
				catch (NonZipFileException e)
				{
					logger.error("upload error:" + e.getMessage());
					resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED); // 412
					resp.getWriter().print("ZIPFILEERROR");
				}
				catch (NonGPSArchiveException e)
				{
					resp.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED); // 412
					resp.getWriter().print("NONGPSARCHIVE");	
				}
				*/
				catch (Exception e)
				{
					logger.debug("exception=" + e.getMessage());
					resp.getWriter().print(e.getMessage());
					//resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					//		"An error occurred while creating the file : " + e.getMessage());
				}
				
			} else {
				 // TODO: rather than just testing for multipart, we also
				// want to throw an error if the upload is not a zip file (probably up above somewhere)
				logger.debug("unsupported contents type");
				resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
				 	"Request contents type is not supported by the servlet.");
			}
			
			long end = System.currentTimeMillis();
			long delta = end - start;
			logger.debug("complete upload and processing (ms): " + delta);
			
		}
	
}
