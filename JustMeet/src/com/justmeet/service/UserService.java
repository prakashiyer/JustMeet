package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Group;
import com.justmeet.entities.Plan;
import com.justmeet.entities.User;
import com.justmeet.entities.UserList;

public class UserService {

	private static final Log log = LogFactory.getLog(UserService.class);

	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private GroupDAO groupDao;
	
	@Autowired
	private PlanDAO planDao;

	public User addUser(String name, String phone) {
		userDao.addUser(name, phone);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully : " + phone + "/" + name);
			return user;
		} else {
			log.warn("User addition failed: " + phone + "/" + name);
			return new User();
		}
	}

	public User fetchUser(String phone) {
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User fetched successfully: " + phone);
			return user;
		} else {
			log.error("User fetch failed: " + phone);
			return new User();
		}
	}

	public byte[] uploadUserImage(String phone, MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			boolean success = userDao.addUserImage(phone, inputStream);
			if (success) {
				InputStream image = userDao.fetchUserImage(phone);
				if (image != null) {
					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image Upload failed: " + phone);
		}

		return null;
	}
	
	public User editUser(String phone, MultipartFile file, String name) {
		try {
			InputStream inputStream = file.getInputStream();
			boolean success = userDao.editUser(phone, inputStream, name);
			if (success) {
				return userDao.fetchUser(phone);
			}
		} catch (IOException e) {
			log.error("Image Upload failed: " + phone);
		}

		return null;
	}

	public byte[] fetchUserImage(String phone) {
		try {
			InputStream image = userDao.fetchUserImage(phone);
			if (image != null) {
				return IOUtils.toByteArray(image);
			}
		} catch (IOException e) {
			log.error("Image fetch failed: " + phone);
		}
		return null;
	}

	public void deleteUser(String phone) {
		User userInformation = userDao
				.fetchUser(phone);
		if (userInformation != null) {
			List<String> groups = userInformation.getGroupIds();
			if (groups != null && !groups.isEmpty()) {
				for (String groupName : groups) {
					Group group = groupDao
							.fetchGroupInformation(groupName);
					if (group != null) {
						List<String> members = group.getMembers();
						members.remove(phone);
						
						String groupIndex = String.valueOf(group.getId());

						if (members.isEmpty()) {
							groupDao.deleteGroup(groupIndex);
						} else {
							groupDao.updateGroupWithUser(groupIndex, members);
							if (phone.equals(group.getAdmin())) {
								groupDao.updateGroupAdmin(groupIndex,
										members.get(0));
							}
						}
					}
				}
				//TODO delete user from group Plans
			}
			userDao.deleteUserInformation(phone);
			
		}
	}
	

	public UserList fetchUserList(String phoneList) {
		List<User> users = userDao.fetchUserList(phoneList);
		if (users != null) {
			log.info("User List fetched successfully, Size is: " + users.size());
			UserList userList = new UserList();
			userList.setUsers(users);
			return userList;
		} else {
			log.error("User List fetch failed ");
			return new UserList();
		}
	}

	public UserList fetchPlanUsers(String planId) {
		Plan plan = planDao.fetchPlanInformation(planId);
		List<String> members = plan.getMembersAttending();
		if(members != null && !members.isEmpty()){
			String phoneList = StringUtils.collectionToCommaDelimitedString(members);
			return fetchUserList(phoneList);
		}
		return new UserList();
	}

	
	
}
