package com.justmeet.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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

import com.justmeet.entities.Group;

public class GroupDAO {
	
	private static final Log log = LogFactory.getLog(GroupDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int addGroup(final String groupName,final String memberList,
			final String admin) {
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				String insertQuery = "INSERT INTO theiyers_whatsThePlan.groups (name, members, admin) VALUES (?, ?, ?)";
                
				
				@Override
				public PreparedStatement createPreparedStatement(Connection arg0)
						throws SQLException {
					
					// TODO Auto-generated method stub
					PreparedStatement ps = arg0.prepareStatement(insertQuery, new String[] {"id"});
					ps.setString(1,groupName);
					ps.setString(2,memberList);
					ps.setString(3,admin);
					
					return ps;
				}
			    }, keyHolder);
			return keyHolder.getKey().intValue();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Group Addition failed: "+groupName);
			return 0;

		}
		
		
	}
	
	public Group fetchGroup(String groupIndex) {
		log.info("Fetching Group" +groupIndex);
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where id=?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Group>() {

						public Group mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Group group = new Group();
								group.setId(rs.getInt(1));
								group.setName(rs.getString(2));
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(3));
								if(membersArray != null){
									List<String> members = Arrays.asList(membersArray);
									if(members != null && !members.isEmpty()){
										group.setMembers(members);
									}
								}
								group.setImage(rs.getBytes(4));
								group.setAdmin(rs.getString(5));
								return group;
							}
							return null;
						}
					}, groupIndex);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean addGroupImage(String groupIndex, InputStream inputStream) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET image=? WHERE id=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, groupIndex);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

	public InputStream fetchGroupImage(String groupIndex) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where id=?";
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
					}, groupIndex);

		} catch (Exception e) {
			return null;
		}
	}
	
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
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(3));
								if(membersArray != null){
									List<String> members = Arrays.asList(membersArray);
									if(members != null && !members.isEmpty()){
										group.setMembers(members);
									}
								}
								group.setImage(rs.getBytes(4));
								group.setAdmin(rs.getString(5));
								return group;
							}
							return null;
						}
					}, groupName);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public boolean deleteGroup(String groupIndex) {
		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.groups WHERE id=?";

		try {
			jdbcTemplate.update(deleteQuery, groupIndex);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public boolean updateGroupWithUser(String groupIndex,
			List<String> members) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET members=? WHERE id=?";
		String membersString = StringUtils.collectionToCommaDelimitedString(members);
		try {
			jdbcTemplate.update(updateQuery, membersString, groupIndex);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean updateGroupAdmin(String groupIndex,
			String admin) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET admin =? WHERE id=?";
		
		try {
			jdbcTemplate.update(updateQuery, admin, groupIndex);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean updateGroup(String groupIndex,
			String name, InputStream inputStream) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET name=?, image=? WHERE id=?";
		
		try {
			jdbcTemplate.update(updateQuery, name, inputStream, groupIndex);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	public List<Group> fetchGroupList(List<String> groupsList) {
		String groups = StringUtils.collectionToCommaDelimitedString(groupsList);
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where id in ("+groups+")";
		try {
			return jdbcTemplate.query(findQUery,
					new ParameterizedRowMapper<Group>() {

						public Group mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Group group = new Group();
								group.setId(rs.getInt(1));
								group.setName(rs.getString(2));
								String[] membersArray = StringUtils.commaDelimitedListToStringArray(rs.getString(3));
								if(membersArray != null){
									List<String> members = Arrays.asList(membersArray);
									if(members != null && !members.isEmpty()){
										group.setMembers(members);
									}
								}
								group.setImage(rs.getBytes(4));
								group.setAdmin(rs.getString(5));
								return group;
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
