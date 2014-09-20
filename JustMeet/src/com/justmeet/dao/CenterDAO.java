package com.justmeet.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import com.justmeet.entities.Center;
import com.justmeet.entities.Group;
import com.justmeet.entities.User;
import com.thoughtworks.xstream.XStream;

public class CenterDAO {
	
	private static final Log log = LogFactory.getLog(CenterDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int addCenter(final String centerName, final String adminName, final String adminPhone, final String address, final String members) {
		
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			
			jdbcTemplate.update(new PreparedStatementCreator() {
				String insertQuery = "INSERT INTO theiyers_whatsThePlan.hm_centers (name, members_file, admin_name, admin_phone, address) VALUES (?, ?, ?, ?, ?)";
                
				
				@Override
				public PreparedStatement createPreparedStatement(Connection arg0)
						throws SQLException {

					PreparedStatement ps = arg0.prepareStatement(insertQuery, new String[] {"id"});
					ps.setString(1,centerName);
					ps.setString(2,members);
					ps.setString(3,adminName);
					ps.setString(4,adminPhone);
					ps.setString(5,address);
					
					return ps;
				}
			    }, keyHolder);
			return keyHolder.getKey().intValue();
		} catch (Exception e) {
			log.warn("Center Addition failed: "+centerName);
			return 0;

		}
	}
	
