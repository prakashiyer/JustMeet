package com.justmeet.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

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
}
