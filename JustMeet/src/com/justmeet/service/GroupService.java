package com.justmeet.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Group;
import com.justmeet.entities.User;

public class GroupService {

	private static final Log log = LogFactory.getLog(GroupService.class);
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private GroupDAO groupDao;
	
	public 
	Group addGroup(String groupName, String phone) {

		List<String> phoneList = new ArrayList<String>();
		phoneList.add(phone);
		// Add group
		log.warn("Inputs: " + groupName + "/" + phone);
		boolean addSuccess = groupDao.addGroup(groupName, phoneList,
				phone);

		if (addSuccess) {
			// Fetch current groups
			User user = userDao
					.fetchUser(phone);
			if (user != null) {
				List<String> groups = user.getGroupNames();
				// Add the new group to user table
				groups.add(groupName);

				boolean updatedSuccess = userDao
						.updateUserWithGroupName(phone, groups);
				if (updatedSuccess) {
					Group responseGroup = groupDao
							.fetchGroup(groupName);
					if (responseGroup != null) {
						// Return group info						
						return responseGroup;
					}

				}
			}
		}
		
		return new Group();
	}
}
