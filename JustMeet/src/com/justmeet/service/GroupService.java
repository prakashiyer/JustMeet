package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

	public Group addGroup(String groupName, String phone, String membersList, MultipartFile file) {

		InputStream inputStream = null;
		try {
			if(file != null){
				inputStream = file.getInputStream();
			}
		} catch (IOException e) {
			log.error("Image upload failed." + groupName);
		}
		// Add group
		log.warn("Inputs: " + groupName + "/" + phone);
		int groupIndex = groupDao.addGroup(groupName, membersList, phone);
		log.info("Members List: "+membersList);
		if (groupIndex > 0) {
			log.info("Group Index"+String.valueOf(groupIndex));
			// Add group Image
			groupDao.addGroupImage(String.valueOf(groupIndex), inputStream);
			String[] membersArray = StringUtils.commaDelimitedListToStringArray(membersList);
			if(membersArray != null && membersArray.length > 0){
				for(String member: membersArray){
					log.info("Member to update"+member);
					User user = userDao.fetchUser(member);
					if (user != null) {
						List<String> groupIds = user.getGroupIds();
						groupIds.add(String.valueOf(groupIndex));
						userDao.updateUserWithGroup(member, groupIds);
					}
				}
				
			}
			
			Group responseGroup = groupDao.fetchGroup(String.valueOf(groupIndex));
			if (responseGroup != null) {
				// Return group info
				return responseGroup;
			}
			// Fetch current groups
			
		}

		return new Group();
	}

	public byte[] uploadGroupImage(MultipartFile file, String groupIndex) {

		try {
			InputStream inputStream = file.getInputStream();
			boolean success = groupDao.addGroupImage(groupIndex, inputStream);
			if (success) {
				InputStream image = groupDao.fetchGroupImage(groupIndex);
				if (image != null) {

					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image upload failed." + groupIndex);
		}

		return null;
	}

	public byte[] fetchGroupImage(String groupIndex) {
		try {
			InputStream image = groupDao.fetchGroupImage(groupIndex);
			if (image != null) {
				return IOUtils.toByteArray(image);
			}
		} catch (IOException e) {
			log.error("Image fetch failed." + groupIndex);
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
						return group;
					}
				}
				
				return new Group();
	}



	public Group leaveGroup(String phone, String groupIndex) {
		User userInformation = userDao
				.fetchUser(phone);
		if (userInformation != null) {
			List<String> groupIds = userInformation.getGroupIds();

			Group group = groupDao.fetchGroup(groupIndex);
			if (group != null) {
				List<Plan> plans = planDao.fetchPlanHistoryForGroup(groupIndex);
				if (plans != null && !plans.isEmpty()) {
					for (Plan plan : plans) {
						if (plan != null) {
							List<String> members = plan.getMembersAttending();
							members.remove(phone);
							if (members.isEmpty()) {
								planDao.deletePlan(String.valueOf(plan.getId()));
							} else {
								planDao.updatePlanWithMember(String.valueOf(plan.getId()),
										members);
							}
						}
					}
				}
				List<String> members = group.getMembers();
				members.remove(phone);
				if (members.isEmpty()) {
					groupDao.deleteGroup(groupIndex);
				} else {
					groupDao.updateGroupWithUser(groupIndex,
							members);
					if (phone.equals(group.getAdmin())) {
						groupDao.updateGroupAdmin(groupIndex,
								members.get(0));

					}

				}
			}

			groupIds.remove(String.valueOf(groupIndex));
			userDao.updateUserWithGroup(phone, groupIds);
			
			return group;
		}
		
		return null;
	}
	
	public void deleteGroup(String groupIndex) {
		groupDao.deleteGroup(groupIndex);
	}

	public GroupList fetchGroupList(String phone) {
		User user = userDao.fetchUser(phone);
		List<String> groupIds = user.getGroupIds();
		GroupList groupList = new GroupList();
		if(groupIds != null && !groupIds.isEmpty()){
			List<Group> groups = groupDao.fetchGroupList(groupIds);
			if(groups != null && !groups.isEmpty()){
				log.info("Group List fetched successfully, Size is: " + groups.size());
				groupList.setGroups(groups);
			}
		} else {
			log.error("Group List fetch failed: " +phone);
			
		}
		return groupList;
	}

	public Group editGroup(String groupId, String name, MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			boolean success = groupDao.updateGroup(groupId, name, inputStream);
			if (success) {
				return groupDao.fetchGroup(groupId);
			}
		} catch (IOException e) {
			log.error("Group update failed." + groupId);
		}
		return null;
	}

	public Group updateMembers(String groupId, String members) {
		Group group = groupDao.fetchGroup(groupId);
		List<String> newMembers = Arrays.asList(StringUtils.commaDelimitedListToStringArray(members));
		List<String> groupMembers = group.getMembers();
		groupMembers.addAll(newMembers);
		groupDao.updateGroupWithUser(groupId, groupMembers);
		return groupDao.fetchGroup(groupId);
	}
}
