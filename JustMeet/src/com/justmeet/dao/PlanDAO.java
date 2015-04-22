package com.justmeet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import com.justmeet.entities.Plan;
import com.thoughtworks.xstream.XStream;

public class PlanDAO {

	private static final Log log = LogFactory.getLog(PlanDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Plan> fetchUpcomingGroupPlans(List<String> groupIds) {

		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where (";
		String groups = "";
		for (String groupId : groupIds) {
			if (groups.equals("")) {
				groups = groups + "groups_invited like '%" + groupId + "%'";
			} else {
				groups = groups + " or groups_invited like '%" + groupId + "%'";
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
								plan.setTitle(rs.getString(2));
								plan.setStartTime(rs.getTimestamp(3).toString());
								plan.setLocation(rs.getString(4));
								
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> membersAttending = new ArrayList<String>();
								if(membersArray != null){
									List<String> membersAttendingDb = Arrays.asList(membersArray);
									if(membersAttendingDb != null && !membersAttendingDb.isEmpty()){
										membersAttending.addAll(membersAttendingDb);
									} 
								}
								plan.setMembersAttending(membersAttending);
								plan.setCreator(rs.getString(6));
								plan.setEndTime(rs.getString(7));
								
								String[] groupsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(8));
								List<String> groupsInvited = new ArrayList<String>();
								if(groupsArray != null){
									List<String> groupsInvitedDb = Arrays.asList(groupsArray);
									if(groupsInvitedDb != null && !groupsInvitedDb.isEmpty()){
										groupsInvited.addAll(groupsInvitedDb);
									} 
								} 
								plan.setGroupsInvited(groupsInvited);
								
								String[] membersIvitedArray = StringUtils.commaDelimitedListToStringArray(rs.getString(9));
								List<String> membersInvited = new ArrayList<String>();
								if(membersIvitedArray != null){
									List<String> membersInvitedDb = Arrays.asList(membersIvitedArray);
									if(membersInvitedDb != null && !membersInvitedDb.isEmpty()){
										membersInvited.addAll(membersInvitedDb);
										plan.setMembersInvited(membersInvited);
									} 
								}
								plan.setMembersInvited(membersInvited);	
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
	
	public List<Plan> fetchUpcomingPlans(String phone) {

		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where (members_invited like '%";
		
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
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
		findQuery = findQuery + phone + "%') and start_time > '" + startTime
				+ "' order by start_time asc";
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {

							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setTitle(rs.getString(2));
								plan.setStartTime(rs.getTimestamp(3).toString());
								plan.setLocation(rs.getString(4));
								
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> membersAttending = new ArrayList<String>();
								if(membersArray != null){
									List<String> membersAttendingDb = Arrays.asList(membersArray);
									if(membersAttendingDb != null && !membersAttendingDb.isEmpty()){
										membersAttending.addAll(membersAttendingDb);
									} 
								}
								plan.setMembersAttending(membersAttending);
								plan.setCreator(rs.getString(6));
								plan.setEndTime(rs.getString(7));
								
								String[] groupsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(8));
								List<String> groupsInvited = new ArrayList<String>();
								if(groupsArray != null){
									List<String> groupsInvitedDb = Arrays.asList(groupsArray);
									if(groupsInvitedDb != null && !groupsInvitedDb.isEmpty()){
										groupsInvited.addAll(groupsInvitedDb);
									} 
								} 
								plan.setGroupsInvited(groupsInvited);
								
								String[] membersIvitedArray = StringUtils.commaDelimitedListToStringArray(rs.getString(9));
								List<String> membersInvited = new ArrayList<String>();
								if(membersIvitedArray != null){
									List<String> membersInvitedDb = Arrays.asList(membersIvitedArray);
									if(membersInvitedDb != null && !membersInvitedDb.isEmpty()){
										membersInvited.addAll(membersInvitedDb);
										plan.setMembersInvited(membersInvited);
									} 
								}
								plan.setMembersInvited(membersInvited);
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

	public Plan fetchPlanInformation(String planIndex) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.plans where id=?";
		
		
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setTitle(rs.getString(2));
								plan.setStartTime(rs.getTimestamp(3).toString());
								plan.setLocation(rs.getString(4));
								
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> membersAttending = new ArrayList<String>();
								if(membersArray != null){
									List<String> membersAttendingDb = Arrays.asList(membersArray);
									if(membersAttendingDb != null && !membersAttendingDb.isEmpty()){
										membersAttending.addAll(membersAttendingDb);
									} 
								}
								plan.setMembersAttending(membersAttending);
								plan.setCreator(rs.getString(6));
								plan.setEndTime(rs.getString(7));
								
								String[] groupsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(8));
								List<String> groupsInvited = new ArrayList<String>();
								if(groupsArray != null){
									List<String> groupsInvitedDb = Arrays.asList(groupsArray);
									if(groupsInvitedDb != null && !groupsInvitedDb.isEmpty()){
										groupsInvited.addAll(groupsInvitedDb);
									} 
								} 
								plan.setGroupsInvited(groupsInvited);
								
								String[] membersIvitedArray = StringUtils.commaDelimitedListToStringArray(rs.getString(9));
								List<String> membersInvited = new ArrayList<String>();
								if(membersIvitedArray != null){
									List<String> membersInvitedDb = Arrays.asList(membersIvitedArray);
									if(membersInvitedDb != null && !membersInvitedDb.isEmpty()){
										membersInvited.addAll(membersInvitedDb);
										plan.setMembersInvited(membersInvited);
									} 
								}
								plan.setMembersInvited(membersInvited);
								return plan;
							}
							return null;
						}
					}, planIndex);

		} catch (Exception e) {
			return null;
		}

	}

	public boolean deletePlan(String planIndex) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.plans WHERE id=?";
		try {
			jdbcTemplate.update(deleteQuery, planIndex);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public boolean updatePlanWithMember(String planId, List<String> members) {
		String membersAttending = StringUtils.collectionToCommaDelimitedString(members);
		String updateQuery = "UPDATE theiyers_whatsThePlan.plans SET member_attending =? WHERE id=?";
		
		try {
			jdbcTemplate.update(updateQuery, membersAttending, planId);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}
	
	public List<Plan> fetchPlanHistory(String phone) {
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where members_invited like '%"+phone;
		
		Calendar endCal = Calendar.getInstance();
		int month = endCal.get(Calendar.MONTH)+1;
		int date = endCal.get(Calendar.DATE);
		int hour = endCal.get(Calendar.HOUR_OF_DAY);
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
		int shour = endCal.get(Calendar.HOUR_OF_DAY);
		int smin = endCal.get(Calendar.MINUTE);
		String strsMon = String.valueOf(smonth);
		if(smonth < 10){
			strsMon = "0"+strsMon;
		}
		String strsdt = String.valueOf(sdate);
		if(sdate < 10){
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
		findQuery = findQuery + "%' and start_time > '"+startTime+"' and start_time < '"+endTime+"' order by start_time desc";
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Plan>() {
				        
						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							
							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setTitle(rs.getString(2));
								plan.setStartTime(rs.getTimestamp(3).toString());
								plan.setLocation(rs.getString(4));
								
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> membersAttending = new ArrayList<String>();
								if(membersArray != null){
									List<String> membersAttendingDb = Arrays.asList(membersArray);
									if(membersAttendingDb != null && !membersAttendingDb.isEmpty()){
										membersAttending.addAll(membersAttendingDb);
									} 
								}
								plan.setMembersAttending(membersAttending);
								plan.setCreator(rs.getString(6));
								plan.setEndTime(rs.getString(7));
								
								String[] groupsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(8));
								List<String> groupsInvited = new ArrayList<String>();
								if(groupsArray != null){
									List<String> groupsInvitedDb = Arrays.asList(groupsArray);
									if(groupsInvitedDb != null && !groupsInvitedDb.isEmpty()){
										groupsInvited.addAll(groupsInvitedDb);
									} 
								} 
								plan.setGroupsInvited(groupsInvited);
								
								String[] membersIvitedArray = StringUtils.commaDelimitedListToStringArray(rs.getString(9));
								List<String> membersInvited = new ArrayList<String>();
								if(membersIvitedArray != null){
									List<String> membersInvitedDb = Arrays.asList(membersIvitedArray);
									if(membersInvitedDb != null && !membersInvitedDb.isEmpty()){
										membersInvited.addAll(membersInvitedDb);
										plan.setMembersInvited(membersInvited);
									} 
								}
								plan.setMembersInvited(membersInvited);
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
	
	public boolean editPlan(String oldName, String newName, String planIndex, String startTime,
			String location, String endTime) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.plans SET name=?, start_time=?, location=?, end_time=? WHERE name=? and planIndex=?";
		
		try {
			jdbcTemplate.update(updateQuery, newName, startTime,
					location, endTime, oldName, planIndex);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}

	}

	public int newPlan(final String planName,final List<String> membersInvitedList,
			final List<String> groupsInvitedList,final String startTime,final String planLocation,
			final List<String> membersAttendingList,final String creator,final String endTime) {
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				String insertQuery = "INSERT INTO theiyers_whatsThePlan.plans ("
						+ "title, start_time, location, member_attending,"
						+ " creator, end_time, groups_invited, members_invited)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				String membersAttending = StringUtils.collectionToCommaDelimitedString(membersAttendingList);
				String groupsInvited = StringUtils.collectionToCommaDelimitedString(groupsInvitedList);
				String membersInvited = StringUtils.collectionToCommaDelimitedString(membersInvitedList);
				@Override
				public PreparedStatement createPreparedStatement(Connection arg0)
						throws SQLException {
					// TODO Auto-generated method stub
					PreparedStatement ps = arg0.prepareStatement(insertQuery, new String[] { "id"});
					ps.setString(1,planName);
					ps.setString(2,startTime);
					ps.setString(3,planLocation);
					ps.setString(4,membersAttending);
					ps.setString(5,creator);
					ps.setString(6,endTime);
					ps.setString(7,groupsInvited);
					ps.setString(8,membersInvited);
					return ps;
				}
			    }, keyHolder);
			return keyHolder.getKey().intValue();
		} catch (Exception e) {
			log.warn(e.getMessage());
			return 0;

		}
	}

	public List<Plan> fetchPlanHistoryForGroup(String groupIndex) {
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where groups_invited like '%"
				+ groupIndex;

		Calendar endCal = Calendar.getInstance();
		int month = endCal.get(Calendar.MONTH) + 1;
		int date = endCal.get(Calendar.DATE);
		int hour = endCal.get(Calendar.HOUR_OF_DAY);
		int min = endCal.get(Calendar.MINUTE);
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
		String endTime = String.valueOf(endCal.get(Calendar.YEAR)) + "-"
				+ strMon + "-" + strdt + " " + strhr + ":" + strMin + ":00";
		endCal.add(Calendar.DATE, -14);

		int smonth = endCal.get(Calendar.MONTH) + 1;
		int sdate = endCal.get(Calendar.DATE);
		int shour = endCal.get(Calendar.HOUR_OF_DAY);
		int smin = endCal.get(Calendar.MINUTE);
		String strsMon = String.valueOf(smonth);
		if (smonth < 10) {
			strsMon = "0" + strsMon;
		}
		String strsdt = String.valueOf(sdate);
		if (sdate < 10) {
			strsdt = "0" + strsdt;
		}
		String strshr = String.valueOf(shour);
		if (hour < 10) {
			strshr = "0" + strshr;
		}
		String strsMin = String.valueOf(smin);
		if (min < 10) {
			strsMin = "0" + strsMin;
		}
		String startTime = String.valueOf(endCal.get(Calendar.YEAR)) + "-"
				+ strsMon + "-" + strsdt + " " + strshr + ":" + strsMin + ":00";
		findQuery = findQuery + "%' and start_time > '" + startTime
				+ "' and start_time < '" + endTime
				+ "' order by start_time desc";
		try {
			return jdbcTemplate.query(findQuery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {

							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setTitle(rs.getString(2));
								plan.setStartTime(rs.getTimestamp(3).toString());
								plan.setLocation(rs.getString(4));

								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> membersAttending = new ArrayList<String>();
								if(membersArray != null){
									List<String> membersAttendingDb = Arrays.asList(membersArray);
									if(membersAttendingDb != null && !membersAttendingDb.isEmpty()){
										membersAttending.addAll(membersAttendingDb);
									} 
								}
								plan.setMembersAttending(membersAttending);
								plan.setCreator(rs.getString(6));
								plan.setEndTime(rs.getString(7));
								
								String[] groupsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(8));
								List<String> groupsInvited = new ArrayList<String>();
								if(groupsArray != null){
									List<String> groupsInvitedDb = Arrays.asList(groupsArray);
									if(groupsInvitedDb != null && !groupsInvitedDb.isEmpty()){
										groupsInvited.addAll(groupsInvitedDb);
									} 
								} 
								plan.setGroupsInvited(groupsInvited);
								
								String[] membersIvitedArray = StringUtils.commaDelimitedListToStringArray(rs.getString(9));
								List<String> membersInvited = new ArrayList<String>();
								if(membersIvitedArray != null){
									List<String> membersInvitedDb = Arrays.asList(membersIvitedArray);
									if(membersInvitedDb != null && !membersInvitedDb.isEmpty()){
										membersInvited.addAll(membersInvitedDb);
										plan.setMembersInvited(membersInvited);
									} 
								}
								plan.setMembersInvited(membersInvited);
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

}
