package org.worldbank.transport.tamt.server.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.worldbank.transport.tamt.shared.TrafficCountRecord;
import org.worldbank.transport.tamt.shared.StudyRegion;
import org.worldbank.transport.tamt.shared.TAMTPoint;

public class TrafficCountRecordDAO extends DAO {

	static Logger logger = Logger.getLogger(TrafficCountRecordDAO.class);

	private static TrafficCountRecordDAO singleton = null;
	public static TrafficCountRecordDAO get() {
		if (singleton == null) {
			singleton = new TrafficCountRecordDAO();
		}
		return singleton;
	}

	public TrafficCountRecordDAO() {

	}

	public ArrayList<TrafficCountRecord> getTrafficCountRecords(StudyRegion region)
			throws Exception {
		ArrayList<TrafficCountRecord> trafficCountRecordList = new ArrayList<TrafficCountRecord>();
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "select id, tag, countdate, daytype, starttime," +
					"endtime, w2, w3, pc, tx, ldv, ldc, hdc, mdb, hdb from trafficcount "
					+ "where region = '" + region.getName()+ "' ORDER BY countdate ASC, starttime ASC";
			logger.debug("SQL for getTrafficCountRecords: " + sql);
			ResultSet r = s.executeQuery(sql);
			while (r.next()) {
				
				String id = r.getString(1);
				String tag = r.getString(2);
				Date date = r.getDate(3);
				String dayType = r.getString(4);
				Date startTime = r.getTimestamp(5);
				Date endTime = r.getTimestamp(6);
				int W2 = r.getInt(7);
				int W3 = r.getInt(8);
				int PC = r.getInt(9);
				int TX = r.getInt(10);
				int LDV = r.getInt(11);
				int LDC = r.getInt(12);
				int HDC = r.getInt(13);
				int MDB = r.getInt(14);
				int HDB = r.getInt(15);

				TrafficCountRecord trafficCountRecord = new TrafficCountRecord();
				trafficCountRecord.setId(id);
				trafficCountRecord.setDate(date);
				trafficCountRecord.setDayType(dayType);
				trafficCountRecord.setEndTime(endTime);
				trafficCountRecord.setHDB(HDB);
				trafficCountRecord.setHDC(HDC);
				trafficCountRecord.setLDC(LDC);
				trafficCountRecord.setLDV(LDV);
				trafficCountRecord.setMDB(MDB);
				trafficCountRecord.setPC(PC);
				trafficCountRecord.setStartTime(startTime);
				trafficCountRecord.setTag(tag);
				trafficCountRecord.setTX(TX);
				trafficCountRecord.setW2(W2);
				trafficCountRecord.setW3(W3);
				
				trafficCountRecordList.add(trafficCountRecord);
			}

			connection.close(); // returns connection to the pool

		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}

