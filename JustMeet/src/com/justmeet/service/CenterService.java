package com.justmeet.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.dao.CenterDAO;
import com.justmeet.dao.GroupDAO;
import com.justmeet.dao.PlanDAO;
import com.justmeet.dao.UserDAO;
import com.justmeet.entities.Center;
import com.justmeet.entities.CenterList;
import com.justmeet.entities.Group;
import com.justmeet.entities.GroupList;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;
import com.justmeet.entities.UserList;

public class CenterService {

	private static final Log log = LogFactory.getLog(CenterService.class);

	@Autowired
	private UserDAO userDao;

	@Autowired
	private CenterDAO centerDao;

	@Autowired
	private PlanDAO planDao;

	public Center addCenter(String centerName, String adminName,
			String adminPhone, String address, String members,
			MultipartFile file) {

		InputStream inputStream = null;
		try {
			if (file != null) {
				inputStream = file.getInputStream();
			}
		} catch (IOException e) {
			log.error("Image upload failed." + centerName);
		}
		// Add group
		log.warn("Inputs: " + centerName + "/" + adminPhone);
		int centerIndex = centerDao.addCenter(centerName, adminName,
				adminPhone, address, members);

		if (centerIndex > 0) {
			// Add group Image
			centerDao.addImage(String.valueOf(centerIndex), inputStream);
			// Fetch current groups
			return centerDao.fetchCenter(String.valueOf(centerIndex));
		}

		return new Center();
	}
			
	public Center fetchCenter(String centerIndex) {
		Center center = centerDao.fetchCenter(centerIndex);
		if (center != null) {
			return center;
		}
		return new Center();
	}
	
	public UserList fetchCenterUsers(String phone) {
		UserList userList = new UserList();
		Center center = centerDao.fetchCenterForAdmin(phone);
		if (center != null) {
			List<String> members = center.getMembers();
			if(members != null && !members.isEmpty()){
				List<User> users = new ArrayList<User>();
				for(String memberPhone: members){
					User user = userDao.fetchUser(memberPhone);
					users.add(user);
				}
				userList.setUsers(users);
			}
		}
		return userList;
	}

	public Center editCenter(String id, String centerName, String adminName,
			 String address, MultipartFile file) {

		try {
			InputStream inputStream = null;
			if(file != null){
				inputStream = file.getInputStream();
			}
			
			boolean success = centerDao.editCenter(id, centerName, adminName,
					 address, inputStream);
			if (success) {
				// Fetch current groups
				return centerDao.fetchCenter(id);
			}
		} catch (IOException e) {
			log.error("Image upload failed, center id: " + id);
		}
		log.warn("Inputs: " + centerName);
		
		return new Center();
	}

	public byte[] uploadCenterImage(MultipartFile file, String id) {

		try {
			InputStream inputStream = file.getInputStream();
			boolean success = centerDao.addImage(id, inputStream);
			if (success) {
				InputStream image = centerDao.fetchCenterImage(id);
				if (image != null) {

					return IOUtils.toByteArray(image);
				}
			}
		} catch (IOException e) {
			log.error("Image upload failed, center id: " + id);
		}

		return null;
	}

	public CenterList fetchCentersList(String phone) {
		User user = userDao.fetchUser(phone);
		
		if(user != null){
			List<String> centerIds = user.getCenters();
			if(centerIds != null && !centerIds.isEmpty()){
				String centerIdsString = StringUtils.collectionToCommaDelimitedString(centerIds);
				if(!StringUtils.isEmpty(centerIdsString)){
					List<Center> centers = centerDao.fetchCentersList(centerIdsString);
					if (centers != null) {
						log.info("Center List fetched successfully, Size is: "
								+ centers.size());
						CenterList centerList = new CenterList();
						centerList.setCenters(centers);
						return centerList;
					} 
				}
				
			}
			
		}
		log.error("Center List fetch failed ");
		return new CenterList();
		
	}

	public CenterList searchCenter(String name) {
		List<Center> centers = centerDao.searchCenter(name);
		if (centers != null) {
			log.info("Center List fetched successfully, Size is: "
					+ centers.size());
			CenterList centerList = new CenterList();
			centerList.setCenters(centers);
			;
			return centerList;
		} else {
			log.error("Center List fetch failed ");
			return new CenterList();
		}

	}

