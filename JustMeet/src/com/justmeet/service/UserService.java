package com.justmeet.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.UserDAO;
import com.justmeet.entities.User;

public class UserService {
	
	private static final Log log = LogFactory.getLog(UserService.class);
	
	@Autowired
	private UserDAO userDao;

	public User addUser(String name, String phone){
		userDao.addUser(name, phone);
		User user = userDao
				.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully: "+phone+"/"+name);
			return user;
		} else {
			log.warn("User addition failed: "+phone+"/"+name);
			return new User();
		}
	}
	
	public User fetchUser(String phone){
		User user = userDao
				.fetchUser(phone);
		if (user != null) {
			log.info("User fetched successfully: "+phone);
			return user;
		} else {
			log.warn("User fetch failed: "+phone);
			return new User();
		}
	}

	

	public byte[] uploadUserImage(String phone, MultipartFile file){
		return null;
	}

	public byte[] fetchUserImage(String phone){
		return null;
	}

}
