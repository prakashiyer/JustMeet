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

	public Group addGroup(String groupName, String phone, MultipartFile file) {

		InputStream inputStream = null;
		try {
			if(file != null){
				inputStream = file.getInputStream();
			}
		} catch (IOException e) {
			log.error("Image upload failed." + groupName);
		}
		List<String> phoneList = new ArrayList<String>();
		phoneList.add(phone);
		// Add group
		log.warn("Inputs: " + groupName + "/" + phone);
		int groupIndex = groupDao.addGroup(groupName, phoneList, phone);

		if (groupIndex > 0) {
			// Add group Image
			groupDao.addGroupImage(groupName, String.valueOf(groupIndex), inputStream);
			// Fetch current groups
			User user = userDao.fetchUser(phone);
			if (user != null) {
				List<String> groups = user.getGroupNames();
				List<String> groupIds = user.getGroupIds();
				// Add the new group to user table
				groups.add(groupName);
				groupIds.add(String.valueOf(groupIndex));

				boolean updatedSuccess = userDao.updateUserWithGroupName(phone,
						groups, groupIds);
				if (updatedSuccess) {
					Group responseGroup = groupDao.fetchGroup(String.valueOf(groupIndex));
					if (responseGroup != null) {
						// Return group info
						return responseGroup;
					}

				}
			}
		}

		return new Group();
	}

	public byte[] uploadGroupImage(String groupName, MultipartFile file, String groupIndex) {

		try {
			InputStream inputStream = file.getInputStream();
			boolean success = groupDao.addGroupImage(groupName, groupIndex, inputStream);
			if (success) {
				InputStream image = groupDao.fetchGroupImage(groupName, groupIndex);
				if (image != null) {

					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image upload failed." + groupName);
		}

		return null;
	}

	public byte[] fetchGroupImage(String groupName, String groupIndex) {
		try {
			InputStream image = groupDao.fetchGroupImage(groupName, groupIndex);
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
	
	public Group fetchGroup(String groupIndex) {
		Group group = groupDao.fetchGroup(groupIndex);
		if (group != null) {
			return group;
		}
		return new Group();
	}

	public Group joinGroup(String groupName, String groupIndex, String phone) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroup(groupIndex);

					if (group != null) {
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.add(phone);
						boolean updateSuccess = groupDao
								.updateGroupWithPendingMember(groupName, groupIndex, pendingMembers);

						if (updateSuccess) {
							List<String> pendingGroups = user.getPendingGroupNames();
							List<String> pendingGroupIds = user.getPendingGroupIds();
							// Add the new pending group to user table
							pendingGroups.add(groupName);
							pendingGroupIds.add(String.valueOf(groupIndex));
							userDao.updateUserWithPendingGroupName(phone,
									pendingGroups, pendingGroupIds);
							
							// Return group info
							
							return group;
						}

					}
				}
				
				return new Group();
	}

	public Group setAdminDecision(String groupName, String groupIndex,String phone,
			 String decision) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroup(groupIndex);

					if (group != null && "yes".equals(decision)) {
						List<String> members = group.getMembers();
						members.add(phone);
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.remove(phone);
						boolean updateSuccess = groupDao
								.updateGroupWithAdminDecision(groupName, groupIndex, members,
										pendingMembers);

						if (updateSuccess) {
							List<String> groups = user.getGroupNames();
							List<String> groupIds = user.getGroupIds();
							// Add group to User
							groups.add(groupName);
							groupIds.add(String.valueOf(groupIndex));
							
							
							List<String> pendingGroups = user.getPendingGroupNames();
							List<String> pendingGroupIds = user.getPendingGroupIds();
							// remove the pending group to user table
							pendingGroups.remove(groupName);
							pendingGroupIds.remove(String.valueOf(groupIndex));
							userDao.updateUserWithBothGroups(phone, groups,
									pendingGroups, groupIds, pendingGroupIds);
							

							// Return group info
							
							return groupDao.fetchGroup(groupIndex);
						}
					} else if (group != null && "no".equals(decision)) {
						List<String> pendingMembers = group.getPendingMembers();
						pendingMembers.remove(phone);

						boolean updateSuccess = groupDao
								.updateGroupWithPendingMember(groupName, groupIndex, pendingMembers);

						if (updateSuccess) {
							List<String> pendingGroups = user.getPendingGroupNames();
							List<String> pendingGroupIds = user.getPendingGroupIds();
							// Add the new group to user table
							pendingGroups.remove(groupName);
							pendingGroupIds.remove(String.valueOf(groupIndex));
							userDao.updateUserWithPendingGroupName(phone,
									pendingGroups, pendingGroupIds);

							// Return group info
							
							return groupDao.fetchGroupInformation(groupIndex);
						}
					}
				}
				
				return new Group();
	}

	public Group invite(String groupName, String groupIndex, String phone) {
		// Fetch current groups
				User user = userDao.fetchUser(phone);
				if (user != null) {

					// Update Group with phone
					Group group = groupDao.fetchGroupInformation(groupName);

					List<String> members = group.getMembers();
					members.add(phone);
					List<String> pendingMembers = group.getPendingMembers();
					boolean updateSuccess = groupDao
							.updateGroupWithAdminDecision(groupName, groupIndex, members,
									pendingMembers);

					if (updateSuccess) {
						List<String> groups = user.getGroupNames();
						List<String> groupIds = user.getGroupIds();
						// Add group to User
						groups.add(groupName);
						groupIds.add(String.valueOf(groupIndex));
						
						
						List<String> pendingGroups = user.getPendingGroupNames();
						List<String> pendingGroupIds = user.getPendingGroupIds();
						// remove the pending group to user table
						pendingGroups.remove(groupName);
						pendingGroupIds.remove(String.valueOf(groupIndex));
						userDao.updateUserWithBothGroups(phone, groups,
								pendingGroups, groupIds, pendingGroupIds);

						// Return group info
						
						return groupDao.fetchGroupInformation(groupName);
					}
				}
				
				return new Group();
	}

	public Group leaveGroup(String phone, String groupName, String groupIndex) {
		User userInformation = userDao
				.fetchUser(phone);
		if (userInformation != null) {
			List<String> groups = userInformation.getGroupNames();
			List<String> groupIds = userInformation.getGroupIds();

			Group group = groupDao.fetchGroup(groupIndex);
			if (group != null) {
				List<String> planIds = group.getPlanIds();
				if (planIds != null && !planIds.isEmpty()) {
					for (String planId : planIds) {
						Plan plan = planDao
								.fetchPlanInformation(null, planId);
						if (plan != null) {
							List<String> members = plan.getMemberNames();
							members.remove(phone);
							if (members.isEmpty()) {
								planDao.deletePlan(plan.getName(), planId);
							} else {
								planDao.updatePlanWithMember(planId,
										members);
							}
						}
					}
				}
				List<String> members = group.getMembers();
				members.remove(phone);
				if (members.isEmpty()) {
					groupDao.deleteGroup(groupName, groupIndex);
				} else {
					groupDao.updateGroupWithUser(groupName, groupIndex,
							members);
					if (phone.equals(group.getAdmin())) {
						groupDao.updateGroupAdmin(groupName, groupIndex,
								members.get(0));

					}

				}
			}

			groups.remove(groupName);
			groupIds.remove(String.valueOf(groupIndex));
			userDao.updateUserWithGroupName(phone, groups, groupIds);
			Group newGroup = groupDao.fetchGroupInformation(groupName);
			
			return newGroup;
		}
		
		return null;
	}

	public GroupList fetchGroupList(String phone) {
		User user = userDao.fetchUser(phone);
		List<String> groupIds = user.getGroupIds();
		if(groupIds != null && !groupIds.isEmpty()){
			List<Group> groups = groupDao.fetchGroupList(groupIds);
			log.info("Group List fetched successfully, Size is: " + groups.size());
			GroupList groupList = new GroupList();
			groupList.setGroups(groups);
			return groupList;
		} else {
			log.error("Group List fetch failed ");
			
		}
		return new GroupList();
	}
}
