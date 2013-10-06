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
		for(String groupName: groupNames){
			if(groups.isEmpty()){
				groups = groups + "'"+groupName+"'";
			} else {
				groups = groups + ",'"+groupName+"'";
			}
			
		} 
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH)+1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
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
				
		String startTime = String.valueOf(calendar.get(Calendar.YEAR))
				+ "-" +strMon
				+ "-" +strdt
				+ " " +strhr
				+ ":" +strMin
				+ ":00";
		log.warn("START TIME:" +startTime);
		findQuery = findQuery + groups+") and start_time > '"+startTime+"' order by start_time asc";
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