	public Center leaveCenter(String id, String phone) {
		// Fetch current groups
		User user = userDao.fetchUser(phone);
		if (user != null) {

			// Update Center with delete phone
			Center center = centerDao.fetchCenter(id);

			if (center != null) {
				List<String> members = new ArrayList<String>();
				List<String> centerMembers = center.getMembers();
				
				if (centerMembers != null && !centerMembers.isEmpty()) {
					members.addAll(centerMembers);
					members.remove(phone);
				}
				
				boolean updateSuccess = centerDao.updateCenterWithUser(id,
						members);

				if (updateSuccess) {
					List<String> centers = new ArrayList<String>();
					List<String> userCenters = user.getCenters();
					if (userCenters != null && !userCenters.isEmpty()) {
						centers.addAll(userCenters);
						centers.remove(id);
					}
					userDao.updateUserWithCenter(phone, centers);

					// Fetch all plans for this center
					String adminPhone = center.getAdminPhone();
					List<String> userAndAdminsList = new ArrayList<String>();
					userAndAdminsList.add(adminPhone);
					List<Plan> planList = planDao
							.fetchUpcomingPlans(userAndAdminsList, user.getDoctorFlag());
					if (planList != null) {
						for (Plan plan : planList) {
							String planFile = plan.getPlanFile();
							planFile = planFile.replace("," + phone + "|N", "");
							planFile = planFile.replace("," + phone + "|Y", "");
							
							planDao.updateRsvp(String.valueOf(plan.getId()),
									plan.getUserRsvp(), plan.getDocRsvp(),
									planFile);
						}
					}

					// Return group info
					return center;
				}

			}
		}

		return new Center();
	}
	
	
	public Center joinCenter(String id, String phone) {
		// Fetch current groups
		User user = userDao.fetchUser(phone);
		if (user != null) {

			// Update Center with phone
			Center center = centerDao.fetchCenter(id);

			if (center != null) {
				List<String> members = new ArrayList<String>();
				List<String> centerMembers = center.getMembers();
				if (centerMembers != null && !centerMembers.isEmpty()) {
					members.addAll(centerMembers);
				}
				members.add(phone);
				boolean updateSuccess = centerDao.updateCenterWithUser(id,
						members);

				if (updateSuccess) {
					List<String> centers = new ArrayList<String>();
					List<String> userCenters = user.getCenters();
					if (userCenters != null && !userCenters.isEmpty()) {
						centers.addAll(userCenters);
					}
					centers.add(id);
					userDao.updateUserWithCenter(phone, centers);

					// Fetch all plans for this center
					String adminPhone = center.getAdminPhone();
					List<String> userAndAdminsList = new ArrayList<String>();
					userAndAdminsList.add(adminPhone);
					List<Plan> planList = planDao
							.fetchUpcomingPlans(userAndAdminsList, user.getDoctorFlag());
					if (planList != null) {
						for (Plan plan : planList) {
							planDao.updateRsvp(String.valueOf(plan.getId()),
									plan.getUserRsvp(), plan.getDocRsvp(),
									plan.getPlanFile() + "," + phone + "|N");
						}
					}

					// Return group info
					return center;
				}

			}
		}

		return new Center();
	}
	

	public CenterList fetchUserCenters(String phone) {
		User user = userDao.fetchUser(phone);
		if(user != null){
			List<String> centerIdList = user.getCenters();
			if(centerIdList != null && !centerIdList.isEmpty()){
				String centerIds = StringUtils
						.collectionToCommaDelimitedString(centerIdList);
				if(!StringUtils.isEmpty(centerIds)){
					List<Center> centers = centerDao.fetchUserCenters(centerIds);
					if (centers != null) {
						log.info("Center List fetched successfully, Size is: "
								+ centers.size());
						CenterList centerList = new CenterList();
						centerList.setCenters(centers);
						return centerList;
					}
				}				
			}
			
		}
		log.error("Center List fetch failed ");
		return new CenterList();
	}

