package com.justmeet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class GcmDAO {

	private static final Log log = LogFactory.getLog(GcmDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public boolean storeRegId(String regId, String phone) {
		String insertQuery = "INSERT INTO theiyers_whatsThePlan.gcm_details (register_id, phone) VALUES (?, ?)";
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
		String insertQuery = "DELETE FROM theiyers_whatsThePlan.gcm_details WHERE phone=?";
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
		StringBuffer findQuery = new StringBuffer();
		findQuery.append("SELECT * FROM theiyers_whatsThePlan.gcm_details where phone in (");
		int size = phoneList.size();
		for(int i=0; i<size; i++){
			findQuery.append(phoneList.get(i));
			if(i<size-1){
				findQuery.append(",");
			}
		}
		findQuery.append(")");
		
		try {
			return jdbcTemplate.query(findQuery.toString(),
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
}
