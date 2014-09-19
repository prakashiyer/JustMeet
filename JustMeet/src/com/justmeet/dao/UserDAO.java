package com.justmeet.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

	public boolean addUser(String name, String phone, String bloodGroup,
			Date dobDate, String sex, String address, String doctorFlag,
			String primaryCenterId, String primaryDoctorId, String centers) {
		
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.hm_user (name, phone, blood_group, dob, sex, doc_flag, primary_center_id, primary_doctor_id, centers) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			jdbcTemplate.update(insertQuery, name, phone, bloodGroup,
					dobDate, sex, address, doctorFlag, primaryCenterId, primaryDoctorId, centers);
			log.info("User added successfully: " + phone + "/" + name);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public User fetchUser(String phone) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_user where phone = ?";
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
								user.setPrimaryCenterId(rs.getInt(4));
								user.setPrimaryDoctorId(rs.getInt(5));
								String centers = rs.getString(6);
								user.setCenters(Arrays.asList(centers.split(",")));
								user.setImage(rs.getBytes(7));
								user.setBloodGroup(rs.getString(8));
								// TODO Fetch Date
								java.sql.Date dbSqlDate = rs.getDate(9);
								
								if(dbSqlDate != null){
									Date date = new Date(dbSqlDate.getTime());
									SimpleDateFormat formatter = new SimpleDateFormat(
											"MM-dd-yyyy");
									user.setDob(formatter.format(date));
								}
								
								user.setSex(rs.getString(10));
								user.setAddress(rs.getString(11));
								user.setDoctorFlag(rs.getString(12));
								return user;
							}

							return null;
						}
					}, phone);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	public boolean editUser(String name, String phone, String bloodGroup,
			Date dobDate, String sex, String address, String doctorFlag,
			String primaryCenterId, String primaryDoctorId, String centers) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_user SET name=?, blood_group=?, dob=?, sex=?, doc_flag=?, primary_center_id=?, primary_doctor_id=?, centers=? WHERE phone=?";
		
		try {
			jdbcTemplate.update(updateQuery, name, bloodGroup,
					dobDate, sex, address, doctorFlag, primaryCenterId, primaryDoctorId, centers, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean addUserImage(String phone, InputStream inputStream) {
		log.info("User Image upload in DAO");
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_user SET image=? WHERE phone=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, phone);
			log.info("User Image uploaded in DAO: " + phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public InputStream fetchUserImage(String phone) {
		log.info("Image fetch in DAO");
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_user where phone = ?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<InputStream>() {

						public InputStream mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								log.info("Image fetched");
								return rs.getBinaryStream(6);
							}
							return null;
						}
					}, phone);

		} catch (Exception e) {
			return null;
		}

	}
	
	public List<User> fetchDocList(String phoneList) {

		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_user where phone in ("
				+ phoneList + ") and doc_flag='true'";
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
								user.setPrimaryCenterId(rs.getInt(4));
								user.setPrimaryDoctorId(rs.getInt(5));
								String centers = rs.getString(6);
								user.setCenters(Arrays.asList(centers.split(",")));
								user.setImage(rs.getBytes(7));
								user.setBloodGroup(rs.getString(8));
								// TODO Fetch Date
								SimpleDateFormat formatter = new SimpleDateFormat(
										"MM-dd-yyyy");
								user.setDob(formatter.format(rs.getDate(9)));
								user.setSex(rs.getString(10));
								user.setAddress(rs.getString(11));
								user.setDoctorFlag(rs.getString(12));
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
	
	
	public boolean updateUserWithCenter(String phone, List<String> memberList) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_user SET members =? WHERE phone=?";
		String members = StringUtils.collectionToCommaDelimitedString(memberList);
		try {
			jdbcTemplate.update(updateQuery, members, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
//	
//
//	
//
//	
//
//	
//
//	public boolean updateUserWithPendingGroupName(String phone,
//			List<String> pendingGroups, List<String> pendingGroupIds) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET pending_groups =?, pending_groups_ids =? WHERE phone=?";
//		// Create groups xml
//		XStream pendingGroupsXs = new XStream();
//		pendingGroupsXs.alias("PendingGroups", List.class);
//		pendingGroupsXs.alias("Entry", String.class);
//		String pendingGroupsXml = pendingGroupsXs.toXML(pendingGroups);
//
//		// Create groups xml
//		XStream pendingGroupIdsXs = new XStream();
//		pendingGroupIdsXs.alias("PendingGroupIds", List.class);
//		pendingGroupIdsXs.alias("Entry", String.class);
//		String pendingGroupIdsXml = pendingGroupIdsXs.toXML(pendingGroupIds);
//
//		try {
//			jdbcTemplate.update(updateQuery, pendingGroupsXml,
//					pendingGroupIdsXml, phone);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	public boolean updateUserWithBothGroups(String phone, List<String> groups,
//			List<String> pendingGroups, List<String> groupIds,
//			List<String> pendingGroupIds) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET groups =?,pending_groups =?,groups_ids =?,pending_groups_ids =? WHERE phone=?";
//		// Create groups xml
//		XStream groupXs = new XStream();
//		groupXs.alias("Groups", List.class);
//		groupXs.alias("Entry", String.class);
//		String groupXml = groupXs.toXML(groups);
//		// Create groups xml
//		XStream pendingGroupsXs = new XStream();
//		pendingGroupsXs.alias("PendingGroups", List.class);
//		pendingGroupsXs.alias("Entry", String.class);
//		String pendingGroupsXml = pendingGroupsXs.toXML(pendingGroups);
//
//		// Create groups xml
//		XStream groupIdsXs = new XStream();
//		groupIdsXs.alias("GroupIds", List.class);
//		groupIdsXs.alias("Entry", String.class);
//		String groupIdsXml = groupIdsXs.toXML(groupIds);
//		// Create groups xml
//		XStream pendingGroupIdsXs = new XStream();
//		pendingGroupIdsXs.alias("PendingGroupIds", List.class);
//		pendingGroupIdsXs.alias("Entry", String.class);
//		String pendingGroupIdsXml = pendingGroupIdsXs.toXML(pendingGroupIds);
//		try {
//			jdbcTemplate.update(updateQuery, groupXml, pendingGroupsXml,
//					groupIdsXml, pendingGroupIdsXml, phone);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}
//
//	public boolean deleteUserInformation(String phone) {
//		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.user_informatiion WHERE phone=?";
//
//		try {
//			jdbcTemplate.update(deleteQuery, phone);
//			return true;
//		} catch (Exception e) {
//			log.warn(e.getMessage());
//			return false;
//		}
//	}
//
//	
}