	public Center fetchCenterForAdmin(String phone) {
		Center center = centerDao.fetchCenterForAdmin(phone);
		if (center != null) {
			log.info("Center fetched successfully" + center.getName());
			return center;
		} else {
			log.error("Center List fetch failed ");
			return new Center();
		}
	}
	
	public void deleteCenter(String phone) {
		Center center = centerDao.fetchCenterForAdmin(phone);
		String id = String.valueOf(center.getId());
		List<String> centerMembers = center.getMembers();
		if (centerMembers != null && !centerMembers.isEmpty()) {
			for(String memberPhone: centerMembers){
				User user = userDao.fetchUser(memberPhone);
				if(user != null){
					List<String> centerIds = user.getCenters();
					if(centerIds != null && !centerIds.isEmpty()){
						List<String> newCenterIds = new ArrayList<String>();
						for(String centerId: centerIds){
							if(!id.equals(centerId)){
								newCenterIds.add(centerId);
							}
						}
						userDao.updateUserWithCenter(memberPhone, newCenterIds);
					}
				}
			}
		}
		
		// Fetch all plans for this center
		String adminPhone = center.getAdminPhone();
		List<String> userAndAdminsList = new ArrayList<String>();
		userAndAdminsList.add(adminPhone);
		List<Plan> planList = planDao
				.fetchUpcomingPlans(userAndAdminsList, "N");
		if (planList != null) {
			for (Plan plan : planList) {
				planDao.deletePlan(String.valueOf(plan.getId()));
			}
		}
		centerDao.deleteCenter(id);
	}
	
	public byte[] fetchCenterImage(String id) {
		try {
			InputStream image = centerDao.fetchCenterImage(id);
			if (image != null) {
				return IOUtils.toByteArray(image);
			}
		} catch (IOException e) {
			log.error("Image fetch failed: " + id);
		}
		return null;
   }

