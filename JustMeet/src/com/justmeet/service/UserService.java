package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Group;
import com.justmeet.entities.Plan;
import com.justmeet.entities.User;

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
			log.info("User added successfully: " + phone + "/" + name);
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
			List<String> groups = userInformation.getGroupNames();
			if (groups != null && !groups.isEmpty()) {
				for (String groupName : groups) {
					Group group = groupDao
							.fetchGroupInformation(groupName);
					if (group != null) {
						List<String> plans = group.getPlanNames();
						if (plans != null && !plans.isEmpty()) {
							for (String planName : plans) {
								Plan plan = planDao
										.fetchPlanInformation(planName);
								if (plan != null) {
									List<String> members = plan
											.getMemberNames();
									members.remove(phone);
									if (members.isEmpty()) {
										planDao.deletePlan(planName);
									} else {
										planDao.updatePlanWithMember(
												planName, members);
									}
								}
							}
						}
						List<String> members = group.getMembers();
						members.remove(phone);

						if (members.isEmpty()) {
							groupDao.deleteGroup(groupName);
						} else {
							groupDao.updateGroupWithUser(
									groupName, members);
							if (phone.equals(group.getAdmin())) {
								groupDao.updateGroupAdmin(groupName,
										members.get(0));
							}
						}
					}
				}
			}
			List<String> pendingGroups = userInformation.getPendingGroupNames();
			if (pendingGroups != null && !pendingGroups.isEmpty()) {
				for (String groupName : groups) {
					Group group = groupDao
							.fetchGroupInformation(groupName);
					List<String> pendingMembers = group.getPendingMembers();
					pendingMembers.remove(phone);

					groupDao.updateGroupWithPendingMember(groupName,
							pendingMembers);
				}
			}
			userDao.deleteUserInformation(phone);
			
		}
	}

}
