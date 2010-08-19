package org.worldbank.transport.tamt.shared;

import java.io.Serializable;

public class DefaultFlow implements Serializable {

	String id;
	
	// Can hold TagDetails.id, and nested StudyRegion.id
	TagDetails tagDetails;
	
	String w2Weekday;
	String w2Saturday;
	String w2SundayHoliday;
	
	String w3Weekday;
	String w3Saturday;
	String w3SundayHoliday;
	
	String pcWeekday;
	String pcSaturday;
	String pcSundayHoliday;
	
	String txWeekday;
	String txSaturday;
	String txSundayHoliday;
	
	String ldvWeekday;
	String ldvSaturday;
	String ldvSundayHoliday;
	
	String ldcWeekday;
	String ldcSaturday;
	String ldcSundayHoliday;
	
	String hdcWeekday;
	String hdcSaturday;
	String hdcSundayHoliday;
	
	String mdbWeekday;
	String mdbSaturday;
	String mdbSundayHoliday;
	
	String hdbWeekday;
	String hdbSaturday;
	String hdbSundayHoliday;
	
	public DefaultFlow()
	{
		
	}

	public String getW2Weekday() {
		return w2Weekday;
	}

	public void setW2Weekday(String w2Weekday) {
		this.w2Weekday = w2Weekday;
	}

	public String getW2Saturday() {
		return w2Saturday;
	}

	public void setW2Saturday(String w2Saturday) {
		this.w2Saturday = w2Saturday;
	}

	public String getW2SundayHoliday() {
		return w2SundayHoliday;
	}

	public void setW2SundayHoliday(String w2SundayHoliday) {
		this.w2SundayHoliday = w2SundayHoliday;
	}

	public String getW3Weekday() {
		return w3Weekday;
	}

	public void setW3Weekday(String w3Weekday) {
		this.w3Weekday = w3Weekday;
	}

	public String getW3Saturday() {
		return w3Saturday;
	}

	public void setW3Saturday(String w3Saturday) {
		this.w3Saturday = w3Saturday;
	}

	public String getW3SundayHoliday() {
		return w3SundayHoliday;
	}

	public void setW3SundayHoliday(String w3SundayHoliday) {
		this.w3SundayHoliday = w3SundayHoliday;
	}

	public String getPcWeekday() {
		return pcWeekday;
	}

	public void setPcWeekday(String pcWeekday) {
		this.pcWeekday = pcWeekday;
	}

	public String getPcSaturday() {
		return pcSaturday;
	}

	public void setPcSaturday(String pcSaturday) {
		this.pcSaturday = pcSaturday;
	}

	public String getPcSundayHoliday() {
		return pcSundayHoliday;
	}

	public void setPcSundayHoliday(String pcSundayHoliday) {
		this.pcSundayHoliday = pcSundayHoliday;
	}

	public String getTxWeekday() {
		return txWeekday;
	}

	public void setTxWeekday(String txWeekday) {
		this.txWeekday = txWeekday;
	}

	public String getTxSaturday() {
		return txSaturday;
	}

	public void setTxSaturday(String txSaturday) {
		this.txSaturday = txSaturday;
	}

	public String getTxSundayHoliday() {
		return txSundayHoliday;
	}

	public void setTxSundayHoliday(String txSundayHoliday) {
		this.txSundayHoliday = txSundayHoliday;
	}

	public String getLdvWeekday() {
		return ldvWeekday;
	}

	public void setLdvWeekday(String ldvWeekday) {
		this.ldvWeekday = ldvWeekday;
	}

	public String getLdvSaturday() {
		return ldvSaturday;
	}

	public void setLdvSaturday(String ldvSaturday) {
		this.ldvSaturday = ldvSaturday;
	}

	public String getLdvSundayHoliday() {
		return ldvSundayHoliday;
	}

	public void setLdvSundayHoliday(String ldvSundayHoliday) {
		this.ldvSundayHoliday = ldvSundayHoliday;
	}

	public String getLdcWeekday() {
		return ldcWeekday;
	}

	public void setLdcWeekday(String ldcWeekday) {
		this.ldcWeekday = ldcWeekday;
	}

	public String getLdcSaturday() {
		return ldcSaturday;
	}

	public void setLdcSaturday(String ldcSaturday) {
		this.ldcSaturday = ldcSaturday;
	}

	public String getLdcSundayHoliday() {
		return ldcSundayHoliday;
	}

