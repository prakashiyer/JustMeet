package com.justmeet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.util.StringUtils;

public class GcmDAO {

	private static final Log log = LogFactory.getLog(GcmDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public boolean storeRegId(String regId, String phone) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.hm_gcm_details (register_id, phone) VALUES (?, ?)";
		try {
			jdbcTemplate.update(insertQuery, regId, phone);
			log.info("Reg Id stored: " +phone+"/"+regId);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public boolean deleteRegId(String phone) {
		String insertQuery = "DELETE FROM theiyers_whatsThePlan.hm_gcm_details WHERE phone=?";
		try {
			jdbcTemplate.update(insertQuery, phone);
			log.info("Reg Id deleted: " +phone);
			return true;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return false;
		}
	}
	
	public List<String> fetchRegIds(List<String> phoneList) {
		
		if(phoneList != null && !phoneList.isEmpty()){
			String phones = StringUtils.collectionToCommaDelimitedString(phoneList);
			String query = "SELECT * FROM theiyers_whatsThePlan.hm_gcm_details where phone in ("+phones+")";
			
			try {
				return jdbcTemplate.query(query.toString(),
						new ParameterizedRowMapper<String>() {
					        
							public String mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								
								if (rs != null) {
									return rs.getString(2);
								}
								return null;
							}
						});

			} catch (Exception e) {
				log.warn(e.getMessage());
				return null;
			}
		}
		
		return null;
		
	}
}
