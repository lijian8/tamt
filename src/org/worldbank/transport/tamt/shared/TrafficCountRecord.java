package org.worldbank.transport.tamt.shared;

import java.io.Serializable;
import java.util.Date;

public class TrafficCountRecord implements Serializable {
	
	public static final String DAYTYPE_WEEKDAY = "WEEKDAY";
	public static final String DAYTYPE_SATURDAY = "SATURDAY";
	public static final String DAYTYPE_SUNDAY_HOLIDAY = "SUNDAY_HOLIDAY";
	
	private String id;
	
	private Date date;
	private String dayType;
	private Date startTime;
	private Date endTime;
	private String tag;
	private int W2;
	private int W3;
	private int PC;
	private int TX;
	private int LDV;
	private int LDC;
	private int HDC;
	private int MDB;
	private int HDB;
	
	public TrafficCountRecord()
	{
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDayType() {
		return dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getW2() {
		return W2;
	}

	public void setW2(int w2) {
		W2 = w2;
	}

	public int getW3() {
		return W3;
	}

	public void setW3(int w3) {
		W3 = w3;
	}

	public int getPC() {
		return PC;
	}

	public void setPC(int pC) {
		PC = pC;
	}

	public int getTX() {
		return TX;
	}

	public void setTX(int tX) {
		TX = tX;
	}

	public int getLDV() {
		return LDV;
	}

	public void setLDV(int lDV) {
		LDV = lDV;
	}

	public int getLDC() {
		return LDC;
	}

	public void setLDC(int lDC) {
		LDC = lDC;
	}

	public int getHDC() {
		return HDC;
	}

	public void setHDC(int hDC) {
		HDC = hDC;
	}

	public int getMDB() {
		return MDB;
	}

	public void setMDB(int mDB) {
		MDB = mDB;
	}

	public int getHDB() {
		return HDB;
	}

	public void setHDB(int hDB) {
		HDB = hDB;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrafficCountRecord [HDB=");
		builder.append(HDB);
		builder.append(", HDC=");
		builder.append(HDC);
		builder.append(", LDC=");
		builder.append(LDC);
		builder.append(", LDV=");
		builder.append(LDV);
		builder.append(", MDB=");
		builder.append(MDB);
		builder.append(", PC=");
		builder.append(PC);
		builder.append(", TX=");
		builder.append(TX);
		builder.append(", W2=");
		builder.append(W2);
		builder.append(", W3=");
		builder.append(W3);
		builder.append(", date=");
		builder.append(date);
		builder.append(", dayType=");
		builder.append(dayType);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", id=");
		builder.append(id);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}
	
}