	public void setLdcSundayHoliday(String ldcSundayHoliday) {
		this.ldcSundayHoliday = ldcSundayHoliday;
	}

	public String getHdcWeekday() {
		return hdcWeekday;
	}

	public void setHdcWeekday(String hdcWeekday) {
		this.hdcWeekday = hdcWeekday;
	}

	public String getHdcSaturday() {
		return hdcSaturday;
	}

	public void setHdcSaturday(String hdcSaturday) {
		this.hdcSaturday = hdcSaturday;
	}

	public String getHdcSundayHoliday() {
		return hdcSundayHoliday;
	}

	public void setHdcSundayHoliday(String hdcSundayHoliday) {
		this.hdcSundayHoliday = hdcSundayHoliday;
	}

	public String getMdbWeekday() {
		return mdbWeekday;
	}

	public void setMdbWeekday(String mdbWeekday) {
		this.mdbWeekday = mdbWeekday;
	}

	public String getMdbSaturday() {
		return mdbSaturday;
	}

	public void setMdbSaturday(String mdbSaturday) {
		this.mdbSaturday = mdbSaturday;
	}

	public String getMdbSundayHoliday() {
		return mdbSundayHoliday;
	}

	public void setMdbSundayHoliday(String mdbSundayHoliday) {
		this.mdbSundayHoliday = mdbSundayHoliday;
	}

	public String getHdbWeekday() {
		return hdbWeekday;
	}

	public void setHdbWeekday(String hdbWeekday) {
		this.hdbWeekday = hdbWeekday;
	}

	public String getHdbSaturday() {
		return hdbSaturday;
	}

	public void setHdbSaturday(String hdbSaturday) {
		this.hdbSaturday = hdbSaturday;
	}

	public String getHdbSundayHoliday() {
		return hdbSundayHoliday;
	}

	public void setHdbSundayHoliday(String hdbSundayHoliday) {
		this.hdbSundayHoliday = hdbSundayHoliday;
	}

	public TagDetails getTagDetails() {
		return tagDetails;
	}

	public void setTagDetails(TagDetails tagDetails) {
		this.tagDetails = tagDetails;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultFlow [hdbSaturday=");
		builder.append(hdbSaturday);
		builder.append(", hdbSundayHoliday=");
		builder.append(hdbSundayHoliday);
		builder.append(", hdbWeekday=");
		builder.append(hdbWeekday);
		builder.append(", hdcSaturday=");
		builder.append(hdcSaturday);
		builder.append(", hdcSundayHoliday=");
		builder.append(hdcSundayHoliday);
		builder.append(", hdcWeekday=");
		builder.append(hdcWeekday);
		builder.append(", id=");
		builder.append(id);
		builder.append(", ldcSaturday=");
		builder.append(ldcSaturday);
		builder.append(", ldcSundayHoliday=");
		builder.append(ldcSundayHoliday);
		builder.append(", ldcWeekday=");
		builder.append(ldcWeekday);
		builder.append(", ldvSaturday=");
		builder.append(ldvSaturday);
		builder.append(", ldvSundayHoliday=");
		builder.append(ldvSundayHoliday);
		builder.append(", ldvWeekday=");
		builder.append(ldvWeekday);
		builder.append(", mdbSaturday=");
		builder.append(mdbSaturday);
		builder.append(", mdbSundayHoliday=");
		builder.append(mdbSundayHoliday);
		builder.append(", mdbWeekday=");
		builder.append(mdbWeekday);
		builder.append(", pcSaturday=");
		builder.append(pcSaturday);
		builder.append(", pcSundayHoliday=");
		builder.append(pcSundayHoliday);
		builder.append(", pcWeekday=");
		builder.append(pcWeekday);
		builder.append(", tagDetails=");
		builder.append(tagDetails);
		builder.append(", txSaturday=");
		builder.append(txSaturday);
		builder.append(", txSundayHoliday=");
		builder.append(txSundayHoliday);
		builder.append(", txWeekday=");
		builder.append(txWeekday);
		builder.append(", w2Saturday=");
		builder.append(w2Saturday);
		builder.append(", w2SundayHoliday=");
		builder.append(w2SundayHoliday);
		builder.append(", w2Weekday=");
		builder.append(w2Weekday);
		builder.append(", w3Saturday=");
		builder.append(w3Saturday);
		builder.append(", w3SundayHoliday=");
		builder.append(w3SundayHoliday);
		builder.append(", w3Weekday=");
		builder.append(w3Weekday);
		builder.append("]");
		return builder.toString();
	}
}
