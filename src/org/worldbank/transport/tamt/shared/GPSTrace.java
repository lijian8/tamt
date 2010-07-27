package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class GPSTrace implements Serializable {
	
	private String id;
	private String name;
	private String description;
	
	// not visible in UI
	private StudyRegion region;
	private String fileId;
	
	private int recordCount = 0;
	private Date uploadDate;
	
	// "processed" (count,date) now represents "assigned to roads"
	// matchedCount is how many points in the trace were matched to roads
	// during the assignment process.
	private int processedCount = 0; 
	private Date processDate;
	private int matchedCount = 0;
	private boolean processed = false;
	
	public GPSTrace() {
		
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StudyRegion getRegion() {
		return region;
	}

	public void setRegion(StudyRegion region) {
		this.region = region;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public int getMatchedCount() {
		return matchedCount;
	}

	public void setMatchedCount(int matchedCount) {
		this.matchedCount = matchedCount;
	}

	public int getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(int processedCount) {
		this.processedCount = processedCount;
	}

}