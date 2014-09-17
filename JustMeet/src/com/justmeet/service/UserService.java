package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
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

	public User addUser(String name, String phone, String bloodGroup,
			String dob, String sex, String address, String doctorFlag,
			String primaryCenterId, String primaryDoctorId, String centers) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Date dobDate = new Date();
		try {
			dobDate = formatter.parse(dob);
		} catch (ParseException e) {
			log.warn("Date Parse exception. This should never happen!");
		}

		userDao.addUser(name, phone, bloodGroup, dobDate, sex, address,
				doctorFlag, primaryCenterId, primaryDoctorId, centers);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully: " + phone + "/" + name);
			return user;
		} else {
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
	
	public User editUser(String name, String phone, String bloodGroup,
			String dob, String sex, String address, String doctorFlag,
			String primaryCenterId, String primaryDoctorId, String centers) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		Date dobDate = new Date();
		try {
			dobDate = formatter.parse(dob);
		} catch (ParseException e) {
			log.warn("Date Parse exception. This should never happen!");
		}

		userDao.editUser(name, phone, bloodGroup, dobDate, sex, address,
				doctorFlag, primaryCenterId, primaryDoctorId, centers);
		User user = userDao.fetchUser(phone);
		if (user != null) {
			log.info("User added successfully: " + phone + "/" + name);
			return user;
		} else {
			return new User();
		}
	}
	
	public UserList fetchDoctorsList(String phoneList) {
		List<User> users = userDao.fetchDocList(phoneList);
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

//	
//	public byte[] fetchUserImage(String phone) {
//		try {
//			InputStream image = userDao.fetchUserImage(phone);
//			if (image != null) {
//				return IOUtils.toByteArray(image);
//			}
//		} catch (IOException e) {
//			log.error("Image fetch failed: " + phone);
//		}
//		return null;
//	}
//
//	public void deleteUser(String phone) {
//		/*User userInformation = userDao.fetchUser(phone);
//		if (userInformation != null) {
//			List<String> groups = userInformation.getGroupNames();
//			if (groups != null && !groups.isEmpty()) {
//				for (String groupName : groups) {
//					Group group = groupDao.fetchGroupInformation(groupName);
//					if (group != null) {
//						List<String> planIds = group.getPlanIds();
//						if (planIds != null && !planIds.isEmpty()) {
//							for (String planId : planIds) {
//								Plan plan = planDao.fetchPlanInformation(null,
//										planId);
//								if (plan != null) {
//									List<String> members = plan
//											.getMemberNames();
//									members.remove(phone);
//									if (members.isEmpty()) {
//										planDao.deletePlan(plan.getName(),
//												planId);
//									} else {
//										planDao.updatePlanWithMember(planId,
//												members);
//									}
//								}
//							}
//						}
//						List<String> members = group.getMembers();
//						members.remove(phone);
//
//						String groupIndex = String.valueOf(group.getId());
//
//						if (members.isEmpty()) {
//							groupDao.deleteGroup(groupName, groupIndex);
//						} else {
//							groupDao.updateGroupWithUser(groupName, groupIndex,
//									members);
//							if (phone.equals(group.getAdmin())) {
//								groupDao.updateGroupAdmin(groupName,
//										groupIndex, members.get(0));
//							}
//						}
//					}
//				}
//			}
//			List<String> pendingGroups = userInformation.getPendingGroupNames();
//			if (pendingGroups != null && !pendingGroups.isEmpty()) {
//				for (String groupName : groups) {
//					Group group = groupDao.fetchGroupInformation(groupName);
//					List<String> pendingMembers = group.getPendingMembers();
//					pendingMembers.remove(phone);
//
//					groupDao.updateGroupWithPendingMember(groupName,
//							String.valueOf(group.getId()), pendingMembers);
//				}
//			}
//			userDao.deleteUserInformation(phone);
//
//		}*/
//	}
//
//	

}