	/*
	 * 
	 * public byte[] fetchGroupImage(String groupName, String groupIndex) { try
	 * { InputStream image = groupDao.fetchGroupImage(groupName, groupIndex); if
	 * (image != null) { return IOUtils.toByteArray(image); } } catch
	 * (IOException e) { log.error("Image fetch failed." + groupName); } return
	 * null; }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public Group setAdminDecision(String groupName, String groupIndex,String
	 * phone, String decision) { // Fetch current groups User user =
	 * userDao.fetchUser(phone); if (user != null) {
	 * 
	 * // Update Group with phone Group group = groupDao.fetchGroup(groupIndex);
	 * 
	 * if (group != null && "yes".equals(decision)) { List<String> members =
	 * group.getMembers(); members.add(phone); List<String> pendingMembers =
	 * group.getPendingMembers(); pendingMembers.remove(phone); boolean
	 * updateSuccess = groupDao .updateGroupWithAdminDecision(groupName,
	 * groupIndex, members, pendingMembers);
	 * 
	 * if (updateSuccess) { List<String> groups = user.getGroupNames();
	 * List<String> groupIds = user.getGroupIds(); // Add group to User
	 * groups.add(groupName); groupIds.add(String.valueOf(groupIndex));
	 * 
	 * 
	 * List<String> pendingGroups = user.getPendingGroupNames(); List<String>
	 * pendingGroupIds = user.getPendingGroupIds(); // remove the pending group
	 * to user table pendingGroups.remove(groupName);
	 * pendingGroupIds.remove(String.valueOf(groupIndex));
	 * userDao.updateUserWithBothGroups(phone, groups, pendingGroups, groupIds,
	 * pendingGroupIds);
	 * 
	 * 
	 * // Return group info
	 * 
	 * return groupDao.fetchGroup(groupIndex); } } else if (group != null &&
	 * "no".equals(decision)) { List<String> pendingMembers =
	 * group.getPendingMembers(); pendingMembers.remove(phone);
	 * 
	 * boolean updateSuccess = groupDao .updateGroupWithPendingMember(groupName,
	 * groupIndex, pendingMembers);
	 * 
	 * if (updateSuccess) { List<String> pendingGroups =
	 * user.getPendingGroupNames(); List<String> pendingGroupIds =
	 * user.getPendingGroupIds(); // Add the new group to user table
	 * pendingGroups.remove(groupName);
	 * pendingGroupIds.remove(String.valueOf(groupIndex));
	 * userDao.updateUserWithPendingGroupName(phone, pendingGroups,
	 * pendingGroupIds);
	 * 
	 * // Return group info
	 * 
	 * return groupDao.fetchGroupInformation(groupIndex); } } }
	 * 
	 * return new Group(); }
	 * 
	 * public Group invite(String groupName, String groupIndex, String phone) {
	 * // Fetch current groups User user = userDao.fetchUser(phone); if (user !=
	 * null) {
	 * 
	 * // Update Group with phone Group group =
	 * groupDao.fetchGroupInformation(groupName);
	 * 
	 * List<String> members = group.getMembers(); members.add(phone);
	 * List<String> pendingMembers = group.getPendingMembers(); boolean
	 * updateSuccess = groupDao .updateGroupWithAdminDecision(groupName,
	 * groupIndex, members, pendingMembers);
	 * 
	 * if (updateSuccess) { List<String> groups = user.getGroupNames();
	 * List<String> groupIds = user.getGroupIds(); // Add group to User
	 * groups.add(groupName); groupIds.add(String.valueOf(groupIndex));
	 * 
	 * 
	 * List<String> pendingGroups = user.getPendingGroupNames(); List<String>
	 * pendingGroupIds = user.getPendingGroupIds(); // remove the pending group
	 * to user table pendingGroups.remove(groupName);
	 * pendingGroupIds.remove(String.valueOf(groupIndex));
	 * userDao.updateUserWithBothGroups(phone, groups, pendingGroups, groupIds,
	 * pendingGroupIds);
	 * 
	 * // Return group info
	 * 
	 * return groupDao.fetchGroupInformation(groupName); } }
	 * 
	 * return new Group(); }
	 * 
	 * public Group leaveGroup(String phone, String groupName, String
	 * groupIndex) { User userInformation = userDao .fetchUser(phone); if
	 * (userInformation != null) { List<String> groups =
	 * userInformation.getGroupNames(); List<String> groupIds =
	 * userInformation.getGroupIds();
	 * 
	 * Group group = groupDao.fetchGroup(groupIndex); if (group != null) {
	 * List<String> planIds = group.getPlanIds(); if (planIds != null &&
	 * !planIds.isEmpty()) { for (String planId : planIds) { Plan plan = planDao
	 * .fetchPlanInformation(null, planId); if (plan != null) { List<String>
	 * members = plan.getMemberNames(); members.remove(phone); if
	 * (members.isEmpty()) { planDao.deletePlan(plan.getName(), planId); } else
	 * { planDao.updatePlanWithMember(planId, members); } } } } List<String>
	 * members = group.getMembers(); members.remove(phone); if
	 * (members.isEmpty()) { groupDao.deleteGroup(groupName, groupIndex); } else
	 * { groupDao.updateGroupWithUser(groupName, groupIndex, members); if
	 * (phone.equals(group.getAdmin())) { groupDao.updateGroupAdmin(groupName,
	 * groupIndex, members.get(0));
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * groups.remove(groupName); groupIds.remove(String.valueOf(groupIndex));
	 * userDao.updateUserWithGroupName(phone, groups, groupIds); Group newGroup
	 * = groupDao.fetchGroupInformation(groupName);
	 * 
	 * return newGroup; }
	 * 
	 * return null; }
	 * 
	 * public GroupList fetchGroupList(String phone) { User user =
	 * userDao.fetchUser(phone); List<String> groupIds = user.getGroupIds();
	 * if(groupIds != null && !groupIds.isEmpty()){ List<Group> groups =
	 * groupDao.fetchGroupList(groupIds);
	 * log.info("Group List fetched successfully, Size is: " + groups.size());
	 * GroupList groupList = new GroupList(); groupList.setGroups(groups);
	 * return groupList; } else { log.error("Group List fetch failed ");
	 * 
	 * } return new GroupList(); }
	 */
}
