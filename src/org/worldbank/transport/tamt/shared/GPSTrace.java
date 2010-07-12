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
	private Date processDate;
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

	/*
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GPSTrace [description=");
		builder.append(description);
		builder.append(", fileId=");
		builder.append(fileId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", processDate=");
		builder.append(processDate);
		builder.append(", processed=");
		builder.append(processed);
		builder.append(", recordCount=");
		builder.append(recordCount);
		builder.append(", region=");
		builder.append(region);
		builder.append(", uploadDate=");
		builder.append(uploadDate);
		builder.append("]");
		return builder.toString();
	}
	*/
}