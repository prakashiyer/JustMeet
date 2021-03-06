package com.justmeet.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.util.StringUtils;

import com.justmeet.entities.User;
import com.thoughtworks.xstream.XStream;

public class UserDAO {

	private static final Log log = LogFactory.getLog(UserDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean addUser(String name, String phone) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.user_informatiion (name, phone, groups_ids) VALUES (?, ?, ?)";
		try {
			jdbcTemplate.update(insertQuery, name, phone,"");
			log.info("User added successfully: " + phone + "/" + name);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public User fetchUser(String phone) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.user_informatiion where phone = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<User>() {

						public User mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								User user = new User();
								user.setId(rs.getInt(1));
								user.setName(rs.getString(2));
								user.setPhone(rs.getString(3));
								user.setImage(rs.getBytes(4));
								log.info("Group Ids: " + rs.getString(5));
								String[] groupIdsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> groupIds = new ArrayList<String>();
								if(groupIdsArray != null){
									List<String> groupIdsDb = Arrays.asList(groupIdsArray);
									if(groupIdsDb != null && !groupIdsDb.isEmpty()){
										groupIds.addAll(groupIdsDb);
										log.info("Group Ids Size: " + groupIds.size());
									}
								} 
								user.setGroupIds(groupIds);
								
								return user;
							}
							return null;
						}
					}, phone);
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

	}

	public boolean updateUserWithGroup(String phone, List<String> groupIds) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET groups_ids=? WHERE phone=?";
		String groupIdString = StringUtils.collectionToCommaDelimitedString(groupIds);
		log.info("Group Ids to update"+groupIdString);
		try {
			jdbcTemplate.update(updateQuery, groupIdString, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addUserImage(String phone, InputStream inputStream) {
		log.info("User Image upload in DAO");
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET image=? WHERE phone=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, phone);
			log.info("User Image uploaded in DAO: " +phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public boolean editUserName(String phone, String name) {
		log.info("Edit User Name in DAO");
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET name=? WHERE phone=?";
		try {
			jdbcTemplate.update(updateQuery, name, phone);
			log.info("User Image uploaded in DAO: " +phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public boolean editUserImage(String phone, InputStream inputStream) {
		log.info("Edit User Image in DAO");
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET image=? WHERE phone=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, phone);
			log.info("User Image uploaded in DAO: " +phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public InputStream fetchUserImage(String phone) {
		log.info("Image fetch in DAO");
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.user_informatiion where phone = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<InputStream>() {

						public InputStream mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								log.info("Image fetched");
								return rs.getBinaryStream(4);
							}
							return null;
						}
					}, phone);

		} catch (Exception e) {
			return null;
		}

	}
	
	public boolean updateUserWithPendingGroupName(String phone,
			List<String> pendingGroups, List<String> pendingGroupIds) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET pending_groups =?, pending_groups_ids =? WHERE phone=?";
		// Create groups xml
		XStream pendingGroupsXs = new XStream();
		pendingGroupsXs.alias("PendingGroups", List.class);
		pendingGroupsXs.alias("Entry", String.class);
		String pendingGroupsXml = pendingGroupsXs.toXML(pendingGroups);
		
		// Create groups xml
		XStream pendingGroupIdsXs = new XStream();
		pendingGroupIdsXs.alias("PendingGroupIds", List.class);
		pendingGroupIdsXs.alias("Entry", String.class);
		String pendingGroupIdsXml = pendingGroupIdsXs.toXML(pendingGroupIds);
				
		try {
			jdbcTemplate.update(updateQuery, pendingGroupsXml, pendingGroupIdsXml, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateUserWithBothGroups(String phone,
			List<String> groups, List<String> pendingGroups,
			List<String> groupIds, List<String> pendingGroupIds) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET groups =?,pending_groups =?,groups_ids =?,pending_groups_ids =? WHERE phone=?";
		// Create groups xml
		XStream groupXs = new XStream();
		groupXs.alias("Groups", List.class);
		groupXs.alias("Entry", String.class);
		String groupXml = groupXs.toXML(groups);
		// Create groups xml
		XStream pendingGroupsXs = new XStream();
		pendingGroupsXs.alias("PendingGroups", List.class);
		pendingGroupsXs.alias("Entry", String.class);
		String pendingGroupsXml = pendingGroupsXs.toXML(pendingGroups);
		
		// Create groups xml
		XStream groupIdsXs = new XStream();
		groupIdsXs.alias("GroupIds", List.class);
		groupIdsXs.alias("Entry", String.class);
		String groupIdsXml = groupIdsXs.toXML(groupIds);
		// Create groups xml
		XStream pendingGroupIdsXs = new XStream();
		pendingGroupIdsXs.alias("PendingGroupIds", List.class);
		pendingGroupIdsXs.alias("Entry", String.class);
		String pendingGroupIdsXml = pendingGroupIdsXs.toXML(pendingGroupIds);
		try {
			jdbcTemplate.update(updateQuery, groupXml, pendingGroupsXml,groupIdsXml,pendingGroupIdsXml, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean deleteUserInformation(String phone) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.user_informatiion WHERE phone=?";

		try {
			jdbcTemplate.update(deleteQuery, phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public List<User> fetchUserList(String phoneList) {
		
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.user_informatiion where phone in ("+phoneList+")";
		try {
			return jdbcTemplate.query(findQUery,
					new ParameterizedRowMapper<User>() {

						public User mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								User user = new User();
								user.setId(rs.getInt(1));
								user.setName(rs.getString(2));
								user.setPhone(rs.getString(3));
								user.setImage(rs.getBytes(4));
								String[] groupIdsArray = StringUtils.commaDelimitedListToStringArray(rs.getString(5));
								List<String> groupIds = new ArrayList<String>();
								if(groupIdsArray != null){
									List<String> groupIdsDb = Arrays.asList(groupIdsArray);
									if(groupIdsDb != null && !groupIdsDb.isEmpty()){
										groupIds.addAll(groupIdsDb);
									}
								} 
								user.setGroupIds(groupIds);
								return user;
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
