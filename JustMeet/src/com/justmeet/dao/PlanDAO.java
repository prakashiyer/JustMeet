package com.justmeet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

	/*@SuppressWarnings("unchecked")
	public List<Plan> fetchUpcomingGroupPlans(List<String> groupNames, List<String> groupIds) {

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
								plan.setName(rs.getString(2));
								plan.setGroupName(rs.getString(3));
								plan.setStartTime(rs.getTimestamp(4).toString());
								plan.setLocation(rs.getString(5));
								
								// Create Members xml
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> memberNames = (List<String>) membersXs
										.fromXML(rs.getString(6));
								plan.setMemberNames(memberNames);
								plan.setCreator(rs.getString(7));
								
								plan.setEndTime(rs.getString(8));
								
								// Create Groups xml
								XStream groupsXs = new XStream();
								groupsXs.alias("Groups", List.class);
								groupsXs.alias("Entry", String.class);
								List<String> groups = (List<String>) groupsXs
										.fromXML(rs.getString(9));
								plan.setGroupsInvited(groups);
								
								// Create Members Invited xml
								XStream membersInvitedXs = new XStream();
								membersInvitedXs.alias("MembersInvited", List.class);
								membersInvitedXs.alias("Entry", String.class);
								List<String> membersInvited = (List<String>) membersInvitedXs
										.fromXML(rs.getString(10));
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

	}*/
	
	public List<Plan> fetchUpcomingPlans(List<String> userAndAdminsList) {
		
		if(!userAndAdminsList.isEmpty()) {
			String userAndAdmins = StringUtils.collectionToCommaDelimitedString(userAndAdminsList);
			String findQuery = "SELECT * FROM theiyers_whatsThePlan.hm_plans where user_phone in ("+userAndAdmins+") ";
			
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
			findQuery = findQuery +" and start_time > '" + startTime
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
									plan.setEndTime(rs.getTimestamp(3).toString());
									plan.setStartTime(rs.getTimestamp(4).toString());
									plan.setUserPhone(rs.getString(5));
									plan.setUserRsvp(rs.getString(6));
									plan.setDocPhone(rs.getString(7));
									plan.setDocRsvp(rs.getString(8));
									plan.setCenterPlanFlag(rs.getString(9));
									plan.setCenterId(rs.getString(10));
									//TODO File code
									plan.setPlanFile(rs.getString(11));
									return plan;
								}
								return null;
							}
						});

			} catch (Exception e) {
				log.warn(e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}
	
	public int newPlan(final String title, final String userPhone, final String userRsvp, final String docPhone, final String docRsvp,
			final String centerFlag, final String centerId, final String centerPlanFile, final String planDate, final String endDate) {
			
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				String insertQuery = "INSERT INTO theiyers_whatsThePlan.hm_plans (title, end_time, start_time, user_phone, user_rsvp, doc_phone, doc_rsvp, center_plan_flag, center_id, plan_file) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
				
				@Override
				public PreparedStatement createPreparedStatement(Connection arg0)
						throws SQLException {
					
					// TODO Auto-generated method stub
					PreparedStatement ps = arg0.prepareStatement(insertQuery, new String[] { "id"});
					ps.setString(1,title);
					ps.setString(2,endDate);
					ps.setString(3,planDate);
					ps.setString(4,userPhone);
					ps.setString(5,userRsvp);
					ps.setString(6,docPhone);
					ps.setString(7,docRsvp);
					ps.setString(8,centerFlag);
					ps.setString(9,centerId);
					ps.setString(10,centerPlanFile);
					return ps;
				}
			    }, keyHolder);
			return keyHolder.getKey().intValue();
		} catch (Exception e) {
			log.warn(e.getMessage());
			return 0;

		}
	}
	
	public Plan fetchPlan(String planIndex) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_plans where id=?";
		
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Plan>() {

						public Plan mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Plan plan = new Plan();
								plan.setId(rs.getInt(1));
								plan.setTitle(rs.getString(2));
								plan.setEndTime(rs.getTimestamp(3).toString());
								plan.setStartTime(rs.getTimestamp(4).toString());
								plan.setUserPhone(rs.getString(5));
								plan.setUserRsvp(rs.getString(6));
								plan.setDocPhone(rs.getString(7));
								plan.setDocRsvp(rs.getString(8));
								plan.setCenterPlanFlag(rs.getString(9));
								plan.setCenterId(rs.getString(10));
								//TODO File code
								plan.setPlanFile(rs.getString(11));
								return plan;
							}
							return null;
						}
					}, planIndex);

		} catch (Exception e) {
			return null;
		}

	}
	
	
	public boolean editPlan(String planId, String title, String planDate, String endDate) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_plans SET title=?, start_time=?, end_time =? WHERE id=?";
		//TODO File code
		try {
			jdbcTemplate.update(updateQuery, title, planDate, endDate, planId);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}
	
	public boolean deletePlan(String id) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.hm_plans WHERE id=?";
		try {
			jdbcTemplate.update(deleteQuery,id);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	
	
	public boolean updateRsvp(String planId, String userRsvp, String docRsvp, String planFile) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_plans SET user_rsvp=?, doc_rsvp=?, plan_file =? WHERE id=?";
		//TODO File code
		try {
			jdbcTemplate.update(updateQuery, userRsvp, docRsvp, planFile, planId);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}

	/*public boolean addPlan(String name, String groupName, String startTime,
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

	

	public boolean deletePlan(String planName, String planIndex) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.plans WHERE name=? and id=?";
		try {
			jdbcTemplate.update(deleteQuery, planName, planIndex);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public boolean updatePlanWithMember(String planId, List<String> members) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.plans SET member_names =? WHERE id=?";
		// Create plans xml
		XStream membersXs = new XStream();
		membersXs.alias("Members", List.class);
		membersXs.alias("Entry", String.class);
		String membersXml = membersXs.toXML(members);
		try {
			jdbcTemplate.update(updateQuery, membersXml, planId);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;

		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> fetchPlanHistory(String groupName, String groupIndex) {
		
		
		String findQuery = "SELECT * FROM theiyers_whatsThePlan.plans where groups_invited like '%"+groupIndex;
		
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

	*/
}
