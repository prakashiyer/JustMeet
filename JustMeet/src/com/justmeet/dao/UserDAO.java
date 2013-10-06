package com.justmeet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.justmeet.entities.User;
import com.thoughtworks.xstream.XStream;

public class UserDAO {

	private static final Log log = LogFactory.getLog(UserDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean addUser(String name, String phone) {
		String groups = "<Groups/>";
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.user_informatiion (name, phone, groups, pending_groups) VALUES (?, ?, ?, ?)";
		try {
			jdbcTemplate.update(insertQuery, name, phone, groups,
					"<PendingGroups/>");
			log.info("User added successfully: "+phone+"/"+name);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
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
								
								XStream groupXs = new XStream();
								groupXs.alias("Groups", List.class);
								groupXs.alias("Entry", String.class);
								List<String> groups = (List<String>) groupXs
										.fromXML(rs.getString(3));
								user.setGroupNames(groups);
								XStream pendingGroupXs = new XStream();
								pendingGroupXs.alias("PendingGroups",
										List.class);
								pendingGroupXs.alias("Entry", String.class);
								List<String> pendingGroups = (List<String>) pendingGroupXs
										.fromXML(rs.getString(5));
								user
										.setPendingGroupNames(pendingGroups);
								user.setPhone(rs.getString(6));
								
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
	
	public boolean updateUserWithGroupName(String phone, List<String> groups) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.user_informatiion SET groups =? WHERE phone=?";
		// Create groups xml
		XStream groupXs = new XStream();
		groupXs.alias("Groups", List.class);
		groupXs.alias("Entry", String.class);
		String groupXml = groupXs.toXML(groups);
		try {
			jdbcTemplate.update(updateQuery, groupXml, phone);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
