package com.justmeet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.justmeet.entities.Plan;
import com.thoughtworks.xstream.XStream;

public class PlanDAO {

	private static final Log log = LogFactory.getLog(PlanDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SuppressWarnings("unchecked")
	public List<Plan> fetchUpcomingPlans(List<String> groupNames) {

		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where group_name in (";
		String groups = "";
		for (String groupName : groupNames) {
			if (groups.isEmpty()) {
				groups = groups + "'" + groupName + "'";
			} else {
				groups = groups + ",'" + groupName + "'";
			}

		}
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		String strMon = String.valueOf(month);
		if (month < 10) {
			strMon = "0" + strMon;
		}
		String strdt = String.valueOf(date);
		if (date < 10) {
			strdt = "0" + strdt;
		}
		String strhr = String.valueOf(hour);
		if (hour < 10) {
			strhr = "0" + strhr;
		}
		String strMin = String.valueOf(min);
		if (min < 10) {
			strMin = "0" + strMin;
		}

		String startTime = String.valueOf(calendar.get(Calendar.YEAR)) + "-"
				+ strMon + "-" + strdt + " " + strhr + ":" + strMin + ":00";
		log.warn("START TIME:" + startTime);
		findQuery = findQuery + groups + ") and start_time > '" + startTime
				+ "' order by start_time asc";
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {

							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setName(rs.getString(2));
								plan.setGroupName(rs.getString(3));
								plan.setStartTime(rs.getTimestamp(4).toString());
								plan.setLocation(rs.getString(5));
								plan.setEndTime(rs.getString(8));
								// Create Members xml
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> memberNames = (List<String>) membersXs
										.fromXML(rs.getString(6));
								plan.setMemberNames(memberNames);
								plan.setCreator(rs.getString(7));
								return plan;
							}
							return null;
						}
					});

		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

	}

	public boolean addPlan(String name, String groupName, String startTime,
			String location, List<String> members, String creator, String endTime) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.plans (name, group_name, start_time, location, member_names, creator, end_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
		// Create members xml
		XStream membersXs = new XStream();
		membersXs.alias("Members", List.class);
		membersXs.alias("Entry", String.class);
		String membersXml = membersXs.toXML(members);
		try {
			jdbcTemplate.update(insertQuery, name, groupName, startTime,
					location, membersXml, creator, endTime);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}

	@SuppressWarnings("unchecked")
	public Plan fetchPlanInformation(String name) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.plans where name = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setName(rs.getString(2));
								plan.setGroupName(rs.getString(3));
								plan.setStartTime(rs.getTimestamp(4).toString());
								plan.setLocation(rs.getString(5));
								plan.setEndTime(rs.getString(8));
								// Create members xml
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> memberNames = (List<String>) membersXs
										.fromXML(rs.getString(6));
								plan.setMemberNames(memberNames);
								plan.setCreator(rs.getString(7));
								return plan;
							}
							return null;
						}
					}, name);

		} catch (Exception e) {
			return null;
		}

	}

	public boolean deletePlan(String planName) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.plans WHERE name=?";
		try {
			jdbcTemplate.update(deleteQuery, planName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public boolean updatePlanWithMember(String planName, List<String> members) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.plans SET member_names =? WHERE name=?";
		// Create plans xml
		XStream membersXs = new XStream();
		membersXs.alias("Members", List.class);
		membersXs.alias("Entry", String.class);
		String membersXml = membersXs.toXML(members);
		try {
			jdbcTemplate.update(updateQuery, membersXml, planName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> fetchPlanHistory(String groupName) {
		
		
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where group_name='"+groupName;
		
		Calendar endCal = Calendar.getInstance();
		int month = endCal.get(Calendar.MONTH)+1;
		int date = endCal.get(Calendar.DATE);
		int hour = endCal.get(Calendar.HOUR);
		int min = endCal.get(Calendar.MINUTE);
		String strMon = String.valueOf(month);
		if(month < 10){
			strMon = "0"+strMon;
		}
		String strdt = String.valueOf(date);
		if(date < 10){
			strdt = "0"+strdt;
		}
		String strhr = String.valueOf(hour);
		if(hour < 10){
			strhr = "0"+strhr;
		}
		String strMin = String.valueOf(min);
		if(min < 10){
			strMin = "0"+strMin;
		}
		String endTime = String.valueOf(endCal.get(Calendar.YEAR))
				+ "-" +strMon
				+ "-" +strdt
				+ " " +strhr
				+ ":" +strMin
				+ ":00";
		endCal.add(Calendar.DATE, -14);
		
		int smonth = endCal.get(Calendar.MONTH)+1;
		int sdate = endCal.get(Calendar.DATE);
		int shour = endCal.get(Calendar.HOUR);
		int smin = endCal.get(Calendar.MINUTE);
		String strsMon = String.valueOf(smonth);
		if(smonth < 10){
			strsMon = "0"+strsMon;
		}
		String strsdt = String.valueOf(sdate);
		if(date < 10){
			strsdt = "0"+strsdt;
		}
		String strshr = String.valueOf(shour);
		if(hour < 10){
			strshr = "0"+strshr;
		}
		String strsMin = String.valueOf(smin);
		if(min < 10){
			strsMin = "0"+strsMin;
		}
		String startTime = String.valueOf(endCal.get(Calendar.YEAR))
				+ "-" +strsMon
				+ "-" +strsdt
				+ " " +strshr
				+ ":" +strsMin
				+ ":00";
		findQuery = findQuery + "' and start_time > '"+startTime+"' and start_time < '"+endTime+"' order by start_time desc";
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Plan>() {
				        
						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							
							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setName(rs.getString(2));
								plan.setGroupName(rs.getString(3));
								plan.setStartTime(rs.getTimestamp(4).toString());
								plan.setLocation(rs.getString(5));
								plan.setEndTime(rs.getString(8));
								// Create members xml
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> memberNames = (List<String>) membersXs
										.fromXML(rs.getString(6));
								plan.setMemberNames(memberNames);
								plan.setCreator(rs.getString(7));
								return plan;
							}
							return null;
						}
					});

		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}
	
	public boolean editPlan(String oldName, String newName, String startTime,
			String location, String endTime) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.plans SET name=?, start_time=?, location=?, end_time=? WHERE name=?";
		
		try {
			jdbcTemplate.update(updateQuery, newName, startTime,
					location, endTime, oldName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}

	}
}
