package com.justmeet.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.justmeet.entities.Group;
import com.thoughtworks.xstream.XStream;

public class GroupDAO {
	
	private static final Log log = LogFactory.getLog(GroupDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean addGroup(String groupName, List<String> memberList,
			String admin) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.groups (name, members, plans, pending_members, admin) VALUES (?, ?, ?, ?, ?)";
		try {
			// Create phones xml
			XStream members = new XStream();
			members.alias("Members", List.class);
			members.alias("Entry", String.class);
			String membersXml = members.toXML(memberList);
			jdbcTemplate.update(insertQuery, groupName, membersXml,
					"<Plans/>", "<PendingMembers/>", admin);
			log.info("Group Added successfully: "+groupName);
			return true;
		} catch (Exception e) {
			log.info("Group Addition failed: "+groupName);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Group fetchGroup(String groupName) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where name = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Group>() {

						public Group mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Group group = new Group();
								group.setId(rs.getInt(1));
								group.setName(rs.getString(2));
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> members = (List<String>) membersXs
										.fromXML(rs.getString(3));
								group.setMembers(members);
								XStream plansXs = new XStream();
								plansXs.alias("Plans", List.class);
								plansXs.alias("Entry", String.class);
								List<String> plans = (List<String>) plansXs
										.fromXML(rs.getString(4));
								group.setPlanNames(plans);
								XStream pendingMembersXs = new XStream();
								pendingMembersXs.alias("PendingMembers",
										List.class);
								pendingMembersXs.alias("Entry", String.class);
								List<String> pendingMembers = (List<String>) pendingMembersXs
										.fromXML(rs.getString(6));
								group.setPendingMembers(pendingMembers);
								group.setAdmin(rs.getString(7));
								return group;
							}
							return null;
						}
					}, groupName);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean addGroupImage(String groupName, InputStream inputStream) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET image=? WHERE name=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, groupName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public InputStream fetchGroupImage(String groupName) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where name = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<InputStream>() {

						public InputStream mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								return rs.getBinaryStream(5);
							}
							return null;
						}
					}, groupName);

		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Group fetchGroupInformation(String groupName) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where name = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Group>() {

						public Group mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Group group = new Group();
								group.setId(rs.getInt(1));
								group.setName(rs.getString(2));
								XStream membersXs = new XStream();
								membersXs.alias("Members", List.class);
								membersXs.alias("Entry", String.class);
								List<String> phones = (List<String>) membersXs
										.fromXML(rs.getString(3));
								group.setMembers(phones);
								XStream plansXs = new XStream();
								plansXs.alias("Plans", List.class);
								plansXs.alias("Entry", String.class);
								List<String> plans = (List<String>) plansXs
										.fromXML(rs.getString(4));
								group.setPlanNames(plans);
								XStream pendingMembersXs = new XStream();
								pendingMembersXs.alias("PendingMembers",
										List.class);
								pendingMembersXs.alias("Entry", String.class);
								List<String> pendingMembers = (List<String>) pendingMembersXs
										.fromXML(rs.getString(6));
								group.setPendingMembers(pendingMembers);
								group.setAdmin(rs.getString(7));
								return group;
							}
							return null;
						}
					}, groupName);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean updateGroupWithUserPlan(String groupName, List<String> plans) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET plans =? WHERE name=?";
		// Create plans xml
		XStream plansXs = new XStream();
		plansXs.alias("Plans", List.class);
		plansXs.alias("Entry", String.class);
		String plansXml = plansXs.toXML(plans);
		try {
			jdbcTemplate.update(updateQuery, plansXml, groupName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}

	}

	public boolean updateGroupWithPendingMember(String groupName,
			List<String> pendingMembers) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET pending_members =? WHERE name=?";
		// Create pending Ids xml
		XStream pendingMembersXs = new XStream();
		pendingMembersXs.alias("PendingMembers", List.class);
		pendingMembersXs.alias("Entry", String.class);
		String pendingMembersXml = pendingMembersXs.toXML(pendingMembers);
		try {
			jdbcTemplate.update(updateQuery, pendingMembersXml, groupName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean updateGroupWithAdminDecision(String groupName,
			List<String> members, List<String> pendingMembers) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET members=?, pending_members=? WHERE name=?";
		// Create members xml
		XStream membersXs = new XStream();
		membersXs.alias("Members", List.class);
		membersXs.alias("Entry", String.class);
		String membersXml = membersXs.toXML(members);
		// Create pending Ids xml
		XStream pendingMembersXs = new XStream();
		pendingMembersXs.alias("PendingMembers", List.class);
		pendingMembersXs.alias("Entry", String.class);
		String pendingMembersXml = pendingMembersXs.toXML(pendingMembers);
		try {
			jdbcTemplate.update(updateQuery, membersXml, pendingMembersXml,
					groupName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean deleteGroup(String groupName) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.groups WHERE name=?";

		try {
			jdbcTemplate.update(deleteQuery, groupName);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public boolean updateGroupWithUser(String groupName,
			List<String> members) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET members =? WHERE name=?";
		// Create members xml
		XStream membersXs = new XStream();
		membersXs.alias("Members", List.class);
		membersXs.alias("Entry", String.class);
		String membersXml = membersXs.toXML(members);
		try {
			jdbcTemplate.update(updateQuery, membersXml, groupName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean updateGroupAdmin(String groupName,
			String admin) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET admin =? WHERE name=?";
		
		try {
			jdbcTemplate.update(updateQuery, admin, groupName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
}
