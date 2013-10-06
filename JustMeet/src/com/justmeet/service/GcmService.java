package com.justmeet.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.GcmDAO;


public class GcmService {

   private static final Log log = LogFactory.getLog(GcmService.class);
	
	@Autowired
	private GcmDAO gcmDao;
	
	public void addRegId(String regId, String phone){
		log.info("Reg Id storage.");
		gcmDao.storeRegId(regId, phone);
	}

}
