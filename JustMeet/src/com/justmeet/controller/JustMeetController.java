package com.justmeet.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justmeet.entities.Center;
import com.justmeet.entities.CenterList;
import com.justmeet.entities.Plan;
import com.justmeet.entities.PlanList;
import com.justmeet.entities.User;
import com.justmeet.entities.UserList;
import com.justmeet.service.CenterService;
import com.justmeet.service.ExpenseService;
import com.justmeet.service.GcmService;
import com.justmeet.service.PlanService;
import com.justmeet.service.UserService;

@Controller
@RequestMapping("/operation")
public class JustMeetController {

	private static final Logger logger = Logger
			.getLogger(JustMeetController.class);

	@Autowired
	private GcmService gcmService;

	@Autowired
	private UserService userService;

	@Autowired
	private CenterService centerService;

	@Autowired
	private PlanService planService;

	@Autowired
	private ExpenseService expenseService;
	
	
	//*****************USER***************************************

	@RequestMapping(method = RequestMethod.GET, value = "/addRegId")
	public @ResponseBody
	void addRegId(@RequestParam(value = "regId") String regId,
			@RequestParam(value = "phone") String phone) {
		logger.info("Reg Id storage.");
		this.gcmService.addRegId(regId, phone);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/addUser")
	public @ResponseBody
	User addUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "bloodGroup") String bloodGroup,
			@RequestParam(value = "dob") String dob,
			@RequestParam(value = "sex") String sex,
			@RequestParam(value = "address") String address,
			@RequestParam(value = "doctorFlag") String doctorFlag,
			@RequestParam(value = "primaryCenterId", required = false) String primaryCenterId,
			@RequestParam(value = "primaryDoctorId", required = false) String primaryDoctorId) {
		logger.info("New User addition: " + phone + "/" + name);
		return this.userService.addUser(name.replace("%20", " "), phone, bloodGroup, dob, sex,
				address, doctorFlag, primaryCenterId, primaryDoctorId, "");
	}

	@RequestMapping(method = RequestMethod.POST, value = "/uploadUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] uploadUserImage(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Image upload started.");
		return userService.uploadUserImage(phone, file);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editUser")
	public @ResponseBody
	User editUser(@RequestParam(value = "name") String name,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "bloodGroup") String bloodGroup,
			@RequestParam(value = "dob") String dob,
			@RequestParam(value = "sex") String sex,
			@RequestParam(value = "address") String address,
			@RequestParam(value = "doctorFlag") String doctorFlag) {
		logger.info("Edit User addition: " + phone + "/" + name);
		return userService.editUser(name.replace("%20", " "), phone, bloodGroup, dob, sex, address,
				doctorFlag);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/fetchUser")
	public @ResponseBody
	User fetchUser(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch User: " + phone);
		return userService.fetchUser(phone);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchExistingDoctors")
	public @ResponseBody
	UserList fetchExistingDoctors(
			@RequestParam(value = "phoneList") String phoneList) {
		return userService.fetchDoctorsList(phoneList);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/searchDoctors")
	public @ResponseBody
	UserList searchDoctors(
			@RequestParam(value = "name") String name) {
		return userService.searchDoctors(name.replace("%20", " "));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
	byte[] fetchUserImage(@RequestParam(value = "phone") String phone) {
		logger.info("Image fetch started.");
		return userService.fetchUserImage(phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/deleteUser")
	public @ResponseBody
	void deleteUser(@RequestParam(value = "phone") String phone) {
		logger.info("Delete User: " + phone);
		userService.deleteUser(phone);
	}
	
	
	//*****************CENTERS***************************************

	@RequestMapping(method = RequestMethod.POST, value = "/addCenter")
	public @ResponseBody
	Center addCenter(@RequestParam(value = "name") String name,
			@RequestParam(value = "adminName") String adminName,
			@RequestParam(value = "adminPhone") String adminPhone,
			@RequestParam(value = "address") String address,
			@RequestParam(value = "members") String members,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Add Center: " + adminPhone + "/" + name);
		return centerService.addCenter(name.replace("%20", " "), adminName.replace("%20", " "), adminPhone, address, members, file);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchCenter")
	public @ResponseBody
	Center fetchCenter(@RequestParam(value = "id") String id) {
		logger.info("Center search begin: " + id);
		return centerService.fetchCenter(id);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchCenterUsers")
	public @ResponseBody
	UserList fetchCenterUsers(@RequestParam(value = "phone") String phone) {
		logger.info("Center Users Fetch: " + phone);
		return centerService.fetchCenterUsers(phone);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/editCenter")
	public @ResponseBody
	Center editCenter(@RequestParam(value = "id") String id,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "adminName") String adminName,
			@RequestParam(value = "adminPhone") String adminPhone,
			@RequestParam(value = "address") String address,
			@RequestParam(value = "image") MultipartFile file) {
		logger.info("Add Center: " + adminPhone + "/" + name);
		return centerService.editCenter(id, name.replace("%20", " "), adminName.replace("%20", " "), adminPhone, address, file);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/uploadCenterImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] uploadCenterImage(
			@RequestParam(value = "image") MultipartFile file,
			@RequestParam(value = "id") String id) {
		logger.info("Image upload started.");
		return centerService.uploadCenterImage(file, id);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchExistingCenters")
	public @ResponseBody
	CenterList fetchExistingCenters(
			@RequestParam(value = "phone") String phone) {
		return centerService.fetchCentersList(phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/searchCenter")
	public @ResponseBody
	CenterList searchCenter(@RequestParam(value = "name") String name) {
		logger.info("center search begin: " + name);
		return centerService.searchCenter(name.replace("%20", " "));
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/joinCenter")
	public @ResponseBody
	Center joinCenter(@RequestParam(value = "id") String id,
			@RequestParam(value = "phone") String phone) {
		return centerService.joinCenter(id, phone);

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/leaveCenter")
	public @ResponseBody
	void leaveCenter(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "id") String id) {
		logger.info("Leave Center: " + phone + "/" + id);
		centerService.leaveCenter(id, phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchUserCenters")
	public @ResponseBody
	CenterList fetchUserCenters(
			@RequestParam(value = "phone") String phone) {
		return centerService.fetchUserCenters(phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchCenterForAdmin")
	public @ResponseBody
	Center fetchCenterForAdmin(
			@RequestParam(value = "phone") String phone) {
		return centerService.fetchCenterForAdmin(phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/deleteCenter")
	public @ResponseBody
	void deleteCenter(@RequestParam(value = "phone") String phone) {
		logger.info("Delete Center: " + phone);
		centerService.deleteCenter(phone);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchCenterImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] fetchGroupImage(@RequestParam(value = "id") String id) {
		logger.info("Center Image fetch started: " + id);
		return centerService.fetchCenterImage(id);
	}
	
	
	//************************************** APPOINTMENTS ***********************************************
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchUpcomingPlans")
	public @ResponseBody
	PlanList fetchUpcomingPlans(@RequestParam(value = "phone") String phone) {
		logger.info("Fetch Upcoming plans for " + phone);
		return planService.fetchUpcomingPlans(phone);

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlan")
	public @ResponseBody
	Plan fetchPlan(@RequestParam(value = "id") String id) {
		logger.info("Fetch plan for " + id);
		return planService.fetchPlan(id);

	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlanUsers")
	public @ResponseBody
	UserList fetchPlanUsers(@RequestParam(value = "id") String id) {
		logger.info("Fetch plan for " + id);
		Plan plan = planService.fetchPlan(id);
		UserList userList = new UserList();
		List<User> users = new ArrayList<User>();
		if("Y".equals(plan.getCenterPlanFlag())){
			String planFile = plan.getPlanFile();
			if(!StringUtils.isEmpty(planFile)){
				String[] memberRsvps = StringUtils.commaDelimitedListToStringArray(planFile);
				for(String memberRsvp: memberRsvps){
					if(!StringUtils.isEmpty(memberRsvp)){
						String[] rsvp = StringUtils.delimitedListToStringArray(memberRsvp, "|");
						if("Y".equals(rsvp[1])){
							User user = userService.fetchUser(rsvp[0]);
							users.add(user);
						}
					}					
				}
			}
			
		} else {
			if("Y".equals(plan.getUserRsvp())){
				String userPhone = plan.getUserPhone();
				User user = userService.fetchUser(userPhone);
				users.add(user);
			}
			if("Y".equals(plan.getDocRsvp())){
				String docPhone = plan.getDocPhone();
				User user = userService.fetchUser(docPhone);
				users.add(user);
			}		
		}
		if(!users.isEmpty()){
			userList.setUsers(users);
		}
		return userList;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/newPlan")
	public @ResponseBody
	Plan newPlan(@RequestParam(value = "title") String title,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "userPhone") String userPhone,
			@RequestParam(value = "userRsvp") String userRsvp,
			@RequestParam(value = "docPhone") String docPhone,
			@RequestParam(value = "docRsvp") String docRsvp,
			@RequestParam(value = "centerPlanFlag") String centerPlanFlag) {
		logger.info("New Plan addition");
		
		List<String> gcmList = new ArrayList<String>();
		String centerPlanFile = "";
		String centerId ="";
		if("Y".equals(centerPlanFlag)){
			Center center = centerService.fetchCenterForAdmin(userPhone);
			List<String> centerMembers = center.getMembers();
			if(!centerMembers.isEmpty()){
				gcmList.addAll(centerMembers);
				centerPlanFile = StringUtils.collectionToDelimitedString(centerMembers, "|N,");
			}
			centerId = String.valueOf(center.getId());
		}
		if(!StringUtils.isEmpty(userPhone)){
			gcmList.add(userPhone);
		}
		if(!StringUtils.isEmpty(docPhone)){
			gcmList.add(docPhone);
		}
		
		Plan plan = planService.newPlan(title, planDate, planTime,
				userPhone, userRsvp, docPhone, docRsvp, centerPlanFlag, endDate, endTime, centerId, centerPlanFile);
		if (plan != null) {

			gcmService.broadcast("Health Meet",
					"A new plan has been added '" + title + "'", gcmList);
			logger.info("Plan created : " + title);
			return plan;
		}
		logger.info("Plan creation failed");
		return new Plan();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/editPlan")
	public @ResponseBody
	Plan editPlan(@RequestParam(value = "id") String id,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "title") String title,
			@RequestParam(value = "date") String planDate,
			@RequestParam(value = "time") String planTime,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value = "endTime") String endTime,
			@RequestParam(value = "centerPlanFlag") String centerFlag,
			@RequestParam(value = "cancelFlag") String cancelFlag) {
		logger.info("Edit Plan Plan: "+id);
		
		List<String> gcmList = new ArrayList<String>();
		Plan plan = planService.fetchPlan(id);
		if("Y".equals(centerFlag)){
			Center center = centerService.fetchCenterForAdmin(phone);
			List<String> centerMembers = center.getMembers();
			if(!centerMembers.isEmpty()){
				gcmList.addAll(centerMembers);
			}
			if("Y".equals(cancelFlag)){
				gcmService.broadcast("Health Meet",
						"Plan '" + title + "' has been cancelled", gcmList);
				planService.deletePlan(id);
				return plan;
			} else {
				gcmService.broadcast("Health Meet",
						"Plan '" + title + "' has been edited", gcmList);
				return planService.editPlan(id, title, planDate, planTime, endDate, endTime);
			}
			
		} else {
			gcmList.add(plan.getUserPhone());
			gcmList.add(plan.getDocPhone());
			if("Y".equals(cancelFlag)){
				gcmService.broadcast("Health Meet",
						"Plan '" + title + "' has been cancelled", gcmList);
				planService.deletePlan(id);
				return plan;
			} else {
				gcmService.broadcast("Health Meet",
						"Plan '" + title + "' has been edited", gcmList);
				return planService.editPlan(id, title, planDate, planTime, endDate, endTime);
			}
		}
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/rsvpPlan")
	public @ResponseBody
	Plan rsvpPlan(@RequestParam(value = "id") String id,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "rsvp") String rsvp,
			@RequestParam(value = "centerPlanFlag") String centerFlag,
			@RequestParam(value = "docFlag") String docFlag) {
		Plan plan = planService.fetchPlan(id);
		User user = userService.fetchUser(phone);
		String userName = user.getName();
		List<String> gcmList = new ArrayList<String>();
		if("Y".equals(centerFlag)){
			String memberRsvp = phone+"|"+rsvp;
			String allMembersRsvps = plan.getPlanFile();
			gcmList.add(plan.getUserPhone());
			gcmList.add(phone);
			if("Y".equals(rsvp)) {
				gcmService.broadcast("Health Meet", "Member "+userName+" has accepted appointment titled: "+plan.getTitle(), gcmList);
				allMembersRsvps = allMembersRsvps.replace(phone+"|N", memberRsvp);
			} else if("N".equals(rsvp)) {
				gcmService.broadcast("Health Meet", "Member "+userName+" has declined appointment titled: "+plan.getTitle(), gcmList);
				allMembersRsvps = allMembersRsvps.replace(phone+"|Y", memberRsvp);
			}
			return planService.updateRsvp(id, plan.getUserRsvp(), plan.getDocRsvp(), allMembersRsvps);
		} else if ("Y".equals(docFlag)){
			gcmList.add(plan.getUserPhone());
			gcmList.add(plan.getDocPhone());
			if("Y".equals(rsvp)) {
				gcmService.broadcast("Health Meet", "Doctor "+userName+" has accepted appointment titled: "+plan.getTitle(), gcmList);
			} else {
				gcmService.broadcast("Health Meet", "Doctor "+userName+" has declined appointment titled: "+plan.getTitle(), gcmList);
			}
			return planService.updateRsvp(id, plan.getUserRsvp(), rsvp, plan.getPlanFile());
		} else {
			if("Y".equals(rsvp)) {
				gcmService.broadcast("Health Meet", userName+" has accepted appointment titled: "+plan.getTitle(), gcmList);
			} else {
				planService.deletePlan(String.valueOf(plan.getId()));
				gcmService.broadcast("Health Meet", userName+" has declined appointment titled: "+plan.getTitle(), gcmList);
				return plan;
			}
			
			return planService.updateRsvp(id, rsvp, plan.getDocRsvp(), plan.getPlanFile());
		}
	}
	
	
	
//	
//	
//	
//
//	
//	
//	
//	
//	
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchUpcomingPlans")
//	public @ResponseBody
//	PlanList fetchUpcomingPlans(@RequestParam(value = "phone") String phone) {
//		logger.info("Fetch Upcoming plans for " + phone);
//		return planService.fetchUpcomingPlans(phone);
//
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchUserImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
//	public @ResponseBody
//	byte[] fetchUserImage(@RequestParam(value = "phone") String phone) {
//		logger.info("Image fetch started.");
//		return userService.fetchUserImage(phone);
//	}
//
//	
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupImage", headers = "Accept=*/*", produces = MediaType.IMAGE_JPEG_VALUE)
//	public @ResponseBody
//	byte[] fetchGroupImage(@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		logger.info("Group Image fetch started: " + groupName);
//		return groupService.fetchGroupImage(groupName, groupIndex);
//	}
//
//	
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupPlans")
//	public @ResponseBody
//	PlanList fetchGroupPlans(
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		logger.info("Fetching plans for the group: " + groupName);
//		return planService.fetchGroupPlans(groupName, groupIndex);
//	}
//
//	/*
//	 * @RequestMapping(method = RequestMethod.GET, value = "/addPlan") public
//	 * @ResponseBody Plan addPlan(@RequestParam(value = "name") String planName,
//	 * 
//	 * @RequestParam(value = "phone") String phone,
//	 * 
//	 * @RequestParam(value = "date") String planDate,
//	 * 
//	 * @RequestParam(value = "time") String planTime,
//	 * 
//	 * @RequestParam(value = "endDate") String endDate,
//	 * 
//	 * @RequestParam(value = "endTime") String endTime,
//	 * 
//	 * @RequestParam(value = "location") String planLocation,
//	 * 
//	 * @RequestParam(value = "groupName") String groupName,
//	 * 
//	 * @RequestParam(value = "creator") String creator) {
//	 * logger.info("Plan addition for " +groupName); Plan plan =
//	 * planService.addPlan(planName, phone, planDate, planTime, planLocation,
//	 * groupName, creator, endDate, endTime); if(plan != null){ Group group =
//	 * this.groupService.searchGroup(groupName); List<String> phoneList =
//	 * group.getMembers(); this.gcmService.broadcast("Just Meet",
//	 * "A new plan has been added '"+ planName+"' to '"+groupName+"'",
//	 * phoneList); logger.info("Plan created : "+ planName); return plan; }
//	 * logger.info("Plan creation failed"); return new Plan(); }
//	 */
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlan")
//	public @ResponseBody
//	Plan fetchPlan(@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planIndex") String planIndex) {
//		logger.info("Plan fetch : " + planName);
//		return planService.fetchPlan(planName, planIndex);
//	}
//
//	
//
//	@RequestMapping(method = RequestMethod.GET, value = "/deletePlan")
//	public @ResponseBody
//	Plan deletePlan(@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planName") String planIndex,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupName") String groupIndex) {
//		Plan plan = planService.fetchPlan(planName, planIndex);
//		gcmService.broadcast("Just Meet", " Plan '" + planName
//				+ "' has been deleted from '" + groupName + "'",
//				plan.getMembersInvited());
//		logger.info("Plan deleted: " + planName);
//		return planService.deletePlan(planName, planIndex, groupName,
//				groupIndex);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchPlanHistory")
//	public @ResponseBody
//	PlanList fetchPlanHistory(
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		return planService.fetchPlanHistory(groupName, groupIndex);
//	}
//
//	
//
//	@RequestMapping(method = RequestMethod.GET, value = "/setAdminDecisionForUser")
//	public @ResponseBody
//	Group setAdminDecisionForUser(
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex,
//			@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "decision") String decision) {
//
//		return groupService.setAdminDecision(groupName, groupIndex, phone,
//				decision);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/invite")
//	public @ResponseBody
//	Group invite(@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex,
//			@RequestParam(value = "phone") String phone) {
//		return groupService.invite(groupName, groupIndex, phone);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/leaveGroup")
//	public @ResponseBody
//	Group leaveGroup(@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		Group group = groupService.fetchGroup(groupIndex);
//		gcmService.broadcast("Just Meet", phone + " has left the group '"
//				+ groupName + "'", group.getMembers());
//		return groupService.leaveGroup(phone, groupName, groupIndex);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/editPlan")
//	public @ResponseBody
//	Plan editPlan(@RequestParam(value = "newName") String newPlanName,
//			@RequestParam(value = "oldName") String oldPlanName,
//			@RequestParam(value = "planIndex") String planIndex,
//			@RequestParam(value = "date") String planDate,
//			@RequestParam(value = "time") String planTime,
//			@RequestParam(value = "endDate") String endDate,
//			@RequestParam(value = "endTime") String endTime,
//			@RequestParam(value = "location") String planLocation,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		Plan plan = planService.fetchPlan(oldPlanName, planIndex);
//		gcmService.broadcast("Just Meet", "Plan " + oldPlanName
//				+ " has been edited to '" + newPlanName + "'",
//				plan.getMemberNames());
//		return planService.editPlan(newPlanName, oldPlanName, planIndex,
//				planDate, planTime, planLocation, groupName, groupIndex,
//				endDate, endTime);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/deleteAccount")
//	public @ResponseBody
//	void deleteAccount(@RequestParam(value = "phone") String phone) {
//		userService.deleteUser(phone);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/addExpense")
//	public @ResponseBody
//	void addExpense(@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planName") String planIndex,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex,
//			@RequestParam(value = "title") String title,
//			@RequestParam(value = "value") String value) {
//		expenseService.add(phone, planName, planIndex, groupName, groupIndex,
//				title, value);
//		Plan plan = planService.fetchPlan(planName, planIndex);
//		logger.info("member names : " + plan.getMemberNames());
//		String phoneName = null;
//		User user = new User();
//		user = this.userService.fetchUser(phone);
//		phoneName = user.getName();
//		gcmService.broadcast("Just Meet", phoneName
//				+ " added a new expense of Rs." + value + " to plan - '"
//				+ planName + "' in '" + groupName + "'", plan.getMemberNames());
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/updateExpense")
//	public @ResponseBody
//	void updateExpense(@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planIndex") String planIndex,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex,
//			@RequestParam(value = "title") String title,
//			@RequestParam(value = "value") String value) {
//		String phoneName = null;
//		User user = new User();
//		user = this.userService.fetchUser(phone);
//		phoneName = user.getName();
//		Plan plan = planService.fetchPlan(planName, planIndex);
//		gcmService.broadcast("Just Meet", phoneName + " updated expense of Rs."
//				+ value + " to plan - '" + planName + "' in '" + groupName
//				+ "'", plan.getMemberNames());
//		expenseService.update(phone, planName, planIndex, groupName,
//				groupIndex, title, value);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/deleteExpense")
//	public @ResponseBody
//	void deleteExpense(@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planIndex") String planIndex,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "title") String title) {
//		Plan plan = planService.fetchPlan(planName, planIndex);
//		expenseService.delete(phone, planName, groupName, title);
//		String phoneName = null;
//		User user = new User();
//		user = this.userService.fetchUser(phone);
//		phoneName = user.getName();
//		gcmService.broadcast("Just Meet", phoneName
//				+ " deleted an expense from plan - '" + planName + "' in '"
//				+ groupName + "'", plan.getMemberNames());
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchExpense")
//	public @ResponseBody
//	ExpenseList fetchExpense(@RequestParam(value = "phone") String phone,
//			@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planIndex") String planIndex,
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		return expenseService.fetch(phone, planName, planIndex, groupName,
//				groupIndex);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/generateReport")
//	public @ResponseBody
//	ExpenseReport generateReport(
//			@RequestParam(value = "planName") String planName,
//			@RequestParam(value = "planIndex") String planIndex,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		return expenseService.generateReport(planName, planIndex, groupIndex);
//	}
//
//	
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchGroupUsers")
//	public @ResponseBody
//	UserList fetchGroupUsers(
//			@RequestParam(value = "groupName") String groupName,
//			@RequestParam(value = "groupIndex") String groupIndex) {
//		Group group = groupService.fetchGroup(groupIndex);
//		List<String> memberList = group.getMembers();
//		String phoneList = StringUtils
//				.collectionToCommaDelimitedString(memberList);
//		return userService.fetchUserList(phoneList);
//	}
//
//	@RequestMapping(method = RequestMethod.GET, value = "/fetchExistingGroups")
//	public @ResponseBody
//	GroupList fetchExistingGroups(@RequestParam(value = "phone") String phone) {
//
//		return groupService.fetchGroupList(phone);
//	}
//
//	

}