		return trafficCountRecordList;

	}

	public TrafficCountRecord getTrafficCountRecordById(String id) throws Exception {
		TrafficCountRecord trafficCountRecord = new TrafficCountRecord();
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "select id, tag, countdate, daytype, starttime," +
			"endtime, w2, w3, pc, tx, ldv, ldc, hdc, mdb, hdb from trafficcount"
			+ "where id = '" + id + "' ORDER BY countdate";
			ResultSet r = s.executeQuery(sql);
			while (r.next()) {
				
				String tag = r.getString(2);
				Date date = r.getDate(3);
				String dayType = r.getString(4);
				Date startTime = r.getDate(5);
				Date endTime = r.getDate(6);
				int W2 = r.getInt(7);
				int W3 = r.getInt(8);
				int PC = r.getInt(9);
				int TX = r.getInt(10);
				int LDV = r.getInt(11);
				int LDC = r.getInt(12);
				int HDC = r.getInt(13);
				int MDB = r.getInt(14);
				int HDB = r.getInt(15);

				trafficCountRecord.setId(id);
				trafficCountRecord.setDate(date);
				trafficCountRecord.setDayType(dayType);
				trafficCountRecord.setEndTime(endTime);
				trafficCountRecord.setHDB(HDB);
				trafficCountRecord.setHDC(HDC);
				trafficCountRecord.setLDC(LDC);
				trafficCountRecord.setLDV(LDV);
				trafficCountRecord.setMDB(MDB);
				trafficCountRecord.setPC(PC);
				trafficCountRecord.setStartTime(startTime);
				trafficCountRecord.setTag(tag);
				trafficCountRecord.setTX(TX);
				trafficCountRecord.setW2(W2);
				trafficCountRecord.setW3(W3);

			}

			connection.close(); // returns connection to pool

		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new Exception("There was an error executing the SQL: "
					+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown exception: " + e.getMessage());
		}

		return trafficCountRecord;

	}

	public TrafficCountRecord saveTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws SQLException {

		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "INSERT INTO trafficcount (pid, id, region, tag, countdate, "
					+ "daytype, starttime, endtime, w2, w3, pc, tx, ldv, ldc, hdc, "
					+ "mdb, hdb) " + "VALUES ("
					+ "(SELECT nextval('studyregion_pid_seq')),"
					+ "'" 
					+ trafficCountRecord.getId()
					+ "', '"
					+ trafficCountRecord.getRegion()
					+ "', '"
					+ trafficCountRecord.getTag()
					+ "', '"
					+ trafficCountRecord.getDate()
					+ "', '"
					+ trafficCountRecord.getDayType()
					+ "', '"
					+ trafficCountRecord.getStartTime()
					+ "', '"
					+ trafficCountRecord.getEndTime()
					+ "', '"
					+ trafficCountRecord.getW2()
					+ "', '"
					+ trafficCountRecord.getW3()
					+ "', '"
					+ trafficCountRecord.getPC()
					+ "', '"
					+ trafficCountRecord.getTX()
					+ "', '"
					+ trafficCountRecord.getLDV()
					+ "', '"
					+ trafficCountRecord.getLDC()
					+ "', '"					
					+ trafficCountRecord.getHDC()
					+ "', '"
					+ trafficCountRecord.getMDB()
					+ "', '"
					+ trafficCountRecord.getHDB()
					+ "')";
			logger.debug("sql=" + sql);
			s.executeUpdate(sql);

			connection.close(); // returns connection to pool
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}

		return trafficCountRecord;

	}

	public TrafficCountRecord updateTrafficCountRecord(TrafficCountRecord trafficCountRecord) throws SQLException {
		try {
			Connection connection = getConnection();
			Statement s = connection.createStatement();
			String sql = "UPDATE trafficcount SET " +
					" tag = '"+trafficCountRecord.getTag()+"'," +
					" countdate = '"+trafficCountRecord.getDate()+"'," +
					" daytype = '"+trafficCountRecord.getDayType()+"'," +
					" starttime = '"+trafficCountRecord.getStartTime()+"'," +
					" endtime = '"+trafficCountRecord.getEndTime()+"'," +
					" w2 = '"+trafficCountRecord.getW2()+"'," +
					" w3 = '"+trafficCountRecord.getW3()+"'," +
					" pc = '"+trafficCountRecord.getPC()+"'," +
					" tx = '"+trafficCountRecord.getTX()+"'," +
					" ldv = '"+trafficCountRecord.getLDV()+"'," +
					" ldc = '"+trafficCountRecord.getLDC()+"'," +
					" hdc = '"+trafficCountRecord.getHDC()+"', " +
					" mdb = '"+trafficCountRecord.getMDB()+"', " +
					" hdb = '"+trafficCountRecord.getHDB()+"' " +
					"WHERE id = '" + trafficCountRecord.getId() + "'";
			logger.debug("UPDATE TrafficCountRecord SQL=" + sql);
			s.executeUpdate(sql);
			connection.close(); // returns connection to pool

		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}

		return trafficCountRecord;
	}

	public void deleteTrafficCountRecords(ArrayList<String> trafficCountRecordIds)
			throws SQLException {
		for (Iterator<String> iterator = trafficCountRecordIds.iterator(); iterator
				.hasNext();) {
			String id = (String) iterator.next();
			deleteTrafficCountRecordById(id);
		}
	}

	public void deleteTrafficCountRecordById(String id) throws SQLException {
		try {

			Connection connection = getConnection();
			Statement s = connection.createStatement();

			String sql = "DELETE FROM trafficcount WHERE id = '" + id + "'";
			logger.debug("delete TrafficCountRecord sql=" + sql);
			s.execute(sql);

			connection.close(); // returns connection to pool

		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	

}