	public Center fetchCenter(String centerIndex) {
		log.info("Fetching Center" +centerIndex);
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_centers where id=?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<Center>() {

						public Center mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Center center = new Center();
								center.setId(rs.getInt(1));
								center.setName(rs.getString(2));
								String members = rs.getString(3);
								if(members != null){
									center.setMembers(Arrays.asList(members.split(",")));
								}								
								center.setAdminName(rs.getString(4));
								center.setAdminPhone(rs.getString(5));
								center.setImage(rs.getBytes(6));
								center.setAddress(rs.getString(7));
								return center;
							}
							return null;
						}
					}, centerIndex);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean addImage(String id, InputStream inputStream) {
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_centers SET image=? WHERE id=?";
		try {
			jdbcTemplate.update(updateQuery, inputStream, id);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}

    public boolean editCenter(String id, String centerName, String adminName, String adminPhone, String address) {
		
    	String updateQuery = "UPDATE theiyers_whatsThePlan.hm_centers SET name=?, admin_name=?, admin_phone=?, address=? WHERE id=?";
		try {
			jdbcTemplate.update(updateQuery, centerName, adminName, adminPhone, address, id);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
    public InputStream fetchCenterImage(String id) {
		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_centers where id=?";
		try {
			return jdbcTemplate.queryForObject(findQUery,
					new ParameterizedRowMapper<InputStream>() {

						public InputStream mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								return rs.getBinaryStream(6);
							}
							return null;
						}
					}, id);

		} catch (Exception e) {
			return null;
		}
	}
	
    public List<Center> fetchCentersList(String phoneList) {

		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_centers where admin_phone in ("
				+ phoneList + ")";
		try {
			return jdbcTemplate.query(findQUery,
					new ParameterizedRowMapper<Center>() {

						public Center mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Center center = new Center();
								center.setId(rs.getInt(1));
								center.setName(rs.getString(2));
								String members = rs.getString(3);
								if(members != null){
									center.setMembers(Arrays.asList(members.split(",")));
								}
								center.setAdminName(rs.getString(4));
								center.setAdminPhone(rs.getString(5));
								center.setImage(rs.getBytes(6));
								center.setAddress(rs.getString(7));
								return center;
							}
							return null;
						}
					});
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

	}
    
    public List<Center> searchCenter(String name) {

		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_centers where name like '%"
				+ name + "%'";
		try {
			return jdbcTemplate.query(findQUery,
					new ParameterizedRowMapper<Center>() {

						public Center mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Center center = new Center();
								center.setId(rs.getInt(1));
								center.setName(rs.getString(2));
								String members = rs.getString(3);
								if(members != null){
									center.setMembers(Arrays.asList(members.split(",")));
								}
								center.setAdminName(rs.getString(4));
								center.setAdminPhone(rs.getString(5));
								center.setImage(rs.getBytes(6));
								center.setAddress(rs.getString(7));
								return center;
							}
							return null;
						}
					});
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

	}
    
    public boolean updateCenterWithUser(String id,
			List<String> memberList) {
    	String members = StringUtils.collectionToCommaDelimitedString(memberList);
		String updateQuery = "UPDATE theiyers_whatsThePlan.hm_centers SET members_file =? WHERE id=?";
		//TODO Add file code
		try {
			jdbcTemplate.update(updateQuery, members, id);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
    
    public List<Center> fetchUserCenters(String centers) {

		String findQUery = "SELECT * FROM theiyers_whatsThePlan.hm_centers where id in ("
				+ centers + ")";
		try {
			return jdbcTemplate.query(findQUery,
					new ParameterizedRowMapper<Center>() {

						public Center mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							if (rs != null) {
								Center center = new Center();
								center.setId(rs.getInt(1));
								center.setName(rs.getString(2));
								String members = rs.getString(3);
								if(members != null){
									center.setMembers(Arrays.asList(members.split(",")));
								}
								center.setAdminName(rs.getString(4));
								center.setAdminPhone(rs.getString(5));
								center.setImage(rs.getBytes(6));
								center.setAddress(rs.getString(7));
								return center;
							}
							return null;
						}
					});
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}

	}
	
//	
//	
//	
//	@SuppressWarnings("unchecked")
//	public Group fetchGroupInformation(String groupName) {
//		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where name = ?";
//		try {
//			return jdbcTemplate.queryForObject(findQUery,
//					new ParameterizedRowMapper<Group>() {
//
//						public Group mapRow(ResultSet rs, int rowNum)
//								throws SQLException {
//							if (rs != null) {
//								Group group = new Group();
//								group.setId(rs.getInt(1));
//								group.setName(rs.getString(2));
//								XStream membersXs = new XStream();
//								membersXs.alias("Members", List.class);
//								membersXs.alias("Entry", String.class);
//								List<String> phones = (List<String>) membersXs
//										.fromXML(rs.getString(3));
//								group.setMembers(phones);
//								XStream plansXs = new XStream();
//								plansXs.alias("Plans", List.class);
//								plansXs.alias("Entry", String.class);
//								List<String> plans = (List<String>) plansXs
//										.fromXML(rs.getString(4));
//								group.setImage(rs.getBytes(5));
//								group.setPlanNames(plans);
//								XStream pendingMembersXs = new XStream();
//								pendingMembersXs.alias("PendingMembers",
//										List.class);
//								pendingMembersXs.alias("Entry", String.class);
//								List<String> pendingMembers = (List<String>) pendingMembersXs
//										.fromXML(rs.getString(6));
//								group.setPendingMembers(pendingMembers);
//								group.setAdmin(rs.getString(7));
//								XStream planIdsXs = new XStream();
//								planIdsXs.alias("PlanIds", List.class);
//								planIdsXs.alias("Entry", String.class);
//								List<String> planIds = (List<String>) planIdsXs
//										.fromXML(rs.getString(8));
//								group.setPlanIds(planIds);
//								return group;
//							}
//							return null;
//						}
//					}, groupName);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//	
//	public boolean updateGroupWithUserPlan(String groupName, String groupIndex, List<String> plans, List<String> planIds) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET plans =?, plans_ids=? WHERE name=? and id=?";
//		// Create plans xml
//		XStream plansXs = new XStream();
//		plansXs.alias("Plans", List.class);
//		plansXs.alias("Entry", String.class);
//		String plansXml = plansXs.toXML(plans);
//		
//		// Create plans xml
//		XStream plansIdsXs = new XStream();
//		plansIdsXs.alias("PlanIds", List.class);
//		plansIdsXs.alias("Entry", String.class);
//		String plansIdsXml = plansIdsXs.toXML(planIds);
//		try {
//			jdbcTemplate.update(updateQuery, plansXml, plansIdsXml, groupName, groupIndex);
//			return true;
//		} catch (Exception e) {
//			log.warn(e.getMessage());
//			return false;
//		}
//
//	}
//
//	
//
//	public boolean updateGroupWithAdminDecision(String groupName, String groupIndex,
//			List<String> members, List<String> pendingMembers) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET members=?, pending_members=? WHERE name=? and id=?";
//		// Create members xml
//		XStream membersXs = new XStream();
//		membersXs.alias("Members", List.class);
//		membersXs.alias("Entry", String.class);
//		String membersXml = membersXs.toXML(members);
//		// Create pending Ids xml
//		XStream pendingMembersXs = new XStream();
//		pendingMembersXs.alias("PendingMembers", List.class);
//		pendingMembersXs.alias("Entry", String.class);
//		String pendingMembersXml = pendingMembersXs.toXML(pendingMembers);
//		try {
//			jdbcTemplate.update(updateQuery, membersXml, pendingMembersXml,
//					groupName, groupIndex);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//
//	}
//	
//	public boolean deleteGroup(String groupName, String groupIndex) {
//		String deleteQuery = "DELETE FROM theiyers_whatsThePlan.groups WHERE name=? and id=?";
//
//		try {
//			jdbcTemplate.update(deleteQuery, groupName, groupIndex);
//			return true;
//		} catch (Exception e) {
//			log.warn(e.getMessage());
//			return false;
//		}
//	}
//	
//	public boolean updateGroupWithUser(String groupName, String groupIndex,
//			List<String> members) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET members =? WHERE name=? and id=?";
//		// Create members xml
//		XStream membersXs = new XStream();
//		membersXs.alias("Members", List.class);
//		membersXs.alias("Entry", String.class);
//		String membersXml = membersXs.toXML(members);
//		try {
//			jdbcTemplate.update(updateQuery, membersXml, groupName, groupIndex);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//
//	}
//	
//	public boolean updateGroupAdmin(String groupName, String groupIndex,
//			String admin) {
//		String updateQuery = "UPDATE theiyers_whatsThePlan.groups SET admin =? WHERE name=? and id=?";
//		
//		try {
//			jdbcTemplate.update(updateQuery, admin, groupName, groupIndex);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//
//	}
//	
//	@SuppressWarnings("unchecked")
//	public List<Group> fetchGroupList(List<String> groups) {
//		
//		StringBuffer stringBuff = new StringBuffer();
//		int size = groups.size();
//		
//		for(int i=0;i<size; i++){
//			stringBuff.append("'");
//			stringBuff.append(groups.get(i));
//			stringBuff.append("'");
//			if(i != size-1){
//				stringBuff.append(",");
//			}
//		}
//		
//		String findQUery = "SELECT * FROM theiyers_whatsThePlan.groups where id in ("+stringBuff.toString()+")";
//		try {
//			return jdbcTemplate.query(findQUery,
//					new ParameterizedRowMapper<Group>() {
//
//						public Group mapRow(ResultSet rs, int rowNum)
//								throws SQLException {
//							if (rs != null) {
//								Group group = new Group();
//								group.setId(rs.getInt(1));
//								group.setName(rs.getString(2));
//								XStream membersXs = new XStream();
//								membersXs.alias("Members", List.class);
//								membersXs.alias("Entry", String.class);
//								List<String> phones = (List<String>) membersXs
//										.fromXML(rs.getString(3));
//								group.setMembers(phones);
//								XStream plansXs = new XStream();
//								plansXs.alias("Plans", List.class);
//								plansXs.alias("Entry", String.class);
//								List<String> plans = (List<String>) plansXs
//										.fromXML(rs.getString(4));
//								group.setPlanNames(plans);
//								group.setImage(rs.getBytes(5));
//								XStream pendingMembersXs = new XStream();
//								pendingMembersXs.alias("PendingMembers",
//										List.class);
//								pendingMembersXs.alias("Entry", String.class);
//								List<String> pendingMembers = (List<String>) pendingMembersXs
//										.fromXML(rs.getString(6));
//								group.setPendingMembers(pendingMembers);
//								group.setAdmin(rs.getString(7));
//
//								return group;
//							}
//							return null;
//						}
//					});
//		} catch (Exception e) {
//			log.warn(e.getMessage());
//			return null;
//		}
//
//	}
}
