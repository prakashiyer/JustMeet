package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.justmeet.entities.GroupList;
import com.justmeet.entities.Plan;
import com.justmeet.entities.User;

public class GroupService {

	private static final Log log = LogFactory.getLog(GroupService.class);

	@Autowired
	private UserDAO userDao;

	@Autowired
	private GroupDAO groupDao;
	
	@Autowired
	private PlanDAO planDao;

	public Group addGroup(String groupName, String phone) {

		List<String> phoneList = new ArrayList<String>();
		phoneList.add(phone);
		// Add group
		log.warn("Inputs: " + groupName + "/" + phone);
		boolean addSuccess = groupDao.addGroup(groupName, phoneList, phone);

		if (addSuccess) {
			// Fetch current groups
			User user = userDao.fetchUser(phone);
			if (user != null) {
				List<String> groups = user.getGroupNames();
				// Add the new group to user table
				groups.add(groupName);

				boolean updatedSuccess = userDao.updateUserWithGroupName(phone,
						groups);
				if (updatedSuccess) {
					Group responseGroup = groupDao.fetchGroup(groupName);
					if (responseGroup != null) {
						// Return group info
						return responseGroup;
					}

				}
			}
		}

		return new Group();
	}

	public byte[] uploadGroupImage(String groupName, MultipartFile file) {

		try {
			InputStream inputStream = file.getInputStream();
			boolean success = groupDao.addGroupImage(groupName, inputStream);
			if (success) {
				InputStream image = groupDao.fetchGroupImage(groupName);
				if (image != null) {

					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image upload failed." + groupName);
		}

		return null;
	}

	public byte[] fetchGroupImage(String groupName) {
		try {
			InputStream image = groupDao.fetchGroupImage(groupName);
			if (image != null) {
				return IOUtils.toByteArray(image);
			}
		} catch (IOException e) {
			log.error("Image fetch failed." + groupName);
		}
		return null;
	}
	
	public Group searchGroup(String groupName) {
		Group group = groupDao.fetchGroupInformation(groupName);
		if (group != null && groupName.equals(group.getName())) {
			return group;
		}
		return new Group();
	}

	public Group joinGroup(String groupName, String phone) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroupInformation(groupName);

					if (group != null) {
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.add(phone);
						boolean updateSuccess = groupDao
								.updateGroupWithPendingMember(groupName, pendingMembers);

						if (updateSuccess) {
							List<String> pendingGroups = user.getPendingGroupNames();
							// Add the new pending group to user table
							pendingGroups.add(groupName);
							userDao.updateUserWithPendingGroupName(phone,
									pendingGroups);
							
							// Return group info
							
							return groupDao.fetchGroupInformation(groupName);
						}

					}
				}
				
				return new Group();
	}

	public Group setAdminDecision(String groupName, String phone,
			 String decision) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroupInformation(groupName);

					if (group != null && "yes".equals(decision)) {
						List<String> members = group.getMembers();
						members.add(phone);
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.remove(phone);
						boolean updateSuccess = groupDao
								.updateGroupWithAdminDecision(groupName, members,
										pendingMembers);

						if (updateSuccess) {
							List<String> groups = user.getGroupNames();
							// Add the new group to user table
							groups.add(groupName);
							List<String> pendingGroups = user.getPendingGroupNames();
							// remove the pending group to user table
							pendingGroups.remove(groupName);
							userDao.updateUserWithBothGroups(phone, groups,
									pendingGroups);
							

							// Return group info
							
							return groupDao.fetchGroupInformation(groupName);
						}
					} else if (group != null && "no".equals(decision)) {
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.remove(phone);

						boolean updateSuccess = groupDao
								.updateGroupWithPendingMember(groupName, pendingMembers);

						if (updateSuccess) {
							List<String> pendingGroups = user.getPendingGroupNames();
							// Add the new group to user table
							pendingGroups.remove(groupName);
							userDao.updateUserWithPendingGroupName(phone,
									pendingGroups);

							// Return group info
							
							return groupDao.fetchGroupInformation(groupName);
						}
					}
				}
				
				return new Group();
	}

	public Group invite(String groupName, String phone) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroupInformation(groupName);

					List<String> members = group.getMembers();
					members.add(phone);
					List<String> pendingMembers = group.getPendingMembers();
					boolean updateSuccess = groupDao
							.updateGroupWithAdminDecision(groupName, members,
									pendingMembers);

					if (updateSuccess) {
						List<String> groups = user.getGroupNames();
						// Add the new group to user table
						groups.add(groupName);
						List<String> pendingGroups = user.getPendingGroupNames();

						userDao.updateUserWithBothGroups(phone, groups,
								pendingGroups);

						// Return group info
						
						return groupDao.fetchGroupInformation(groupName);
					}
				}
				
				return new Group();
	}

	public Group leaveGroup(String phone, String groupName) {
		User userInformation = userDao
				.fetchUser(phone);
		if (userInformation != null) {
			List<String> groups = userInformation.getGroupNames();

			Group group = groupDao.fetchGroupInformation(groupName);
			if (group != null) {
				List<String> plans = group.getPlanNames();
				if (plans != null && !plans.isEmpty()) {
					for (String planName : plans) {
						Plan plan = planDao
								.fetchPlanInformation(planName);
						if (plan != null) {
							List<String> members = plan.getMemberNames();
							members.remove(phone);
							if (members.isEmpty()) {
								planDao.deletePlan(planName);
							} else {
								planDao.updatePlanWithMember(planName,
										members);
							}
						}
					}
				}
				List<String> members = group.getMembers();
				members.remove(phone);
				if (members.isEmpty()) {
					groupDao.deleteGroup(groupName);
				} else {
					groupDao.updateGroupWithUser(groupName,
							members);
					if (phone.equals(group.getAdmin())) {
						groupDao.updateGroupAdmin(groupName,
								members.get(0));

					}

				}
			}

			groups.remove(groupName);
			userDao.updateUserWithGroupName(phone, groups);
			Group newGroup = groupDao.fetchGroupInformation(groupName);
			
			return newGroup;
		}
		
		return null;
	}

	public GroupList fetchGroupList(String phone) {
		User user = userDao.fetchUser(phone);
		List<String> groupNames = user.getGroupNames();
		List<Group> groups = groupDao.fetchGroupList(groupNames);
		if (groups != null) {
			log.info("Group List fetched successfully, Size is: " + groups.size());
			GroupList groupList = new GroupList();
			groupList.setGroups(groups);
			return groupList;
		} else {
			log.error("Group List fetch failed ");
			return new GroupList();
		}
	}
}
